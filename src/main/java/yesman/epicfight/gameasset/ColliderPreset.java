package yesman.epicfight.gameasset;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.main.EpicFightMod;

public class ColliderPreset implements PreparableReloadListener {
	private static final BiMap<ResourceLocation, Collider> PRESETS = HashBiMap.create();
	
	public static Collider registerCollider(ResourceLocation rl, Collider collider) {
		if (PRESETS.containsKey(rl)) {
			throw new IllegalStateException("Collider named " + rl + " already registered.");
		}
		
		PRESETS.put(rl, collider);
		
		return collider;
	}
	
	public static Set<Map.Entry<ResourceLocation, Collider>> entries() {
		return Collections.unmodifiableSet(PRESETS.entrySet());
	}
	
	public static ResourceLocation getKey(Collider collider) {
		return PRESETS.inverse().get(collider);
	}
	
	public static Collider get(ResourceLocation rl) {
		return PRESETS.get(rl);
	}
	
	public static final Collider DAGGER = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dagger"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.6D, 0.0D, 0.0D, -0.1D));
	public static final Collider DUAL_DAGGER_DASH = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dual_dagger_dash"), new OBBCollider(0.8D, 0.5D, 1.0D, 0.0D, 1.0D, -0.6D));
	public static final Collider BIPED_BODY_COLLIDER = registerCollider(new ResourceLocation(EpicFightMod.MODID, "biped_body_collider"), new MultiOBBCollider(
			new OBBCollider(0.8D, 0.5D, 1.0D, 0.0D, 1.0D, -0.6D),
			new OBBCollider(0.8D, 0.5D, 1.0D, 0.0D, 1.0D, -0.6D)
		));
	public static final Collider DRAGON_BODY = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dragon_body"), new OBBCollider(2.0D, 1.5D, 4.0D, 0.0D, 1.5D, -0.5D));
	public static final Collider DRAGON_LEG = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dragon_leg"), new MultiOBBCollider(3, 0.8D, 1.6D, 0.8D, 0.0D, -0.6D, 0.7D));
	public static final Collider DUAL_SWORD = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dual_sword"), new OBBCollider(0.8D, 0.5D, 1.0D, 0.0D, 0.5D, -1.0D));
	public static final Collider DUAL_SWORD_DASH = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dual_sword_dash"), new OBBCollider(0.8D, 0.5D, 1.0D, 0D, 1.0D, -1.0D));
	public static final Collider BATTOJUTSU = registerCollider(new ResourceLocation(EpicFightMod.MODID, "battojutsu"), new OBBCollider(2.5D, 0.25D, 1.5D, 0D, 1.0D, -1.0D));
	public static final Collider BATTOJUTSU_DASH = registerCollider(new ResourceLocation(EpicFightMod.MODID, "battojutsu_dash"), new MultiOBBCollider(
			new OBBCollider(0.7D, 0.7D, 1.0D, 0.0D, 1.0D, -1.0D),
			new OBBCollider(0.7D, 0.7D, 1.0D, 0.0D, 1.0D, -1.0D),
			new OBBCollider(0.7D, 0.7D, 1.0D, 0.0D, 1.0D, -1.0D),
			new OBBCollider(0.7D, 0.7D, 1.0D, 0.0D, 1.0D, -1.0D),
			new OBBCollider(1.5D, 0.7D, 1.0D, 0.0D, 1.0D, -1.0D)
		));
	public static final Collider FIST = registerCollider(new ResourceLocation(EpicFightMod.MODID, "fist"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.4D, 0D, 0D, 0D));
	public static final Collider GREATSWORD = registerCollider(new ResourceLocation(EpicFightMod.MODID, "greatsword"), new MultiOBBCollider(3, 0.5D, 0.8D, 1.0D, 0D, 0D, -1.0D));
	public static final Collider HEAD = registerCollider(new ResourceLocation(EpicFightMod.MODID, "head"), new OBBCollider(0.4D, 0.4D, 0.4D, 0D, 0D, -0.3D));
	public static final Collider HEADBUTT_RAVAGER = registerCollider(new ResourceLocation(EpicFightMod.MODID, "headbutt_ravager"), new OBBCollider(0.8D, 0.8D, 0.8D, 0D, 0D, -0.3D));
	public static final Collider UCHIGATANA = registerCollider(new ResourceLocation(EpicFightMod.MODID, "uchigatana"), new MultiOBBCollider(5, 0.4D, 0.4D, 0.7D, 0D, 0D, -0.7D));
	public static final Collider TACHI = registerCollider(new ResourceLocation(EpicFightMod.MODID, "tachi"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.95D, 0D, 0D, -0.95D));
	public static final Collider SWORD = registerCollider(new ResourceLocation(EpicFightMod.MODID, "sword"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.7D, 0D, 0D, -0.35D));
	public static final Collider LONGSWORD = registerCollider(new ResourceLocation(EpicFightMod.MODID, "longsword"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.8D, 0D, 0D, -0.75D));
	public static final Collider SPEAR = registerCollider(new ResourceLocation(EpicFightMod.MODID, "spear"), new MultiOBBCollider(3, 0.6D, 0.6D, 1.0D, 0D, 0D, -1.0D));
	public static final Collider SPIDER = registerCollider(new ResourceLocation(EpicFightMod.MODID, "spider"), new OBBCollider(0.8D, 0.8D, 0.8D, 0D, 0D, -0.4D));
	public static final Collider TOOLS = registerCollider(new ResourceLocation(EpicFightMod.MODID, "tools"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.55D, 0D, 0.0D, -0.25D));
	public static final Collider ENDERMAN_LIMB = registerCollider(new ResourceLocation(EpicFightMod.MODID, "enderman_limb"), new OBBCollider(0.4D, 0.8D, 0.4D, 0D, 0D, 0D));
	public static final Collider GOLEM_SMASHDOWN = registerCollider(new ResourceLocation(EpicFightMod.MODID, "golem_smashdown"), new MultiOBBCollider(3, 0.75D, 0.5D, 0.5D, 0.6D, 0.5D, 0D));
	public static final Collider GOLEM_SWING_ARM = registerCollider(new ResourceLocation(EpicFightMod.MODID, "golem_swing_arm"), new MultiOBBCollider(2, 0.6D, 0.9D, 0.6D, 0D, 0D, 0D));
	public static final Collider FIST_FIXED = registerCollider(new ResourceLocation(EpicFightMod.MODID, "fist_fixed"), new OBBCollider(0.4D, 0.4D, 0.5D, 0D, 1.0D, -0.85D));
	public static final Collider DUAL_SWORD_AIR_SLASH = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dual_sword_air_slash"), new OBBCollider(0.8D, 0.4D, 1.0D, 0D, 0.5D, -0.5D));
	public static final Collider DUAL_DAGGER_AIR_SLASH = registerCollider(new ResourceLocation(EpicFightMod.MODID, "dual_dagger_air_slash"), new OBBCollider(0.8D, 0.4D, 0.75D, 0D, 0.5D, -0.5D));
	public static final Collider WITHER_CHARGE = registerCollider(new ResourceLocation(EpicFightMod.MODID, "wither_charge"), new MultiOBBCollider(5, 0.7D, 0.9D, 0.7D, 0D, 1.0D, -0.35D));
	public static final Collider VEX_CHARGE = registerCollider(new ResourceLocation(EpicFightMod.MODID, "vex_charge"), new MultiOBBCollider(3, 0.4D, 0.4D, 0.95D, 0D, 1.0D, -0.85D));
	
