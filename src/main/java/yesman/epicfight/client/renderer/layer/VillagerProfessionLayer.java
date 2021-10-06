package yesman.epicfight.client.renderer.layer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.VillagerLevelPendantLayer;
import net.minecraft.client.renderer.entity.model.ZombieVillagerModel;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.client.resources.data.VillagerMetadataSection.HatType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.registry.Registry;
import yesman.epicfight.capabilities.entity.mob.ZombieData;
import yesman.epicfight.client.model.ClientModel;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class VillagerProfessionLayer extends AnimatedLayer<ZombieVillagerEntity, ZombieData<ZombieVillagerEntity>, ZombieVillagerModel<ZombieVillagerEntity>, VillagerLevelPendantLayer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>>> {
	private static final Object2ObjectMap<VillagerType, VillagerMetadataSection.HatType> BY_TYPE = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectMap<VillagerProfession, VillagerMetadataSection.HatType> BY_PROFESSION = new Object2ObjectOpenHashMap<>();
	
	@Override
	public void renderLayer(ZombieData<ZombieVillagerEntity> entitydata, ZombieVillagerEntity entityliving, VillagerLevelPendantLayer<ZombieVillagerEntity, ZombieVillagerModel<ZombieVillagerEntity>> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		ClientModel model1 = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
		ClientModel model2 = ClientModels.LOGICAL_CLIENT.villagerZombieBody;
		ClientModel drawingModel;
		VillagerData villagerdata = ((IVillagerDataHolder)entitydata.getOriginalEntity()).getVillagerData();
		
		HatType typeHat = originalRenderer.func_215350_a(BY_TYPE, "type", Registry.VILLAGER_TYPE, villagerdata.getType());
        @SuppressWarnings("deprecation")
		HatType professionHat = originalRenderer.func_215350_a(BY_PROFESSION, "profession", Registry.VILLAGER_PROFESSION, villagerdata.getProfession());
        drawingModel = (professionHat == HatType.NONE || professionHat == HatType.PARTIAL && typeHat != HatType.FULL) ? model1 : model2;
        
		IVertexBuilder builder1 = bufferIn.getBuffer(ModRenderTypes.getEntityCutoutNoCull(originalRenderer.func_215351_a("type", Registry.VILLAGER_TYPE.getKey(villagerdata.getType()))));
		drawingModel.draw(matrixStackIn, builder1, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		drawingModel = entitydata.getOriginalEntity().getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty() ? model1 : model2;
		
		if (villagerdata.getProfession() != VillagerProfession.NONE) {
			IVertexBuilder builder2 = bufferIn.getBuffer(ModRenderTypes.getEntityCutoutNoCull(
					originalRenderer.func_215351_a("profession", villagerdata.getProfession().getRegistryName())));
			drawingModel.draw(matrixStackIn, builder2, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		}
	}
}