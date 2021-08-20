package maninhouse.epicfight.events;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.CapabilityEntity;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.provider.ProviderEntity;
import maninhouse.epicfight.capabilities.provider.ProviderItem;
import maninhouse.epicfight.capabilities.provider.ProviderProjectile;
import maninhouse.epicfight.capabilities.provider.ProviderSkill;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID)
public class CapabilityEvent {
	@SubscribeEvent
	public static void attachItemCapability(AttachCapabilitiesEvent<ItemStack> event) {
		if (event.getObject().getCapability(ModCapabilities.CAPABILITY_ITEM).orElse(null) == null) {
			ProviderItem prov = new ProviderItem(event.getObject().getItem(), true);
			if (prov.hasCapability()) {
				event.addCapability(new ResourceLocation(EpicFightMod.MODID, "item_cap"), prov);
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SubscribeEvent
	public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
		if (event.getObject().getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null) == null) {
			ProviderEntity prov = new ProviderEntity(event.getObject());
			if (prov.hasCapability()) {
				CapabilityEntity entityCap = prov.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
				entityCap.onEntityConstructed(event.getObject());
				event.addCapability(new ResourceLocation(EpicFightMod.MODID, "entity_cap"), prov);
				if (entityCap instanceof PlayerData<?>) {
					if (event.getObject().getCapability(ModCapabilities.CAPABILITY_SKILL).orElse(null) == null) {
						PlayerData<?> playerdata = (PlayerData<?>)entityCap;
						if (playerdata != null) {
							ProviderSkill skillProvider = new ProviderSkill(playerdata);
							event.addCapability(new ResourceLocation(EpicFightMod.MODID, "skill_cap"), skillProvider);
						}
					}
				}
			}
		}
		
		if (event.getObject() instanceof ProjectileEntity) {
			ProjectileEntity projectile = ((ProjectileEntity)event.getObject());
			if(event.getObject().getCapability(ModCapabilities.CAPABILITY_PROJECTILE).orElse(null) == null) {
				ProviderProjectile prov = new ProviderProjectile(projectile);
				if(prov.hasCapability()) {
					event.addCapability(new ResourceLocation(EpicFightMod.MODID, "projectile_cap"), prov);
				}
			}
		}
	}
}