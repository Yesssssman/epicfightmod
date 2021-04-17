package maninthehouse.epicfight.main.proxy;

import maninthehouse.epicfight.capabilities.ProviderEntity;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.input.ModKeys;
import maninthehouse.epicfight.client.model.ClientModels;

public class ClientProxy extends CommonProxy implements IProxy {
	@Override
	public void init() {
		super.init();
		new ClientEngine();
		ClientEngine.INSTANCE.renderEngine.buildRenderer();
		ProviderEntity.makeMapClient();
		ModKeys.registerKeys();
		ClientModels.LOGICAL_CLIENT.buildMeshData();
	}
}