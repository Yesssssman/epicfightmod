package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import de.teamlapen.vampirism.client.renderer.entity.layers.VampirePlayerHeadLayer;
import net.minecraft.resources.ResourceLocation;

@Mixin(value = VampirePlayerHeadLayer.class)
public interface VampirismMixinVampirePlayerHeadLayer {
	@Accessor
    public ResourceLocation[] getEyeOverlays();
	@Accessor
	public ResourceLocation[] getFangOverlays();
}