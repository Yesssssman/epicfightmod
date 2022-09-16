package yesman.epicfight.world.capabilities.provider;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

public class ProviderSkill implements ICapabilityProvider, NonNullSupplier<CapabilitySkill>, ICapabilitySerializable<CompoundNBT> {
	private CapabilitySkill capability;
	private LazyOptional<CapabilitySkill> optional = LazyOptional.of(this);
	
	public ProviderSkill(PlayerPatch<?> player) {
		this.capability = new CapabilitySkill(player);
	}
	
	@Override
	public CapabilitySkill get() {
		return this.capability;
	}
	
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return cap == EpicFightCapabilities.CAPABILITY_SKILL ? this.optional.cast() :  LazyOptional.empty();
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