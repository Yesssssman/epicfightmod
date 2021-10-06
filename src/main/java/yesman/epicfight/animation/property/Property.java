package yesman.epicfight.animation.property;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.utils.game.AttackResult.Priority;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.ExtraDamageCalculator;
import yesman.epicfight.utils.math.ValueCorrector;

public abstract class Property<T> {
	public static class StaticAnimationProperty<T> extends Property<T> {
		/**
		 * Must register in order of time.
		 */
		public static final StaticAnimationProperty<StaticAnimation.SoundKey[]> SOUNDS = new StaticAnimationProperty<StaticAnimation.SoundKey[]> ();
	}
	
	public static class AttackAnimationProperty<T> extends Property<T> {
		/**
		 * This property determines if the player's camera is fixed during the attacking phase.
		 */
		public static final AttackAnimationProperty<Boolean> LOCK_ROTATION = new AttackAnimationProperty<Boolean> ();
		
		/**
		 * This property determines the animation can be rotated vertically based on the player's view.
		 */
		public static final AttackAnimationProperty<Boolean> DIRECTIONAL = new AttackAnimationProperty<Boolean> ();
		
		/**
		 * This property determines if the animation has a fixed amount of move distance not depending on the distance between attacker and target entity
		 */
		public static final AttackAnimationProperty<Boolean> FIXED_MOVE_DISTANCE = new AttackAnimationProperty<Boolean> ();
		
		/**
		 * This property determines how much the play speed affect by entity's attack speed.
		 */
		public static final AttackAnimationProperty<Float> ATTACK_SPEED_FACTOR = new AttackAnimationProperty<Float> ();
		
		/**
		 * This property determines the basis of the speed factor. Without this value, the basis is the total animation time.
		 */
		public static final AttackAnimationProperty<Float> BASIS_ATTACK_SPEED = new AttackAnimationProperty<Float> ();
		
		/**
		 * This property adds colliders when detecting hit entity by @MultiCollider.
		 */
		public static final AttackAnimationProperty<Integer> COLLIDER_ADDER = new AttackAnimationProperty<Integer> ();
	}
	
	public static class AttackPhaseProperty<T> extends Property<T> {
		public static final AttackPhaseProperty<ValueCorrector> MAX_STRIKES = new AttackPhaseProperty<ValueCorrector> ();
		public static final AttackPhaseProperty<ValueCorrector> DAMAGE = new AttackPhaseProperty<ValueCorrector> ();
		public static final AttackPhaseProperty<ExtraDamageCalculator> EXTRA_DAMAGE = new AttackPhaseProperty<ExtraDamageCalculator> ();
		public static final AttackPhaseProperty<ValueCorrector> ARMOR_NEGATION = new AttackPhaseProperty<ValueCorrector> ();
		public static final AttackPhaseProperty<ValueCorrector> IMPACT = new AttackPhaseProperty<ValueCorrector> ();
		public static final AttackPhaseProperty<StunType> STUN_TYPE = new AttackPhaseProperty<StunType> ();
		public static final AttackPhaseProperty<SoundEvent> SWING_SOUND = new AttackPhaseProperty<SoundEvent> ();
		public static final AttackPhaseProperty<SoundEvent> HIT_SOUND = new AttackPhaseProperty<SoundEvent> ();
		public static final AttackPhaseProperty<RegistryObject<HitParticleType>> PARTICLE = new AttackPhaseProperty<RegistryObject<HitParticleType>> ();
		public static final AttackPhaseProperty<Priority> TARGET_PRIORITY = new AttackPhaseProperty<Priority> ();
	}
}