package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.RavagerData;
import maninhouse.epicfight.model.Armature;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerRenderer extends ArmatureRenderer<RavagerEntity, RavagerData> {
	public static final ResourceLocation RAVABER_TEXTURE = new ResourceLocation("textures/entity/illager/ravager.png");
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, RavagerEntity entityIn, RavagerData entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(9, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(RavagerEntity entityIn) {
		return RAVABER_TEXTURE;
	}
}