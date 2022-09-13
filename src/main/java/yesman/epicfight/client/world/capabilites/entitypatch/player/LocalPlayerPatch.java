package yesman.epicfight.client.world.capabilites.entitypatch.player;

import java.util.UUID;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
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
import yesman.epicfight.network.client.CPPlayAnimation;
import yesman.epicfight.network.client.CPSetPlayerTarget;
import yesman.epicfight.network.client.CPChangePlayerMode;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@OnlyIn(Dist.CLIENT)
public class LocalPlayerPatch extends AbstractClientPlayerPatch<LocalPlayer> {
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("d1a1e102-1621-11ed-861d-0242ac120002");
	private Minecraft minecraft;
	private LivingEntity rayTarget;
	private float prevStamina;
	
	@Override
	public void onConstructed(LocalPlayer entity) {
		super.onConstructed(entity);
		this.minecraft = Minecraft.getInstance();
		ClientEngine.instance.controllEngine.setPlayerPatch(this);
	}
	
	@Override
	public void onJoinWorld(LocalPlayer entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		this.eventListeners.addEventListener(EventType.ACTION_EVENT_CLIENT, ACTION_EVENT_UUID, (playerEvent) -> {
			ClientEngine.instance.controllEngine.unlockHotkeys();
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
		HitResult rayResult = this.minecraft.hitResult;
		
		if (rayResult.getType() == HitResult.Type.ENTITY) {
			Entity hit = ((EntityHitResult)rayResult).getEntity();
			
			if (hit != this.rayTarget) {
				if (hit instanceof LivingEntity) {
					if (!(hit instanceof ArmorStand)) {
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
				this.minecraft.options.setCameraType(CameraType.FIRST_PERSON);
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
				this.minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
			
			if (synchronize) {
				EpicFightNetworkManager.sendToServer(new CPChangePlayerMode(PlayerMode.BATTLE));
			}
		}
		
		super.toBattleMode(synchronize);
	}
	
	@Override
	public boolean isFirstPerson() {
		return this.minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
	}
	
	@Override
	public boolean shouldBlockMoving() {
		return ClientEngine.instance.controllEngine.isKeyDown(this.minecraft.options.keyDown);
	}
	
	public float getPrevStamina() {
		return this.prevStamina;
	}
	
	@Override
	public void openSkillBook(ItemStack itemstack, InteractionHand hand) {
		if (itemstack.hasTag() && itemstack.getTag().contains("skill")) {
			Minecraft.getInstance().setScreen(new SkillBookScreen(this.original, itemstack, hand));
		}
	}
}