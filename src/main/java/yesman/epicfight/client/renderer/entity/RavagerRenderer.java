package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.model.RavagerModel;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.RavagerData;
import yesman.epicfight.model.Armature;

@OnlyIn(Dist.CLIENT)
public class RavagerRenderer extends ArmatureRenderer<RavagerEntity, RavagerData, RavagerModel> {
	public static final ResourceLocation RAVABER_TEXTURE = new ResourceLocation("textures/entity/illager/ravager.png");
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, RavagerEntity entityIn, RavagerData entitydata, float partialTicks) {
		super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(RavagerEntity entityIn) {
		return RAVABER_TEXTURE;
	}
}