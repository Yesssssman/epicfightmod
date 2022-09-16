package yesman.epicfight.world.capabilities.entitypatch;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributeSupplier;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;

public abstract class LivingEntityPatch<T extends LivingEntity> extends EntityPatch<T> {
	public static final DataParameter<Float> STUN_SHIELD = new DataParameter<Float> (251, DataSerializers.FLOAT);
	public static final DataParameter<Float> MAX_STUN_SHIELD = new DataParameter<Float> (252, DataSerializers.FLOAT);
	
	private float stunTimeReduction;
	protected EntityState state = EntityState.DEFAULT;
	protected Animator animator;
	public LivingMotion currentLivingMotion = LivingMotions.IDLE;
	public LivingMotion currentCompositeMotion = LivingMotions.IDLE;
	public List<LivingEntity> currentlyAttackedEntity;
	protected Vector3d lastAttackPosition;
	
	@Override
	public void onConstructed(T entityIn) {
		super.onConstructed(entityIn);
		this.animator = EpicFightMod.getAnimator(this);
		this.animator.init();
		this.currentlyAttackedEntity = new ArrayList<LivingEntity>();
		this.original.getEntityData().define(STUN_SHIELD, Float.valueOf(0.0F));
		this.original.getEntityData().define(MAX_STUN_SHIELD, Float.valueOf(0.0F));
	}
	
	@Override
	public void onJoinWorld(T entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		this.original.getAttributes().supplier = new EpicFightAttributeSupplier(this.original.getAttributes().supplier);
		this.initAttributes();
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void initAnimator(ClientAnimator clientAnimator);
	public abstract void updateMotion(boolean considerInaction);
	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);
	
	protected void initAttributes() {
		this.original.getAttribute(EpicFightAttributes.WEIGHT.get()).setBaseValue(this.original.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(1.0D);
		this.original.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).setBaseValue(0.0D);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(0.5D);
	}
	
	@Override
	protected void clientTick(LivingUpdateEvent event) {
	}
	
	@Override
	protected void serverTick(LivingUpdateEvent event) {
		if (this.stunTimeReduction > 0.0F) {
			float stunArmor = this.getStunArmor();
			this.stunTimeReduction -= 0.05F * (1.1F - this.stunTimeReduction * this.stunTimeReduction) * (1.0F - stunArmor / (7.5F + stunArmor));
			this.stunTimeReduction = Math.max(0.0F, this.stunTimeReduction);
		}
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		this.animator.tick();
		
		if (this.isLogicalClient()) {
			this.clientTick(event);
		} else {
			this.serverTick(event);
		}
		
		if (this.original.deathTime == 19) {
			this.aboutToDeath();
		}
	}
	
	public void onDeath() {
		this.getAnimator().playDeathAnimation();
		this.currentLivingMotion = LivingMotions.DEATH;
	}
	
	public void updateEntityState() {
		this.state = this.animator.getEntityState();
	}
	
	public void cancelUsingItem() {
		this.original.stopUsingItem();
		ForgeEventFactory.onUseItemStop(this.original, this.original.getUseItem(), this.original.getUseItemRemainingTicks());
	}
	
	public CapabilityItem getHoldingItemCapability(Hand hand) {
		
		if ( hand == null ) {
			return CapabilityItem.EMPTY;
		}
		
		return EpicFightCapabilities.getItemStackCapability(this.original.getItemInHand(hand));
	}
	
