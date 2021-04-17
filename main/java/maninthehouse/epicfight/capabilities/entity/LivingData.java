package maninthehouse.epicfight.capabilities.entity;

import java.util.ArrayList;
import java.util.List;

import maninthehouse.epicfight.animation.Animator;
import maninthehouse.epicfight.animation.AnimatorServer;
import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Colliders;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimation;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.DamageType;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninthehouse.epicfight.utils.math.MathUtils;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class LivingData<T extends EntityLivingBase> extends CapabilityEntity<T> {
	private float stunTimeReduction;
	protected boolean inaction = false;
	public LivingMotion currentMotion = LivingMotion.IDLE;
	public LivingMotion currentMixMotion = LivingMotion.NONE;
	protected Animator animator;
	public List<Entity> currentlyAttackedEntity;
	private float widthResetSize;
	private float heightResetSize;
	private boolean shouldReset;
	
	@Override
	public void onEntityConstructed(T entityIn) {
		super.onEntityConstructed(entityIn);
		if(this.orgEntity.world.isRemote) {
			this.animator = new AnimatorClient(this);
			this.initAnimator(this.getClientAnimator());
		} else {
			this.animator = new AnimatorServer(this);
		}
		this.registerAttributes();
		this.inaction = false;
		this.currentlyAttackedEntity = new ArrayList<Entity>();
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		this.initAttributes();
	}
	
	protected abstract void initAnimator(AnimatorClient animatorClient);
	public abstract void updateMotion();
	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);
	
	protected void registerAttributes() {
		this.registerIfAbsent(ModAttributes.WEIGHT);
		this.registerIfAbsent(ModAttributes.MAX_STRIKES);
		this.registerIfAbsent(ModAttributes.ARMOR_NEGATION);
		this.registerIfAbsent(ModAttributes.IMPACT);
	}
	
	protected void registerIfAbsent(IAttribute attribute) {
		AbstractAttributeMap attributeMap = this.orgEntity.getAttributeMap();
		if (attributeMap.getAttributeInstance(attribute) == null) {
			attributeMap.registerAttribute(attribute);
		}
	}
	
	protected void initAttributes() {
		this.orgEntity.getEntityAttribute(ModAttributes.WEIGHT).setBaseValue(this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.orgEntity.getEntityAttribute(ModAttributes.MAX_STRIKES).setBaseValue(1.0D);
		this.orgEntity.getEntityAttribute(ModAttributes.ARMOR_NEGATION).setBaseValue(0.0D);
		this.orgEntity.getEntityAttribute(ModAttributes.IMPACT).setBaseValue(0.5D);
	}
	
	@Override
	protected void updateOnClient() {
		AnimatorClient animator = getClientAnimator();
		
		if(this.inaction) {
			this.currentMotion = LivingMotion.IDLE;
		} else {
			this.updateMotion();
			if(!animator.compareMotion(currentMotion)) {
				animator.playLoopMotion();
			}
			if(!animator.compareMixMotion(currentMixMotion)) {
				animator.playMixLoopMotion();
			}
		}
	}
	
	@Override
	protected void updateOnServer() {
		if(stunTimeReduction > 0.0F) {
			stunTimeReduction = Math.max(0.0F, stunTimeReduction - 0.05F);
		}
	}
	
	@Override
	public void update() {
		updateInactionState();
		
		if (isRemote()) {
			updateOnClient();
		} else {
			updateOnServer();
		}

		this.animator.update();
		if (this.orgEntity.deathTime == 19) {
			aboutToDeath();
		}
	}

	public void updateInactionState() {
		EntityState state = this.getEntityState();
		if (!state.isCameraRotationLocked() && !state.isMovementLocked()) {
			this.inaction = false;
		} else {
			this.inaction = true;
		}
	}

	protected final void commonBipedCreatureAnimatorInit(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	protected final void commonCreatureUpdateMotion() {
		if(this.orgEntity.getHealth() <= 0.0F) {
			currentMotion = LivingMotion.DEATH;
		} else if(orgEntity.getRidingEntity() != null) {
			currentMotion = LivingMotion.MOUNT;
		} else {
			if(orgEntity.motionY < -0.55F)
				currentMotion = LivingMotion.FALL;
			else if(orgEntity.limbSwingAmount > 0.01F)
				currentMotion = LivingMotion.WALKING;
			else
				currentMotion = LivingMotion.IDLE;
		}
	}
	
	public void cancelUsingItem() {
		this.orgEntity.resetActiveHand();
		net.minecraftforge.event.ForgeEventFactory.onUseItemStop(this.orgEntity, this.orgEntity.getActiveItemStack(), this.orgEntity.getItemInUseCount());
	}
	
	public CapabilityItem getHeldItemCapability(EnumHand hand) {
		return ModCapabilities.stackCapabilityGetter(this.orgEntity.getHeldItem(hand));
	}

	public boolean isInaction() {
		return this.inaction;
	}
	
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if(this.getEntityState().isInvincible()) {
			if(damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagicDamage()) {
				return false;
			}
		}
		
		return true;
	}
	
	public IExtendedDamageSource getDamageSource(StunType stunType, DamageType damageType, int animationId) {
		return IExtendedDamageSource.causeMobDamage(orgEntity, stunType, damageType, animationId);
	}
	
	public float getDamageToEntity(Entity targetEntity, EnumHand hand) {
		float damage = 0;
		if (hand == EnumHand.MAIN_HAND) {
			damage = (float) this.getAttributeValue(SharedMonsterAttributes.ATTACK_DAMAGE);
		} else {
			damage = (float) this.getAttributeValue(ModAttributes.OFFHAND_ATTACK_DAMAGE);
		}

		float bonus;
		if (targetEntity instanceof EntityLivingBase) {
			bonus = EnchantmentHelper.getModifierForCreature(orgEntity.getHeldItem(hand), ((EntityLivingBase) targetEntity).getCreatureAttribute());
		} else {
			bonus = EnchantmentHelper.getModifierForCreature(orgEntity.getHeldItem(hand), EnumCreatureAttribute.UNDEFINED);
		}

		return damage + bonus;
	}
	
	public boolean hurtEntity(Entity hitTarget, EnumHand handIn, IExtendedDamageSource source, float amount) {
		boolean succed = hitTarget.attackEntityFrom((DamageSource) source, amount);
		
		if (succed) {
			int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, this.orgEntity.getHeldItem(handIn));
			if (hitTarget instanceof EntityLivingBase) {
				if (j > 0 && !hitTarget.isBurning())
					hitTarget.setFire(j * 4);
			}
		}
		
		return succed;
	}
	
	public void lookAttacker(Entity attacker) {
		Vec3d vector3d = this.orgEntity.getPositionVector();
		Vec3d target = attacker.getPositionVector();
		double d0 = target.x - vector3d.x;
	    double d1 = target.y - vector3d.y;
	    double d2 = target.z - vector3d.z;
	    double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
	    this.orgEntity.rotationPitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
	    this.orgEntity.rotationYaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
	    this.orgEntity.setRotationYawHead(this.orgEntity.rotationYaw);
	    this.orgEntity.prevRotationPitch = this.orgEntity.rotationPitch;
	    this.orgEntity.prevRotationYaw = this.orgEntity.rotationYaw;
	    this.orgEntity.prevRotationYawHead = this.orgEntity.rotationYawHead;
	    this.orgEntity.renderYawOffset = this.orgEntity.rotationYawHead;
	    this.orgEntity.prevRenderYawOffset = this.orgEntity.renderYawOffset;
	}
	
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {}
	
	public void setStunTimeReduction() {
		this.stunTimeReduction += (1.0F - stunTimeReduction) * 0.8F;
	}

	public float getStunTimeTimeReduction() {
		return this.stunTimeReduction;
	}

	public void knockBackEntity(Entity entityIn, float power) {
		double d1 = entityIn.posX - this.orgEntity.posX;
        double d0;
        
		for (d0 = entityIn.posZ - this.orgEntity.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }
        
		if (orgEntity.getRNG().nextDouble() >= this.getAttributeValue(SharedMonsterAttributes.KNOCKBACK_RESISTANCE)) {
        	this.orgEntity.isAirBorne = true;
            float f = MathHelper.sqrt(d1 * d1 + d0 * d0);
            double x = this.orgEntity.motionX;
            double y = this.orgEntity.motionY;
            double z = this.orgEntity.motionZ;
            
            x /= 2.0D;
            z /= 2.0D;
            x -= d1 / (double)f * (double)power;
            z -= d0 / (double)f * (double)power;

			if (!this.orgEntity.onGround) {
				y /= 2.0D;
				y += (double) power;

				if (y > 0.4000000059604645D) {
					y = 0.4000000059604645D;
				}
			}
            
			this.orgEntity.motionX = x;
			this.orgEntity.motionY = y;
			this.orgEntity.motionZ = z;
        }
	}
	
	public float getMaxStunArmor() {
		IAttributeInstance stun_resistance = this.orgEntity.getEntityAttribute(ModAttributes.MAX_STUN_ARMOR);
		return (float) (stun_resistance == null ? 0 : stun_resistance.getAttributeValue());
	}
	
	public float getStunArmor() {
		return getMaxStunArmor() == 0 ? 0 : this.orgEntity.getDataManager().get(DataKeys.STUN_ARMOR).floatValue();
	}
	
	public void setStunArmor(float value) {
		float f1 = Math.max(Math.min(value, this.getMaxStunArmor()), 0);
		this.orgEntity.getDataManager().set(DataKeys.STUN_ARMOR, f1);
	}
	
	public double getWeight() {
		return this.getAttributeValue(ModAttributes.WEIGHT);
	}
	
	public IAttributeInstance getAttribute(IAttribute attribute) {
		return this.orgEntity.getAttributeMap().getAttributeInstance(attribute);
	}
	
	public double getAttributeValue(IAttribute attribute) {
		return this.orgEntity.getAttributeMap().getAttributeInstance(attribute).getAttributeValue();
	}
	
	public float getAttackDirectionPitch() {
		float partialTicks = EpicFightMod.isPhysicalClient() ? Minecraft.getMinecraft().getRenderPartialTicks() : 1.0F;
		float pitch = -MathUtils.interpolateRotation(this.orgEntity.prevRotationPitch, this.orgEntity.rotationPitch, partialTicks);
		float correct = (pitch > 0) ? 0.03333F * (float)Math.pow(pitch, 2) : -0.03333F * (float)Math.pow(pitch, 2);
		
		return MathHelper.clamp(correct, -30.0F, 30.0F);
	}
	
	public void rotateTo(float degree, float limit, boolean partialSync) {
		EntityLivingBase entity = this.getOriginalEntity();
		float amount = MathHelper.wrapDegrees(degree - entity.rotationYaw);
		
        while(amount < -180.0F) {
        	amount += 360.0F;
        }
        
        while(amount > 180.0F) {
        	amount -= 360.0F;
        }
        
        if (amount > limit) {
			amount = limit;
        }
        if (amount < -limit) {
        	amount = -limit;
        }
        
        float f1 = entity.rotationYaw + amount;
        
		if(partialSync) {
			entity.prevRotationYaw = f1;
			entity.prevRotationYawHead = f1;
			entity.prevRenderYawOffset = f1;
		}
		
		entity.rotationYaw = f1;
		entity.rotationYawHead = f1;
		entity.renderYawOffset = f1;
	}
	
	public void rotateTo(Entity target, float limit, boolean partialSync) {
		double d0 = target.posX - this.orgEntity.posX;
        double d1 = target.posZ - this.orgEntity.posZ;
        float degree = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
    	rotateTo(degree, limit, partialSync);
	}
	
	public void playSound(SoundEvent sound, float minPitch, float maxPitch) {
		float randPitch = this.orgEntity.getRNG().nextFloat() * 2.0F - 1.0F;
		randPitch = Math.min(Math.max(randPitch, minPitch), maxPitch);
		if(!this.isRemote()) {
			this.orgEntity.world.playSound(null, orgEntity.posX, orgEntity.posY, orgEntity.posZ, sound, orgEntity.getSoundCategory(), 1.0F, 1.0F + randPitch);
		} else {
			this.orgEntity.world.playSound(orgEntity.posX, orgEntity.posY, orgEntity.posZ, sound, orgEntity.getSoundCategory(), 1.0F, 1.0F + randPitch, false);
		}
	}
	
	public EntityLivingBase getAttackTarget() {
		return this.orgEntity.getLastAttackedEntity();
	}
	
	public float getPitch(float partialTicks) {
		return (float)MathUtils.lerp((double)partialTicks, this.orgEntity.prevRotationPitch, this.orgEntity.rotationPitch);
	}
	
	public VisibleMatrix4f getHeadMatrix(float partialTicks) {
		float f;
        float f1;
        float f2;
		
		if (inaction) {
			f2 = 0;
		} else {
			f = MathUtils.interpolateRotation(orgEntity.prevRenderYawOffset, orgEntity.renderYawOffset, partialTicks);
			f1 = MathUtils.interpolateRotation(orgEntity.prevRotationYawHead, orgEntity.rotationYawHead, partialTicks);
			f2 = f1 - f;

			if (orgEntity.getRidingEntity() != null) {
				if (f2 > 45.0F) {
					f2 = 45.0F;
				} else if (f2 < -45.0F) {
					f2 = -45.0F;
				}
			}
		}
        
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.prevRotationPitch, orgEntity.rotationPitch, f2, f2, partialTicks, 1, 1, 1);
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		float prevRotYaw;
		float rotyaw;
		float scaleX = 1.0F;
		float scaleY = 1.0F;
		float scaleZ = 1.0F;
		
		if (orgEntity.getRidingEntity() instanceof EntityLivingBase) {
			EntityLivingBase ridingEntity = (EntityLivingBase) orgEntity.getRidingEntity();
			prevRotYaw = ridingEntity.prevRenderYawOffset;
			rotyaw = ridingEntity.renderYawOffset;
		} else {
			prevRotYaw = (inaction ? orgEntity.rotationYaw : orgEntity.prevRenderYawOffset);
			rotyaw = (inaction ? orgEntity.rotationYaw : orgEntity.renderYawOffset);
		}
		
		if (this.orgEntity.isChild()) {
			scaleX *= 0.5F;
			scaleY *= 0.5F;
			scaleZ *= 0.5F;
		}
		
		return MathUtils.getModelMatrixIntegrated((float)orgEntity.lastTickPosX, (float)orgEntity.posX, (float)orgEntity.lastTickPosY, (float)orgEntity.posY,
				(float)orgEntity.lastTickPosZ, (float)orgEntity.posZ, 0, 0, prevRotYaw, rotyaw, partialTicks, scaleX, scaleY, scaleZ);
	}
	
	public void resetLivingMixLoop() {
		this.currentMixMotion = LivingMotion.NONE;
		this.getClientAnimator().resetMixMotion();
	}

	public void playAnimationSynchronize(int id, float modifyTime) {
		this.animator.playAnimation(id, modifyTime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}

	public void playAnimationSynchronize(StaticAnimation animation, float modifyTime) {
		this.playAnimationSynchronize(animation.getId(), modifyTime);
	}
	
	public void notifyToReset(float width, float height) {
		this.widthResetSize = width;
		this.heightResetSize = height;
		this.shouldReset = true;
	}
	
	public void resetSize() {
		if (this.shouldReset) {
			float f = orgEntity.width;
			orgEntity.width = this.widthResetSize;
			orgEntity.height = this.heightResetSize;
			if (orgEntity.width < f) {
				double d0 = (double) this.widthResetSize * 0.5D;
				orgEntity.setEntityBoundingBox(
						new AxisAlignedBB(orgEntity.posX - d0, orgEntity.posY, orgEntity.posZ - d0, orgEntity.posX + d0,
								orgEntity.posY + (double) orgEntity.height, orgEntity.posZ + d0));
				return;
			}

			AxisAlignedBB axisalignedbb = orgEntity.getEntityBoundingBox();
			orgEntity.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ,
					axisalignedbb.minX + (double) orgEntity.width, axisalignedbb.minY + (double) orgEntity.height,
					axisalignedbb.minZ + (double) orgEntity.width));

			if (orgEntity.width > f && !orgEntity.world.isRemote) {
				orgEntity.move(MoverType.SELF, (double) (f - orgEntity.width), 0.0D, (double) (f - orgEntity.width));
			}

			this.shouldReset = false;
		}
	}
	
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EntityEquipmentSlot slotType) {
		
	}
	
	@SuppressWarnings("unchecked")
	public <A extends Animator> A getAnimator() {
		return (A) this.animator;
	}

	public AnimatorClient getClientAnimator() {
		return this.<AnimatorClient>getAnimator();
	}

	public AnimatorServer getServerAnimator() {
		return this.<AnimatorServer>getAnimator();
	}

	public abstract StaticAnimation getHitAnimation(StunType stunType);

	@Override
	public void aboutToDeath() {
		this.animator.onEntityDeath();
	}

	@Override
	public T getOriginalEntity() {
		return orgEntity;
	}

	public SoundEvent getWeaponHitSound(EnumHand hand) {
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null)
			return cap.getHitSound();

		return Sounds.BLUNT_HIT;
	}

	public SoundEvent getSwingSound(EnumHand hand) {
		CapabilityItem cap = getHeldItemCapability(hand);

		if (cap != null) {
			return cap.getSmashingSound();
		}

		return Sounds.WHOOSH;
	}

	public Collider getColliderMatching(EnumHand hand) {
		CapabilityItem itemCap = this.getHeldItemCapability(hand);
		return itemCap != null ? itemCap.getWeaponCollider() : Colliders.fist;
	}

	public int getHitEnemies() {
		return (int) this.getAttributeValue(ModAttributes.MAX_STRIKES);
	}

	public float getDefenceIgnore() {
		return (float) this.getAttributeValue(ModAttributes.ARMOR_NEGATION);
	}

	public float getImpact() {
		return (float) this.getAttributeValue(ModAttributes.IMPACT);
	}

	public boolean isTeam(Entity entityIn) {
		if (orgEntity.getRidingEntity() != null && orgEntity.getRidingEntity().equals(entityIn))
			return true;
		else if (this.isMountedTeam(entityIn))
			return true;

		return this.orgEntity.isOnSameTeam(entityIn);
	}

	private boolean isMountedTeam(Entity entityIn) {
		EntityLivingBase orgEntity = this.getOriginalEntity();
		for (Entity passanger : orgEntity.getPassengers()) {
			if (passanger.equals(entityIn))
				return true;
		}

		for (Entity passanger : entityIn.getPassengers()) {
			if (passanger.equals(orgEntity))
				return true;
		}

		return false;
	}
	
	public boolean isFirstPerson() {
		return false;
	}
	
	public EntityState getEntityState() {
		return this.animator.getPlayer().getPlay().getState(animator.getPlayer().getElapsedTime());
	}

	public static enum EntityState {
		FREE(false, false, false, false, true, 0),
		FREE_CAMERA(false, true, false, false, false, 1),
		FREE_INPUT(false, false, false, false, true, 3),
		PRE_DELAY(true, true, false, false, false, 1),
		CONTACT(true, true, true, false, false, 2),
		ROTATABLE_CONTACT(false, true, true, false, false, 2),
		POST_DELAY(true, true, false, false, true, 3),
		ROTATABLE_POST_DELAY(false, true, false, false, true, 3),
		HIT(true, true, false, false, false, 3),
		DODGE(true, true, false, true, false, 3);
		
		boolean cameraLock;
		boolean movementLock;
		boolean collideDetection;
		boolean invincible;
		boolean canAct;
		// none : 0, beforeContact : 1, contact : 2, afterContact : 3
		int level;
		
		EntityState(boolean cameraLock, boolean movementLock, boolean collideDetection, boolean invincible, boolean canAct, int level) {
			this.cameraLock = cameraLock;
			this.movementLock = movementLock;
			this.collideDetection = collideDetection;
			this.invincible = invincible;
			this.canAct = canAct;
			this.level = level;
		}
		
		public boolean isCameraRotationLocked() {
			return this.cameraLock;
		}
		
		public boolean isMovementLocked() {
			return this.movementLock;
		}
		
		public boolean shouldDetectCollision() {
			return this.collideDetection;
		}
		
		public boolean isInvincible() {
			return this.invincible;
		}
		
		public boolean canAct() {
			return this.canAct;
		}
		
		public int getLevel() {
			return this.level;
		}
	}
}