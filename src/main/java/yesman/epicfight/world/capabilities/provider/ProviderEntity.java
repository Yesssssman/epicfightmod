package yesman.epicfight.world.capabilities.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import yesman.epicfight.client.world.capabilites.entitypatch.player.AbstractClientPlayerPatch;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherGhostPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.CaveSpiderPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.CreeperPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.DrownedPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.EvokerPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.HoglinPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.PiglinBrutePatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.PiglinPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.PillagerPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.RavagerPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.SkeletonPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.SpiderPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.StrayPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.VexPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.VindicatorPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitchPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.WitherSkeletonPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZoglinPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombiePatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombieVillagerPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.ZombifiedPiglinPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.EpicFightEntities;

public class ProviderEntity implements ICapabilityProvider, NonNullSupplier<EntityPatch<?>> {
	private static final Map<EntityType<?>, Function<Entity, Supplier<EntityPatch<?>>>> CAPABILITY_MAP = new HashMap<EntityType<?>, Function<Entity, Supplier<EntityPatch<?>>>> ();
	
	public static void registerPatches() {
		CAPABILITY_MAP.put(EntityType.PLAYER, (entityIn) -> ServerPlayerPatch::new);
		CAPABILITY_MAP.put(EntityType.ZOMBIE, (entityIn) -> ZombiePatch<Zombie>::new);
		CAPABILITY_MAP.put(EntityType.CREEPER, (entityIn) -> CreeperPatch::new);
		CAPABILITY_MAP.put(EntityType.ENDERMAN, (entityIn) -> EndermanPatch::new);
		CAPABILITY_MAP.put(EntityType.SKELETON, (entityIn) -> SkeletonPatch<Skeleton>::new);
		CAPABILITY_MAP.put(EntityType.WITHER_SKELETON, (entityIn) -> WitherSkeletonPatch::new);
		CAPABILITY_MAP.put(EntityType.STRAY, (entityIn) -> StrayPatch::new);
		CAPABILITY_MAP.put(EntityType.ZOMBIFIED_PIGLIN, (entityIn) -> ZombifiedPiglinPatch::new);
		CAPABILITY_MAP.put(EntityType.ZOMBIE_VILLAGER, (entityIn) -> ZombieVillagerPatch::new);
		CAPABILITY_MAP.put(EntityType.HUSK, (entityIn) -> ZombiePatch<Husk>::new);
		CAPABILITY_MAP.put(EntityType.SPIDER, (entityIn) -> SpiderPatch::new);
		CAPABILITY_MAP.put(EntityType.CAVE_SPIDER, (entityIn) -> CaveSpiderPatch::new);
		CAPABILITY_MAP.put(EntityType.IRON_GOLEM, (entityIn) -> IronGolemPatch::new);
		CAPABILITY_MAP.put(EntityType.VINDICATOR, (entityIn) -> VindicatorPatch::new);
		CAPABILITY_MAP.put(EntityType.EVOKER, (entityIn) -> EvokerPatch::new);
		CAPABILITY_MAP.put(EntityType.WITCH, (entityIn) -> WitchPatch::new);
		CAPABILITY_MAP.put(EntityType.DROWNED, (entityIn) -> DrownedPatch::new);
		CAPABILITY_MAP.put(EntityType.PILLAGER, (entityIn) -> PillagerPatch::new);
		CAPABILITY_MAP.put(EntityType.RAVAGER, (entityIn) -> RavagerPatch::new);
		CAPABILITY_MAP.put(EntityType.VEX, (entityIn) -> VexPatch::new);
		CAPABILITY_MAP.put(EntityType.PIGLIN, (entityIn) -> PiglinPatch::new);
		CAPABILITY_MAP.put(EntityType.PIGLIN_BRUTE, (entityIn) -> PiglinBrutePatch::new);
		CAPABILITY_MAP.put(EntityType.HOGLIN, (entityIn) -> HoglinPatch::new);
		CAPABILITY_MAP.put(EntityType.ZOGLIN, (entityIn) -> ZoglinPatch::new);
		CAPABILITY_MAP.put(EntityType.ENDER_DRAGON, (entityIn) -> {
			if (entityIn instanceof EnderDragon) {
				return EnderDragonPatch::new;
			}
			return () -> null;
		});
		CAPABILITY_MAP.put(EntityType.WITHER, (entityIn) -> WitherPatch::new);
		
		CAPABILITY_MAP.put(EpicFightEntities.WITHER_SKELETON_MINION.get(), (entityIn) -> WitherSkeletonPatch::new);
		CAPABILITY_MAP.put(EpicFightEntities.WITHER_GHOST_CLONE.get(), (entityIn) -> WitherGhostPatch::new);
	}
	
	public static void makeMapClient() {
		CAPABILITY_MAP.put(EntityType.PLAYER, (entityIn) -> {
			if (entityIn instanceof LocalPlayer) {
				return LocalPlayerPatch::new;
			} else if (entityIn instanceof RemotePlayer) {
				return AbstractClientPlayerPatch<RemotePlayer>::new;
			} else if (entityIn instanceof ServerPlayer) {
				return ServerPlayerPatch::new;
			} else {
				return () -> null;
			}
		});
	}
	
	private EntityPatch<?> capability;
	private LazyOptional<EntityPatch<?>> optional = LazyOptional.of(this);
	
	public ProviderEntity(Entity entity) {
		if (CAPABILITY_MAP.containsKey(entity.getType())) {
			this.capability = CAPABILITY_MAP.get(entity.getType()).apply(entity).get();
		}
	}
	
	public boolean hasCapability() {
		return capability != null;
	}
	
	@Override
	public EntityPatch<?> get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == EpicFightCapabilities.CAPABILITY_ENTITY ? this.optional.cast() :  LazyOptional.empty();
	}
}