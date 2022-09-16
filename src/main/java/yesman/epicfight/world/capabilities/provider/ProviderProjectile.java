package yesman.epicfight.world.capabilities.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.projectile.ArrowPatch;
import yesman.epicfight.world.capabilities.projectile.DragonFireballPatch;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.capabilities.projectile.WitherSkullPatch;

public class ProviderProjectile implements ICapabilityProvider, NonNullSupplier<ProjectilePatch<?>> {
	private static final Map<EntityType<?>, Supplier<ProjectilePatch<?>>> CAPABILITY_BY_TYPE = new HashMap<EntityType<?>, Supplier<ProjectilePatch<?>>> ();
	private static final Map<Class<? extends ProjectileEntity>, Supplier<ProjectilePatch<?>>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends ProjectileEntity>, Supplier<ProjectilePatch<?>>> ();
	
	public static void registerPatches() {
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.ARROW, (type) -> ArrowPatch::new);
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.WITHER_SKULL, (type) -> WitherSkullPatch::new);
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.DRAGON_FIREBALL, (type) -> DragonFireballPatch::new);
		
		CAPABILITY_BY_CLASS.put(AbstractArrowEntity.class, ArrowPatch::new);
	}
	
	private ProjectilePatch<?> capability;
	private LazyOptional<ProjectilePatch<?>> optional = LazyOptional.of(this);

	public ProviderProjectile(ProjectileEntity projectile) {
		if (CAPABILITY_BY_TYPE.containsKey(projectile.getType())) {
			this.capability = CAPABILITY_BY_TYPE.get(projectile.getType()).get();
		} else {
			Supplier<ProjectilePatch<?>> capSupplier = this.makeCustomCapability(projectile);
			CAPABILITY_BY_TYPE.put(projectile.getType(), capSupplier);
			this.capability = capSupplier.get();
		}
	}
	
	private Supplier<ProjectilePatch<?>> makeCustomCapability(ProjectileEntity projectileEntity) {
		Class<?> clazz = projectileEntity.getClass();
		Supplier<ProjectilePatch<?>> cap = () -> null;
		for (; clazz != null && cap.get() == null; clazz = clazz.getSuperclass()) {
			cap = CAPABILITY_BY_CLASS.getOrDefault(clazz, () -> null);
		}
		return cap;
	}
	
	public boolean hasCapability() {
		return this.capability != null;
	}
	
	@Override
	public ProjectilePatch<?> get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == EpicFightCapabilities.CAPABILITY_PROJECTILE ? this.optional.cast() :  LazyOptional.empty();
	}
}