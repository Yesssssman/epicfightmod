package yesman.epicfight.compat;

import net.minecraftforge.eventbus.api.IEventBus;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;
import yesman.epicfight.api.client.model.armor.GeoArmor;

public class GeckolibCompat implements ICompatModule {
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		CustomModelBakery.registerNewTransformer(new GeoArmor());
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
		eventBus.addListener(GeoArmor::getGeoArmorTexturePath);
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
	}
}