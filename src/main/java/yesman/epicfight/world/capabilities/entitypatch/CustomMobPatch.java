package yesman.epicfight.world.capabilities.entitypatch;

import com.mojang.datafixers.util.Pair;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class CustomMobPatch<T extends PathfinderMob> extends MobPatch<T> {
	private final MobPatchReloadListener.CustomMobPatchProvider provider;
	
	public CustomMobPatch(Faction faction, MobPatchReloadListener.CustomMobPatchProvider provider) {
		super(faction);
		this.provider = provider;
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, ((CombatBehaviors.Builder<CustomMobPatch<T>>)this.provider.getCombatBehaviorsBuilder()).build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), this.provider.getChasingSpeed(), true));
	}
	
	public void initAttributes() {
		this.original.getAttribute(EpicFightAttributes.MAX_STRIKES.get()).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.MAX_STRIKES.get()));
		this.original.getAttribute(EpicFightAttributes.ARMOR_NEGATION.get()).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.ARMOR_NEGATION.get()));
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.IMPACT.get()));
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(this.provider.getAttributeValues().getDouble(EpicFightAttributes.STUN_ARMOR.get()));
		
		if (this.provider.getAttributeValues().containsKey(Attributes.ATTACK_DAMAGE)) {
			this.original.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(this.provider.getAttributeValues().getDouble(Attributes.ATTACK_DAMAGE));
		}
	}
	
	@Override
	public void initAnimator(Animator animator) {
		for (Pair<LivingMotion, StaticAnimation> pair : this.provider.getDefaultAnimations()) {
			animator.addLivingAnimation(pair.getFirst(), pair.getSecond());
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return this.provider.getStunAnimations().get(stunType);
	}
	
	@Override
	public SoundEvent getWeaponHitSound(InteractionHand hand) {
		CapabilityItem itemCap = this.getAdvancedHoldingItemCapability(hand);
		
		if (itemCap.isEmpty()) {
			return this.provider.getHitSound();
		}
		
		return itemCap.getHitSound();
	}

	@Override
	public SoundEvent getSwingSound(InteractionHand hand) {
		CapabilityItem itemCap = this.getAdvancedHoldingItemCapability(hand);
		
		if (itemCap.isEmpty()) {
			return this.provider.getSwingSound();
		}
		
		return itemCap.getSmashingSound();
	}

	@Override
	public HitParticleType getWeaponHitParticle(InteractionHand hand) {
		CapabilityItem itemCap = this.getAdvancedHoldingItemCapability(hand);
		
		if (itemCap.isEmpty()) {
			return this.provider.getHitParticle();
		}
		
		return itemCap.getHitParticle();
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		float scale = this.provider.getScale();
		return super.getModelMatrix(partialTicks).scale(scale, scale, scale);
	}
}