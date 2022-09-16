package yesman.epicfight.client.renderer.patched.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModel;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedVillagerProfessionLayer extends PatchedLayer<ZombieVillagerEntity, MobPatch<ZombieVillagerEntity>, ZombieVillagerModel<ZombieVillagerEntity>, VillagerLevelPendantLayer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>>> {
	@Override
	public void renderLayer(MobPatch<ZombieVillagerEntity> entitypatch, ZombieVillagerEntity entityliving, VillagerLevelPendantLayer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		if (!entityliving.isInvisible()) {
			ClientModel model1 = entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT);
			ClientModel model2 = ClientModels.LOGICAL_CLIENT.villagerZombieBody;
			ClientModel drawingModel;
			VillagerData villagerdata = entitypatch.getOriginal().getVillagerData();
			
			VillagerMetadataSection.HatType typeHat = originalRenderer.getHatData(originalRenderer.typeHatCache, "type", Registry.VILLAGER_TYPE, villagerdata.getType());
	        @SuppressWarnings("deprecation")
	        VillagerMetadataSection.HatType professionHat = originalRenderer.getHatData(originalRenderer.professionHatCache, "profession", Registry.VILLAGER_PROFESSION, villagerdata.getProfession());
	        drawingModel = (professionHat == VillagerMetadataSection.HatType.NONE || professionHat == VillagerMetadataSection.HatType.PARTIAL && typeHat != VillagerMetadataSection.HatType.FULL) ? model1 : model2;
	        
			IVertexBuilder builder1 = bufferIn.getBuffer(EpicFightRenderTypes.animatedModel(originalRenderer.getResourceLocation("type", Registry.VILLAGER_TYPE.getKey(villagerdata.getType()))));
			drawingModel.drawAnimatedModel(matrixStackIn, builder1, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, LivingRenderer.getOverlayCoords(entityliving, 0.0F), poses);
			drawingModel = entitypatch.getOriginal().getItemBySlot(EquipmentSlotType.HEAD).isEmpty() ? model1 : model2;
			
			if (villagerdata.getProfession() != VillagerProfession.NONE) {
				IVertexBuilder builder2 = bufferIn.getBuffer(EpicFightRenderTypes.animatedModel(originalRenderer.getResourceLocation("profession", villagerdata.getProfession().getRegistryName())));
				drawingModel.drawAnimatedModel(matrixStackIn, builder2, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, LivingRenderer.getOverlayCoords(entityliving, 0.0F), poses);
			}
		}
	}
}