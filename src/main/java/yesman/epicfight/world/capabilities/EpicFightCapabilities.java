package yesman.epicfight.world.capabilities;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.projectile.ProjectilePatch;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

@SuppressWarnings("rawtypes")
public class EpicFightCapabilities {
	public static final Capability<EntityPatch> CAPABILITY_ENTITY = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<CapabilityItem> CAPABILITY_ITEM = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<ProjectilePatch> CAPABILITY_PROJECTILE = CapabilityManager.get(new CapabilityToken<>(){});
    public static final Capability<CapabilitySkill> CAPABILITY_SKILL = CapabilityManager.get(new CapabilityToken<>(){});
	
	public static void registerCapabilities(RegisterCapabilitiesEvent event) {
		event.register(CapabilityItem.class);
		event.register(EntityPatch.class);
		event.register(ProjectilePatch.class);
		event.register(CapabilitySkill.class);
	}
	
	public static CapabilityItem getItemStackCapability(ItemStack stack) {
		return stack.isEmpty() ? CapabilityItem.EMPTY : stack.getCapability(CAPABILITY_ITEM, null).orElse(CapabilityItem.EMPTY);
	}
}