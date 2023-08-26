package yesman.epicfight.world.damagesource;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

public class ExtraDamageInstance {
	public static final ExtraDamage TARGET_LOST_HEALTH = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
			return (target.getMaxHealth() - target.getHealth()) * (float)params[0];
		}, (itemstack, tooltips, baseDamage, params) -> {
			tooltips.append(new TranslatableComponent("damage_source.epicfight.target_lost_health", 
					new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(params[0] * 100F) + "%").withStyle(ChatFormatting.RED)
				).withStyle(ChatFormatting.DARK_GRAY));
		});
	
	public static final ExtraDamage SWEEPING_EDGE_ENCHANTMENT = new ExtraDamage((attacker, itemstack, target, baseDamage, params) -> {
			int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, itemstack);
			float modifier = (i > 0) ? (float)i / (float)(i + 1.0F) : 0.0F;
			
			return baseDamage * modifier;
		}, (itemstack, tooltips, baseDamage, params) -> {
			int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SWEEPING_EDGE, itemstack);
			
			if (i > 0) {
				double modifier = (double)i / (double)(i + 1.0D);
				double damage = baseDamage * modifier;
				
				tooltips.append(new TranslatableComponent("damage_source.epicfight.sweeping_edge_enchant_level", 
						new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(damage)).withStyle(ChatFormatting.DARK_PURPLE), i
					).withStyle(ChatFormatting.DARK_GRAY));
			}
		});
	
	private ExtraDamage calculator;
	private float[] params;
	
	public ExtraDamageInstance(ExtraDamage calculator, float... params) {
		this.calculator = calculator;
		this.params = params;
	}
	
	public float[] getParams() {
		return this.params;
	}
	
	public Object[] toTransableComponentParams() {
		Object[] params = new Object[this.params.length];
		
		for (int i = 0; i < params.length; i++) {
			params[i] = new TextComponent(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.params[i] * 100F) + "%").withStyle(ChatFormatting.RED);
		}
		
		return params;
	}
	
	public float get(LivingEntity attacker, ItemStack hurtItem, LivingEntity target, float baseDamage) {
		return this.calculator.extraDamage.getBonusDamage(attacker, hurtItem, target, baseDamage, this.params);
	}
	
	public void setTooltips(ItemStack itemstack, MutableComponent tooltip, double baseDamage) {
		this.calculator.tooltip.setTooltip(itemstack, tooltip, baseDamage, this.params);
	}
	
	@FunctionalInterface
	public interface ExtraDamageFunction {
		float getBonusDamage(LivingEntity attacker, ItemStack hurtItem, LivingEntity target, float baseDamage, float[] params);
	}
	
	@FunctionalInterface
	public interface ExtraDamageTooltipFunction {
		void setTooltip(ItemStack itemstack, MutableComponent tooltips, double baseDamage, float[] params);
	}
	
	public static class ExtraDamage {
		ExtraDamageFunction extraDamage;
		ExtraDamageTooltipFunction tooltip;
		
		public ExtraDamage(ExtraDamageFunction extraDamage, ExtraDamageTooltipFunction tooltip) {
			this.extraDamage = extraDamage;
			this.tooltip = tooltip;
		}

		public ExtraDamageInstance create(float... params) {
			return new ExtraDamageInstance(this, params);
		}
	}
}
