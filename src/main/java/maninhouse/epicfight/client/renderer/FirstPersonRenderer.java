package maninhouse.epicfight.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.animation.types.ActionAnimation;
import maninhouse.epicfight.animation.types.AimAnimation;
import maninhouse.epicfight.client.animation.Layer.Priority;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.client.model.ClientModel;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.entity.ArmatureRenderer;
import maninhouse.epicfight.client.renderer.layer.ElytraAnimatedLayer;
import maninhouse.epicfight.client.renderer.layer.HeldItemAnimatedLayer;
import maninhouse.epicfight.client.renderer.layer.NoRenderingLayer;
import maninhouse.epicfight.client.renderer.layer.WearableItemLayer;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import maninhouse.epicfight.utils.math.Vec4f;
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
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class FirstPersonRenderer extends ArmatureRenderer<ClientPlayerEntity, ClientPlayerData, PlayerModel<ClientPlayerEntity>> {
	public FirstPersonRenderer() {
		super();
		this.layerRendererReplace.put(ElytraLayer.class, new ElytraAnimatedLayer<>());
		this.layerRendererReplace.put(HeldItemLayer.class, new HeldItemAnimatedLayer<>());
		this.layerRendererReplace.put(BipedArmorLayer.class, new WearableItemLayer<>(EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET));
		this.layerRendererReplace.put(HeadLayer.class, new NoRenderingLayer<>());
		this.layerRendererReplace.put(ArrowLayer.class, new NoRenderingLayer<>());
		this.layerRendererReplace.put(BeeStingerLayer.class, new NoRenderingLayer<>());
		this.layerRendererReplace.put(SpinAttackEffectLayer.class, new NoRenderingLayer<>());
	}
	
	@Override
	public void render(ClientPlayerEntity entityIn, ClientPlayerData entitydata, LivingRenderer<ClientPlayerEntity, PlayerModel<ClientPlayerEntity>> renderer, IRenderTypeBuffer buffer,
			MatrixStack matStackIn, int packedLightIn, float partialTicks) {
		ActiveRenderInfo renderInfo = Minecraft.getInstance().gameRenderer.getActiveRenderInfo();
		Vector3d projView = renderInfo.getProjectedView();
		double x = MathHelper.lerp(partialTicks, entityIn.prevPosX, entityIn.getPosX()) - projView.getX();
		double y = MathHelper.lerp(partialTicks, entityIn.prevPosY, entityIn.getPosY()) - projView.getY();
		double z = MathHelper.lerp(partialTicks, entityIn.prevPosZ, entityIn.getPosZ()) - projView.getZ();
		ClientModel model = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entitydata.getClientAnimator().setPoseToModel(partialTicks);
		OpenMatrix4f[] poses = armature.getJointTransforms();
		
		matStackIn.push();
		Vec4f headPos = new Vec4f(0, entityIn.getEyeHeight(), 0, 1.0F);
		OpenMatrix4f.transform(poses[9], headPos, headPos);
		float pitch = renderInfo.getPitch();
		
		boolean flag1 = entitydata.getClientAnimator().getMainFrameLayer().animationPlayer.getPlay() instanceof ActionAnimation;
		boolean flag2 = entitydata.getClientAnimator().getLayer(Priority.MIDDLE).animationPlayer.getPlay() instanceof AimAnimation;
		
		float zCoord = flag1 ? 0 : poses[0].m32;
		float posZ = Math.min(headPos.z - zCoord, 0);
		
		if (headPos.z > poses[0].m32) {
			posZ += (poses[0].m32 - headPos.z);
		}
		
		if (!flag2) {
			matStackIn.rotate(Vector3f.XP.rotationDegrees(pitch));
		}
		
		float interpolation = pitch > 0.0F ? pitch / 90.0F : 0.0F;
		matStackIn.translate(x, y - 0.1D - (0.2D * (flag2 ? 0.8D : interpolation)), z + 0.1D + (0.7D * (flag2 ? 0.0D : interpolation)) - posZ);
		
		ClientModel firstModel = entityIn.getSkinType().equals("slim") ? ClientModels.LOGICAL_CLIENT.ENTITY_BIPED_FIRST_PERSON_SLIM : ClientModels.LOGICAL_CLIENT.ENTITY_BIPED_FIRST_PERSON_CLASSIC;
		firstModel.draw(matStackIn, buffer.getBuffer(ModRenderTypes.getAnimatedModel(entitydata.getOriginalEntity().getLocationSkin())), packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		
		if(!entityIn.isSpectator()) {
			renderLayer(renderer, entitydata, entityIn, poses, buffer, matStackIn, packedLightIn, partialTicks);
		}
		
		matStackIn.pop();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(ClientPlayerEntity entityIn) {
		return entityIn.getLocationSkin();
	}
}