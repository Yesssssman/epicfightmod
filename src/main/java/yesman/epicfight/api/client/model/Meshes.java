package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.client.model.AnimatedMesh.AnimatedModelPart;
import yesman.epicfight.api.client.model.Mesh.RenderProperties;
import yesman.epicfight.api.client.model.RawMesh.RawModelPart;
import yesman.epicfight.api.forgeevent.ModelBuildEvent;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.client.mesh.CreeperMesh;
import yesman.epicfight.client.mesh.DragonMesh;
import yesman.epicfight.client.mesh.EndermanMesh;
import yesman.epicfight.client.mesh.HoglinMesh;
import yesman.epicfight.client.mesh.HumanoidMesh;
import yesman.epicfight.client.mesh.IronGolemMesh;
import yesman.epicfight.client.mesh.PiglinMesh;
import yesman.epicfight.client.mesh.RavagerMesh;
import yesman.epicfight.client.mesh.SpiderMesh;
import yesman.epicfight.client.mesh.VexMesh;
import yesman.epicfight.client.mesh.VillagerMesh;
import yesman.epicfight.client.mesh.WitherMesh;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class Meshes implements PreparableReloadListener {
	public static final Meshes INSTANCE = new Meshes();
	
	@FunctionalInterface
	public interface MeshContructor<P extends ModelPart<V>, V extends VertexBuilder, M extends Mesh<P, V>> {
		M invoke(Map<String, float[]> arrayMap, Map<String, List<V>> parts, M parent, RenderProperties properties);
	}
	
	private static final BiMap<ResourceLocation, Mesh<?, ?>> MESHES = HashBiMap.create();
	
	public static HumanoidMesh ALEX;
	public static HumanoidMesh BIPED;
	public static HumanoidMesh BIPED_OLD_TEX;
	public static HumanoidMesh BIPED_OUTLAYER;
	public static HumanoidMesh VILLAGER_ZOMBIE;
	public static CreeperMesh CREEPER;
	public static EndermanMesh ENDERMAN;
	public static HumanoidMesh SKELETON;
	public static SpiderMesh SPIDER;
	public static IronGolemMesh IRON_GOLEM;
	public static HumanoidMesh ILLAGER;
	public static VillagerMesh WITCH;
	public static RavagerMesh RAVAGER;
	public static VexMesh VEX;
	public static PiglinMesh PIGLIN;
	public static HoglinMesh HOGLIN;
	public static DragonMesh DRAGON;
	public static WitherMesh WITHER;
	
	public static AnimatedMesh HELMET;
	public static AnimatedMesh HELMET_PIGLIN;
	public static AnimatedMesh HELMET_VILLAGER;
	public static AnimatedMesh CHESTPLATE;
	public static AnimatedMesh LEGGINS;
	public static AnimatedMesh BOOTS;
	
	public static RawMesh AIR_BURST;
	public static RawMesh FORCE_FIELD;
	public static RawMesh LASER;
	
	public static void build(ResourceManager resourceManager) {
		MESHES.values().stream().filter((mesh) -> mesh instanceof AnimatedMesh).map((mesh) -> (AnimatedMesh)mesh).forEach(AnimatedMesh::destroy);
		MESHES.clear();
		ModelBuildEvent.MeshBuild event = new ModelBuildEvent.MeshBuild(resourceManager, MESHES);
		
		//Entities
		ALEX = event.getAnimated(EpicFightMod.MODID, "entity/biped_slim_arm", HumanoidMesh::new);
		BIPED = event.getAnimated(EpicFightMod.MODID, "entity/biped", HumanoidMesh::new);
		BIPED_OLD_TEX = event.getAnimated(EpicFightMod.MODID, "entity/biped_old_texture", HumanoidMesh::new);
		BIPED_OUTLAYER = event.getAnimated(EpicFightMod.MODID, "entity/biped_outlayer", HumanoidMesh::new);
		VILLAGER_ZOMBIE = event.getAnimated(EpicFightMod.MODID, "entity/zombie_villager", VillagerMesh::new);
		CREEPER = event.getAnimated(EpicFightMod.MODID, "entity/creeper", CreeperMesh::new);
		ENDERMAN = event.getAnimated(EpicFightMod.MODID, "entity/enderman", EndermanMesh::new);
		SKELETON = event.getAnimated(EpicFightMod.MODID, "entity/skeleton", HumanoidMesh::new);
		SPIDER = event.getAnimated(EpicFightMod.MODID, "entity/spider", SpiderMesh::new);
		IRON_GOLEM = event.getAnimated(EpicFightMod.MODID, "entity/iron_golem", IronGolemMesh::new);
		ILLAGER = event.getAnimated(EpicFightMod.MODID, "entity/illager", VillagerMesh::new);
		WITCH = event.getAnimated(EpicFightMod.MODID, "entity/witch", VillagerMesh::new);
		RAVAGER = event.getAnimated(EpicFightMod.MODID, "entity/ravager", RavagerMesh::new);
		VEX = event.getAnimated(EpicFightMod.MODID, "entity/vex", VexMesh::new);
		PIGLIN = event.getAnimated(EpicFightMod.MODID, "entity/piglin", PiglinMesh::new);
		HOGLIN = event.getAnimated(EpicFightMod.MODID, "entity/hoglin", HoglinMesh::new);
		DRAGON = event.getAnimated(EpicFightMod.MODID, "entity/dragon", DragonMesh::new);
		WITHER = event.getAnimated(EpicFightMod.MODID, "entity/wither", WitherMesh::new);
		
		//Particles
		AIR_BURST = event.getRaw(EpicFightMod.MODID, "particle/air_burst", RawMesh::new);
		FORCE_FIELD = event.getRaw(EpicFightMod.MODID, "particle/force_field", RawMesh::new);
		LASER = event.getRaw(EpicFightMod.MODID, "particle/laser", RawMesh::new);
		
		//Armors
		HELMET = event.getAnimated(EpicFightMod.MODID, "armor/helmet", AnimatedMesh::new);
		HELMET_PIGLIN = event.getAnimated(EpicFightMod.MODID, "armor/piglin_helmet", AnimatedMesh::new);
		HELMET_VILLAGER = event.getAnimated(EpicFightMod.MODID, "armor/villager_helmet", AnimatedMesh::new);
		CHESTPLATE = event.getAnimated(EpicFightMod.MODID, "armor/chestplate", AnimatedMesh::new);
		LEGGINS = event.getAnimated(EpicFightMod.MODID, "armor/leggins", AnimatedMesh::new);
		BOOTS = event.getAnimated(EpicFightMod.MODID, "armor/boots", AnimatedMesh::new);
		
		ModLoader.get().postEvent(event);
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends RawMesh> M getOrCreateRawMesh(ResourceManager rm, ResourceLocation rl, MeshContructor<RawModelPart, VertexBuilder, M> constructor) {
		return (M) MESHES.computeIfAbsent(rl, (key) -> {
			JsonModelLoader jsonModelLoader = new JsonModelLoader(rm, wrapLocation(rl));
			return jsonModelLoader.loadMesh(constructor);
		});
	}
	
	@SuppressWarnings("unchecked")
	public static <M extends AnimatedMesh> M getOrCreateAnimatedMesh(ResourceManager rm, ResourceLocation rl, MeshContructor<AnimatedModelPart, AnimatedVertexBuilder, M> constructor) {
		return (M) MESHES.computeIfAbsent(rl, (key) -> {
			JsonModelLoader jsonModelLoader = new JsonModelLoader(rm, wrapLocation(rl));
			return jsonModelLoader.loadAnimatedMesh(constructor);
		});
	}
	
	public static ResourceLocation getKey(Mesh<?, ?> mesh) {
		return MESHES.inverse().get(mesh);
	}
	
	public static Mesh<?, ?> getMeshOrNull(ResourceLocation rl) {
		return MESHES.get(rl);
	}
	
	public static void addMesh(ResourceLocation rl, Mesh<?, ?> mesh) {
		MESHES.put(rl, mesh);
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Mesh<?, ?>> Set<Pair<ResourceLocation, MeshProvider<T>>> entries(Class<T> filterInstance) {
		return MESHES.entrySet().stream().filter((entry) -> filterInstance.isAssignableFrom(entry.getValue().getClass())).map((entry) -> Pair.of(entry.getKey(), (MeshProvider<T>)() -> (T)MESHES.get(entry.getKey()))).collect(Collectors.toSet());
	}
	
	public static ResourceLocation wrapLocation(ResourceLocation rl) {
		return rl.getPath().matches("animmodels/.*\\.json") ? rl : new ResourceLocation(rl.getNamespace(), "animmodels/" + rl.getPath() + ".json");
	}
	
	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> {
			Meshes.build(resourceManager);
		}, gameExecutor).thenCompose(stage::wait);
	}
}