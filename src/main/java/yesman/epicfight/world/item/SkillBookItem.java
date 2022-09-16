package yesman.epicfight.world.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SkillBookItem extends Item {
	public static void setContainingSkill(String name, ItemStack stack) {
		stack.getOrCreateTag().put("skill", StringNBT.valueOf(name));
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
	public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.getTag() != null && stack.getTag().contains("skill")) {
			Skill skill = Skills.getSkill(stack.getTag().getString("skill"));
			
			if (skill != null) {
				tooltip.add(new TranslationTextComponent(skill.getTranslatableText()).withStyle(TextFormatting.DARK_GRAY));
			}
		}
	}
	
	@Override
	public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == EpicFightItemGroup.ITEMS) {
			Skills.getLearnableSkills().forEach((skill) -> {
				ItemStack stack = new ItemStack(this);
				setContainingSkill(skill, stack);
				items.add(stack);
			});
		}
	}
	
	@Override
	public ActionResult<ItemStack> use(World worldIn, PlayerEntity playerIn, Hand hand) {
		ItemStack itemstack = playerIn.getItemInHand(hand);
		playerIn.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).ifPresent((capability) -> {
			if (capability instanceof PlayerPatch) {
				((PlayerPatch<?>)capability).openSkillBook(itemstack, hand);
			}
		});
		playerIn.awardStat(Stats.ITEM_USED.get(this));
		return ActionResult.pass(itemstack);
	}
}