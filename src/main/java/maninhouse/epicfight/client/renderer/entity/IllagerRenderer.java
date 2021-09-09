package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.AbstractIllagerData;
import maninhouse.epicfight.client.renderer.layer.HeadAnimatedLayer;
import maninhouse.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import maninhouse.epicfight.model.Armature;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.IllagerModel;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllagerRenderer<E extends AbstractIllagerEntity, T extends AbstractIllagerData<E>> extends SimpleTextureRenderer<E, T, IllagerModel<E>> {
	public IllagerRenderer(String textureLocation) {
		super(textureLocation);
		this.layerRendererReplace.put(HeldItemLayer.class, new HeldItemAnimatedLayer<>());
		this.layerRendererReplace.put(HeadLayer.class, new HeadAnimatedLayer<>());
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, E entityIn, T entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
}