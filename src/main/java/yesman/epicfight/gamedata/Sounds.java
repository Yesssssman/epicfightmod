package yesman.epicfight.gamedata;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import yesman.epicfight.main.EpicFightMod;

public class Sounds {
	public static SoundEvent BLADE_HIT = registerSound("entity.hit.blade");
	public static SoundEvent BLUNT_HIT = registerSound("entity.hit.blunt");
	public static SoundEvent BLUNT_HIT_HARD = registerSound("entity.hit.blunt_hard");
	public static SoundEvent CLASH = registerSound("entity.hit.clash");
	public static SoundEvent EVISCERATE = registerSound("entity.hit.eviscerate");
	public static SoundEvent BLADE_RUSH_FINISHER = registerSound("entity.hit.blade_rush_last");
	public static SoundEvent SWORD_IN = registerSound("entity.common.sword_in");
	public static SoundEvent WHOOSH = registerSound("entity.common.whoosh");
	public static SoundEvent WHOOSH_BIG = registerSound("entity.common.whoosh_hard");
	public static SoundEvent WHOOSH_SHARP = registerSound("entity.common.whoosh_sharp");
	
	private static SoundEvent registerSound(String name) {
		ResourceLocation res = new ResourceLocation(EpicFightMod.MODID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		return soundEvent;
	}
	
	public static void registerSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(
	    	Sounds.BLADE_HIT,
	    	Sounds.BLUNT_HIT,
	    	Sounds.BLUNT_HIT_HARD,
	    	Sounds.CLASH,
	    	Sounds.EVISCERATE,
	    	Sounds.BLADE_RUSH_FINISHER,
	    	Sounds.SWORD_IN,
	    	Sounds.WHOOSH,
	    	Sounds.WHOOSH_BIG,
	    	Sounds.WHOOSH_SHARP
	    );
	}
}