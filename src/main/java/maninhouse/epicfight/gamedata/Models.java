package maninhouse.epicfight.gamedata;

import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.model.Model;
import net.minecraft.util.ResourceLocation;

public class Models<T extends Model> {
	public static final ServerModels LOGICAL_SERVER = new ServerModels();

	public static class ServerModels extends Models<Model> {
		public ServerModels() {
			ENTITY_BIPED = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped.dae"));
			ENTITY_BIPED_64_32_TEX = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped.dae"));
			ENTITY_BIPED_SLIM_ARM = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_slim_arm.dae"));
			ENTITY_VILLAGER_ZOMBIE = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager.dae"));
			ENTITY_VILLAGER_ZOMBIE_BODY = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager_body.dae"));
			ENTITY_CREEPER = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/creeper.dae"));
			ENTITY_ENDERMAN = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/enderman.dae"));
			ENTITY_SKELETON = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/skeleton.dae"));
			ENTITY_SPIDER = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/spider.dae"));
			ENTITY_GOLEM = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/iron_golem.dae"));
			ENTITY_ILLAGER = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/illager.dae"));
			ENTITY_WITCH = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/witch.dae"));
			ENTITY_RAVAGER = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/ravager.dae"));
			ENTITY_VEX = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/vex.dae"));
			ENTITY_PIGLIN = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/piglin.dae"));
			ENTITY_HOGLIN = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/hoglin.dae"));
		}
	}
	
	/** 
	 * 0Root 1Thigh_R 2Leg_R 3Knee_R 4Thigh_L 5Leg_L 6Knee_L 7Torso 8Chest 9Head 10Shoulder_R 11Arm_R 12Hand_R 13Elbow_R 14Tool_R 15Shoulder_L 16Arm_L
	 * 17Hand_L 18Elbow_L 19Tool_L 
	 **/
	public T ENTITY_BIPED;
	public T ENTITY_BIPED_64_32_TEX;
	public T ENTITY_BIPED_SLIM_ARM;
	public T ENTITY_VILLAGER_ZOMBIE;
	public T ENTITY_VILLAGER_ZOMBIE_BODY;
	public T ENTITY_CREEPER;
	public T ENTITY_ENDERMAN;
	public T ENTITY_SKELETON;
	public T ENTITY_SPIDER;
	public T ENTITY_GOLEM;
	public T ENTITY_ILLAGER;
	public T ENTITY_WITCH;
	public T ENTITY_RAVAGER;
	public T ENTITY_VEX;
	public T ENTITY_PIGLIN;
	public T ENTITY_HOGLIN;
	
	public void buildArmatureData() {
		ENTITY_BIPED.loadArmatureData();
		ENTITY_BIPED_64_32_TEX.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_BIPED_SLIM_ARM.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_VILLAGER_ZOMBIE.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_CREEPER.loadArmatureData();
		ENTITY_SKELETON.loadArmatureData();
		ENTITY_ENDERMAN.loadArmatureData();
		ENTITY_SPIDER.loadArmatureData();
		ENTITY_GOLEM.loadArmatureData();
		ENTITY_RAVAGER.loadArmatureData();
		ENTITY_VEX.loadArmatureData();
		ENTITY_PIGLIN.loadArmatureData();
		ENTITY_ILLAGER.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_WITCH.loadArmatureData(ENTITY_BIPED.getArmature());
		ENTITY_HOGLIN.loadArmatureData();
	}
}