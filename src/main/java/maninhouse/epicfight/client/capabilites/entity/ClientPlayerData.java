package maninhouse.epicfight.client.capabilites.entity;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.client.gui.SkillDescriptionGui;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSPlayAnimation;
import maninhouse.epicfight.network.client.CTSSetPlayerTarget;
import maninhouse.epicfight.network.client.CTSToggleMode;
import maninhouse.epicfight.skill.SkillCategory;
import maninhouse.epicfight.utils.math.MathUtils;
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

@OnlyIn(Dist.CLIENT)
public class ClientPlayerData extends RemoteClientPlayerData<ClientPlayerEntity> {
	private LivingEntity rayTarget;
	private float prevStamina;
	
	@Override
	public void onEntityConstructed(ClientPlayerEntity entity) {
		super.onEntityConstructed(entity);
		ClientEngine.INSTANCE.inputController.setPlayerData(this);
	}
	
	@Override
	public void onEntityJoinWorld(ClientPlayerEntity entity) {
		super.onEntityJoinWorld(entity);
		ModNetworkManager.sendToServer(new CTSToggleMode(ClientEngine.INSTANCE.isBattleMode()));
	}
	
	@Override
	public void updateMotion() {
		super.updateMotion();
		if (!this.getClientAnimator().prevAiming()) {
			if (this.currentOverridenMotion == LivingMotion.AIM) {
				this.orgEntity.getItemInUseCount();
				ClientEngine.INSTANCE.renderEngine.zoomIn();
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
		ClientEngine.INSTANCE.renderEngine.zoomOut(40);
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime) {
		ModNetworkManager.sendToServer(new CTSPlayAnimation(id, modifyTime, false, true));
	}
	
	@Override
	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		super.onHeldItemChange(mainHandCap, offHandCap);
		
		if (mainHandCap != null) {
			mainHandCap.onHeld(this);
		} else {
			this.getSkill(SkillCategory.WEAPON_PASSIVE).setSkill(null);
		}
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		return this.rayTarget;
	}
	
	@Override
	public boolean shouldSkipRender() {
		return !ClientEngine.INSTANCE.isBattleMode() && EpicFightMod.CLIENT_INGAME_CONFIG.filterAnimation.getValue();
	}
	
	@Override
	public boolean isFirstPerson() {
		return Minecraft.getInstance().gameSettings.getPointOfView() == PointOfView.FIRST_PERSON;
	}
	
	@Override
	public boolean shouldBlockMoving() {
		return ClientEngine.INSTANCE.inputController.isKeyDown(Minecraft.getInstance().gameSettings.keyBindBack);
	}
	
	public float getPrevStamina() {
		return this.prevStamina;
	}
	
	@Override
	public void openSkillBook(ItemStack itemstack) {
		Minecraft.getInstance().displayGuiScreen(new SkillDescriptionGui(this.orgEntity, itemstack));
	}
}