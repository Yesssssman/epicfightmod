package maninthehouse.epicfight.client.renderer.layer;

import java.util.HashMap;
import java.util.Map;

import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.item.ArmorCapability;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.model.ClientModel;
import maninthehouse.epicfight.client.model.custom.CustomModelBakery;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class WearableItemLayer<E extends EntityLivingBase, T extends LivingData<E>> extends Layer<E, T> {
	private static final Map<ResourceLocation, ClientModel> ARMOR_MODEL_MAP = new HashMap<ResourceLocation, ClientModel>();
	private static final Map<ModelBiped, ClientModel> ARMOR_MODEL_MAP_BY_MODEL = new HashMap<ModelBiped, ClientModel>();
	private static final ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	private final EntityEquipmentSlot slot;
	
	public WearableItemLayer(EntityEquipmentSlot slotType) {
		this.slot = slotType;
	}
	
	@Override
	public void renderLayer(T entitydata, E entityliving, VisibleMatrix4f[] poses, float partialTicks) {
		ItemStack stack = entityliving.getItemStackFromSlot(this.slot);
		Item item = stack.getItem();
		GlStateManager.pushMatrix();
		if(this.slot == EntityEquipmentSlot.HEAD && entityliving instanceof EntityZombieVillager) {
			GlStateManager.translate(0.0D, 0.1D, 0.0D);
		}
		
		if (item instanceof ItemArmor) {
			TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
			ItemArmor armorItem = (ItemArmor) stack.getItem();
			ClientModel model = this.getArmorModel(entityliving, armorItem, stack);
			boolean hasOverlay = armorItem.hasOverlay(stack);
			textureManager.bindTexture(this.getArmorTexture(stack, entityliving, armorItem.getEquipmentSlot(), null));
			
			if (hasOverlay) {
				int i = armorItem.getColor(stack);
				float r = (float) (i >> 16 & 255) / 255.0F;
				float g = (float) (i >> 8 & 255) / 255.0F;
				float b = (float) (i & 255) / 255.0F;
                GlStateManager.color(1.0F * r, 1.0F * g, 1.0F * b, 1.0F);
			} else {
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			}
			
			this.renderArmor(model, poses);
			
			if (hasOverlay) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                textureManager.bindTexture(this.getArmorTexture(stack, entityliving, armorItem.getEquipmentSlot(), "overlay"));
				this.renderArmor(model, poses);
			}
			
			if (stack.hasEffect()) {
				this.renderEnchantedGlint(entityliving, model, partialTicks, poses);
			}
		} else {
			if (item != Items.AIR) {
				ClientEngine.INSTANCE.renderEngine.getItemRenderer(stack.getItem()).renderItemOnHead(stack, entitydata, partialTicks);
			}
		}
		
		GlStateManager.popMatrix();
	}
	
	private ClientModel getArmorModel(E entityliving, ItemArmor armorItem, ItemStack stack) {
		ResourceLocation registryName = armorItem.getRegistryName();
		if (ARMOR_MODEL_MAP.containsKey(registryName)) {
			return ARMOR_MODEL_MAP.get(registryName);
		} else {
			ModelBiped originalModel = new ModelBiped(0.5F);
			ClientModel model;
			RenderLivingBase<?> entityRenderer = (RenderLivingBase<?>) Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(entityliving);
			
			for (LayerRenderer<?> layer : entityRenderer.layerRenderers) {
				if (layer instanceof LayerArmorBase) {
					originalModel = (ModelBiped) ((LayerArmorBase) layer).getModelFromSlot(this.slot);
				}
			}
			
			ModelBiped customModel = armorItem.getArmorModel(entityliving, stack, slot, originalModel);
			
			if (customModel == null) {
				ArmorCapability cap = (ArmorCapability) stack.getCapability(ModCapabilities.CAPABILITY_ITEM, null);
				
				if (cap == null) {
					model = ArmorCapability.getBipedArmorModel(armorItem.getEquipmentSlot());
				} else {
					model = cap.getArmorModel(armorItem.getEquipmentSlot());
				}
				ARMOR_MODEL_MAP.put(registryName, model);
				return model;
			} else {
				if (ARMOR_MODEL_MAP_BY_MODEL.containsKey(customModel)) {
					model = ARMOR_MODEL_MAP_BY_MODEL.get(customModel);
				} else {
					EpicFightMod.LOGGER.info("baked new model for " + registryName);
					model = CustomModelBakery.bakeBipedCustomArmorModel(customModel, armorItem);
				}
				ARMOR_MODEL_MAP.put(registryName, model);
				return model;
			}
		}
	}
	
	private ResourceLocation getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		ItemArmor item = (ItemArmor) stack.getItem();
		String texture = item.getArmorMaterial().getName();
		String domain = "minecraft";
		int idx = texture.indexOf(':');

		if (idx != -1) {
			domain = texture.substring(0, idx);
			texture = texture.substring(idx + 1);
		}

		String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture,
				(slot == EntityEquipmentSlot.LEGS ? 2 : 1), type == null ? "" : String.format("_%s", type));
		s1 = ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
		ResourceLocation resourcelocation = LayerBipedArmor.ARMOR_TEXTURE_RES_MAP.get(s1);
		if (resourcelocation == null) {
			resourcelocation = new ResourceLocation(s1);
			LayerBipedArmor.ARMOR_TEXTURE_RES_MAP.put(s1, resourcelocation);
		}

		return resourcelocation;
	}
	
	private void renderArmor(ClientModel model, VisibleMatrix4f[] poses) {
		model.draw(poses);
	}
	
	private void renderEnchantedGlint(EntityLivingBase entityIn, ClientModel model, float partialTicks, VisibleMatrix4f[] poses) {
		float f = (float)entityIn.ticksExisted + partialTicks;
        Minecraft.getMinecraft().getTextureManager().bindTexture(ENCHANTED_ITEM_GLINT_RES);
        Minecraft.getMinecraft().entityRenderer.setupFogColor(true);
        GlStateManager.enableBlend();
        GlStateManager.depthFunc(514);
        GlStateManager.depthMask(false);
        GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);

		for (int i = 0; i < 2; ++i) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
            GlStateManager.color(0.38F, 0.19F, 0.608F, 1.0F);
            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.scale(0.33333334F, 0.33333334F, 0.33333334F);
            GlStateManager.rotate(30.0F - (float)i * 60.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(0.0F, f * (0.001F + (float)i * 0.003F) * 20.0F, 0.0F);
            GlStateManager.matrixMode(5888);
            model.draw(poses);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        }

        GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        GlStateManager.matrixMode(5888);
        GlStateManager.enableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.depthFunc(515);
        GlStateManager.disableBlend();
        Minecraft.getMinecraft().entityRenderer.setupFogColor(false);
	}
}