package yesman.epicfight.api.animation.types;

import javax.annotation.Nullable;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.math.ExtraDamageType;
import yesman.epicfight.api.utils.math.ValueCorrector;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SpecialAttackAnimation extends AttackAnimation {
	public SpecialAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, path, model, new Phase(0.0F, antic, preDelay, contact, recovery, Float.MAX_VALUE, index, collider));
	}
	
	public SpecialAttackAnimation(float convertTime, String path, Model model, Phase... phases) {
		super(convertTime, path, model, phases);
	}
	
	@Override
	protected float getDamageTo(LivingEntityPatch<?> entitypatch, LivingEntity target, Phase phase, ExtendedDamageSource source) {
		float f = entitypatch.getDamageTo(target, source, phase.hand);
		int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, entitypatch.getOriginal());
		ValueCorrector cor = new ValueCorrector(0, (i > 0) ? 1.0F + (float)i / (float)(i + 1.0F) : 1.0F, 0);
		phase.getProperty(AttackPhaseProperty.DAMAGE).ifPresent((opt) -> cor.merge(opt));
		float totalDamage = cor.getTotalValue(f);
		ExtraDamageType extraCalculator = phase.getProperty(AttackPhaseProperty.EXTRA_DAMAGE).orElse(null);
		
		if (extraCalculator != null) {
			totalDamage += extraCalculator.get(entitypatch.getOriginal(), target);
		}
		
		return totalDamage;
	}
}