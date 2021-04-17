package maninthehouse.epicfight.item;

import java.util.List;

import javax.annotation.Nullable;

import maninthehouse.epicfight.gamedata.Skills;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.skill.Skill;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SkillBookItem extends Item {
	public SkillBookItem() {
		super();
		this.setRegistryName("skill_book").setUnlocalizedName("skill_book");
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return stack.getTagCompound() != null && stack.getTagCompound().hasKey("skill");
	}
	
	public static void setContainingSkilll(Skill skill, ItemStack stack) {
		stack.setTagCompound(new NBTTagCompound());
		stack.getTagCompound().setString("skill", skill.getRegistryName().getResourcePath());
	}
	
	public static Skill getContainSkill(ItemStack stack) {
		String skillName = stack.getTagCompound().getString("skill");
		return Skills.MODIFIABLE_SKILLS.get(new ResourceLocation(EpicFightMod.MODID, skillName));
	}
	
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		if (stack.getTagCompound() != null && stack.getTagCompound().hasKey("skill")) {
			tooltip.add(new TextComponentTranslation("skill." + EpicFightMod.MODID + "." + stack.getTagCompound().getString("skill"))
					.setStyle(new Style().setColor(TextFormatting.GREEN)).getFormattedText());
		}
    }
	
	@Override
	public IRarity getForgeRarity(ItemStack stack) {
		return EnumRarity.RARE;
	}

	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (tab == EpicFightItemGroup.ITEMS) {
			Skills.MODIFIABLE_SKILLS.values().stream().forEach((skill)->{
				ItemStack stack = new ItemStack(this);
				setContainingSkilll(skill, stack);
				items.add(stack);
			});
		}
	}
}