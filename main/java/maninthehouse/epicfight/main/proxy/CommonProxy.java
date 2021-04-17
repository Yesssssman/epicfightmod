package maninthehouse.epicfight.main.proxy;

import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.ProviderEntity;
import maninthehouse.epicfight.capabilities.ProviderItem;
import maninthehouse.epicfight.network.ModNetworkManager;

public class CommonProxy implements IProxy {
	public void init() {
		ModCapabilities.registerCapabilities();
		ModNetworkManager.registerPackets();
		ProviderItem.makeMap();
		ProviderEntity.makeMap();
	}
}