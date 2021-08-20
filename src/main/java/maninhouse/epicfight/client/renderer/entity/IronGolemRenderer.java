package maninhouse.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.capabilities.entity.mob.IronGolemData;
import maninhouse.epicfight.client.renderer.layer.GolemCrackLayer;
import maninhouse.epicfight.model.Armature;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolemRenderer extends ArmatureRenderer<IronGolemEntity, IronGolemData> {
	private static final ResourceLocation IRON_GOLEM_TEXTURE = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");
	
	public IronGolemRenderer() {
		this.layers.add(new GolemCrackLayer());
	}
	
	@Override
	protected void applyRotations(MatrixStack matStack, Armature armature, IronGolemEntity entityIn, IronGolemData entitydata, float partialTicks) {
        super.applyRotations(matStack, armature, entityIn, entitydata, partialTicks);
        transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(IronGolemEntity entityIn) {
		return IRON_GOLEM_TEXTURE;
	}
}