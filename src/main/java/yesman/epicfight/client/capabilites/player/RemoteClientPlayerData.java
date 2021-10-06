package yesman.epicfight.client.capabilites.player;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfig;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Model;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSReqPlayerInfo;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class RemoteClientPlayerData<T extends AbstractClientPlayerEntity> extends PlayerData<T> {
	protected float prevYaw;
	protected float bodyYaw;
	protected float prevBodyYaw;
	private Item prevHeldItem;
	private Item prevHeldItemOffHand;
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.prevHeldItem = Items.AIR;
		this.prevHeldItemOffHand = Items.AIR;
	}
	
	@Override
	public void postInit() {
		if (!(this instanceof ClientPlayerData)) {
			ModNetworkManager.sendToServer(new CTSReqPlayerInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.isInaction() && considerInaction) {
			currentMotion = LivingMotion.INACTION;
		} else {
			AnimatorClient animator = getClientAnimator();
			if (this.orgEntity.getHealth() <= 0.0F) {
				currentMotion = LivingMotion.DEATH;
			} else if (orgEntity.isElytraFlying() || orgEntity.isSpinAttacking()) {
				currentMotion = LivingMotion.FLY;
			} else if (orgEntity.getRidingEntity() != null) {
				currentMotion = LivingMotion.MOUNT;
			} else if (orgEntity.isActualySwimming()) {
				currentMotion = LivingMotion.SWIM;
			} else if (orgEntity.isSleeping()) {
				currentMotion = LivingMotion.SLEEP;
			} else if (!orgEntity.isOnGround() && orgEntity.isOnLadder()) {
				currentMotion = LivingMotion.CLIMB;
				double y = orgEntity.chasingPosY - orgEntity.prevChasingPosY;
				if (Math.abs(y) < 0.04D) {
					animator.getLivingLayer().pause();
				} else {
					animator.getLivingLayer().resume();
				}
			} else {
				if (orgEntity.canSwim() && (orgEntity.chasingPosY - orgEntity.prevChasingPosY) < -0.005)
					currentMotion = LivingMotion.FLOAT;
				else if(orgEntity.chasingPosY - orgEntity.prevChasingPosY < -0.25F)
					currentMotion = LivingMotion.FALL;
				else if (orgEntity.limbSwingAmount > 0.01F) {
					if(orgEntity.isSneaking())
						currentMotion = LivingMotion.SNEAK;
					else if (orgEntity.isSprinting())
						currentMotion = LivingMotion.RUN;
					else
						currentMotion = LivingMotion.WALK;
					
					if (orgEntity.moveForward > 0)
						animator.getLivingLayer().animationPlayer.setReversed(false);
					else if (orgEntity.moveForward < 0) {
						animator.getLivingLayer().animationPlayer.setReversed(true);
					}
				} else {
					animator.getLivingLayer().animationPlayer.setReversed(false);
					if (orgEntity.isSneaking())
						currentMotion = LivingMotion.KNEEL;
					else
						currentMotion = LivingMotion.IDLE;
				}
			}
		}
		
		if (this.orgEntity.isHandActive() && this.orgEntity.getItemInUseCount() > 0) {
			CapabilityItem activeItem = this.getHeldItemCapability(this.orgEntity.getActiveHand());
			UseAction useAction = this.orgEntity.getHeldItem(this.orgEntity.getActiveHand()).getUseAction();
			UseAction secondUseAction = activeItem.getUseAction(this);
			
			if (useAction == UseAction.BLOCK || secondUseAction == UseAction.BLOCK) {
				if (activeItem.getWeaponCategory() == WeaponCategory.SHIELD) {
					currentOverwritingMotion = LivingMotion.BLOCK_SHIELD;
				} else {
					currentOverwritingMotion = LivingMotion.BLOCK;
				}
			} else if (useAction == UseAction.BOW || useAction == UseAction.SPEAR)
				currentOverwritingMotion = LivingMotion.AIM;
			else if (useAction == UseAction.CROSSBOW)
				currentOverwritingMotion = LivingMotion.RELOAD;
			else
				currentOverwritingMotion = currentMotion;
		} else {
			if (CrossbowItem.isCharged(this.orgEntity.getHeldItemMainhand()))
				currentOverwritingMotion = LivingMotion.AIM;
			else if (this.getClientAnimator().getLayer(Layer.Priority.MIDDLE).animationPlayer.getPlay().isReboundAnimation())
				currentOverwritingMotion = LivingMotion.NONE;
			else if (this.orgEntity.isSwingInProgress)
				currentOverwritingMotion = LivingMotion.DIGGING;
			else
				currentOverwritingMotion = currentMotion;
			
			if (this.getClientAnimator().prevAiming() && currentOverwritingMotion != LivingMotion.AIM) {
				this.playReboundAnimation();
			}
		}
	}

	@Override
	protected void updateOnClient() {
		this.prevYaw = this.yaw;
		this.prevBodyYaw = this.bodyYaw;
		
		if (this.getEntityState().isInaction()) {
			this.orgEntity.renderYawOffset = this.orgEntity.rotationYaw;
		}
		
		this.bodyYaw = this.orgEntity.renderYawOffset;
		boolean isMainHandChanged = this.prevHeldItem != this.orgEntity.inventory.getCurrentItem().getItem();
		boolean isOffHandChanged = this.prevHeldItemOffHand != this.orgEntity.inventory.offHandInventory.get(0).getItem();

		if (isMainHandChanged || isOffHandChanged) {
			this.updateHeldItem(this.getHeldItemCapability(Hand.MAIN_HAND), this.getHeldItemCapability(Hand.OFF_HAND));
			if (isMainHandChanged) {
				this.prevHeldItem = this.orgEntity.inventory.getCurrentItem().getItem();
			}
			if (isOffHandChanged) {
				this.prevHeldItemOffHand = this.orgEntity.inventory.offHandInventory.get(0).getItem();
			}
		}
		
		super.updateOnClient();
		
		if (this.orgEntity.deathTime == 1) {
			this.getClientAnimator().playDeathAnimation();
		}
	}
	
	public void updateHeldItem(CapabilityItem mainHandCap, CapabilityItem offHandCap) {
		this.cancelUsingItem();
	}

	@Override
	public void playAnimationSynchronize(int namespaceId, int id, float modifyTime) {
		;
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return this.orgEntity.getSkinType().equals("slim") ? modelDB.bipedAlex : modelDB.biped;
	}
	
	@Override
	public boolean shouldSkipRender() {
		return !this.isBattleMode && EpicFightMod.CLIENT_INGAME_CONFIG.filterAnimation.getValue();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public OpenMatrix4f getHeadMatrix(float partialTick) {
        float yaw = 0;
        float pitch = 0;
        float prvePitch = 0;
        
		if (this.getEntityState().isInaction() || this.orgEntity.getRidingEntity() != null || (!orgEntity.isOnGround() && this.orgEntity.isOnLadder())) {
	        yaw = 0;
		} else {
			float f = MathUtils.interpolateRotation(this.prevBodyYaw, this.bodyYaw, partialTick);
			float f1 = MathUtils.interpolateRotation(this.orgEntity.prevRotationYawHead, this.orgEntity.rotationYawHead, partialTick);
	        yaw = f1 - f;
		}
        
		if (!(this.orgEntity.isElytraFlying() || this.orgEntity.isActualySwimming())) {
			prvePitch = this.orgEntity.prevRotationPitch;
			pitch = this.orgEntity.rotationPitch;
		}
        
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, prvePitch, pitch, yaw, yaw, partialTick, 1, 1, 1);
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTick) {
		Direction direction;
		if (this.orgEntity.isSpinAttacking()) {
			OpenMatrix4f mat = MathUtils.getModelMatrixIntegrated(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0, 0, 0, 0, partialTick, 1, 1, 1);
			float yawDegree = MathUtils.interpolateRotation(this.orgEntity.prevRotationYaw, this.orgEntity.rotationYaw, partialTick);
			float pitchDegree = MathUtils.interpolateRotation(this.orgEntity.prevRotationPitch, this.orgEntity.rotationPitch, partialTick);
			OpenMatrix4f.rotate((float)-Math.toRadians(yawDegree), new Vec3f(0F, 1F, 0F), mat, mat);
			OpenMatrix4f.rotate((float)-Math.toRadians(pitchDegree), new Vec3f(1F, 0F, 0F), mat, mat);
			OpenMatrix4f.rotate((float)Math.toRadians((this.orgEntity.ticksExisted + partialTick) * -55.0F), new Vec3f(0F, 0F, 1F), mat, mat);
			OpenMatrix4f.translate(new Vec3f(0F, -0.39F, 0F), mat, mat);
            return mat;
		} else if (this.orgEntity.isElytraFlying()) {
			OpenMatrix4f mat = MathUtils.getModelMatrixIntegrated(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0, 0, 0, 0, partialTick, 1, 1, 1);
			OpenMatrix4f.rotate((float)-Math.toRadians(this.orgEntity.renderYawOffset), new Vec3f(0F, 1F, 0F), mat, mat);
            float f = (float)orgEntity.getTicksElytraFlying() + Minecraft.getInstance().getRenderPartialTicks();
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            OpenMatrix4f.rotate((float)Math.toRadians(f1 * (- this.orgEntity.rotationPitch)), new Vec3f(1F, 0F, 0F), mat, mat);
            
            Vector3d vec3d = this.orgEntity.getLook(Minecraft.getInstance().getRenderPartialTicks());
            Vector3d vec3d1 = this.orgEntity.getMotion();
            
            double d0 = vec3d1.x * vec3d1.x + vec3d1.z * vec3d1.z;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            
			if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
                
                OpenMatrix4f.rotate((float)Math.toRadians((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / (float)Math.PI), new Vec3f(0F, 1F, 0F), mat, mat);
            }
			
            return mat;
		} else if (this.orgEntity.isSleeping()) {
			BlockState blockstate = this.orgEntity.getBlockState();
			float yaw = 0.0F;
			
			if (blockstate.isBed(this.orgEntity.world, this.orgEntity.getPosition(), this.orgEntity)) {
				if (blockstate.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            		switch(blockstate.get(BlockStateProperties.HORIZONTAL_FACING)) {
            		case EAST:
        				yaw = 90.0F;
        				break;
        			case WEST:
        				yaw = -90.0F;
        				break;
        			case SOUTH:
        				yaw = 180.0F;
        				break;
        			default:
        				break;
            		}
            	}
			}
			
			return MathUtils.getModelMatrixIntegrated(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, yaw, yaw, 0, 1.0F, 1.0F, 1.0F);
		} else if ((direction = this.getLadderDirection(this.orgEntity.getBlockState(), this.orgEntity.world, this.orgEntity.getPosition(), this.orgEntity)) != Direction.UP) {
			float yaw = 0.0F;
			
			switch(direction) {
			case EAST:
				yaw = 90.0F;
				break;
			case WEST:
				yaw = -90.0F;
				break;
			case SOUTH:
				yaw = 180.0F;
				break;
			default:
				break;
			}
			
			return MathUtils.getModelMatrixIntegrated(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, yaw, yaw, 0.0F, 1.0F, 1.0F, 1.0F);
		} else {
			float yaw;
			float prevRotYaw;
			float rotyaw;
			float prevPitch = 0;
			float pitch = 0;
			
			if (this.orgEntity.getRidingEntity() instanceof LivingEntity) {
				LivingEntity ridingEntity = (LivingEntity)this.orgEntity.getRidingEntity();
				prevRotYaw = ridingEntity.prevRenderYawOffset;
				rotyaw = ridingEntity.renderYawOffset;
			} else {
				yaw = MathUtils.interpolateRotation(this.prevYaw, this.yaw, partialTick);
				prevRotYaw = this.prevBodyYaw + yaw;
				rotyaw = this.bodyYaw + yaw;
			}
			
			if (!this.getEntityState().isInaction() && this.orgEntity.getPose() == Pose.SWIMMING) {
				float f = this.orgEntity.getSwimAnimation(partialTick);
				float f3 = this.orgEntity.isInWater() ? this.orgEntity.rotationPitch : 0;
		        float f4 = MathHelper.lerp(f, 0.0F, f3);
		        prevPitch = f4;
		        pitch = f4;
			}
			
			return MathUtils.getModelMatrixIntegrated(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, prevPitch, pitch, prevRotYaw, rotyaw, partialTick, 1.0F, 1.0F, 1.0F);
		}
	}
	
	public Direction getLadderDirection(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
		boolean isSpectator = (entity instanceof PlayerEntity && ((PlayerEntity)entity).isSpectator());
        if (isSpectator || this.orgEntity.isOnGround() || !this.orgEntity.isAlive()) {
        	return Direction.UP;
        }
        
		if (ForgeConfig.SERVER.fullBoundingBoxLadders.get()) {
            if (state.isLadder(world, pos, entity)) {
            	if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            		return state.get(BlockStateProperties.HORIZONTAL_FACING);
            	}
            	
            	if (state.hasProperty(BlockStateProperties.UP) && state.get(BlockStateProperties.UP)) {
            		return Direction.UP;
            	} else if (state.hasProperty(BlockStateProperties.NORTH) && state.get(BlockStateProperties.NORTH)) {
            		return Direction.SOUTH;
            	} else if (state.hasProperty(BlockStateProperties.WEST) && state.get(BlockStateProperties.WEST)) {
            		return Direction.EAST;
            	} else if (state.hasProperty(BlockStateProperties.SOUTH) && state.get(BlockStateProperties.SOUTH)) {
            		return Direction.NORTH;
            	} else if (state.hasProperty(BlockStateProperties.EAST) && state.get(BlockStateProperties.EAST)) {
            		return Direction.WEST;
            	}
            }
		} else {
            AxisAlignedBB bb = entity.getBoundingBox();
            int mX = MathHelper.floor(bb.minX);
            int mY = MathHelper.floor(bb.minY);
            int mZ = MathHelper.floor(bb.minZ);
            
			for (int y2 = mY; y2 < bb.maxY; y2++) {
				for (int x2 = mX; x2 < bb.maxX; x2++) {
					for (int z2 = mZ; z2 < bb.maxZ; z2++) {
                        BlockPos tmp = new BlockPos(x2, y2, z2);
                        state = world.getBlockState(tmp);
						if (state.isLadder(world, tmp, entity)) {
							if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
			            		return state.get(BlockStateProperties.HORIZONTAL_FACING);
			            	}
			            	if (state.hasProperty(BlockStateProperties.UP) && state.get(BlockStateProperties.UP)) {
			            		return Direction.UP;
			            	} else if (state.hasProperty(BlockStateProperties.NORTH) && state.get(BlockStateProperties.NORTH)) {
			            		return Direction.SOUTH;
			            	} else if (state.hasProperty(BlockStateProperties.WEST) && state.get(BlockStateProperties.WEST)) {
			            		return Direction.EAST;
			            	} else if (state.hasProperty(BlockStateProperties.SOUTH) && state.get(BlockStateProperties.SOUTH)) {
			            		return Direction.NORTH;
			            	} else if (state.hasProperty(BlockStateProperties.EAST) && state.get(BlockStateProperties.EAST)) {
			            		return Direction.WEST;
			            	}
                        }
                    }
                }
            }
        }
		return Direction.UP;
	}
}