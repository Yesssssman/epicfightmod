package yesman.epicfight.compat;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillCategories;

public class EpicFightSkillsCategory implements IRecipeCategory<SkillCategories> {
	public static final ResourceLocation UID = new ResourceLocation(EpicFightMod.MODID, "skills");
	
	@Override
	public RecipeType<SkillCategories> getRecipeType() {
		return null;
	}
	
	@Override
	public Component getTitle() {
		return null;
	}
	
	@Override
	public IDrawable getBackground() {
		return null;
	}
	
	@Override
	public IDrawable getIcon() {
		return null;
	}
	
	@Override
	public void setRecipe(IRecipeLayoutBuilder builder, SkillCategories recipe, IFocusGroup focuses) {
		
	}
}
