package maninthehouse.epicfight.effects;

import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;

public class ModEffect extends Potion {
	protected ModEffect(boolean isBadEffectIn, int liquidColorIn, int iconIndexX, int iconIndexY, String potionName) {
		super(isBadEffectIn, liquidColorIn);
		this.setPotionName("effect." + potionName);
		this.setIconIndex(iconIndexX, iconIndexY);
		this.setRegistryName(new ResourceLocation(EpicFightMod.MODID, potionName));
	}
	
	public ResourceLocation getIcon() {
		return new ResourceLocation(EpicFightMod.MODID, "textures/effects/" + this.getRegistryName().getResourcePath() + ".png");
	}
}