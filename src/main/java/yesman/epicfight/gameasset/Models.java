package yesman.epicfight.gameasset;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.main.EpicFightMod;

public abstract class Models<T extends Model> {
	public static final ServerModels LOGICAL_SERVER = new ServerModels();
	protected final Map<ResourceLocation, T> models = Maps.newHashMap();
	
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
	
	public static class ServerModels extends Models<Model> {
		public ServerModels() {
			this.biped = register(new ResourceLocation(EpicFightMod.MODID, "entity/biped"));
			this.bipedOldTexture = register(new ResourceLocation(EpicFightMod.MODID, "entity/biped_old_texture"));
			this.bipedAlex = register(new ResourceLocation(EpicFightMod.MODID, "entity/biped_slim_arm"));
			this.villagerZombie = register(new ResourceLocation(EpicFightMod.MODID, "entity/zombie_villager"));
			this.villagerZombieBody = register(new ResourceLocation(EpicFightMod.MODID, "entity/zombie_villager_body"));
			this.creeper = register(new ResourceLocation(EpicFightMod.MODID, "entity/creeper"));
			this.enderman = register(new ResourceLocation(EpicFightMod.MODID, "entity/enderman"));
			this.skeleton = register(new ResourceLocation(EpicFightMod.MODID, "entity/skeleton"));
			this.spider = register(new ResourceLocation(EpicFightMod.MODID, "entity/spider"));
			this.ironGolem = register(new ResourceLocation(EpicFightMod.MODID, "entity/iron_golem"));
			this.illager = register(new ResourceLocation(EpicFightMod.MODID, "entity/illager"));
			this.witch = register(new ResourceLocation(EpicFightMod.MODID, "entity/witch"));
			this.ravager = register(new ResourceLocation(EpicFightMod.MODID, "entity/ravager"));
			this.vex = register(new ResourceLocation(EpicFightMod.MODID, "entity/vex"));
			this.piglin = register(new ResourceLocation(EpicFightMod.MODID, "entity/piglin"));
			this.hoglin = register(new ResourceLocation(EpicFightMod.MODID, "entity/hoglin"));
			this.dragon = register(new ResourceLocation(EpicFightMod.MODID, "entity/dragon"));
			this.wither = register(new ResourceLocation(EpicFightMod.MODID, "entity/wither"));
		}
		
		@Override
		public Models<?> getModels(boolean isLogicalClient) {
			return LOGICAL_SERVER;
		}
		
		@Override
		public Model register(ResourceLocation rl) {
			Model model = new Model(rl);
			this.models.put(rl, model);
			return model;
		}
	}
	
	public abstract T register(ResourceLocation rl);
	
	public T get(ResourceLocation location) {
		return this.models.get(location);
	}
	
	public void loadArmatures(IResourceManager resourceManager) {
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
	
	public abstract Models<?> getModels(boolean isLogicalClient);
}