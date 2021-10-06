package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.VexModel;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.VexData;
import yesman.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import yesman.epicfight.model.Armature;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends ArmatureRenderer<VexEntity, VexData, VexModel> {
	public static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
	public static final ResourceLocation VEX_CHARGE_TEXTURE = new ResourceLocation("textures/entity/illager/vex_charging.png");
	
	public VexRenderer() {
		this.layerRendererReplace.put(HeldItemLayer.class, new HeldItemAnimatedLayer<>());
	}
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, VexEntity entityIn, VexData entitydata, float partialTicks) {
		super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(7, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(VexEntity entityIn) {
		return entityIn.isCharging() ? VEX_CHARGE_TEXTURE : VEX_TEXTURE;
	}
}