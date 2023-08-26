package yesman.epicfight.client.renderer.patched.entity;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedCapeLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;

@OnlyIn(Dist.CLIENT)
public class PPlayerRenderer extends PHumanoidRenderer<AbstractClientPlayer, AbstractClientPlayerPatch<AbstractClientPlayer>, PlayerModel<AbstractClientPlayer>, HumanoidMesh> {
	public PPlayerRenderer() {
		super(Meshes.BIPED);
		this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(CapeLayer.class, new PatchedCapeLayer());
		this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<>());
	}
	
	@Override
	protected void prepareModel(HumanoidMesh mesh, AbstractClientPlayer entity, AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch) {
		super.prepareModel(mesh, entity, entitypatch);
		
		if (entity.isSpectator()) {
			for (ModelPart<AnimatedVertexIndicator> part : mesh.getAllParts()) {
				part.hidden = true;
			}
			
			mesh.head.hidden = false;
		} else {
			mesh.hat.hidden = !entity.isModelPartShown(PlayerModelPart.HAT);
			mesh.jacket.hidden = !entity.isModelPartShown(PlayerModelPart.JACKET);
			mesh.leftPants.hidden = !entity.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
			mesh.rightPants.hidden = !entity.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
			mesh.leftSleeve.hidden = !entity.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
			mesh.rightSleeve.hidden = !entity.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
		}
	}
	
	@Override
	public HumanoidMesh getMesh(AbstractClientPlayerPatch<AbstractClientPlayer> entitypatch) {
		return entitypatch.getOriginal().getModelName().equals("slim") ? Meshes.ALEX : Meshes.BIPED;
	}
}