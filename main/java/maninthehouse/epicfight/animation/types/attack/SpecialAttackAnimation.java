package maninthehouse.epicfight.animation.types.attack;

import javax.annotation.Nullable;

import maninthehouse.epicfight.animation.types.AnimationProperty;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.physics.Collider;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumHand;

public class SpecialAttackAnimation extends AttackAnimation {
	public SpecialAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider,
			String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public SpecialAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, EnumHand hand, @Nullable Collider collider,
			String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public SpecialAttackAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases) {
		super(id, convertTime, affectY, path, phases);
	}
	
	@Override
	protected float getDamageAmount(LivingData<?> entitydata, Entity target, EnumHand hand) {
		float multiplier = this.getProperty(AnimationProperty.DAMAGE_MULTIPLIER).orElse(1.0F);
		float adder = this.getProperty(AnimationProperty.DAMAGE_ADDER).orElse(0.0F);
	    multiplier += EnchantmentHelper.getSweepingDamageRatio(entitydata.getOriginalEntity());
		return entitydata.getDamageToEntity(target, hand) * multiplier + adder;
	}
}