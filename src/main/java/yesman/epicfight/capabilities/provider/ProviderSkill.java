package yesman.epicfight.capabilities.provider;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.skill.CapabilitySkill;

public class ProviderSkill implements ICapabilityProvider, NonNullSupplier<CapabilitySkill>, ICapabilitySerializable<CompoundNBT> {
	private CapabilitySkill capability;
	private LazyOptional<CapabilitySkill> optional = LazyOptional.of(this);
	
	public ProviderSkill(PlayerData<?> player) {
		this.capability = new CapabilitySkill(player);
	}
	
	@Override
	public CapabilitySkill get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == ModCapabilities.CAPABILITY_SKILL ? this.optional.cast() :  LazyOptional.empty();
	}

	@Override
	public CompoundNBT serializeNBT() {
		return this.capability.toNBT();
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.capability.fromNBT(nbt);
	}
}