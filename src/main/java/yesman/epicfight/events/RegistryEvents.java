package yesman.epicfight.events;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.main.EpicFightMod;

@Mod.EventBusSubscriber(modid=EpicFightMod.MODID, bus=EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    @SubscribeEvent
	public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
    	Sounds.registerSoundRegistry(event);
    }
}