package yesman.epicfight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Armatures;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderer extends PatchedLivingEntityRenderer<LocalPlayer, LocalPlayerPatch, PlayerModel<LocalPlayer>, HumanoidMesh> {
	public FirstPersonRenderer() {
		super();
		this.addPatchedLayer(ElytraLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(Meshes.BIPED, true));
		this.addPatchedLayer(CustomHeadLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(SpinAttackEffectLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(CapeLayer.class, new EmptyLayer<>());
	}
	
	@Override
	public void render(LocalPlayer entityIn, LocalPlayerPatch entitypatch, LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer,
			MultiBufferSource buffer, PoseStack matStackIn, int packedLightIn, float partialTicks) {
		Armature armature = entitypatch.getArmature();
		armature.initializeTransform();
		OpenMatrix4f[] poses = armature.getPoseAsTransformMatrix(entitypatch.getClientAnimator().getComposedLayerPose(partialTicks));
		matStackIn.pushPose();
		OpenMatrix4f mat = entitypatch.getArmature().getBindedTransformFor(entitypatch.getArmature().getPose(partialTicks), Armatures.BIPED.head);
		mat.translate(0, 0.2F, 0);
		
		Vec3f translateVectorOfHead = mat.toTranslationVector();
		matStackIn.translate(-translateVectorOfHead.x, -translateVectorOfHead.y, -translateVectorOfHead.z);
		HumanoidMesh mesh = this.getMesh(entitypatch);
		this.prepareModel(mesh, entityIn, entitypatch);
		
		if (!entitypatch.getOriginal().isInvisible()) {
			for (ModelPart<AnimatedVertexIndicator> p : mesh.getAllParts()) {
				p.hidden = true;
			}
			
			mesh.lefrArm.hidden = false;
			mesh.rightArm.hidden = false;
			
			mesh.drawModelWithPose(matStackIn, buffer.getBuffer(EpicFightRenderTypes.triangles(RenderType.entityCutoutNoCull(entityIn.getSkinTextureLocation()))),
					packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, armature, poses);
			
		}
		
		if (!entityIn.isSpectator()) {
			renderLayer(renderer, entitypatch, entityIn, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.popPose();
	}
	
	@Override
	public HumanoidMesh getMesh(LocalPlayerPatch entitypatch) {
		return entitypatch.getOriginal().getModelName().equals("slim") ? Meshes.ALEX : Meshes.BIPED;
	}
	
	@Override
	protected void prepareModel(HumanoidMesh mesh, LocalPlayer entity, LocalPlayerPatch entitypatch) {
		mesh.initialize();
		mesh.head.hidden = true;
		mesh.hat.hidden = true;
	}
}