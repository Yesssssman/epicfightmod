package maninthehouse.epicfight.client.capabilites.entity;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSReqPlayerInfo;
import maninthehouse.epicfight.utils.math.MathUtils;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RemoteClientPlayerData<T extends AbstractClientPlayer> extends PlayerData<T> {
	protected float prevYaw;
	protected float bodyYaw;
	protected float prevBodyYaw;
	private ItemStack prevHeldItem;
	private ItemStack prevHeldItemOffHand;
	private boolean swingArm;
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.prevHeldItem = ItemStack.EMPTY;
		this.prevHeldItemOffHand = ItemStack.EMPTY;
		if(!(this instanceof ClientPlayerData)) {
			ModNetworkManager.sendToServer(new CTSReqPlayerInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	public void updateMotion() {
		if (orgEntity.isElytraFlying()) {
			currentMotion = LivingMotion.FLYING;
		} else if (orgEntity.getRidingEntity() != null) {
			currentMotion = LivingMotion.MOUNT;
		} else {
			AnimatorClient animator = getClientAnimator();

			if (orgEntity.isInWater() && orgEntity.motionY < -0.005)
				currentMotion = LivingMotion.FLOATING;
			else if (orgEntity.motionY < -0.55F)
				currentMotion = LivingMotion.FALL;
			else if (orgEntity.limbSwingAmount > 0.01F) {
				if (orgEntity.isSneaking())
					currentMotion = LivingMotion.SNEAKING;
				else if (orgEntity.isSprinting())
					currentMotion = LivingMotion.RUNNING;
				else
					currentMotion = LivingMotion.WALKING;

				if (orgEntity.moveForward > 0)
					animator.reversePlay = false;
				else if (orgEntity.moveForward < 0)
					animator.reversePlay = true;
			} else {
				animator.reversePlay = false;

				if (orgEntity.isSneaking())
					currentMotion = LivingMotion.KNEELING;
				else
					currentMotion = LivingMotion.IDLE;
			}
		}
		
		if (this.orgEntity.isHandActive() && orgEntity.getItemInUseCount() > 0) {
			EnumAction useAction = this.orgEntity.getHeldItem(this.orgEntity.getActiveHand()).getItemUseAction();
			
			if(useAction == EnumAction.BLOCK)
				currentMixMotion = LivingMotion.BLOCKING;
			else if(useAction == EnumAction.BOW)
				currentMixMotion = LivingMotion.AIMING;
			else
				currentMixMotion = LivingMotion.NONE;
		} else {
			if(this.getClientAnimator().prevAiming())
				this.playReboundAnimation();
			else
				currentMixMotion = LivingMotion.NONE;
		}
	}
	
	@Override
	protected void updateOnClient() {
		this.prevYaw = this.yaw;
		this.prevBodyYaw = this.bodyYaw;
		this.bodyYaw = this.inaction ? orgEntity.rotationYaw : orgEntity.prevRenderYawOffset;
		
		boolean isMainHandChanged = prevHeldItem.getItem() != this.orgEntity.inventory.getCurrentItem().getItem();
		boolean isOffHandChanged = prevHeldItemOffHand.getItem() != this.orgEntity.inventory.offHandInventory.get(0).getItem();
		
		if (isMainHandChanged || isOffHandChanged) {
			onHeldItemChange(this.getHeldItemCapability(EnumHand.MAIN_HAND), this.getHeldItemCapability(EnumHand.OFF_HAND));
			if(isMainHandChanged)
				prevHeldItem = this.orgEntity.inventory.getCurrentItem();
			if(isOffHandChanged)
				prevHeldItemOffHand = this.orgEntity.inventory.offHandInventory.get(0);
		}
		
		super.updateOnClient();
		
		if(this.orgEntity.deathTime == 1)
			this.getClientAnimator().playDeathAnimation();
		
		if (this.swingArm != orgEntity.isSwingInProgress) {
			if(!this.swingArm)
				this.getClientAnimator().playMixLayerAnimation(Animations.BIPED_DIG);
			else
				this.getClientAnimator().offMixLayer(false);
			
			this.swingArm = orgEntity.isSwingInProgress;
		}
	}
	
	public void onHeldItemChange(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		this.getClientAnimator().resetMixMotion();
		this.getClientAnimator().offMixLayer(false);
		this.cancelUsingItem();
	}
	
	protected void playReboundAnimation() {
		this.getClientAnimator().playReboundAnimation();
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime) {
		//
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return this.orgEntity.getSkinType().equals("slim") ? modelDB.ENTITY_BIPED_SLIM_ARM : modelDB.ENTITY_BIPED;
	}
	
	@Override
	public VisibleMatrix4f getHeadMatrix(float partialTick) {
		T entity = getOriginalEntity();
        float yaw;
        float pitch = 0;
        float prvePitch = 0;
        
		if (inaction || entity.getRidingEntity() != null) {
			yaw = 0;
		} else {
			float f = MathUtils.interpolateRotation(this.prevBodyYaw, this.bodyYaw, partialTick);
			float f1 = MathUtils.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTick);
			yaw = f1 - f;
		}
        
		if (!orgEntity.isElytraFlying()) {
        	prvePitch = entity.prevRotationPitch;
	        pitch = entity.rotationPitch;
        }
        
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, prvePitch, pitch, yaw, yaw, partialTick, 1, 1, 1);
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTick) {
		if (orgEntity.isElytraFlying()) {
			VisibleMatrix4f mat = MathUtils.getModelMatrixIntegrated((float)orgEntity.lastTickPosX, (float)orgEntity.posX, (float)orgEntity.lastTickPosY, (float)orgEntity.posY,
					(float)orgEntity.lastTickPosZ, (float)orgEntity.posZ, 0, 0, 0, 0, partialTick, 1, 1, 1);
			
			VisibleMatrix4f.rotate((float)-Math.toRadians(orgEntity.renderYawOffset), new Vec3f(0F, 1F, 0F), mat, mat);
			
            float f = (float)orgEntity.getTicksElytraFlying() + Minecraft.getMinecraft().getRenderPartialTicks();
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            VisibleMatrix4f.rotate((float)Math.toRadians(f1 * (-90F - orgEntity.rotationPitch)), new Vec3f(1F, 0F, 0F), mat, mat);
            
            Vec3d vec3d = orgEntity.getLook(Minecraft.getMinecraft().getRenderPartialTicks());
            Vec3d vec3d1 = new Vec3d(orgEntity.motionX, orgEntity.motionY, orgEntity.motionZ);
            
            double d0 = vec3d1.x * vec3d1.x + vec3d1.z * vec3d1.z;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;

			if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                VisibleMatrix4f.rotate((float)Math.toRadians((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI), new Vec3f(0F, 1F, 0F), mat, mat);
            }
			
            return mat;
		} else {
			float yaw;
			float prevRotYaw;
			float rotyaw;
			float prevPitch = 0;
			float pitch = 0;
			
			if (orgEntity.getRidingEntity() instanceof EntityLivingBase) {
				EntityLivingBase ridingEntity = (EntityLivingBase) orgEntity.getRidingEntity();
				prevRotYaw = ridingEntity.prevRenderYawOffset;
				rotyaw = ridingEntity.renderYawOffset;
			} else {
				yaw = inaction ? MathUtils.interpolateRotation(this.prevYaw, this.yaw, partialTick) : 0;
				prevRotYaw = this.prevBodyYaw + yaw;
				rotyaw = this.bodyYaw + yaw;
			}
			
			return MathUtils.getModelMatrixIntegrated((float)orgEntity.lastTickPosX, (float)orgEntity.posX, (float)orgEntity.lastTickPosY, (float)orgEntity.posY,
					(float)orgEntity.lastTickPosZ, (float)orgEntity.posZ, prevPitch, pitch, prevRotYaw, rotyaw, partialTick, 1, 1, 1);
		}
	}
}