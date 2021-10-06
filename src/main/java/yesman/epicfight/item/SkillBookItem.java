package yesman.epicfight.item;

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
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;

public class SkillBookItem extends Item {
	public static void setContainingSkill(String skillName, ItemStack stack) {
		stack.getOrCreateTag().put("skill", StringNBT.valueOf(skillName));
	}
	
	public static void setContainingSkill(Skill skill, ItemStack stack) {
		setContainingSkill(skill.getSkillName(), stack);
	}
	
	public static Skill getContainSkill(ItemStack stack) {
		String skillName = stack.getTag().getString("skill");
		return Skills.findSkill(skillName);
	}
	
	public SkillBookItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getTag() != null && stack.getTag().contains("skill");
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		if (stack.getTag() != null && stack.getTag().contains("skill")) {
			tooltip.add(new TranslationTextComponent(String.format("skill.%s.%s", EpicFightMod.MODID, stack.getTag().get("skill").getString()))
				.mergeStyle(TextFormatting.DARK_GRAY));
		}
	}
	
	@Override
	public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
		if (group == EpicFightItemGroup.ITEMS) {
			Skills.getModifiableSkillCollection().forEach((skill) -> {
				ItemStack stack = new ItemStack(this);
				setContainingSkill(skill, stack);
				items.add(stack);
			});
		}
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		playerIn.getCapability(ModCapabilities.CAPABILITY_ENTITY).ifPresent((capability) -> {
			if (capability instanceof PlayerData) {
				((PlayerData<?>)capability).openSkillBook(itemstack);
			}
		});
		playerIn.addStat(Stats.ITEM_USED.get(this));
		return ActionResult.resultPass(itemstack);
	}
}