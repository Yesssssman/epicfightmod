package maninthehouse.epicfight.capabilities;

import maninthehouse.epicfight.capabilities.entity.CapabilityEntity;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class ModCapabilities {
	@CapabilityInject(CapabilityEntity.class)
	public static final Capability<CapabilityEntity<?>> CAPABILITY_ENTITY = null;
	@CapabilityInject(CapabilityItem.class)
	public static final Capability<CapabilityItem> CAPABILITY_ITEM = null;

	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(CapabilityItem.class, new IStorage<CapabilityItem>() {
			@Override
			public NBTBase writeNBT(Capability<CapabilityItem> capability, CapabilityItem instance, EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<CapabilityItem> capability, CapabilityItem instance, EnumFacing side, NBTBase nbt) {

			}
		}, () -> null);

		CapabilityManager.INSTANCE.register(CapabilityEntity.class, new IStorage<CapabilityEntity>() {
			@Override
			public NBTBase writeNBT(Capability<CapabilityEntity> capability, CapabilityEntity instance,
					EnumFacing side) {
				return null;
			}

			@Override
			public void readNBT(Capability<CapabilityEntity> capability, CapabilityEntity instance, EnumFacing side,
					NBTBase nbt) {

			}
		}, () -> null);
	}

	public static CapabilityItem stackCapabilityGetter(ItemStack stack) {
		return stack.isEmpty() ? null : stack.getCapability(CAPABILITY_ITEM, null);
	}
}