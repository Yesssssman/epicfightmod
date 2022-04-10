package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.main.EpicFightMod;

public class ClientModels extends Models<ClientModel> implements PreparableReloadListener {
	public static final ClientModels LOGICAL_CLIENT = new ClientModels();
	public final List<ClientModel> registeredModels = Lists.newArrayList();
	/** Entity Models **/
	public final ClientModel playerFirstPerson;
	public final ClientModel playerFirstPersonAlex;
	public final ClientModel drownedOuterLayer;
	public final ClientModel helmet;
	public final ClientModel chestplate;
	public final ClientModel leggins;
	public final ClientModel boots;
	public final ClientModel endermanEye;
	public final ClientModel spiderEye;
	/** Particle Models **/
	public final ClientModel forceField;
	public final ClientModel laser;
	
	public ClientModels() {
		this.biped = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/biped")));
		this.bipedOldTexture = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/biped_old_texture")));
		this.bipedAlex = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/biped_slim_arm")));
		this.villagerZombie = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/zombie_villager")));
		this.villagerZombieBody = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/zombie_villager_body")));
		this.creeper = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/creeper")));
		this.enderman = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/enderman")));
		this.skeleton = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/skeleton")));
		this.spider = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/spider")));
		this.ironGolem = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/iron_golem")));
		this.illager = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/illager")));
		this.witch = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/witch")));
		this.ravager = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/ravager")));
		this.vex = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/vex")));
		this.piglin = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/piglin")));
		this.hoglin = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/hoglin")));
		this.playerFirstPerson = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/biped_firstperson")));
		this.playerFirstPersonAlex = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/biped_firstperson_slim")));
		this.drownedOuterLayer = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/biped_outer_layer")));
		this.endermanEye = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/enderman_face")));
		this.spiderEye = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/spider_face")));
		this.helmet = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "item/armor/armor_helmet")));
		this.chestplate = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "item/armor/armor_chestplate")));
		this.leggins = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "item/armor/armor_leggins")));
		this.boots = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "item/armor/armor_boots")));
		this.dragon = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/dragon")));
		this.wither = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "entity/wither")));
		
		this.forceField = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "particle/force_field")));
		this.laser = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "particle/laser")));
	}
	
	public void loadMeshData(ResourceManager resourceManager) {
		this.registeredModels.forEach((model) -> {
			model.loadMeshData(resourceManager);
		});
	}
	
	public ClientModel register(ClientModel model) {
		this.registeredModels.add(model);
		return model;
	}
	
	@Override
	public Models<?> getModelContainer(boolean isLogicalClient) {
		return isLogicalClient ? LOGICAL_CLIENT : LOGICAL_SERVER;
	}
	
	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> {
			this.loadMeshData(resourceManager);
			this.loadArmatureData(resourceManager);
		}, gameExecutor).thenCompose(stage::wait);
	}
}