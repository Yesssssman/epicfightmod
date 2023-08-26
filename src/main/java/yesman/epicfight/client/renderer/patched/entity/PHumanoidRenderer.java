package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
public class PHumanoidRenderer<E extends LivingEntity, T extends LivingEntityPatch<E>, M extends HumanoidModel<E>, AM extends HumanoidMesh> extends PatchedLivingEntityRenderer<E, T, M, AM> {
	private final AM mesh;
	
	public PHumanoidRenderer(AM mesh) {
		this.mesh = mesh;
		
		this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer<>());
		this.addPatchedLayer(ItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(mesh, false));
		this.addPatchedLayer(CustomHeadLayer.class, new PatchedHeadLayer<>());
	}
	
	@Override
	protected void setJointTransforms(T entitypatch, Armature armature, float partialTicks) {
		if (entitypatch.getOriginal().isBaby()) {
			this.setJointTransform("Head", armature, new OpenMatrix4f().scale(new Vec3f(1.25F, 1.25F, 1.25F)));
		}
		
		this.setJointTransform("Head", armature, entitypatch.getHeadMatrix(partialTicks));
	}
	
	@Override
	protected int getRootJointIndex() {
		return 7;
	}
	
	@Override
	protected double getLayerCorrection() {
		return 0.75F;
	}

	@Override
	public AM getMesh(T entitypatch) {
		return this.mesh;
	}
}