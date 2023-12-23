package yesman.epicfight.compat;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.api.client.model.armor.AzureArmorGeoArmor;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;

public class AzureLibArmorCompat implements ICompatModule {
	@Override
	public void onModEventBus(IEventBus eventBus) {
		if (FMLEnvironment.dist.isClient()) {
			CustomModelBakery.registerNewTransformer(new AzureArmorGeoArmor());
		}
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
		if (FMLEnvironment.dist.isClient()) {
			eventBus.addListener(AzureArmorGeoArmor::getGeoArmorTexturePath);
		}
	}
}