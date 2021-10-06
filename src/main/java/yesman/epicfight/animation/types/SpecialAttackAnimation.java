package yesman.epicfight.animation.types;

import javax.annotation.Nullable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.model.Model;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.game.IExtendedDamageSource;
import yesman.epicfight.utils.math.ExtraDamageCalculator;
import yesman.epicfight.utils.math.ValueCorrector;

public class SpecialAttackAnimation extends AttackAnimation {
	public SpecialAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, affectY, path, model, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public SpecialAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, affectY, path, model, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public SpecialAttackAnimation(float convertTime, boolean affectY, String path, Model model, Phase... phases) {
		super(convertTime, affectY, path, model, phases);
	}
	
	@Override
	protected float getDamageTo(LivingData<?> entitydata, LivingEntity target, Phase phase, IExtendedDamageSource source) {
		float f = entitydata.getDamageToEntity(target, source, phase.hand);
		int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.SWEEPING, entitydata.getOriginalEntity());
		ValueCorrector cor = new ValueCorrector(0, (i > 0) ? (float)i / (float)(i + 1.0F) : 0.0F, 0);
		phase.getProperty(AttackPhaseProperty.DAMAGE).ifPresent((opt) -> cor.merge(opt));
		float totalDamage = cor.get(f);
		ExtraDamageCalculator extraCalculator = phase.getProperty(AttackPhaseProperty.EXTRA_DAMAGE).orElse(null);
		if (extraCalculator != null) {
			totalDamage += extraCalculator.get(entitydata.getOriginalEntity(), target);
		}
		
		return totalDamage;
	}
}