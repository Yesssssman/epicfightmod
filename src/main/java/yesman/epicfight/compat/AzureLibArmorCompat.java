package yesman.epicfight.compat;

import net.minecraftforge.eventbus.api.IEventBus;
import yesman.epicfight.api.client.model.transformer.AzureArmorTransformer;
import yesman.epicfight.api.client.model.transformer.CustomModelBakery;

public class AzureLibArmorCompat implements ICompatModule {
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		CustomModelBakery.registerNewTransformer(new AzureArmorTransformer());
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
		eventBus.addListener(AzureArmorTransformer::getGeoArmorTexturePath);
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
	}
}