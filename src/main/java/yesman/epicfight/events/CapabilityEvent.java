package yesman.epicfight.events;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.provider.ProviderEntity;
import yesman.epicfight.world.capabilities.provider.ProviderItem;
import yesman.epicfight.world.capabilities.provider.ProviderProjectile;
import yesman.epicfight.world.capabilities.provider.ProviderSkill;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class CapabilityEvent {
	@SubscribeEvent
	public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject() != null) {
			ProviderItem prov = new ProviderItem(event.getObject());
			if (prov.hasCapability()) {
				event.addCapability(new ResourceLocation(EpicFightMod.MODID, "item_cap"), prov);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject().getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null) == null) {
			ProviderEntity prov = new ProviderEntity(event.getObject());
			if (prov.hasCapability()) {
				EntityPatch entityCap = prov.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
				entityCap.onConstructed(event.getObject());
				event.addCapability(new ResourceLocation(EpicFightMod.MODID, "entity_cap"), prov);
				if (entityCap instanceof PlayerPatch<?>) {
					if (event.getObject().getCapability(EpicFightCapabilities.CAPABILITY_SKILL).orElse(null) == null) {
						PlayerPatch<?> playerpatch = (PlayerPatch<?>)entityCap;
						if (playerpatch != null) {
							ProviderSkill skillProvider = new ProviderSkill(playerpatch);
							event.addCapability(new ResourceLocation(EpicFightMod.MODID, "skill_cap"), skillProvider);
						}
					}
				}
			}
		}
		
		if (event.getObject() instanceof ProjectileEntity) {
			if(event.getObject().getCapability(EpicFightCapabilities.CAPABILITY_PROJECTILE).orElse(null) == null) {
				ProviderProjectile prov = new ProviderProjectile(((ProjectileEntity)event.getObject()));
				if(prov.hasCapability()) {
					event.addCapability(new ResourceLocation(EpicFightMod.MODID, "projectile_cap"), prov);
				}
			}
		}
	}
}