package yesman.epicfight.compat;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.api.client.model.armor.AzureGeoArmor;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;

public class AzureLibCompat implements ICompatModule {
	@Override
	public void onModEventBus(IEventBus eventBus) {
		if (FMLEnvironment.dist.isClient()) {
			CustomModelBakery.registerNewTransformer(new AzureGeoArmor());
		}
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(AzureGeoArmor::getGeoArmorTexturePath);
		}
	}
	
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
	}
}