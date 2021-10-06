package yesman.epicfight.capabilities.provider;

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
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.projectile.ArrowData;
import yesman.epicfight.capabilities.entity.projectile.CapabilityProjectile;

public class ProviderProjectile<P extends ProjectileEntity> implements ICapabilityProvider, NonNullSupplier<CapabilityProjectile<?>> {
	private static final Map<EntityType<?>, Supplier<CapabilityProjectile<?>>> CAPABILITY_BY_TYPE = new HashMap<EntityType<?>, Supplier<CapabilityProjectile<?>>> ();
	private static final Map<Class<? extends ProjectileEntity>, Supplier<CapabilityProjectile<?>>> CAPABILITY_BY_CLASS = new HashMap<Class<? extends ProjectileEntity>, Supplier<CapabilityProjectile<?>>> ();
	
	public static void makeMap() {
		CAPABILITY_BY_TYPE.computeIfAbsent(EntityType.ARROW, (type) -> ArrowData::new);
		
		CAPABILITY_BY_CLASS.put(AbstractArrowEntity.class, ArrowData::new);
	}
	
	private CapabilityProjectile<?> capability;
	private LazyOptional<CapabilityProjectile<?>> optional = LazyOptional.of(this);

	public ProviderProjectile(P projectile) {
		if (CAPABILITY_BY_TYPE.containsKey(projectile.getType())) {
			this.capability = CAPABILITY_BY_TYPE.get(projectile.getType()).get();
		} else {
			Supplier<CapabilityProjectile<?>> capSupplier = this.makeCustomCapability(projectile);
			CAPABILITY_BY_TYPE.put(projectile.getType(), capSupplier);
			this.capability = capSupplier.get();
		}
	}
	
	private Supplier<CapabilityProjectile<?>> makeCustomCapability(P projectileEntity) {
		Class<?> clazz = projectileEntity.getClass();
		Supplier<CapabilityProjectile<?>> cap = () -> null;
		for (; clazz != null && cap.get() == null; clazz = clazz.getSuperclass()) {
			cap = CAPABILITY_BY_CLASS.getOrDefault(clazz, () -> null);
		}
		return cap;
	}
	
	public boolean hasCapability() {
		return this.capability != null;
	}
	
	@Override
	public CapabilityProjectile<?> get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_PROJECTILE ? this.optional.cast() :  LazyOptional.empty();
	}
}