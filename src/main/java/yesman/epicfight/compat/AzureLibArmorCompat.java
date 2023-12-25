package yesman.epicfight.compat;

import net.minecraftforge.eventbus.api.IEventBus;
import yesman.epicfight.api.client.model.armor.AzureArmorGeoArmor;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;

public class AzureLibArmorCompat implements ICompatModule {
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		CustomModelBakery.registerNewTransformer(new AzureArmorGeoArmor());
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
		eventBus.addListener(AzureArmorGeoArmor::getGeoArmorTexturePath);
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
	}
}