	/**
	 * Returns an empty capability if the item in mainhand is incompatible with the item in offhand 
	 */
	public CapabilityItem getAdvancedHoldingItemCapability(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return getHoldingItemCapability(hand);
		} else {
			return this.isOffhandItemValid() ? this.getHoldingItemCapability(hand) : CapabilityItem.EMPTY;
		}
	}
	
	public ExtendedDamageSource getDamageSource(StunType stunType, StaticAnimation animation, Hand hand) {
		return ExtendedDamageSource.causeMobDamage(this.original, stunType, animation);
	}
	
	public float getDamageTo(@Nullable Entity targetEntity, @Nullable ExtendedDamageSource source, Hand hand) {
		float damage = 0;
		
		if (hand == Hand.MAIN_HAND) {
			damage = (float) this.original.getAttributeValue(Attributes.ATTACK_DAMAGE);
		} else {
			damage = this.isOffhandItemValid() ? (float) this.original.getAttributeValue(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get()) : (float) this.original.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
		}
		
		damage += EnchantmentHelper.getDamageBonus(this.getValidItemInHand(hand), (targetEntity instanceof LivingEntity) ? ((LivingEntity)targetEntity).getMobType() : CreatureAttribute.UNDEFINED);
		
		return damage;
	}
	
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		if (this.getEntityState().invulnerableTo(damageSource)) {
			return new AttackResult(AttackResult.ResultType.FAILED, amount);
		}
		
		return new AttackResult(AttackResult.ResultType.SUCCESS, amount);
	}
	
	public AttackResult tryHarm(Entity target, ExtendedDamageSource damagesource, float amount) {
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)target.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		AttackResult result = (entitypatch != null) ? entitypatch.tryHurt((DamageSource)damagesource, amount) : new AttackResult(AttackResult.ResultType.SUCCESS, amount);
		return result;
	}
	
	public void onHurtSomeone(Entity target, Hand handIn, ExtendedDamageSource damagesource, float amount, boolean succeed) {
		int j = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.FIRE_ASPECT, this.getValidItemInHand(handIn));
		
		if (target instanceof LivingEntity) {
			this.getOriginal().doEnchantDamageEffects(this.getOriginal(), target);
			
			if (j > 0 && !target.isOnFire()) {
				target.setSecondsOnFire(j * 4);
			}
		}
	}
	
	public boolean onDrop(LivingDropsEvent event) {
		return false;
	}
	
	public void gatherDamageDealt(ExtendedDamageSource source, float amount) {}
	
	public void setStunReductionOnHit() {
		this.stunTimeReduction += Math.max((1.0F - this.stunTimeReduction) * 0.8F, 0.5F);
		this.stunTimeReduction = Math.min(1.0F, this.stunTimeReduction);
	}
	
	public float getStunTimeTimeReduction() {
		return this.stunTimeReduction;
	}
	
	public void knockBackEntity(Vector3d sourceLocation, float power) {
		double d1 = sourceLocation.x() - this.original.getX();
        double d0;
        
		for (d0 = sourceLocation.z() - this.original.getZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }
		
		if (this.original.getRandom().nextDouble() >= this.original.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) {
        	Vector3d vec = this.original.getDeltaMovement();
        	
        	this.original.hasImpulse = true;
            float f = (float) Math.sqrt(d1 * d1 + d0 * d0);
            
            double x = vec.x;
            double y = vec.y;
            double z = vec.z;
            
            x /= 2.0D;
            z /= 2.0D;
            x -= d1 / (double)f * (double)power;
            z -= d0 / (double)f * (double)power;

			if (!this.original.isOnGround()) {
				y /= 2.0D;
				y += (double) power;

				if (y > 0.4000000059604645D) {
					y = 0.4000000059604645D;
				}
			}
			
            this.original.setDeltaMovement(x, y, z);
            this.original.hurtMarked = true;
        }
	}
	
	public float getStunArmor() {
		ModifiableAttributeInstance stunArmor = this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get());
		return (float) (stunArmor == null ? 0 : stunArmor.getValue());
	}
	
	public float getStunShield() {
		return this.original.getEntityData().get(STUN_SHIELD).floatValue();
	}
	
	public void setStunShield(float value) {
		value = Math.max(value, 0);
		this.original.getEntityData().set(STUN_SHIELD, value);
	}
	
	public float getMaxStunShield() {
		return this.original.getEntityData().get(MAX_STUN_SHIELD).floatValue();
	}
	
	public void setMaxStunShield(float value) {
		value = Math.max(value, 0);
		this.original.getEntityData().set(MAX_STUN_SHIELD, value);
	}
	
	public float getWeight() {
		return (float)this.original.getAttributeValue(EpicFightAttributes.WEIGHT.get());
	}
	
	public void rotateTo(float degree, float limit, boolean synchronizeOld) {
		LivingEntity entity = this.getOriginal();
		float amount = degree - entity.yRot;
		
        while (amount < -180.0F) {
        	amount += 360.0F;
        }
        
        while (amount > 180.0F) {
        	amount -= 360.0F;
        }
        
        amount = MathHelper.clamp(amount, -limit, limit);
        float f1 = entity.yRot + amount;
        
		if (synchronizeOld) {
			entity.yRotO = f1;
			entity.yHeadRotO = f1;
			entity.yBodyRotO = f1;
		}
		
		entity.yRot = f1;
		entity.yHeadRot = f1;
		entity.yBodyRot = f1;
	}
	
	public void rotateTo(Entity target, float limit, boolean partialSync) {
		double d0 = target.getX() - this.original.getX();
        double d1 = target.getZ() - this.original.getZ();
        float degree = -(float)Math.toDegrees(MathHelper.atan2(d0, d1));
    	this.rotateTo(degree, limit, partialSync);
	}
	
	public void playSound(SoundEvent sound, float pitchModifierMin, float pitchModifierMax) {
		this.playSound(sound, 1.0F, pitchModifierMin, pitchModifierMax);
	}
	
	public void playSound(SoundEvent sound, float volume, float pitchModifierMin, float pitchModifierMax) {
		float pitch = (this.original.getRandom().nextFloat() * 2.0F - 1.0F) * (pitchModifierMax - pitchModifierMin);
		
		if (!this.isLogicalClient()) {
			this.original.level.playSound(null, this.original.getX(), this.original.getY(), this.original.getZ(), sound, this.original.getSoundSource(), volume, 1.0F + pitch);
		} else {
			this.original.level.playLocalSound(this.original.getX(), this.original.getY(), this.original.getZ(), sound, this.original.getSoundSource(), volume, 1.0F + pitch, false);
		}
	}
	
	public LivingEntity getTarget() {
		return this.original.getLastHurtMob();
	}
	
	public float getAttackDirectionPitch() {
		float partialTicks = EpicFightMod.isPhysicalClient() ? Minecraft.getInstance().getFrameTime() : 1.0F;
		float pitch = -this.getOriginal().getViewXRot(partialTicks);
		float correct = (pitch > 0) ? 0.03333F * (float)Math.pow(pitch, 2) : -0.03333F * (float)Math.pow(pitch, 2);
		return MathHelper.clamp(correct, -30.0F, 30.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	public OpenMatrix4f getHeadMatrix(float partialTicks) {
        float f2;
        
		if (this.state.inaction()) {
			f2 = 0;
		} else {
			float f = MathUtils.lerpBetween(this.original.yBodyRotO, this.original.yBodyRot, partialTicks);
			float f1 = MathUtils.lerpBetween(this.original.yHeadRotO, this.original.yHeadRot, partialTicks);
			f2 = f1 - f;
			
			if (this.original.getVehicle() != null) {
				if (f2 > 45.0F) {
					f2 = 45.0F;
				} else if (f2 < -45.0F) {
					f2 = -45.0F;
				}
			}
		}
		
		
		return MathUtils.getModelMatrixIntegral(0, 0, 0, 0, 0, 0, this.original.xRotO, this.original.xRot, f2, f2, partialTicks, 1, 1, 1);
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		float prevYRot;
		float yRot;
		float scale = this.original.isBaby() ? 0.5F : 1.0F;
		
		if (this.original.getVehicle() instanceof LivingEntity) {
			LivingEntity ridingEntity = (LivingEntity) this.original.getVehicle();
			prevYRot = ridingEntity.yBodyRotO;
			yRot = ridingEntity.yBodyRot;
		} else {
			prevYRot = this.isLogicalClient() ? this.original.yBodyRotO : this.original.yRot;
			yRot = this.isLogicalClient() ? this.original.yBodyRot : this.original.yRot;
		}
		
		return MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, prevYRot, yRot, partialTicks, scale, scale, scale);
	}
	
	public void reserveAnimation(StaticAnimation animation) {
		this.animator.reserveAnimation(animation);
		EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPPlayAnimation(animation, this.original.getId(), 0.0F), this.original);
	}
	
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier) {
		this.playAnimationSynchronized(animation, convertTimeModifier, SPPlayAnimation::new);
	}
	
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier, AnimationPacketProvider packetProvider) {
		this.animator.playAnimation(animation, convertTimeModifier);
		
		EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(packetProvider.get(animation, convertTimeModifier, this), this.original);
	}
	
	@FunctionalInterface
	public static interface AnimationPacketProvider {
		public SPPlayAnimation get(StaticAnimation animation, float convertTimeModifier, LivingEntityPatch<?> entitypatch);
	}
	
	protected void playReboundAnimation() {
		this.getClientAnimator().playReboundAnimation();
	}
	
	public void resetSize(EntitySize size) {
		EntitySize entitysize = this.original.dimensions;
		EntitySize entitysize1 = size;
		this.original.dimensions = entitysize1;
	    if (entitysize1.width < entitysize.width) {
	    	double d0 = (double)entitysize1.width / 2.0D;
	    	this.original.setBoundingBox(new AxisAlignedBB(original.getX() - d0, original.getY(), original.getZ() - d0, original.getX() + d0,
	    			original.getY() + (double)entitysize1.height, original.getZ() + d0));
	    } else {
	    	AxisAlignedBB axisalignedbb = this.original.getBoundingBox();
	    	this.original.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width,
	    			axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
	    	
	    	if (entitysize1.width > entitysize.width && !original.level.isClientSide()) {
	    		float f = entitysize.width - entitysize1.width;
	        	this.original.move(MoverType.SELF, new Vector3d((double)f, 0.0D, (double)f));
	    	}
	    }
    }
	
	public void updateHeldItem(CapabilityItem fromCap, CapabilityItem toCap, ItemStack from, ItemStack to, Hand hand) {
	}
	
	public void updateArmor(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
	}
	
	public void onAttackBlocked(HurtEvent.Pre hurtEvent, LivingEntityPatch<?> opponent) {
	}
	
	public void onMount(boolean isMountOrDismount, Entity ridingEntity) {
	}
	
	@SuppressWarnings("unchecked")
	public <A extends Animator> A getAnimator() {
		return (A) this.animator;
	}
	
	public ClientAnimator getClientAnimator() {
		return this.<ClientAnimator>getAnimator();
	}
	
	public ServerAnimator getServerAnimator() {
		return this.<ServerAnimator>getAnimator();
	}
	
	public abstract StaticAnimation getHitAnimation(StunType stunType);
	public void aboutToDeath() {}
	
	public SoundEvent getWeaponHitSound(Hand hand) {
		return this.getAdvancedHoldingItemCapability(hand).getHitSound();
	}

	public SoundEvent getSwingSound(Hand hand) {
		return this.getAdvancedHoldingItemCapability(hand).getSmashingSound();
	}
	
	public HitParticleType getWeaponHitParticle(Hand hand) {
		return this.getAdvancedHoldingItemCapability(hand).getHitParticle();
	}

	public Collider getColliderMatching(Hand hand) {
		return this.getAdvancedHoldingItemCapability(hand).getWeaponCollider();
	}

	public int getMaxStrikes(Hand hand) {
		return (int) (hand == Hand.MAIN_HAND ? this.original.getAttributeValue(EpicFightAttributes.MAX_STRIKES.get()) : 
			this.isOffhandItemValid() ? this.original.getAttributeValue(EpicFightAttributes.OFFHAND_MAX_STRIKES.get()) : this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).getBaseValue());
	}
	
	public float getArmorNegation(Hand hand) {
		return (float) (hand == Hand.MAIN_HAND ? this.original.getAttributeValue(EpicFightAttributes.ARMOR_NEGATION.get()) : 
			this.isOffhandItemValid() ? this.original.getAttributeValue(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get()) : this.original.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).getBaseValue());
	}
	
	public float getImpact(Hand hand) {
		float impact;
		int i = 0;
		
		if (hand == Hand.MAIN_HAND) {
			impact = (float)this.original.getAttributeValue(EpicFightAttributes.IMPACT.get());
			i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, this.getOriginal().getMainHandItem());
		} else {
			if (this.isOffhandItemValid()) {
				impact = (float)this.original.getAttributeValue(EpicFightAttributes.OFFHAND_IMPACT.get());
				i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.KNOCKBACK, this.getOriginal().getOffhandItem());
			} else {
				impact = (float)this.original.getAttribute(EpicFightAttributes.IMPACT.get()).getBaseValue();
			}
		}
		
		return impact * (1.0F + i * 0.12F);
	}
	
	public ItemStack getValidItemInHand(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return this.original.getItemInHand(hand);
		} else {
			return this.isOffhandItemValid() ? this.original.getItemInHand(hand) : ItemStack.EMPTY;
		}
	}
	
	public boolean isOffhandItemValid() {
		return this.getHoldingItemCapability(Hand.MAIN_HAND).checkOffhandValid(this);
	}
	
	public boolean isTeammate(Entity entityIn) {
		if (this.original.getVehicle() != null && this.original.getVehicle().equals(entityIn)) {
			return true;
		} else if (this.isRideOrBeingRidden(entityIn)) {
			return true;
		}
		
		return this.original.isAlliedTo(entityIn) && this.original.getTeam() != null && !this.original.getTeam().isAllowFriendlyFire();
	}
	
	public Vector3d getLastAttackPosition() {
		return this.lastAttackPosition;
	}
	
	public void setLastAttackPosition() {
		this.lastAttackPosition = this.original.position();
	}
	
	private boolean isRideOrBeingRidden(Entity entityIn) {
		LivingEntity orgEntity = this.getOriginal();
		for (Entity passanger : orgEntity.getPassengers()) {
			if (passanger.equals(entityIn)) {
				return true;
			}
		}
		for (Entity passanger : entityIn.getPassengers()) {
			if (passanger.equals(orgEntity)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isFirstPerson() {
		return false;
	}
	
	public boolean shouldSkipRender() {
		return false;
	}
	
	public boolean shouldBlockMoving() {
		return false;
	}
	
	public float getYRotLimit() {
		return 20.0F;
	}
	
	public EntityState getEntityState() {
		return this.state;
	}
	
	public LivingMotion getCurrentLivingMotion() {
		return this.currentLivingMotion;
	}
}