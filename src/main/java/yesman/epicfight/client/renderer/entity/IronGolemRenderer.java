package yesman.epicfight.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.layers.IronGolemCracksLayer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.IronGolemData;
import yesman.epicfight.client.renderer.layer.GolemCrackLayer;
import yesman.epicfight.model.Armature;

@OnlyIn(Dist.CLIENT)
public class IronGolemRenderer extends ArmatureRenderer<IronGolemEntity, IronGolemData, IronGolemModel<IronGolemEntity>> {
	private static final ResourceLocation IRON_GOLEM_TEXTURE = new ResourceLocation("textures/entity/iron_golem/iron_golem.png");
	
	public IronGolemRenderer() {
		this.layerRendererReplace.put(IronGolemCracksLayer.class, new GolemCrackLayer());
	}
	
	@Override
	protected void applyTransforms(MatrixStack matStack, Armature armature, IronGolemEntity entityIn, IronGolemData entitydata, float partialTicks) {
        super.applyTransforms(matStack, armature, entityIn, entitydata, partialTicks);
        transformJoint(2, armature, entitydata.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(IronGolemEntity entityIn) {
		return IRON_GOLEM_TEXTURE;
	}
}