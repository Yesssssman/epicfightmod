package yesman.epicfight.client.renderer.layer;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.item.ArmorCapability;
import yesman.epicfight.client.model.ClientModel;
import yesman.epicfight.client.model.custom.CustomModelBakery;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class WearableItemLayer<E extends LivingEntity, T extends LivingData<E>, M extends BipedModel<E>> extends AnimatedLayer<E, T, M, BipedArmorLayer<E, M, M>> {
	private static final Map<ResourceLocation, ClientModel> ARMOR_MODEL_MAP = new HashMap<ResourceLocation, ClientModel>();
	private final EquipmentSlotType[] slots;
	
	public WearableItemLayer(EquipmentSlotType... slotType) {
		this.slots = slotType;
	}
	
	private void renderArmor(MatrixStack matStack, IRenderTypeBuffer buf, int packedLightIn, boolean hasEffect, ClientModel model, float r, float g, float b, ResourceLocation armorResource, OpenMatrix4f[] poses) {
		IVertexBuilder ivertexbuilder = ModRenderTypes.getArmorVertexBuilder(buf, ModRenderTypes.getAnimatedArmorModel(armorResource), hasEffect);
		model.draw(matStack, ivertexbuilder, packedLightIn, r, g, b, 1.0F, poses);
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, BipedArmorLayer<E, M, M> originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer buffer, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		for (EquipmentSlotType slot : this.slots) {
			ItemStack stack = entityliving.getItemStackFromSlot(slot);
			Item item = stack.getItem();
			
			if (item instanceof ArmorItem) {
				ArmorItem armorItem = (ArmorItem) stack.getItem();
				matrixStackIn.push();
				if (slot != armorItem.getEquipmentSlot()) {
					matrixStackIn.pop();
					return;
				}
				
				if (slot == EquipmentSlotType.HEAD && entityliving instanceof ZombieVillagerEntity) {
					matrixStackIn.translate(0.0D, 0.1D, 0.0D);
				}
				
				ClientModel model = this.getArmorModel(originalRenderer, entityliving, armorItem, stack, slot);
				boolean hasEffect = stack.hasEffect();
				if (armorItem instanceof IDyeableArmorItem) {
					int i = ((IDyeableArmorItem) armorItem).getColor(stack);
					float r = (float) (i >> 16 & 255) / 255.0F;
					float g = (float) (i >> 8 & 255) / 255.0F;
					float b = (float) (i & 255) / 255.0F;
					this.renderArmor(matrixStackIn, buffer, packedLightIn, hasEffect, model, r, g, b,
							this.getArmorTexture(stack, entityliving, slot, null), poses);
					this.renderArmor(matrixStackIn, buffer, packedLightIn, hasEffect, model, 1.0F, 1.0F, 1.0F,
							this.getArmorTexture(stack, entityliving, slot, "overlay"), poses);
				} else {
					this.renderArmor(matrixStackIn, buffer, packedLightIn, hasEffect, model, 1.0F, 1.0F, 1.0F,
							this.getArmorTexture(stack, entityliving, slot, null), poses);
				}
				matrixStackIn.pop();
			}
		}
	}
	
	private ClientModel getArmorModel(BipedArmorLayer<E, M, M> originalRenderer, E entityliving, ArmorItem armorItem, ItemStack stack, EquipmentSlotType slot) {
		ResourceLocation registryName = armorItem.getRegistryName();
		if (ARMOR_MODEL_MAP.containsKey(registryName)) {
			return ARMOR_MODEL_MAP.get(registryName);
		} else {
			BipedModel<E> customModel = armorItem.getArmorModel(entityliving, stack, slot, originalRenderer.func_241736_a_(slot));
			ClientModel model;
			
			if (customModel == null) {
				ArmorCapability cap = (ArmorCapability)ModCapabilities.getItemStackCapability(stack);
				if (cap == null) {
					model = ArmorCapability.getBipedArmorModel(slot);
				} else {
					model = cap.getArmorModel(slot);
				}
			} else {
				EpicFightMod.LOGGER.info("baked new model for " + registryName);
				model = CustomModelBakery.bakeCustomArmorModel(customModel, armorItem, slot);
			}
			
			ARMOR_MODEL_MAP.put(registryName, model);
			return model;
		}
	}
	
	private ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
		ArmorItem item = (ArmorItem) stack.getItem();
		String texture = item.getArmorMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');

		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}

		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
				(slot == EquipmentSlotType.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
		s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = BipedArmorLayer.ARMOR_TEXTURE_RES_MAP.get(s1);
		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			BipedArmorLayer.ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
		}

		return resourcelocation;
	}
}