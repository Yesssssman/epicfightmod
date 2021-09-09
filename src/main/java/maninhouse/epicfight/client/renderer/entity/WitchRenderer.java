package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.WitchData;
import maninhouse.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import maninhouse.epicfight.model.Armature;
import net.minecraft.client.renderer.entity.layers.WitchHeldItemLayer;
import net.minecraft.client.renderer.entity.model.WitchModel;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WitchRenderer extends SimpleTextureRenderer<WitchEntity, WitchData, WitchModel<WitchEntity>> {
	public WitchRenderer(String textureLocation) {
		super(textureLocation);
		this.layerRendererReplace.put(WitchHeldItemLayer.class, new HeldItemAnimatedLayer<>());
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, WitchEntity entityIn, WitchData entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}