package yesman.epicfight.client.model;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.Lists;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.main.EpicFightMod;

public class ClientModels extends Models<ClientModel> implements IFutureReloadListener {
	public static final ClientModels LOGICAL_CLIENT = new ClientModels();
	public final List<ClientModel> registeredModels = Lists.newArrayList();
	public final ClientModel playerFirstPerson;
	public final ClientModel playerFirstPersonAlex;
	public final ClientModel drownedOuterLayer;
	public final ClientModel helmet;
	public final ClientModel chestplate;
	public final ClientModel leggins;
	public final ClientModel robe;
	public final ClientModel boots;
	public final ClientModel endermanEye;
	public final ClientModel spiderEye;
	
	public ClientModels() {
		this.biped = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped.dae")));
		this.bipedOldTexture = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_old_texture.dae")));
		this.bipedAlex = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_slim_arm.dae")));
		this.villagerZombie = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager.dae")));
		this.villagerZombieBody = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/zombie_villager_body.dae")));
		this.creeper = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/creeper.dae")));
		this.enderman = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/enderman.dae")));
		this.skeleton = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/skeleton.dae")));
		this.spider = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/spider.dae")));
		this.ironGolem = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/iron_golem.dae")));
		this.illager = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/illager.dae")));
		this.witch = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/witch.dae")));
		this.ravager = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/ravager.dae")));
		this.vex = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/vex.dae")));
		this.piglin = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/piglin.dae")));
		this.hoglin = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/hoglin.dae")));
		this.playerFirstPerson = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_firstperson.dae")));
		this.playerFirstPersonAlex = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_firstperson_slim.dae")));
		this.drownedOuterLayer = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/biped_outer_layer.dae")));
		this.endermanEye = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/enderman_face.dae")));
		this.spiderEye = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/entity/spider_face.dae")));
		this.helmet = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_helmet.dae")));
		this.chestplate = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_chestplate.dae")));
		this.leggins = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_leggins.dae")));
		this.robe = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_leggins_cloth.dae")));
		this.boots = register(new ClientModel(new ResourceLocation(EpicFightMod.MODID, "models/item/armor/armor_boots.dae")));
	}
	
	public void loadMeshData(IResourceManager resourceManager) {
		this.registeredModels.forEach((model) -> {
			model.loadMeshData(resourceManager);
		});
	}
	
	public ClientModel register(ClientModel model) {
		this.registeredModels.add(model);
		return model;
	}
	
	@Override
	public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> {
			this.loadMeshData(resourceManager);
			this.loadArmatureData(resourceManager);
		}, gameExecutor).thenCompose(stage::markCompleteAwaitingOthers);
	}
}