	public static Collider deserializeSimpleCollider(CompoundTag tag) {
		int number = tag.getInt("number");
		
		if (number < 1) {
			throw new IllegalArgumentException("Datapack deserialization error: the number of colliders must bigger than 0!");
		}
		
		ListTag sizeVector = tag.getList("size", 6);
		ListTag centerVector = tag.getList("center", 6);
		
		double sizeX = sizeVector.getDouble(0);
		double sizeY = sizeVector.getDouble(1);
		double sizeZ = sizeVector.getDouble(2);
		
		double centerX = centerVector.getDouble(0);
		double centerY = centerVector.getDouble(1);
		double centerZ = centerVector.getDouble(2);
		
		if (sizeX < 0 || sizeY < 0 || sizeZ < 0) {
			throw new IllegalArgumentException("Datapack deserialization error: the size of the collider must be non-negative value!");
		}
		
		if (number == 1) {
			return new OBBCollider(sizeX, sizeY, sizeZ, centerX, centerY, centerZ);
		} else {
			return new MultiOBBCollider(number, sizeX, sizeY, sizeZ, centerX, centerY, centerZ);
		}
	}
	
	@Override
	public CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
		return CompletableFuture.runAsync(() -> {
			//Collider newCOllider = new OBBCollider(0.7D, 0.7D, 3.5D, 0D, 1.0D, -3.5D);
			//((AttackAnimation)Animations.FATAL_DRAW_DASH).changeCollider(newCOllider, 0);
		}, gameExecutor).thenCompose(stage::wait);
	}
}