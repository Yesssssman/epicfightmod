package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.VexModel;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeldItemLayer;
import yesman.epicfight.world.capabilities.entitypatch.mob.VexPatch;

@OnlyIn(Dist.CLIENT)
public class PVexRenderer extends PatchedLivingEntityRenderer<Vex, VexPatch, VexModel> {
	public static final ResourceLocation VEX_TEXTURE = new ResourceLocation("textures/entity/illager/vex.png");
	public static final ResourceLocation VEX_CHARGE_TEXTURE = new ResourceLocation("textures/entity/illager/vex_charging.png");
	
	public PVexRenderer() {
		this.layerRendererReplace.put(ItemInHandLayer.class, new PatchedHeldItemLayer<>());
	}
	
	@Override
	protected void setJointTransforms(VexPatch entitypatch, Armature armature, float partialTicks) {
		this.setJointTransform(7, armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected ResourceLocation getEntityTexture(VexPatch entitypatch) {
		return entitypatch.getOriginal().isCharging() ? VEX_CHARGE_TEXTURE : VEX_TEXTURE;
	}
}