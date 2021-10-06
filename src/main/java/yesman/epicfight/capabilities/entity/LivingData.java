package yesman.epicfight.capabilities.entity;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import yesman.epicfight.animation.Animator;
import yesman.epicfight.animation.AnimatorServer;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.config.CapabilityConfig;
import yesman.epicfight.config.CapabilityConfig.CustomEntityConfig;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Model;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCPlayAnimation;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.game.IExtendedDamageSource;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;

public abstract class LivingData<T extends LivingEntity> extends CapabilityEntity<T> {
	private float stunTimeReduction;
	protected EntityState state = EntityState.FREE;
	public LivingMotion currentMotion = LivingMotion.IDLE;
	public LivingMotion currentOverwritingMotion = LivingMotion.IDLE;
	protected Animator animator;
	public List<LivingEntity> currentlyAttackedEntity;
	
	@Override
	public void onEntityConstructed(T entityIn) {
		super.onEntityConstructed(entityIn);
		if (this.orgEntity.world.isRemote) {
			this.animator = new AnimatorClient(this);
			this.initAnimator(this.getClientAnimator());
			this.getClientAnimator().playInitialLivingMotion();
		} else {
			this.animator = new AnimatorServer(this);
		}
		this.currentlyAttackedEntity = new ArrayList<LivingEntity>();
		this.orgEntity.getDataManager().register(DataKeys.STUN_SHIELD, Float.valueOf(0.0F));
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		this.initAttributes();
		if (CapabilityConfig.CUSTOM_ENTITY_MAP.containsKey(EntityType.getKey(this.orgEntity.getType()))) {
			CustomEntityConfig config = CapabilityConfig.CUSTOM_ENTITY_MAP.get(EntityType.getKey(this.orgEntity.getType()));
			this.orgEntity.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(config.getMaxStrikes());
			this.orgEntity.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).setBaseValue(config.getArmorNegation());
			this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(config.getImpact());
		}
	}
	
	protected abstract void initAnimator(AnimatorClient animatorClient);
	public abstract void updateMotion(boolean considerInaction);
	public abstract <M extends Model> M getEntityModel(Models<M> modelDB);
	
	protected void initAttributes() {
		this.orgEntity.getAttribute(EpicFightAttributes.WEIGHT.get()).setBaseValue(this.orgEntity.getAttribute(Attributes.MAX_HEALTH).getBaseValue() * 2.0D);
		this.orgEntity.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(1.0D);
		this.orgEntity.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).setBaseValue(0.0D);
		this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(0.5D);
	}
	
	@Override
	protected void updateOnClient() {
		AnimatorClient animator = this.getClientAnimator();
		if (this.state.isInaction()) {
			this.orgEntity.renderYawOffset = this.orgEntity.rotationYaw;
		}
		this.updateMotion(true);
		animator.update();
	}
	
	@Override
	protected void updateOnServer() {
		if (this.stunTimeReduction > 0.0F) {
			float stunArmor = this.getStunArmor();
			this.stunTimeReduction = Math.max(0.0F, this.stunTimeReduction - 0.03F * (1 - stunArmor / (7.5F + stunArmor)));
		}
		this.animator.update();
	}
	
	@Override
	public void update() {
		this.updateEntityState();
		
		if (isRemote()) {
			this.updateOnClient();
		} else {
			this.updateOnServer();
		}
		
		if (this.orgEntity.deathTime == 19) {
			this.aboutToDeath();
		}
	}
	
	public void updateEntityState() {
		this.state = this.animator.getEntityState();
	}
	
	protected final void commonBipedCreatureAnimatorInit(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	protected final void commonCreatureUpdateMotion(boolean considerInaction) {
		if (this.state.isInaction() && considerInaction) {
			currentMotion = LivingMotion.INACTION;
		} else {
			if (this.orgEntity.getHealth() <= 0.0F) {
				currentMotion = LivingMotion.DEATH;
			} else if (orgEntity.getRidingEntity() != null) {
				currentMotion = LivingMotion.MOUNT;
			} else {
				if (this.orgEntity.getMotion().y < -0.55F)
					currentMotion = LivingMotion.FALL;
				else if (orgEntity.limbSwingAmount > 0.01F)
					currentMotion = LivingMotion.WALK;
				else
					currentMotion = LivingMotion.IDLE;
			}
		}
		
		this.currentOverwritingMotion = this.currentMotion;
	}
	
	protected final void commonRangedAttackCreatureUpdateMotion(boolean considerInaction) {
		this.commonCreatureUpdateMotion(considerInaction);
		UseAction useAction = this.orgEntity.getHeldItem(this.orgEntity.getActiveHand()).getUseAction();
		if (this.orgEntity.isHandActive()) {
			if (useAction == UseAction.CROSSBOW)
				currentOverwritingMotion = LivingMotion.RELOAD;
			else
				currentOverwritingMotion = LivingMotion.AIM;
		} else {
			if (this.getClientAnimator().getLayer(Layer.Priority.MIDDLE).animationPlayer.getPlay().isReboundAnimation())
				currentOverwritingMotion = LivingMotion.NONE;
		}
		
		if (CrossbowItem.isCharged(this.orgEntity.getHeldItemMainhand()))
			currentOverwritingMotion = LivingMotion.AIM;
		else if (this.getClientAnimator().prevAiming() && currentOverwritingMotion != LivingMotion.AIM)
			this.playReboundAnimation();
	}
	
	public void cancelUsingItem() {
		this.orgEntity.resetActiveHand();
		net.minecraftforge.event.ForgeEventFactory.onUseItemStop(this.orgEntity, this.orgEntity.getActiveItemStack(), this.orgEntity.getItemInUseCount());
	}
	
	public CapabilityItem getHeldItemCapability(Hand hand) {
		return ModCapabilities.getItemStackCapability(this.orgEntity.getHeldItem(hand));
	}
	
	public CapabilityItem getAdvancedHeldItemCapability(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return getHeldItemCapability(hand);
		} else {
			return this.isValidOffhandItem() ? this.getHeldItemCapability(hand) : CapabilityItem.EMPTY;
		}
	}
	
	public boolean hurtBy(LivingAttackEvent event) {
		if (this.getEntityState().isInvincible()) {
			DamageSource damageSource = event.getSource();
			if (damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagicDamage()) {
				return false;
			}
		}
		return true;
	}
	
	public IExtendedDamageSource getDamageSource(StunType stunType, AttackAnimation animation, Hand hand) {
		return IExtendedDamageSource.causeMobDamage(this.orgEntity, stunType, animation);
	}
	
	public float getDamageToEntity(@Nullable Entity targetEntity, @Nullable IExtendedDamageSource source, Hand hand) {
		float damage = 0;
		if (hand == Hand.MAIN_HAND) {
			damage = (float) this.orgEntity.getAttributeValue(Attributes.ATTACK_DAMAGE);
		} else {
			damage = this.isValidOffhandItem() ? (float) this.orgEntity.getAttributeValue(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get()) :
				(float) this.orgEntity.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
		}
		
		float bonus;
		if (targetEntity instanceof LivingEntity) {
			bonus = EnchantmentHelper.getModifierForCreature(this.getHeldItemAdvanced(hand), ((LivingEntity)targetEntity).getCreatureAttribute());
		} else {
			bonus = EnchantmentHelper.getModifierForCreature(this.getHeldItemAdvanced(hand), CreatureAttribute.UNDEFINED);
		}
		
		return damage + bonus;
	}
	
	public boolean hurtEntity(Entity hitTarget, Hand handIn, IExtendedDamageSource source, float amount) {
		int resistTime = hitTarget.hurtResistantTime;
		hitTarget.hurtResistantTime = 0;
		boolean succed = hitTarget.attackEntityFrom((DamageSource)source, amount);
		if (succed) {
			int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, this.getHeldItemAdvanced(handIn));
			if (hitTarget instanceof LivingEntity) {
				if (j > 0 && !hitTarget.isBurning()) {
					hitTarget.setFire(j * 4);
				}
			}
		}
		hitTarget.hurtResistantTime = resistTime;
		return succed;
	}
	
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {}
	
	public void setStunReductionOnHit() {
		this.stunTimeReduction += (1.0F - this.stunTimeReduction) * 0.8F;
	}

	public float getStunTimeTimeReduction() {
		return this.stunTimeReduction;
	}

	public void knockBackEntity(Entity entityIn, float power) {
		double d1 = entityIn.getPosX() - this.orgEntity.getPosX();
        double d0;
        
		for (d0 = entityIn.getPosZ() - this.orgEntity.getPosZ(); d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
            d1 = (Math.random() - Math.random()) * 0.01D;
        }
        
		if (this.orgEntity.getRNG().nextDouble() >= this.orgEntity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) {
        	Vector3d vec = this.orgEntity.getMotion();
        	
        	this.orgEntity.isAirBorne = true;
            float f = MathHelper.sqrt(d1 * d1 + d0 * d0);
            
            double x = vec.x;
            double y = vec.y;
            double z = vec.z;
            
            x /= 2.0D;
            z /= 2.0D;
            x -= d1 / (double)f * (double)power;
            z -= d0 / (double)f * (double)power;

			if (!this.orgEntity.isOnGround()) {
				y /= 2.0D;
				y += (double) power;

				if (y > 0.4000000059604645D) {
					y = 0.4000000059604645D;
				}
			}
			
            this.orgEntity.setMotion(x, y, z);
            this.orgEntity.velocityChanged = true;
        }
	}
	
	public float getStunArmor() {
		ModifiableAttributeInstance stunArmor = this.orgEntity.getAttribute(EpicFightAttributes.STUN_ARMOR.get());
		return (float) (stunArmor == null ? 0 : stunArmor.getValue());
	}
	
	public float getStunShield() {
		return this.orgEntity.getDataManager().get(DataKeys.STUN_SHIELD).floatValue();
	}
	
	public void setStunShield(float value) {
		float f1 = Math.max(Math.min(value, this.getStunArmor()), 0);
		this.orgEntity.getDataManager().set(DataKeys.STUN_SHIELD, f1);
	}
	
	public float getWeight() {
		return (float)this.orgEntity.getAttributeValue(EpicFightAttributes.WEIGHT.get());
	}
	
	public void rotateTo(float degree, float limit, boolean partialSync) {
		LivingEntity entity = this.getOriginalEntity();
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
        
		if (partialSync) {
			entity.prevRotationYaw = f1;
			entity.prevRotationYawHead = f1;
			entity.prevRenderYawOffset = f1;
		}
		
		entity.rotationYaw = f1;
		entity.rotationYawHead = f1;
		entity.renderYawOffset = f1;
	}
	
	public void rotateTo(Entity target, float limit, boolean partialSync) {
		double d0 = target.getPosX() - this.orgEntity.getPosX();
        double d1 = target.getPosZ() - this.orgEntity.getPosZ();
        float degree = (float)(MathHelper.atan2(d1, d0) * (180D / Math.PI)) - 90.0F;
    	rotateTo(degree, limit, partialSync);
	}
	
	public void playSound(SoundEvent sound, float minPitch, float maxPitch) {
		float randPitch = this.orgEntity.getRNG().nextFloat() * 2.0F - 1.0F;
		randPitch = Math.min(Math.max(randPitch, minPitch), maxPitch);
		if (!this.isRemote()) {
			this.orgEntity.world.playSound(null, this.orgEntity.getPosX(), this.orgEntity.getPosY(), this.orgEntity.getPosZ(), sound, this.orgEntity.getSoundCategory(),
					1.0F, 1.0F + randPitch);
		} else {
			this.orgEntity.world.playSound(this.orgEntity.getPosX(), this.orgEntity.getPosY(), this.orgEntity.getPosZ(), sound, this.orgEntity.getSoundCategory(),
					1.0F, 1.0F + randPitch, false);
		}
	}
	
	public LivingEntity getAttackTarget() {
		return this.orgEntity.getLastAttackedEntity();
	}
	
	public float getAttackDirectionPitch() {
		float partialTicks = EpicFightMod.isPhysicalClient() ? Minecraft.getInstance().getRenderPartialTicks() : 1.0F;
		float pitch = -this.getOriginalEntity().getPitch(partialTicks);
		float correct = (pitch > 0) ? 0.03333F * (float)Math.pow(pitch, 2) : -0.03333F * (float)Math.pow(pitch, 2);
		return MathHelper.clamp(correct, -30.0F, 30.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	public OpenMatrix4f getHeadMatrix(float partialTicks) {
		float f;
        float f1;
        float f2;
		if (this.state.isInaction()) {
			f2 = 0;
		} else {
			f = MathUtils.interpolateRotation(this.orgEntity.prevRenderYawOffset, this.orgEntity.renderYawOffset, partialTicks);
			f1 = MathUtils.interpolateRotation(this.orgEntity.prevRotationYawHead, this.orgEntity.rotationYawHead, partialTicks);
			f2 = f1 - f;
			
			if (this.orgEntity.getRidingEntity() != null) {
				if (f2 > 45.0F) {
					f2 = 45.0F;
				} else if (f2 < -45.0F) {
					f2 = -45.0F;
				}
			}
		}
		
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, this.orgEntity.prevRotationPitch, this.orgEntity.rotationPitch, f2, f2, partialTicks, 1, 1, 1);
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		float prevRotYaw;
		float rotyaw;
		float scaleX = 1.0F;
		float scaleY = 1.0F;
		float scaleZ = 1.0F;
		
		if (this.orgEntity.getRidingEntity() instanceof LivingEntity) {
			LivingEntity ridingEntity = (LivingEntity) this.orgEntity.getRidingEntity();
			prevRotYaw = ridingEntity.prevRenderYawOffset;
			rotyaw = ridingEntity.renderYawOffset;
		} else {
			prevRotYaw = this.isRemote() ? this.orgEntity.prevRenderYawOffset : this.orgEntity.rotationYaw;
			rotyaw = this.isRemote() ? this.orgEntity.renderYawOffset : this.orgEntity.rotationYaw;
		}
		
		if (this.orgEntity.isChild()) {
			scaleX *= 0.5F;
			scaleY *= 0.5F;
			scaleZ *= 0.5F;
		}
		
		return MathUtils.getModelMatrixIntegrated(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, prevRotYaw, rotyaw, partialTicks, scaleX, scaleY, scaleZ);
	}
	
	public void reserverAnimationSynchronize(StaticAnimation animation) {
		this.animator.reserveAnimation(animation);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(animation.getNamespaceId(), animation.getId(), this.orgEntity.getEntityId(), 0.0F), this.orgEntity);
	}
	
	public void playAnimationSynchronize(int namespaceId, int id, float modifyTime) {
		this.animator.playAnimation(namespaceId, id, modifyTime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(namespaceId, id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}

	public void playAnimationSynchronize(StaticAnimation animation, float modifyTime) {
		this.playAnimationSynchronize(animation.getNamespaceId(), animation.getId(), modifyTime);
	}
	
	protected void playReboundAnimation() {
		this.getClientAnimator().playReboundAnimation();
	}
	
	public void resetSize(EntitySize size) {
		EntitySize entitysize = this.orgEntity.size;
		EntitySize entitysize1 = size;
		this.orgEntity.size = entitysize1;
	    if (entitysize1.width < entitysize.width) {
	    	double d0 = (double)entitysize1.width / 2.0D;
	    	this.orgEntity.setBoundingBox(new AxisAlignedBB(orgEntity.getPosX() - d0, orgEntity.getPosY(), orgEntity.getPosZ() - d0, orgEntity.getPosX() + d0,
	    			orgEntity.getPosY() + (double)entitysize1.height, orgEntity.getPosZ() + d0));
	    } else {
	    	AxisAlignedBB axisalignedbb = this.orgEntity.getBoundingBox();
	    	this.orgEntity.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width,
	    			axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
	    	
	    	if (entitysize1.width > entitysize.width && !orgEntity.world.isRemote) {
	    		float f = entitysize.width - entitysize1.width;
	        	this.orgEntity.move(MoverType.SELF, new Vector3d((double)f, 0.0D, (double)f));
	    	}
	    }
    }
	
	public void updateArmor(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		
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
	
	public void aboutToDeath() {
		
	}
	
	@Override
	public T getOriginalEntity() {
		return orgEntity;
	}

	public SoundEvent getWeaponHitSound(Hand hand) {
		return this.getAdvancedHeldItemCapability(hand).getHitSound();
	}

	public SoundEvent getSwingSound(Hand hand) {
		return this.getAdvancedHeldItemCapability(hand).getSmashingSound();
	}
	
	public HitParticleType getWeaponHitParticle(Hand hand) {
		return this.getAdvancedHeldItemCapability(hand).getHitParticle();
	}

	public Collider getColliderMatching(Hand hand) {
		return this.getAdvancedHeldItemCapability(hand).getWeaponCollider();
	}

	public int getHitEnemies(Hand hand) {
		return (int) (hand == Hand.MAIN_HAND ? this.orgEntity.getAttributeValue(EpicFightAttributes.MAX_STRIKES.get()) : 
			this.isValidOffhandItem() ? this.orgEntity.getAttributeValue(EpicFightAttributes.OFFHAND_MAX_STRIKES.get()) : this.orgEntity.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).getBaseValue());
	}

	public float getArmorNegation(Hand hand) {
		return (float) (hand == Hand.MAIN_HAND ? this.orgEntity.getAttributeValue(EpicFightAttributes.ARMOR_NEGATION.get()) : 
			this.isValidOffhandItem() ? this.orgEntity.getAttributeValue(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get()) : this.orgEntity.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).getBaseValue());
	}

	public float getImpact(Hand hand) {
		return (float) (hand == Hand.MAIN_HAND ? this.orgEntity.getAttributeValue(EpicFightAttributes.IMPACT.get()) : 
			this.isValidOffhandItem() ? this.orgEntity.getAttributeValue(EpicFightAttributes.OFFHAND_IMPACT.get()) : this.orgEntity.getAttribute(EpicFightAttributes.IMPACT.get()).getBaseValue());
	}
	
	public ItemStack getHeldItemAdvanced(Hand hand) {
		if (hand == Hand.MAIN_HAND) {
			return this.orgEntity.getHeldItem(hand);
		} else {
			return this.isValidOffhandItem() ? this.orgEntity.getHeldItem(hand) : ItemStack.EMPTY;
		}
	}
	
	public boolean isValidOffhandItem() {
		return this.getHeldItemCapability(Hand.MAIN_HAND).isValidOffhandItem(this.orgEntity.getHeldItemOffhand());
	}
	
	public boolean canAttack(Entity entityIn) {
		if (this.orgEntity.getRidingEntity() != null && this.orgEntity.getRidingEntity().equals(entityIn)) {
			return true;
		} else if (this.isRideOrBeingRidden(entityIn)) {
			return true;
		}
		return this.orgEntity.isOnSameTeam(entityIn) && this.orgEntity.getTeam() != null && !this.orgEntity.getTeam().getAllowFriendlyFire();
	}
	
	private boolean isRideOrBeingRidden(Entity entityIn) {
		LivingEntity orgEntity = this.getOriginalEntity();
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
	
	public EntityState getEntityState() {
		return this.state;
	}
	
	public LivingMotion getCurrentMotion() {
		return this.currentMotion;
	}
}