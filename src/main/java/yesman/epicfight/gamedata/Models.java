package yesman.epicfight.gamedata;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Model;

public abstract class Models<T extends Model> {
	public static final ServerModels LOGICAL_SERVER = new ServerModels();
	public static class ServerModels extends Models<Model> {
		public ServerModels() {
			this.biped = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped.dae"));
			this.bipedOldTexture = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped.dae"));
			this.bipedAlex = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_slim_arm.dae"));
			this.villagerZombie = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager.dae"));
			this.villagerZombieBody = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager_body.dae"));
			this.creeper = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/creeper.dae"));
			this.enderman = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/enderman.dae"));
			this.skeleton = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/skeleton.dae"));
			this.spider = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/spider.dae"));
			this.ironGolem = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/iron_golem.dae"));
			this.illager = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/illager.dae"));
			this.witch = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/witch.dae"));
			this.ravager = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/ravager.dae"));
			this.vex = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/vex.dae"));
			this.piglin = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/piglin.dae"));
			this.hoglin = new Model(new ResourceLocation(EpicFightMod.MODID, "models/entity/hoglin.dae"));
		}
	}
	
	/** 
	 * 0Root 1Thigh_R 2Leg_R 3Knee_R 4Thigh_L 5Leg_L 6Knee_L 7Torso 8Chest 9Head 10Shoulder_R 11Arm_R 12Hand_R 13Elbow_R 14Tool_R 15Shoulder_L 16Arm_L
	 * 17Hand_L 18Elbow_L 19Tool_L 
	 **/
	public T biped;
	public T bipedOldTexture;
	public T bipedAlex;
	public T villagerZombie;
	public T villagerZombieBody;
	public T creeper;
	public T enderman;
	public T skeleton;
	public T spider;
	public T ironGolem;
	public T illager;
	public T witch;
	public T ravager;
	public T vex;
	public T piglin;
	public T hoglin;
	
	public void loadArmatureData(IResourceManager resourceManager) {
		this.biped.loadArmatureData(resourceManager);
		this.bipedOldTexture.loadArmatureData(this.biped.getArmature());
		this.bipedAlex.loadArmatureData(this.biped.getArmature());
		this.villagerZombie.loadArmatureData(this.biped.getArmature());
		this.creeper.loadArmatureData(resourceManager);
		this.skeleton.loadArmatureData(resourceManager);
		this.enderman.loadArmatureData(resourceManager);
		this.spider.loadArmatureData(resourceManager);
		this.ironGolem.loadArmatureData(resourceManager);
		this.ravager.loadArmatureData(resourceManager);
		this.vex.loadArmatureData(resourceManager);
		this.piglin.loadArmatureData(resourceManager);
		this.illager.loadArmatureData(this.biped.getArmature());
		this.witch.loadArmatureData(this.biped.getArmature());
		this.hoglin.loadArmatureData(resourceManager);
	}
}