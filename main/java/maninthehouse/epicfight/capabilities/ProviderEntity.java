package maninthehouse.epicfight.capabilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import maninthehouse.epicfight.capabilities.entity.CapabilityEntity;
import maninthehouse.epicfight.capabilities.entity.mob.CaveSpiderData;
import maninthehouse.epicfight.capabilities.entity.mob.CreeperData;
import maninthehouse.epicfight.capabilities.entity.mob.EndermanData;
import maninthehouse.epicfight.capabilities.entity.mob.EvokerData;
import maninthehouse.epicfight.capabilities.entity.mob.IronGolemData;
import maninthehouse.epicfight.capabilities.entity.mob.SkeletonData;
import maninthehouse.epicfight.capabilities.entity.mob.SpiderData;
import maninthehouse.epicfight.capabilities.entity.mob.StrayData;
import maninthehouse.epicfight.capabilities.entity.mob.VexData;
import maninthehouse.epicfight.capabilities.entity.mob.VindicatorData;
import maninthehouse.epicfight.capabilities.entity.mob.WitchData;
import maninthehouse.epicfight.capabilities.entity.mob.WitherSkeletonData;
import maninthehouse.epicfight.capabilities.entity.mob.ZombieData;
import maninthehouse.epicfight.capabilities.entity.mob.ZombieVillagerData;
import maninthehouse.epicfight.capabilities.entity.mob.ZombifiedPiglinData;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.client.capabilites.entity.RemoteClientPlayerData;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEvoker;
import net.minecraft.entity.monster.EntityHusk;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityStray;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.entity.monster.EntityVindicator;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class ProviderEntity implements ICapabilityProvider {
	private static final Map<Class<? extends Entity>, Supplier<CapabilityEntity<?>>> capabilityMap =
			new HashMap<Class<? extends Entity>, Supplier<CapabilityEntity<?>>> ();
	
	public static void makeMap() {
		capabilityMap.put(EntityPlayerMP.class, ServerPlayerData::new);
		capabilityMap.put(EntityZombie.class, ZombieData<EntityZombie>::new);
		capabilityMap.put(EntityCreeper.class, CreeperData::new);
		capabilityMap.put(EntityEnderman.class, EndermanData::new);
		capabilityMap.put(EntitySkeleton.class, SkeletonData<EntitySkeleton>::new);
		capabilityMap.put(EntityWitherSkeleton.class, WitherSkeletonData::new);
		capabilityMap.put(EntityStray.class, StrayData::new);
		capabilityMap.put(EntityPigZombie.class, ZombifiedPiglinData::new);
		capabilityMap.put(EntityZombieVillager.class, ZombieVillagerData::new);
		capabilityMap.put(EntityHusk.class, ZombieData<EntityHusk>::new);
		capabilityMap.put(EntitySpider.class, SpiderData::new);
		capabilityMap.put(EntityCaveSpider.class, CaveSpiderData::new);
		capabilityMap.put(EntityIronGolem.class, IronGolemData::new);
		capabilityMap.put(EntityVindicator.class, VindicatorData::new);
		capabilityMap.put(EntityEvoker.class, EvokerData::new);
		capabilityMap.put(EntityWitch.class, WitchData::new);
		capabilityMap.put(EntityVex.class, VexData::new);
	}
	
	public static void makeMapClient() {
		capabilityMap.put(EntityOtherPlayerMP.class, RemoteClientPlayerData<EntityOtherPlayerMP>::new);
		capabilityMap.put(EntityPlayerSP.class, ClientPlayerData::new);
	}
	
	private CapabilityEntity<?> capability;
	
	public ProviderEntity(Entity entity) {
		if(capabilityMap.containsKey(entity.getClass())) {
			capability = capabilityMap.get(entity.getClass()).get();
		}
	}
	
	public boolean hasCapability() {
		return capability != null;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == ModCapabilities.CAPABILITY_ENTITY && this.capability != null ? true : false;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == ModCapabilities.CAPABILITY_ENTITY && this.capability != null) {
			return (T) this.capability;
		}
		return null;
	}
}