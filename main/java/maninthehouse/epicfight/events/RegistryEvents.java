package maninthehouse.epicfight.events;

import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.item.ModItems;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID)
public class RegistryEvents {
    @SubscribeEvent
	public static void onSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
    	event.getRegistry().registerAll(
    		Sounds.BLADE_HIT, Sounds.BLUNT_HIT, Sounds.BLUNT_HIT_HARD, Sounds.WHOOSH, Sounds.WHOOSH_BIG, Sounds.WHOOSH_SHARP
    	);
    }
    
	@SubscribeEvent
	public static void onItemRegister(RegistryEvent.Register<Item> event) {
		ModItems.registerItems(event);
	}
}