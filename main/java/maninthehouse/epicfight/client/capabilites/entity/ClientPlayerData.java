package maninthehouse.epicfight.client.capabilites.entity;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSPlayAnimation;
import maninthehouse.epicfight.skill.SkillContainer;
import maninthehouse.epicfight.skill.SkillSlot;
import maninthehouse.epicfight.utils.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientPlayerData extends RemoteClientPlayerData<EntityPlayerSP> {
	private EntityLivingBase rayTarget;
	
	@Override
	public void onEntityConstructed(EntityPlayerSP entity) {
		super.onEntityConstructed(entity);
		ClientEngine.INSTANCE.setPlayerData(this);
		ClientEngine.INSTANCE.inputController.setGamePlayer(this);
	}
	
	@Override
	public void updateMotion() {
		super.updateMotion();

		if (!this.getClientAnimator().prevAiming()) {
			if (this.currentMixMotion == LivingMotion.AIMING) {
				this.orgEntity.getItemInUseCount();
				ClientEngine.INSTANCE.renderEngine.zoomIn();
			}
		}
	}
	
	@Override
	public void updateOnClient() {
		super.updateOnClient();
		RayTraceResult rayResult = Minecraft.getMinecraft().objectMouseOver;
		
		if (rayResult != null && rayResult.entityHit != null) {
			Entity hit = rayResult.entityHit;
			if (hit instanceof EntityLivingBase)
				this.rayTarget = (EntityLivingBase)hit;
		}
		
		if (this.rayTarget != null) {
			if(!this.rayTarget.isEntityAlive()) {
				this.rayTarget = null;
			} else if(this.getOriginalEntity().getDistanceSq(this.rayTarget) > 64.0D) {
				this.rayTarget = null;
			} else if(MathUtils.getAngleBetween(this.getOriginalEntity(), this.rayTarget) > 1.5707963267948966D) {
				this.rayTarget = null;
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
			this.getSkill(SkillSlot.WEAPON_GIMMICK).setSkill(null);
		}
	}
	
	@Override
	public void aboutToDeath() {
		;
	}
	
	public void initFromOldOne(ClientPlayerData old) {
		this.skills = old.skills;

		for (SkillContainer skill : this.skills) {
			skill.setExecuter(this);
		}
		
		this.setStunArmor(old.getStunArmor());
	}
	
	@Override
	public EntityLivingBase getAttackTarget() {
		return this.rayTarget;
	}
	
	@Override
	public boolean isFirstPerson() {
		return Minecraft.getMinecraft().gameSettings.thirdPersonView == 0;
	}
}