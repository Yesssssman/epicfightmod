package yesman.epicfight.client.world.capabilites.entitypatch.player;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPChangePlayerMode;
import yesman.epicfight.network.client.CPPlayAnimation;
import yesman.epicfight.network.client.CPSetPlayerTarget;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@OnlyIn(Dist.CLIENT)
public class LocalPlayerPatch extends AbstractClientPlayerPatch<ClientPlayerEntity> {
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("d1a1e102-1621-11ed-861d-0242ac120002");
	private Minecraft minecraft;
	private LivingEntity rayTarget;
	private float prevStamina;
	
	@Override
	public void onConstructed(ClientPlayerEntity entity) {
		super.onConstructed(entity);
		this.minecraft = Minecraft.getInstance();
		ClientEngine.instance.inputController.setPlayerPatch(this);
	}
	
	@Override
	public void onJoinWorld(ClientPlayerEntity entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		this.eventListeners.addEventListener(EventType.ACTION_EVENT_CLIENT, ACTION_EVENT_UUID, (playerEvent) -> {
			ClientEngine.instance.inputController.unlockHotkeys();
		});
	}
	
	public void onRespawnLocalPlayer(ClientPlayerNetworkEvent.RespawnEvent event) {
		this.onJoinWorld(event.getNewPlayer(), new EntityJoinWorldEvent(event.getNewPlayer(), event.getNewPlayer().level));
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.updateMotion(considerInaction);
		
		if (!this.getClientAnimator().isAiming()) {
			if (this.currentCompositeMotion == LivingMotions.AIM) {
				this.original.getUseItemRemainingTicks();
				ClientEngine.instance.renderEngine.zoomIn();
			}
		}
	}
	
	@Override
	public void clientTick(LivingUpdateEvent event) {
		this.prevStamina = this.getStamina();
		super.clientTick(event);
		RayTraceResult rayResult = this.minecraft.hitResult;
		
		if (rayResult.getType() == RayTraceResult.Type.ENTITY) {
			Entity hit = ((EntityRayTraceResult)rayResult).getEntity();
			
			if (hit != this.rayTarget) {
				if (hit instanceof LivingEntity) {
					if (!(hit instanceof ArmorStandEntity)) {
						this.rayTarget = (LivingEntity)hit;this.rayTarget = (LivingEntity)hit;
					}
				} else if (hit instanceof PartEntity<?>) {
					Entity parent = ((PartEntity<?>)hit).getParent();
					
					if (parent instanceof LivingEntity) {
						this.rayTarget = (LivingEntity)parent;
					}
				} else {
					this.rayTarget = null;
				}
				
				if (this.rayTarget != null) {
					EpicFightNetworkManager.sendToServer(new CPSetPlayerTarget(this.getTarget().getId()));
				}
			}
		}
		
		if (this.rayTarget != null) {
			if (!this.rayTarget.isAlive() || this.getOriginal().distanceToSqr(this.rayTarget) > 64.0D || this.getAngleTo(this.rayTarget) > 100.0D) {
				this.rayTarget = null;
				EpicFightNetworkManager.sendToServer(new CPSetPlayerTarget(-1));
			}
		}
	}
	
	@Override
	protected void playReboundAnimation() {
		super.playReboundAnimation();
		ClientEngine.instance.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier, AnimationPacketProvider packetProvider) {
		EpicFightNetworkManager.sendToServer(new CPPlayAnimation(animation.getNamespaceId(), animation.getId(), convertTimeModifier, false, true));
	}
	
	@Override
	public void updateHeldItem(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		super.updateHeldItem(mainHandCap, offHandCap);
		
		if (EpicFightMod.CLIENT_INGAME_CONFIG.battleAutoSwitchItems.contains(this.original.getMainHandItem().getItem())) {
			this.toBattleMode(true);
		} else if (EpicFightMod.CLIENT_INGAME_CONFIG.miningAutoSwitchItems.contains(this.original.getMainHandItem().getItem())) {
			this.toMiningMode(true);
		}
	}
	
	@Override
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		AttackResult result = super.tryHurt(damageSource, amount);
		
		if (EpicFightMod.CLIENT_INGAME_CONFIG.autoPreparation.getValue() && result.resultType == AttackResult.ResultType.SUCCESS && !this.isBattleMode()) {
			this.toBattleMode(true);
		}
		
		return result;
	}
	
	@Override
	public LivingEntity getTarget() {
		return this.rayTarget;
	}
	
	@Override
	public boolean shouldSkipRender() {
		return !this.isBattleMode() && EpicFightMod.CLIENT_INGAME_CONFIG.filterAnimation.getValue();
	}
	
	@Override
	public void toMiningMode(boolean synchronize) {
		if (this.playerMode != PlayerMode.MINING) {
			ClientEngine.instance.renderEngine.downSlideSkillUI();
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				this.minecraft.options.setCameraType(PointOfView.FIRST_PERSON);
			}
			
			if (synchronize) {
				EpicFightNetworkManager.sendToServer(new CPChangePlayerMode(PlayerMode.MINING));
			}
		}
		
		super.toMiningMode(synchronize);
	}
	
	@Override
	public void toBattleMode(boolean synchronize) {
		if (this.playerMode != PlayerMode.BATTLE) {
			ClientEngine.instance.renderEngine.upSlideSkillUI();
			
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				this.minecraft.options.setCameraType(PointOfView.THIRD_PERSON_BACK);
			}
			
			if (synchronize) {
				EpicFightNetworkManager.sendToServer(new CPChangePlayerMode(PlayerMode.BATTLE));
			}
		}
		
		super.toBattleMode(synchronize);
	}
	
	@Override
	public boolean isFirstPerson() {
		return this.minecraft.options.getCameraType() == PointOfView.FIRST_PERSON;
	}
	
	@Override
	public boolean shouldBlockMoving() {
		return ClientEngine.instance.inputController.isKeyDown(this.minecraft.options.keyDown);
	}
	
	public float getPrevStamina() {
		return this.prevStamina;
	}
	
	@Override
	public void openSkillBook(ItemStack itemstack, Hand hand) {
		if (itemstack.hasTag() && itemstack.getTag().contains("skill")) {
			Minecraft.getInstance().setScreen(new SkillBookScreen(this.original, itemstack, hand));
		}
	}
}