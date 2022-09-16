package yesman.epicfight.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
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
public class FirstPersonRenderer extends PatchedLivingEntityRenderer<ClientPlayerEntity, LocalPlayerPatch, PlayerModel<ClientPlayerEntity>> {
	public FirstPersonRenderer() {
		super();
		this.addPatchedLayer(ElytraLayer.class, new PatchedElytraLayer<>());
		this.addPatchedLayer(HeldItemLayer.class, new PatchedItemInHandLayer<>());
		this.addPatchedLayer(BipedArmorLayer.class, new WearableItemLayer<>(true));
		this.addPatchedLayer(HeadLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(ArrowLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(BeeStingerLayer.class, new EmptyLayer<>());
		this.addPatchedLayer(SpinAttackEffectLayer.class, new EmptyLayer<>());
	}
	
	@Override
	public void render(ClientPlayerEntity entityIn, LocalPlayerPatch entitypatch, LivingRenderer<ClientPlayerEntity, PlayerModel<ClientPlayerEntity>> renderer, IRenderTypeBuffer buffer, MatrixStack matStackIn, int packedLightIn, float partialTicks) {
		Minecraft mc = Minecraft.getInstance();
		ActiveRenderInfo renderInfo = mc.gameRenderer.getMainCamera();
		Vector3d projView = renderInfo.getPosition();
		double x = MathHelper.lerp(partialTicks, entityIn.xo, entityIn.getX()) - projView.x();
		double y = MathHelper.lerp(partialTicks, entityIn.yo, entityIn.getY()) - projView.y();
		double z = MathHelper.lerp(partialTicks, entityIn.zo, entityIn.getZ()) - projView.z();
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
		matStackIn.translate(x, y - 0.1D - (0.2D * (flag2 ? 0.8D : interpolation)), z + 0.1D + (0.7D * (flag2 ? 0.0D : interpolation)) - posZ);
		
		ClientModel firstModel = entityIn.getModelName().equals("slim") ? ClientModels.LOGICAL_CLIENT.playerFirstPersonAlex : ClientModels.LOGICAL_CLIENT.playerFirstPerson;
		firstModel.drawAnimatedModel(matStackIn, buffer.getBuffer(EpicFightRenderTypes.animatedModel(entitypatch.getOriginal().getSkinTextureLocation())), packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, poses);
		
		if(!entityIn.isSpectator()) {
			renderLayer(renderer, entitypatch, entityIn, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.popPose();
	}
}