package yesman.epicfight.client.renderer.patched.entity;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedCapeLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@OnlyIn(Dist.CLIENT)
public class PPlayerRenderer extends PHumanoidRenderer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, PlayerRenderer, HumanoidMesh> {
	public PPlayerRenderer() {
		super(Meshes.BIPED);
		
		this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(CapeLayer.class, new PatchedCapeLayer());
		this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void prepareModel(HumanoidMesh mesh, AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, PlayerRenderer renderer) {
		super.prepareModel(mesh, entity, entitypatch, renderer);
		
		renderer.setModelProperties(entity);
		PlayerModel<AbstractClientPlayer> model = renderer.getModel();
		
		mesh.head.hidden = !model.head.visible;
		mesh.hat.hidden = !model.hat.visible;
		mesh.jacket.hidden = !model.jacket.visible;
		mesh.torso.hidden = !model.body.visible;
		mesh.leftArm.hidden = !model.leftArm.visible;
		mesh.leftLeg.hidden = !model.leftLeg.visible;
		mesh.leftPants.hidden = !model.leftPants.visible;
		mesh.leftSleeve.hidden = !model.leftSleeve.visible;
		mesh.rightArm.hidden = !model.rightArm.visible;
		mesh.rightLeg.hidden = !model.rightLeg.visible;
		mesh.rightPants.hidden = !model.rightPants.visible;
		mesh.rightSleeve.hidden = !model.rightSleeve.visible;
	}
	
	@Override
	public void render(AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch, PlayerRenderer renderer, MultiBufferSource buffer, PoseStack poseStack, int packedLight, float partialTicks) {
		super.render(entity, entitypatch, renderer, buffer, poseStack, packedLight, partialTicks);
	}
	
	@Override
	public HumanoidMesh getMesh(AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch) {
		return entitypatch.getOriginal().getModelName().equals("slim") ? Meshes.ALEX : Meshes.BIPED;
	}
}