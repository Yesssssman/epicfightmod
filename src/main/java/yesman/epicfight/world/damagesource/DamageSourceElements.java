package yesman.epicfight.world.damagesource;

import java.util.Set;

import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.utils.math.ValueModifier;

public class DamageSourceElements {
	ValueModifier damageModifier = ValueModifier.empty();
	ItemStack hurtItem = ItemStack.EMPTY;
	float impact = 0.5F;
	float armorNegation = 0.0F;
	StunType stunType = StunType.SHORT;
	Set<ExtraDamageInstance> extraDamages;
}