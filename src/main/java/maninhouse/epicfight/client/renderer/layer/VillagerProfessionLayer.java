package maninhouse.epicfight.client.renderer.layer;

import java.io.IOException;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import maninhouse.epicfight.capabilities.entity.mob.ZombieData;
import maninhouse.epicfight.client.model.ClientModel;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.client.renderer.ModRenderTypes;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.data.VillagerMetadataSection;
import net.minecraft.client.resources.data.VillagerMetadataSection.HatType;
import net.minecraft.entity.merchant.villager.VillagerData;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.villager.IVillagerDataHolder;
import net.minecraft.entity.villager.VillagerType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.DefaultedRegistry;
import net.minecraft.util.registry.Registry;

public class VillagerProfessionLayer extends Layer<ZombieVillagerEntity, ZombieData<ZombieVillagerEntity>> {
	private static final Object2ObjectMap<VillagerType, VillagerMetadataSection.HatType> BY_TYPE = new Object2ObjectOpenHashMap<>();
	private static final Object2ObjectMap<VillagerProfession, VillagerMetadataSection.HatType> BY_PROFESSION = new Object2ObjectOpenHashMap<>();
	private final IResourceManager rm;
	
	public VillagerProfessionLayer() {
		this.rm = Minecraft.getInstance().getResourceManager();
	}
	
	@Override
	public void renderLayer(ZombieData<ZombieVillagerEntity> entitydata, ZombieVillagerEntity entityliving,
			MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, OpenMatrix4f[] poses, float partialTicks) {
		
		ClientModel model1 = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT);
		ClientModel model2 = ClientModels.LOGICAL_CLIENT.ENTITY_VILLAGER_ZOMBIE_BODY;
		
		ClientModel drawingModel;
		VillagerData villagerdata = ((IVillagerDataHolder)entitydata.getOriginalEntity()).getVillagerData();
		
		HatType typeHat = this.func_215350_a(BY_TYPE, "type", Registry.VILLAGER_TYPE, villagerdata.getType());
        @SuppressWarnings("deprecation")
		HatType professionHat = this.func_215350_a(BY_PROFESSION, "profession", Registry.VILLAGER_PROFESSION, villagerdata.getProfession());
        drawingModel = (professionHat == HatType.NONE || professionHat == HatType.PARTIAL && typeHat != HatType.FULL) ? model1 : model2;
        
		IVertexBuilder builder1 = bufferIn.getBuffer(ModRenderTypes.getEntityCutoutNoCull(
				this.getOutlayerTexture("type", Registry.VILLAGER_TYPE.getKey(villagerdata.getType()))));
		drawingModel.draw(matrixStackIn, builder1, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		drawingModel = entitydata.getOriginalEntity().getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty() ? model1 : model2;
		
		if (villagerdata.getProfession() != VillagerProfession.NONE) {
			IVertexBuilder builder2 = bufferIn.getBuffer(ModRenderTypes.getEntityCutoutNoCull(
					this.getOutlayerTexture("profession", villagerdata.getProfession().getRegistryName())));
			drawingModel.draw(matrixStackIn, builder2, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		}
	}
	
	protected <K> VillagerMetadataSection.HatType func_215350_a(Object2ObjectMap<K, VillagerMetadataSection.HatType> map, String p_215350_2_,
			DefaultedRegistry<K> p_215350_3_, K p_215350_4_) {
		return map.computeIfAbsent(p_215350_4_, (p_215349_4_) -> {
			try (IResource iresource = this.rm
					.getResource(this.getOutlayerTexture(p_215350_2_, p_215350_3_.getKey(p_215350_4_)))) {
				VillagerMetadataSection villagermetadatasection = iresource
						.getMetadata(VillagerMetadataSection.field_217827_a);
				if (villagermetadatasection != null) {
					VillagerMetadataSection.HatType villagermetadatasection$hattype = villagermetadatasection
							.func_217826_a();
					return villagermetadatasection$hattype;
				}
			} catch (IOException var21) {
				;
			}
			return VillagerMetadataSection.HatType.NONE;
		});
	}

	protected ResourceLocation getOutlayerTexture(String path, ResourceLocation rl) {
		return new ResourceLocation("textures/entity/zombie_villager/" + path + "/" + rl.getPath() + ".png");
	}
}