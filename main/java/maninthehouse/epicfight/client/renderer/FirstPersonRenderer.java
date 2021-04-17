package maninthehouse.epicfight.client.renderer;

import maninthehouse.epicfight.animation.types.ActionAnimation;
import maninthehouse.epicfight.animation.types.AimingAnimation;
import maninthehouse.epicfight.animation.types.DynamicAnimation;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.client.model.ClientModel;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.client.renderer.entity.ArmatureRenderer;
import maninthehouse.epicfight.client.renderer.layer.HeldItemLayer;
import maninthehouse.epicfight.client.renderer.layer.WearableItemLayer;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class FirstPersonRenderer extends ArmatureRenderer<EntityPlayerSP, ClientPlayerData> {
	public FirstPersonRenderer() {
		super();
		layers.add(new HeldItemLayer<>());
		layers.add(new WearableItemLayer<>(EntityEquipmentSlot.CHEST));
		layers.add(new WearableItemLayer<>(EntityEquipmentSlot.LEGS));
		layers.add(new WearableItemLayer<>(EntityEquipmentSlot.FEET));
	}
	
	public void render(EntityPlayerSP entityIn, ClientPlayerData entitydata, RenderLivingBase<? extends Entity> renderer, float partialTicks) {
		double x = 0;
		double y = -entityIn.getEyeHeight();
		double z = 0;
		ClientModel model = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
		Armature armature = model.getArmature();
		armature.initializeTransform();
		entitydata.getClientAnimator().setPoseToModel(partialTicks);
		VisibleMatrix4f[] poses = armature.getJointTransforms();
		
		GlStateManager.pushMatrix();
		Vec4f headPos = new Vec4f(0, entityIn.getEyeHeight(), 0, 1.0F);
		VisibleMatrix4f.transform(poses[9], headPos, headPos);
		float pitch = entityIn.rotationPitch;
		
		DynamicAnimation base = entitydata.getClientAnimator().getPlayer().getPlay();
		DynamicAnimation mix = entitydata.getClientAnimator().mixLayer.animationPlayer.getPlay();
		
		boolean flag1 = base instanceof ActionAnimation;
		boolean flag2 = mix instanceof AimingAnimation;
		
		float zCoord = flag1 ? 0 : poses[0].m32;
		float posZ = Math.min(headPos.z - zCoord, 0);
		
		if (headPos.z > poses[0].m32) {
			posZ += (poses[0].m32 - headPos.z);
		}
		
		if (!flag2) {
			GlStateManager.rotate(pitch, 1, 0, 0);
		}
		
		float interpolation = pitch > 0.0F ? pitch / 90.0F : 0.0F;
		GlStateManager.translate(x, y - 0.1D - (0.2D * (flag2 ? 0.8D : interpolation)), z + 0.1D + (0.7D * (flag2 ? 0.0D : interpolation)) - posZ);
		Minecraft.getMinecraft().getTextureManager().bindTexture(entitydata.getOriginalEntity().getLocationSkin());
		
		RenderHelper.enableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.enableLightmap();
		int i = entityIn.getBrightnessForRender();
        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float)j, (float)k);
		ClientModels.LOGICAL_CLIENT.ENTITY_BIPED_FIRST_PERSON.draw(poses);
		
		if(!entityIn.isSpectator()) {
			renderLayer(entitydata, entityIn, poses, partialTicks);
		}
		
		RenderHelper.disableStandardItemLighting();
		Minecraft.getMinecraft().entityRenderer.disableLightmap();
		GlStateManager.popMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(EntityPlayerSP entityIn) {
		return entityIn.getLocationSkin();
	}
}