package yesman.epicfight.effects;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

public class ModEffect extends Effect {
	protected final ResourceLocation icon;

	public ModEffect(EffectType typeIn, String potionName, int liquidColorIn) {
		super(typeIn, liquidColorIn);
		this.icon = new ResourceLocation(EpicFightMod.MODID, "textures/mob_effect/" + potionName + ".png");
	}
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)@Override
	public void renderHUDEffect(EffectInstance effect, AbstractGui gui, MatrixStack mStack, int x, int y, float z, float alpha) {
		GlStateManager.disableTexture();
		GlStateManager.color4f(0.13F, 0.13F, 0.13F, 1.0F);
		AbstractGui.blit(mStack, x+3, y+3, 0, 0, 0, 18, 18, 18, 18);
		GlStateManager.enableTexture();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    	Minecraft.getInstance().textureManager.bindTexture(this.icon);
    	AbstractGui.blit(mStack, x+3, y+3, 1, 0, 0, 18, 18, 18, 18);
    }
	
	@SuppressWarnings("deprecation")
	@OnlyIn(Dist.CLIENT)@Override
	public void renderInventoryEffect(EffectInstance effect, DisplayEffectsScreen<?> gui, MatrixStack mStack, int x, int y, float z) {
		GlStateManager.disableTexture();
		GlStateManager.color4f(0.13F, 0.13F, 0.13F, 1.0F);
		AbstractGui.blit(mStack, x+6, y+7, 1, 0, 0, 18, 18, 18, 18);
		GlStateManager.enableTexture();
		GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
		Minecraft.getInstance().textureManager.bindTexture(this.icon);
    	AbstractGui.blit(mStack, x+6, y+7, 1, 0, 0, 18, 18, 18, 18);
	}
	
	@OnlyIn(Dist.CLIENT)
	public ResourceLocation getIcon() {
		return this.icon;
	}
}