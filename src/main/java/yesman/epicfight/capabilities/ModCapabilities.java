package yesman.epicfight.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import yesman.epicfight.capabilities.entity.CapabilityEntity;
import yesman.epicfight.capabilities.entity.projectile.CapabilityProjectile;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.skill.CapabilitySkill;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

@SuppressWarnings("rawtypes")
public class ModCapabilities {
	@CapabilityInject(CapabilityEntity.class)
	public static final Capability<CapabilityEntity> CAPABILITY_ENTITY = null;
	@CapabilityInject(CapabilityItem.class)
    public static final Capability<CapabilityItem> CAPABILITY_ITEM = null;
	@CapabilityInject(CapabilityProjectile.class)
    public static final Capability<CapabilityProjectile> CAPABILITY_PROJECTILE = null;
	@CapabilityInject(CapabilitySkill.class)
    public static final Capability<CapabilitySkill> CAPABILITY_SKILL = null;
	
	public static void registerCapabilities() {
		CapabilityManager.INSTANCE.register(CapabilityItem.class, new IStorage<CapabilityItem>() {
			@Override
			public INBT writeNBT(Capability<CapabilityItem> capability, CapabilityItem instance, Direction side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<CapabilityItem> capability, CapabilityItem instance, Direction side, INBT nbt) {
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(CapabilityEntity.class, new IStorage<CapabilityEntity>() {
			@Override
			public INBT writeNBT(Capability<CapabilityEntity> capability, CapabilityEntity instance, Direction side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<CapabilityEntity> capability, CapabilityEntity instance, Direction side, INBT nbt) {
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(CapabilityProjectile.class, new IStorage<CapabilityProjectile>() {
			@Override
			public INBT writeNBT(Capability<CapabilityProjectile> capability, CapabilityProjectile instance, Direction side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<CapabilityProjectile> capability, CapabilityProjectile instance, Direction side, INBT nbt) {
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(CapabilitySkill.class, new IStorage<CapabilitySkill>() {
			@Override
			public INBT writeNBT(Capability<CapabilitySkill> capability, CapabilitySkill instance, Direction side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<CapabilitySkill> capability, CapabilitySkill instance, Direction side, INBT nbt) {
			}
		}, () -> null);
	}
	
	public static CapabilityItem getItemStackCapability(ItemStack stack) {
		return stack.isEmpty() ? CapabilityItem.EMPTY : stack.getCapability(CAPABILITY_ITEM, null).orElse(CapabilityItem.EMPTY);
	}
}