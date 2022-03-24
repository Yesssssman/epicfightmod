package yesman.epicfight.gameasset;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.main.EpicFightMod;

public abstract class Models<T extends Model> {
	public static final ServerModels LOGICAL_SERVER = new ServerModels();
	public static class ServerModels extends Models<Model> {
		public ServerModels() {
			this.biped = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/biped"));
			this.bipedOldTexture = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/biped"));
			this.bipedAlex = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/biped_slim_arm"));
			this.villagerZombie = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/zombie_villager"));
			this.villagerZombieBody = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/zombie_villager_body"));
			this.creeper = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/creeper"));
			this.enderman = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/enderman"));
			this.skeleton = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/skeleton"));
			this.spider = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/spider"));
			this.ironGolem = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/iron_golem"));
			this.illager = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/illager"));
			this.witch = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/witch"));
			this.ravager = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/ravager"));
			this.vex = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/vex"));
			this.piglin = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/piglin"));
			this.hoglin = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/hoglin"));
			this.dragon = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/dragon"));
			this.wither = new Model(new ResourceLocation(EpicFightMod.MODID, "entity/wither"));
		}
		
		@Override
		public Models<?> getModelContainer(boolean isLogicalClient) {
			return LOGICAL_SERVER;
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
	public T dragon;
	public T wither;
	
	public void loadArmatureData(ResourceManager resourceManager) {
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
		this.dragon.loadArmatureData(resourceManager);
		this.wither.loadArmatureData(resourceManager);
	}
	
	public abstract Models<?> getModelContainer(boolean isLogicalClient);
}