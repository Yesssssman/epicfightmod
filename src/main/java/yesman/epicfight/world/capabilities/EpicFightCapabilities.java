package yesman.epicfight.world.capabilities;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

@SuppressWarnings("rawtypes")
public class EpicFightCapabilities {
	@CapabilityInject(EntityPatch.class)
	public static final Capability<EntityPatch> CAPABILITY_ENTITY = null;
	@CapabilityInject(CapabilityItem.class)
    public static final Capability<CapabilityItem> CAPABILITY_ITEM = null;
	@CapabilityInject(ProjectilePatch.class)
    public static final Capability<ProjectilePatch> CAPABILITY_PROJECTILE = null;
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
		
		CapabilityManager.INSTANCE.register(EntityPatch.class, new IStorage<EntityPatch>() {
			@Override
			public INBT writeNBT(Capability<EntityPatch> capability, EntityPatch instance, Direction side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<EntityPatch> capability, EntityPatch instance, Direction side, INBT nbt) {
			}
		}, () -> null);
		
		CapabilityManager.INSTANCE.register(ProjectilePatch.class, new IStorage<ProjectilePatch>() {
			@Override
			public INBT writeNBT(Capability<ProjectilePatch> capability, ProjectilePatch instance, Direction side) {
				return null;
			}
			
			@Override
			public void readNBT(Capability<ProjectilePatch> capability, ProjectilePatch instance, Direction side, INBT nbt) {
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