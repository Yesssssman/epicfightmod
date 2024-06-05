package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class PCustomLivingEntityRenderer extends PatchedLivingEntityRenderer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, LivingEntityRenderer<LivingEntity, EntityModel<LivingEntity>>, AnimatedMesh> {
	private final AnimatedMesh mesh;
	
	public PCustomLivingEntityRenderer(AnimatedMesh mesh) {
		this.mesh = mesh;
	}
	
	@Override
	public AnimatedMesh getMesh(LivingEntityPatch<LivingEntity> entitypatch) {
		return this.mesh;
	}
}
