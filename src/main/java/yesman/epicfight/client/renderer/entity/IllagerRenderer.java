package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.AbstractIllagerData;
import yesman.epicfight.client.renderer.layer.HeadAnimatedLayer;
import yesman.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import yesman.epicfight.model.Armature;

@OnlyIn(Dist.CLIENT)
public class IllagerRenderer<E extends AbstractIllagerEntity, T extends AbstractIllagerData<E>> extends SimpleTextureRenderer<E, T, IllagerModel<E>> {
	public IllagerRenderer(String textureLocation) {
		super(textureLocation);
		this.layerRendererReplace.put(HeldItemLayer.class, new HeldItemAnimatedLayer<>());
		this.layerRendererReplace.put(HeadLayer.class, new HeadAnimatedLayer<>());
	}
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
		super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}