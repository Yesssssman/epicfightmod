package yesman.epicfight.capabilities.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.CapabilityEntity;
import yesman.epicfight.capabilities.entity.mob.CaveSpiderData;
import yesman.epicfight.capabilities.entity.mob.CreeperData;
import yesman.epicfight.capabilities.entity.mob.DrownedData;
import yesman.epicfight.capabilities.entity.mob.EndermanData;
import yesman.epicfight.capabilities.entity.mob.EvokerData;
import yesman.epicfight.capabilities.entity.mob.HoglinData;
import yesman.epicfight.capabilities.entity.mob.IronGolemData;
import yesman.epicfight.capabilities.entity.mob.PiglinBruteData;
import yesman.epicfight.capabilities.entity.mob.PiglinData;
import yesman.epicfight.capabilities.entity.mob.PillagerData;
import yesman.epicfight.capabilities.entity.mob.RavagerData;
import yesman.epicfight.capabilities.entity.mob.SkeletonData;
import yesman.epicfight.capabilities.entity.mob.SpiderData;
import yesman.epicfight.capabilities.entity.mob.StrayData;
import yesman.epicfight.capabilities.entity.mob.VexData;
import yesman.epicfight.capabilities.entity.mob.VindicatorData;
import yesman.epicfight.capabilities.entity.mob.WitchData;
import yesman.epicfight.capabilities.entity.mob.WitherSkeletonData;
import yesman.epicfight.capabilities.entity.mob.ZoglinData;
import yesman.epicfight.capabilities.entity.mob.ZombieData;
import yesman.epicfight.capabilities.entity.mob.ZombieVillagerData;
import yesman.epicfight.capabilities.entity.mob.ZombifiedPiglinData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.capabilites.player.RemoteClientPlayerData;
import yesman.epicfight.config.CapabilityConfig;
import yesman.epicfight.config.CapabilityConfig.CustomEntityConfig;
import yesman.epicfight.main.EpicFightMod;

public class ProviderEntity implements ICapabilityProvider, NonNullSupplier<CapabilityEntity<?>> {
	private static final Map<EntityType<?>, Function<Entity, Supplier<CapabilityEntity<?>>>> CAPABILITY_MAP = new HashMap<EntityType<?>, Function<Entity, Supplier<CapabilityEntity<?>>>> ();
	
	public static void makeMap() {
		CAPABILITY_MAP.put(EntityType.PLAYER, (entityIn) -> ServerPlayerData::new);
		CAPABILITY_MAP.put(EntityType.ZOMBIE, (entityIn) -> ZombieData<ZombieEntity>::new);
		CAPABILITY_MAP.put(EntityType.CREEPER, (entityIn) -> CreeperData::new);
		CAPABILITY_MAP.put(EntityType.ENDERMAN, (entityIn) -> EndermanData::new);
		CAPABILITY_MAP.put(EntityType.SKELETON, (entityIn) -> SkeletonData<SkeletonEntity>::new);
		CAPABILITY_MAP.put(EntityType.WITHER_SKELETON, (entityIn) -> WitherSkeletonData::new);
		CAPABILITY_MAP.put(EntityType.STRAY, (entityIn) -> StrayData::new);
		CAPABILITY_MAP.put(EntityType.ZOMBIFIED_PIGLIN, (entityIn) -> ZombifiedPiglinData::new);
		CAPABILITY_MAP.put(EntityType.ZOMBIE_VILLAGER, (entityIn) -> ZombieVillagerData::new);
		CAPABILITY_MAP.put(EntityType.HUSK, (entityIn) -> ZombieData<HuskEntity>::new);
		CAPABILITY_MAP.put(EntityType.SPIDER, (entityIn) -> SpiderData::new);
		CAPABILITY_MAP.put(EntityType.CAVE_SPIDER, (entityIn) -> CaveSpiderData::new);
		CAPABILITY_MAP.put(EntityType.IRON_GOLEM, (entityIn) -> IronGolemData::new);
		CAPABILITY_MAP.put(EntityType.VINDICATOR, (entityIn) -> VindicatorData::new);
		CAPABILITY_MAP.put(EntityType.EVOKER, (entityIn) -> EvokerData::new);
		CAPABILITY_MAP.put(EntityType.WITCH, (entityIn) -> WitchData::new);
		CAPABILITY_MAP.put(EntityType.DROWNED, (entityIn) -> DrownedData::new);
		CAPABILITY_MAP.put(EntityType.PILLAGER, (entityIn) -> PillagerData::new);
		CAPABILITY_MAP.put(EntityType.RAVAGER, (entityIn) -> RavagerData::new);
		CAPABILITY_MAP.put(EntityType.VEX, (entityIn) -> VexData::new);
		CAPABILITY_MAP.put(EntityType.PIGLIN, (entityIn) -> PiglinData::new);
		CAPABILITY_MAP.put(EntityType.field_242287_aj, (entityIn) -> PiglinBruteData::new);
		CAPABILITY_MAP.put(EntityType.HOGLIN, (entityIn) -> HoglinData::new);
		CAPABILITY_MAP.put(EntityType.ZOGLIN, (entityIn) -> ZoglinData::new);
		makeConfigEntities();
	}
	
	public static void makeMapClient() {
		CAPABILITY_MAP.put(EntityType.PLAYER, (entityIn)->{
			if (entityIn instanceof ClientPlayerEntity) {
				return ClientPlayerData::new;
			} else if (entityIn instanceof RemoteClientPlayerEntity) {
				return RemoteClientPlayerData<RemoteClientPlayerEntity>::new;
			} else if (entityIn instanceof ServerPlayerEntity) {
				return ServerPlayerData::new;
			} else {
				return () -> null;
			}
		});
	}
	
	public static void makeConfigEntities() {
		for (Map.Entry<ResourceLocation, CustomEntityConfig> config : CapabilityConfig.CUSTOM_ENTITY_MAP.entrySet()) {
			if (ForgeRegistries.ENTITIES.containsKey(config.getKey())) {
				CAPABILITY_MAP.put(ForgeRegistries.ENTITIES.getValue(config.getKey()), (entityIn) -> config.getValue().getEntityAIType().getCapability());
			} else {
				EpicFightMod.LOGGER.warn("Invalid entity type " + config.getKey());
			}
		}
	}
	
	private CapabilityEntity<?> capability;
	private LazyOptional<CapabilityEntity<?>> optional = LazyOptional.of(this);
	
	public ProviderEntity(Entity entity) {
		if (CAPABILITY_MAP.containsKey(entity.getType())) {
			this.capability = CAPABILITY_MAP.get(entity.getType()).apply(entity).get();
		}
	}
	
	public boolean hasCapability() {
		return capability != null;
	}
	
	@Override
	public CapabilityEntity<?> get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_ENTITY ? this.optional.cast() :  LazyOptional.empty();
	}
}