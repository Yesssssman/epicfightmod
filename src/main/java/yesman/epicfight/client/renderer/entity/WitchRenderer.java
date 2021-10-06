package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.WitchData;
import yesman.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import yesman.epicfight.model.Armature;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends SimpleTextureRenderer<WitchEntity, WitchData, WitchModel<WitchEntity>> {
	public WitchRenderer(String textureLocation) {
		super(textureLocation);
		this.layerRendererReplace.put(WitchHeldItemLayer.class, new HeldItemAnimatedLayer<>());
	}
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, WitchEntity entityIn, WitchData entitydata, float partialTicks) {
		super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}