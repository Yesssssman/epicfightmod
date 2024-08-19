package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import dev.tr7zw.skinlayers.versionless.render.CustomModelPart;

@Mixin(value = CustomModelPart.class)
public interface SkinLayer3DMixinCustomModelPart {
	@Accessor
	public float getX();
	
	@Accessor
	public float getY();
	
	@Accessor
	public float getZ();
	
	@Accessor
	public float getXRot();
	
	@Accessor
	public float getYRot();
	
	@Accessor
	public float getZRot();
}
