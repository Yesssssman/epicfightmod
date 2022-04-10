package yesman.epicfight.world.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import yesman.epicfight.main.EpicFightMod;

public class VisibleMobEffect extends MobEffect {
	protected final ResourceLocation icon;
	
	public VisibleMobEffect(MobEffectCategory category, String potionName, int color) {
		super(MobEffectCategory.BENEFICIAL, color);
		this.icon = new ResourceLocation(EpicFightMod.MODID, "textures/mob_effect/" + potionName + ".png");
	}
	
	public ResourceLocation getIcon() {
		return this.icon;
	}
}