package yesman.epicfight.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.PlayerItemInHandLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.AimAnimation;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.EmptyLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedElytraLayer;
import yesman.epicfight.client.renderer.patched.layer.PatchedItemInHandLayer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

@OnlyIn(Dist.CLIENT)
public class FirstPersonRenderer extends PatchedLivingEntityRenderer<LocalPlayer, LocalPlayerPatch, PlayerModel<LocalPlayer>> {
	public FirstPersonRenderer() {
		super();
		this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer<>());
		this.addPatchedLayer(PlayerItemInHandLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(HumanoidArmorLayer.class, new WearableItemLayer<>(true));
		this.addPatchedLayer(CustomHeadLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(SpinAttackEffectLayer.class, new EmptyLayer<>());
	}
	
	@Override
	public void render(LocalPlayer entityIn, LocalPlayerPatch entitypatch, LivingEntityRenderer<LocalPlayer, PlayerModel<LocalPlayer>> renderer, MultiBufferSource buffer, PoseStack matStackIn, int packedLightIn, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		Camera renderInfo = mc.gameRenderer.getMainCamera();
		Vec3 projView = renderInfo.getPosition();
		double x = Mth.lerp(partialTicks, entityIn.xo, entityIn.getX()) - projView.x();
		double y = Mth.lerp(partialTicks, entityIn.yo, entityIn.getY()) - projView.y();
		double z = Mth.lerp(partialTicks, entityIn.zo, entityIn.getZ()) - projView.z();
		ClientModel model = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entitypatch.getClientAnimator().setPoseToModel(partialTicks);
		OpenMatrix4f[] poses = armature.getJointTransforms();
		
		matStackIn.pushPose();
		Vec4f headPos = new Vec4f(0, entityIn.getEyeHeight(), 0, 1.0F);
		OpenMatrix4f.transform(poses[9], headPos, headPos);
		float pitch = renderInfo.getXRot();
		
		boolean flag1 = entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation() instanceof ActionAnimation;
		boolean flag2 = entitypatch.getClientAnimator().getCompositeLayer(Priority.MIDDLE).animationPlayer.getAnimation() instanceof AimAnimation;
		
		float zCoord = flag1 ? 0 : poses[0].m32;
		float posZ = Math.min(headPos.z - zCoord, 0);
		
		if (headPos.z > poses[0].m32) {
			posZ += (poses[0].m32 - headPos.z);
		}
		
		if (!flag2) {
			matStackIn.mulPose(Vector3f.XP.rotationDegrees(pitch));
		}
		
		float interpolation = pitch > 0.0F ? pitch / 90.0F : 0.0F;
		
		double yCorrection = y - 0.1D - (0.2D * (flag2 ? 0.8D : interpolation));
		double zCorrection = z + 0.1D + (0.7D * (flag2 ? 0.0D : interpolation)) - posZ;
		
		matStackIn.translate(x, yCorrection, zCorrection);
		
		ClientModel firstModel = entityIn.getModelName().equals("slim") ? ClientModels.LOGICAL_CLIENT.playerFirstPersonAlex : ClientModels.LOGICAL_CLIENT.playerFirstPerson;
		firstModel.drawAnimatedModel(matStackIn, buffer.getBuffer(EpicFightRenderTypes.animatedModel(entitypatch.getOriginal().getSkinTextureLocation())), packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, poses);
		
		if(!entityIn.isSpectator()) {
			renderLayer(renderer, entitypatch, entityIn, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.popPose();
	}
}