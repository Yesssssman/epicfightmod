package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.PatchedElytraLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedHeadLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PCustomHumanoidEntityRenderer<AM extends HumanoidMesh> extends PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, HumanoidModel<LivingEntity>, LivingEntityRenderer<LivingEntity, HumanoidModel<LivingEntity>>, AM> {
	private final AM mesh;
	
	public PCustomHumanoidEntityRenderer(AM mesh, EntityRendererProvider.Context context, EntityType<?> entityType) {
		super(context, entityType);
		
		this.mesh = mesh;
		this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer<>());
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(mesh, false, context.getModelManager()));
		this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
	}
	
	@Override
	protected void setJointTransforms(LivingEntityPatch<LivingEntity> entitypatch, Armature armature, Pose pose, float partialTicks) {
		if (entitypatch.getOriginal().isBaby()) {
			pose.getOrDefaultTransform("Head").frontResult(JointTransform.getScale(new Vec3f(1.25F, 1.25F, 1.25F)), OpenMatrix4f::mul);
		}
	}
	
	@Override
	protected double getLayerCorrection() {
		return 0.75F;
	}
	
	@Override
	public AM getMesh(LivingEntityPatch<LivingEntity> entitypatch) {
		return this.mesh;
	}
}