package yesman.epicfight.world.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SkillBookItem extends Item {
	public static void setContainingSkill(String name, ItemStack stack) {
		stack.getOrCreateTag().put("skill", StringTag.valueOf(name));
	}
	
	public static void setContainingSkill(Skill skill, ItemStack stack) {
		setContainingSkill(skill.toString(), stack);
	}
	
	public static Skill getContainSkill(ItemStack stack) {
		String skillName = stack.getTag().getString("skill");
		return Skills.getSkill(skillName);
	}
	
	public SkillBookItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().contains("skill");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (stack.getTag() != null && stack.getTag().contains("skill")) {
			Skill skill = Skills.getSkill(stack.getTag().getString("skill"));
			
			if (skill != null) {
				tooltip.add(new TranslatableComponent(skill.getTranslatableText()).withStyle(ChatFormatting.DARK_GRAY));
			}
		}
	}
	
	@Override
	public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
		if (group == EpicFightItemGroup.ITEMS) {
			Skills.getLearnableSkills().forEach((skill) -> {
				ItemStack stack = new ItemStack(this);
				setContainingSkill(skill, stack);
				items.add(stack);
			});
		}
	}
	
	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
		ItemStack itemstack = playerIn.getItemInHand(hand);
		playerIn.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent((capability) -> {
			if (capability instanceof PlayerPatch) {
				((PlayerPatch<?>)capability).openSkillBook(itemstack, hand);
			}
		});
		playerIn.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResultHolder.pass(itemstack);
	}
}