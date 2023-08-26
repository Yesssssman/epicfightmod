package yesman.epicfight.gameasset;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.forgeevent.ModelBuildEvent;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.armature.CreeperArmature;
import yesman.epicfight.model.armature.DragonArmature;
import yesman.epicfight.model.armature.EndermanArmature;
import yesman.epicfight.model.armature.HoglinArmature;
import yesman.epicfight.model.armature.HumanoidArmature;
import yesman.epicfight.model.armature.IronGolemArmature;
import yesman.epicfight.model.armature.PiglinArmature;
import yesman.epicfight.model.armature.RavagerArmature;
import yesman.epicfight.model.armature.SpiderArmature;
import yesman.epicfight.model.armature.VexArmature;
import yesman.epicfight.model.armature.WitherArmature;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.entity.EpicFightEntities;

public class Armatures implements PreparableReloadListener {
	
	public static final Armatures INSTANCE = new Armatures();
	
	@FunctionalInterface
	public static interface ArmatureContructor<T extends Armature> {		
		public T invoke(int jointNumber, Joint joint, Map<String, Joint> jointMap);
	}
	
	private static final Map<ResourceLocation, Armature> ARMATURES = Maps.newHashMap();
	private static final Map<EntityType<?>, Function<EntityPatch<?>, Armature>> ENTITY_TYPE_ARMATURE = Maps.newHashMap();
	
	public static HumanoidArmature BIPED;
	public static CreeperArmature CREEPER;
	public static EndermanArmature ENDERMAN;
	public static HumanoidArmature SKELETON;
	public static SpiderArmature SPIDER;
	public static IronGolemArmature IRON_GOLEM;
	public static RavagerArmature RAVAGER;
	public static VexArmature VEX;
	public static PiglinArmature PIGLIN;
	public static HoglinArmature HOGLIN;
	public static DragonArmature DRAGON;
	public static WitherArmature WITHER;
	
	public static void build(ResourceManager resourceManager) {
		ARMATURES.clear();
		ModelBuildEvent.ArmatureBuild event = new ModelBuildEvent.ArmatureBuild(resourceManager, ARMATURES);
		
		BIPED = event.get(EpicFightMod.MODID, "entity/biped", HumanoidArmature::new);
		CREEPER = event.get(EpicFightMod.MODID, "entity/creeper", CreeperArmature::new);
		ENDERMAN = event.get(EpicFightMod.MODID, "entity/enderman", EndermanArmature::new);
		SKELETON = event.get(EpicFightMod.MODID, "entity/skeleton", HumanoidArmature::new);
		SPIDER = event.get(EpicFightMod.MODID, "entity/spider", SpiderArmature::new);
		IRON_GOLEM = event.get(EpicFightMod.MODID, "entity/iron_golem", IronGolemArmature::new);
		RAVAGER = event.get(EpicFightMod.MODID, "entity/ravager", RavagerArmature::new);
		VEX = event.get(EpicFightMod.MODID, "entity/vex", VexArmature::new);
		PIGLIN = event.get(EpicFightMod.MODID, "entity/piglin", PiglinArmature::new);
		HOGLIN = event.get(EpicFightMod.MODID, "entity/hoglin", HoglinArmature::new);
		DRAGON = event.get(EpicFightMod.MODID, "entity/dragon", DragonArmature::new);
		WITHER = event.get(EpicFightMod.MODID, "entity/wither", WitherArmature::new);
		
		registerEntityTypeArmature(EntityType.CAVE_SPIDER, SPIDER);
		registerEntityTypeArmature(EntityType.CREEPER, CREEPER);
		registerEntityTypeArmature(EntityType.DROWNED, BIPED);
		registerEntityTypeArmature(EntityType.ENDERMAN, ENDERMAN);
		registerEntityTypeArmature(EntityType.EVOKER, BIPED);
		registerEntityTypeArmature(EntityType.HOGLIN, HOGLIN);
		registerEntityTypeArmature(EntityType.HUSK, BIPED);
		registerEntityTypeArmature(EntityType.IRON_GOLEM, IRON_GOLEM);
		registerEntityTypeArmature(EntityType.PIGLIN_BRUTE, PIGLIN);
		registerEntityTypeArmature(EntityType.PIGLIN, PIGLIN);
		registerEntityTypeArmature(EntityType.PILLAGER, BIPED);
		registerEntityTypeArmature(EntityType.RAVAGER, RAVAGER);
		registerEntityTypeArmature(EntityType.SKELETON, SKELETON);
		registerEntityTypeArmature(EntityType.SPIDER, SPIDER);
		registerEntityTypeArmature(EntityType.STRAY, SKELETON);
		registerEntityTypeArmature(EntityType.VEX, VEX);
		registerEntityTypeArmature(EntityType.VINDICATOR, BIPED);
		registerEntityTypeArmature(EntityType.WITCH, BIPED);
		registerEntityTypeArmature(EntityType.WITHER_SKELETON, SKELETON);
		registerEntityTypeArmature(EntityType.ZOGLIN, HOGLIN);
		registerEntityTypeArmature(EntityType.ZOMBIE, BIPED);
		registerEntityTypeArmature(EntityType.ZOMBIE_VILLAGER, BIPED);
		registerEntityTypeArmature(EntityType.ZOMBIFIED_PIGLIN, PIGLIN);
		registerEntityTypeArmature(EntityType.PLAYER, BIPED);
		registerEntityTypeArmature(EntityType.ENDER_DRAGON, DRAGON);
		registerEntityTypeArmature(EntityType.WITHER, WITHER);
		registerEntityTypeArmature(EpicFightEntities.WITHER_SKELETON_MINION.get(), SKELETON);
		registerEntityTypeArmature(EpicFightEntities.WITHER_GHOST_CLONE.get(), WITHER);
		
		ModLoader.get().postEvent(event);
	}
	
	public static void registerEntityTypeArmature(EntityType<?> entityType, Armature armature) {
		ENTITY_TYPE_ARMATURE.put(entityType, (entitypatch) -> armature.deepCopy());
	}
	
	public static void registerEntityTypeArmature(EntityType<?> entityType, Function<EntityPatch<?>, Armature> armatureGetFunction) {
		ENTITY_TYPE_ARMATURE.put(entityType, armatureGetFunction);
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Armature> A getArmatureFor(EntityPatch<?> patch) {
		return (A)ENTITY_TYPE_ARMATURE.get(patch.getOriginal().getType()).apply(patch).deepCopy();
	}
	
	@SuppressWarnings("unchecked")
	public static <A extends Armature> A getOrCreateArmature(ResourceManager rm, ResourceLocation rl, ArmatureContructor<A> constructor) {
		return (A) ARMATURES.computeIfAbsent(rl, (key) -> {
			JsonModelLoader jsonModelLoader = new JsonModelLoader(rm, rl);
			return jsonModelLoader.loadArmature(constructor);
		});
	}
	
	public Armature register(ResourceManager rm, ResourceLocation rl) {
		JsonModelLoader modelLoader = new JsonModelLoader(rm, rl);
		Armature armature = modelLoader.loadArmature(Armature::new);
		ARMATURES.put(rl, armature);
		
		return armature;
	}

	@Override
	public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> {
			Armatures.build(resourceManager);
		}, gameExecutor).thenCompose(stage::wait);
	}
}