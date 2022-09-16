package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.Lists;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.main.EpicFightMod;

public class ClientModels extends Models<ClientModel> implements IFutureReloadListener {
	public static final ClientModels LOGICAL_CLIENT = new ClientModels();
	
	/** Entities **/
	public final ClientModel playerFirstPerson;
	public final ClientModel playerFirstPersonAlex;
	public final ClientModel drownedOuterLayer;
	/** Layers **/
	public final ClientModel endermanEye;
	public final ClientModel spiderEye;
	/** Armors **/
	public final ClientModel helmet;
	public final ClientModel chestplate;
	public final ClientModel leggins;
	public final ClientModel boots;
	/** Particles **/
	public final ClientModel forceField;
	public final ClientModel laser;
	
	public ClientModels() {
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
		this.playerFirstPerson = register(new ResourceLocation(EpicFightMod.MODID, "entity/biped_firstperson"));
		this.playerFirstPersonAlex = register(new ResourceLocation(EpicFightMod.MODID, "entity/biped_firstperson_slim"));
		this.drownedOuterLayer = register(new ResourceLocation(EpicFightMod.MODID, "entity/biped_outer_layer"));
		this.endermanEye = register(new ResourceLocation(EpicFightMod.MODID, "entity/enderman_face"));
		this.spiderEye = register(new ResourceLocation(EpicFightMod.MODID, "entity/spider_face"));
		this.dragon = register(new ResourceLocation(EpicFightMod.MODID, "entity/dragon"));
		this.wither = register(new ResourceLocation(EpicFightMod.MODID, "entity/wither"));
		
		this.helmet = register(new ResourceLocation(EpicFightMod.MODID, "armor/helmet_default"));
		this.chestplate = register(new ResourceLocation(EpicFightMod.MODID, "armor/chestplate_default"));
		this.leggins = register(new ResourceLocation(EpicFightMod.MODID, "armor/leggings_default"));
		this.boots = register(new ResourceLocation(EpicFightMod.MODID, "armor/boots_default"));
		
		this.forceField = register(new ResourceLocation(EpicFightMod.MODID, "particle/force_field"));
		this.laser = register(new ResourceLocation(EpicFightMod.MODID, "particle/laser"));
	}
	
	@Override
	public ClientModel register(ResourceLocation rl) {
		ClientModel model = new ClientModel(rl);
		this.register(rl, model);
		return model;
	}
	
	public void register(ResourceLocation rl, ClientModel model) {
		this.models.put(rl, model);
	}
	
	public void loadModels(IResourceManager resourceManager) {
		List<ResourceLocation> emptyResourceLocations = Lists.newArrayList();
		
		this.models.entrySet().forEach((entry) -> {
			if (!entry.getValue().loadMeshAndProperties(resourceManager)) {
				emptyResourceLocations.add(entry.getKey());
			}
		});
		
		emptyResourceLocations.forEach(this.models::remove);
	}
	
	@Override
	public Models<?> getModels(boolean isLogicalClient) {
		return isLogicalClient ? LOGICAL_CLIENT : LOGICAL_SERVER;
	}
	
	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> {
			this.loadModels(resourceManager);
			this.loadArmatures(resourceManager);
		}, gameExecutor).thenCompose(stage::wait);
	}
}