package yesman.epicfight.events;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.RegisterEvent;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.main.EpicFightMod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class ModBusEvents {
    @SubscribeEvent
	public static void onSoundRegistry(final RegisterEvent event) {
    	EpicFightSounds.registerSoundRegistry(event);
    }
}