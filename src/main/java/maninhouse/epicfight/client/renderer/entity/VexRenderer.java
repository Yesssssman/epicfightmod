package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.VexData;
import maninhouse.epicfight.model.Armature;
import net.minecraft.client.renderer.entity.model.VexModel;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VexRenderer extends ArmatureRenderer<VexEntity, VexData, VexModel> {
	public static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
	public static final ResourceLocation VEX_CHARGE_TEXTURE = new ResourceLocation("textures/entity/illager/vex_charging.png");
	
	public VexRenderer() {
		//this.layers.add(new HeldItemAnimatedLayer<>());
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, VexEntity entityIn, VexData entitydata, float partialTicks) {
		super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
		this.transformJoint(7, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(VexEntity entityIn) {
		return entityIn.isCharging() ? VEX_CHARGE_TEXTURE : VEX_TEXTURE;
	}
}