package maninthehouse.epicfight.client.model;

import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.util.ResourceLocation;

public class ClientModels extends Models<ClientModel> {
	public static final ClientModels LOGICAL_CLIENT = new ClientModels();
	
	public final ClientModel ENTITY_BIPED_FIRST_PERSON;
	public final ClientModel ENTITY_BIPED_OUTER_LAYER;
	public final ClientModel ITEM_HELMET;
	public final ClientModel ITEM_CHESTPLATE;
	public final ClientModel ITEM_LEGGINS;
	public final ClientModel ITEM_LEGGINS_CLOTH;
	public final ClientModel ITEM_BOOTS;
	
	public ClientModels() {
		ENTITY_BIPED = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped.dae"));
		ENTITY_BIPED_64_32_TEX = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_old_texture.dae"));
		ENTITY_BIPED_SLIM_ARM = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_slim_arm.dae"));
		ENTITY_VILLAGER_ZOMBIE = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager.dae"));
		ENTITY_CREEPER = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/creeper.dae"));
		ENTITY_ENDERMAN = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/enderman.dae"));
		ENTITY_SKELETON = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/skeleton.dae"));
		ENTITY_SPIDER = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/spider.dae"));
		ENTITY_GOLEM = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/iron_golem.dae"));
		ENTITY_ILLAGER = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/illager.dae"));
		ENTITY_WITCH = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/witch.dae"));
		ENTITY_VEX = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/vex.dae"));
		ENTITY_BIPED_FIRST_PERSON = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_firstperson.dae"));
		ENTITY_BIPED_OUTER_LAYER = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_outer_layer.dae"));
		
		ITEM_HELMET = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_helmet.dae"));
		ITEM_CHESTPLATE = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_chestplate.dae"));
		ITEM_LEGGINS = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_leggins.dae"));
		ITEM_LEGGINS_CLOTH = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_leggins_cloth.dae"));
		ITEM_BOOTS = new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_boots.dae"));
	}
	
	public void buildMeshData() {
		ENTITY_BIPED.loadMeshData();
		ENTITY_BIPED_64_32_TEX.loadMeshData();
		ENTITY_BIPED_SLIM_ARM.loadMeshData();
		ENTITY_BIPED_FIRST_PERSON.loadMeshData();
		ENTITY_BIPED_OUTER_LAYER.loadMeshData();
		ENTITY_CREEPER.loadMeshData();
		ENTITY_SKELETON.loadMeshData();
		ENTITY_VILLAGER_ZOMBIE.loadMeshData();
		ENTITY_ENDERMAN.loadMeshData();
		ENTITY_SPIDER.loadMeshData();
		ENTITY_GOLEM.loadMeshData();
		ENTITY_WITCH.loadMeshData();
		ENTITY_VEX.loadMeshData();
		ENTITY_ILLAGER.loadMeshData();
		
		ITEM_HELMET.loadMeshData();
		ITEM_CHESTPLATE.loadMeshData();
		ITEM_LEGGINS.loadMeshData();
		ITEM_LEGGINS_CLOTH.loadMeshData();
		ITEM_BOOTS.loadMeshData();
	}
}