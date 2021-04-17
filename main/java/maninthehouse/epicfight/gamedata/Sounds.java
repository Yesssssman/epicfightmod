package maninthehouse.epicfight.gamedata;

import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class Sounds
{
	public static SoundEvent BLADE_HIT = registerSound("entity.hit.blade");
	public static SoundEvent BLUNT_HIT = registerSound("entity.hit.blunt");
	public static SoundEvent BLUNT_HIT_HARD = registerSound("entity.hit.blunt_hard");
	public static SoundEvent SWORD_IN = registerSound("entity.common.sword_in");
	public static SoundEvent WHOOSH = registerSound("entity.common.whoosh");
	public static SoundEvent WHOOSH_BIG = registerSound("entity.common.whoosh_hard");
	public static SoundEvent WHOOSH_SHARP = registerSound("entity.common.whoosh_sharp");
	
	private static SoundEvent registerSound(String name)
	{
		ResourceLocation res = new ResourceLocation(EpicFightMod.MODID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		
		return soundEvent;
	}
}