package yesman.epicfight.compat;

import dev.tr7zw.firstperson.api.ActivationHandler;
import dev.tr7zw.firstperson.api.FirstPersonAPI;
import net.minecraftforge.eventbus.api.IEventBus;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class FirstPersonCompat implements ICompatModule {
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		FirstPersonAPI.getActivationHandlers().add(new ActivationHandler() {
			public boolean preventFirstperson() {
				PlayerPatch<?> playerpatch = ClientEngine.getInstance().getPlayerPatch();
				
				if (playerpatch != null && playerpatch.getPlayerMode() == PlayerPatch.PlayerMode.BATTLE && EpicFightMod.CLIENT_CONFIGS.firstPersonModel.getValue()) {
					return true;
				}
				
				return false;
			}
		});
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
	}
}