package yesman.epicfight.client.capabilites.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.SkillDescriptionGui;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSPlayAnimation;
import yesman.epicfight.network.client.CTSSetPlayerTarget;
import yesman.epicfight.network.client.CTSToggleMode;
import yesman.epicfight.utils.math.MathUtils;

@OnlyIn(Dist.CLIENT)
public class ClientPlayerData extends RemoteClientPlayerData<ClientPlayerEntity> {
	private LivingEntity rayTarget;
	private float prevStamina;
	
	@Override
	public void onEntityConstructed(ClientPlayerEntity entity) {
		super.onEntityConstructed(entity);
		ClientEngine.instance.inputController.setPlayerData(this);
	}
	
	@Override
	public void onEntityJoinWorld(ClientPlayerEntity entity) {
		super.onEntityJoinWorld(entity);
		ModNetworkManager.sendToServer(new CTSToggleMode(ClientEngine.instance.isBattleMode()));
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.updateMotion(considerInaction);
		if (!this.getClientAnimator().prevAiming()) {
			if (this.currentOverwritingMotion == LivingMotion.AIM) {
				this.orgEntity.getItemInUseCount();
				ClientEngine.instance.renderEngine.zoomIn();
			}
		}
	}
	
	@Override
	public void updateOnClient() {
		this.prevStamina = this.getStamina();
		super.updateOnClient();
		RayTraceResult rayResult = Minecraft.getInstance().objectMouseOver;
		if (rayResult.getType() == RayTraceResult.Type.ENTITY) {
			Entity hit = ((EntityRayTraceResult) rayResult).getEntity();
			if (hit instanceof LivingEntity) {
				this.rayTarget = (LivingEntity)hit;
				ModNetworkManager.sendToServer(new CTSSetPlayerTarget(this.getAttackTarget().getEntityId()));
			}
		}
		if (this.rayTarget != null) {
			if (!this.rayTarget.isAlive() || this.getOriginalEntity().getDistanceSq(this.rayTarget) > 64.0D
					|| MathUtils.getAngleBetween(this.getOriginalEntity(), this.rayTarget) > 1.5707963267948966D) {
				this.rayTarget = null;
				ModNetworkManager.sendToServer(new CTSSetPlayerTarget(-1));
			}
		}
	}
	
	@Override
	protected void playReboundAnimation() {
		this.getClientAnimator().playReboundAnimation();
		ClientEngine.instance.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronize(int namespaceId, int id, float modifyTime) {
		ModNetworkManager.sendToServer(new CTSPlayAnimation(namespaceId, id, modifyTime, false, true));
	}
	
	@Override
	public void updateHeldItem(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		super.updateHeldItem(mainHandCap, offHandCap);
		
		if (mainHandCap != null) {
			mainHandCap.onHeld(this);
		}
		
		if (EpicFightMod.CLIENT_INGAME_CONFIG.battleAutoSwitchItems.contains(this.orgEntity.getHeldItemMainhand().getItem())) {
			ClientEngine.instance.switchToBattleMode();
		} else if (EpicFightMod.CLIENT_INGAME_CONFIG.miningAutoSwitchItems.contains(this.orgEntity.getHeldItemMainhand().getItem())) {
			ClientEngine.instance.switchToMiningMode();
		}
	}
	
	@Override
	public boolean hurtBy(LivingAttackEvent event) {
		boolean hurt = super.hurtBy(event);
		if (EpicFightMod.CLIENT_INGAME_CONFIG.autoPreparation.getValue() && hurt && !ClientEngine.instance.isBattleMode()) {
			ClientEngine.instance.toggleActingMode();
		}
		return hurt;
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		return this.rayTarget;
	}
	
	@Override
	public boolean shouldSkipRender() {
		return !ClientEngine.instance.isBattleMode() && EpicFightMod.CLIENT_INGAME_CONFIG.filterAnimation.getValue();
	}
	
	@Override
	public boolean isFirstPerson() {
		return Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON;
	}
	
	@Override
	public boolean shouldBlockMoving() {
		return ClientEngine.instance.inputController.isKeyDown(Minecraft.getInstance().gameSettings.keyBindBack);
	}
	
	public float getPrevStamina() {
		return this.prevStamina;
	}
	
	@Override
	public void openSkillBook(ItemStack itemstack) {
		if (itemstack.hasTag() && itemstack.getTag().contains("skill")) {
			Minecraft.getInstance().displayGuiScreen(new SkillDescriptionGui(this.orgEntity, itemstack));
		}
	}
}