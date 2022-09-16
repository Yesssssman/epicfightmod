package yesman.epicfight.world.entity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightEntities {
	public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, EpicFightMod.MODID);
	
	public static final RegistryObject<EntityType<AreaEffectBreath>> AREA_EFFECT_BREATH = ENTITIES.register("area_effect_breath", () ->
		EntityType.Builder.<AreaEffectBreath>of(AreaEffectBreath::new, EntityClassification.MISC)
			.fireImmune().sized(6.0F, 0.5F).clientTrackingRange(10).updateInterval(Integer.MAX_VALUE).noSummon().build("area_effect_breath")
		);
	
	public static final RegistryObject<EntityType<DroppedNetherStar>> DROPPED_NETHER_STAR = ENTITIES.register("dropped_nether_star", () ->
		EntityType.Builder.<DroppedNetherStar>of(DroppedNetherStar::new, EntityClassification.MISC)
			.sized(0.25F, 0.25F).clientTrackingRange(6).updateInterval(20).noSummon().build("dropped_nether_star")
		);
	
	public static final RegistryObject<EntityType<WitherSkeletonMinion>> WITHER_SKELETON_MINION = ENTITIES.register("wither_skeleton_minion", () ->
		EntityType.Builder.<WitherSkeletonMinion>of(WitherSkeletonMinion::new, EntityClassification.MONSTER)
			.fireImmune().immuneTo(Blocks.WITHER_ROSE).sized(0.7F, 2.4F).clientTrackingRange(8).build("wither_skeleton_minion")
		);
	
	public static final RegistryObject<EntityType<WitherGhostClone>> WITHER_GHOST_CLONE = ENTITIES.register("wither_ghost", () -> 
		EntityType.Builder.<WitherGhostClone>of(WitherGhostClone::new, EntityClassification.MONSTER)
			.fireImmune().sized(0.9F, 3.5F).clientTrackingRange(10).build("wither_ghost")
		);
	
	public static void registerSpawnPlacements() {
		EntitySpawnPlacementRegistry.register(WITHER_SKELETON_MINION.get(), EntitySpawnPlacementRegistry.PlacementType.ON_GROUND,
				Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, MonsterEntity::checkAnyLightMonsterSpawnRules);
	}
}