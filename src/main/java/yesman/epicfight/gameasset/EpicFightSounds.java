package yesman.epicfight.gameasset;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightSounds {
	public static final SoundEvent BLADE_HIT = registerSound("entity.hit.blade");
	public static final SoundEvent BLUNT_HIT = registerSound("entity.hit.blunt");
	public static final SoundEvent BLUNT_HIT_HARD = registerSound("entity.hit.blunt_hard");
	public static final SoundEvent CLASH = registerSound("entity.hit.clash");
	public static final SoundEvent EVISCERATE = registerSound("entity.hit.eviscerate");
	public static final SoundEvent BLADE_RUSH_FINISHER = registerSound("entity.hit.blade_rush_last");
	public static final SoundEvent SWORD_IN = registerSound("entity.common.sword_in");
	public static final SoundEvent WHOOSH = registerSound("entity.common.whoosh");
	public static final SoundEvent WHOOSH_BIG = registerSound("entity.common.whoosh_hard");
	public static final SoundEvent WHOOSH_SHARP = registerSound("entity.common.whoosh_sharp");
	public static final SoundEvent ENDER_DRAGON_BREATH = registerSound("entity.enderdragon.dragon_breath");
	public static final SoundEvent ENDER_DRAGON_BREATH_FINALE = registerSound("entity.enderdragon.dragon_breath_finale");
	public static final SoundEvent ENDER_DRAGON_CRYSTAL_LINK = registerSound("entity.enderdragon.dragon_crystal_link");
	public static final SoundEvent WITHER_SPELL_ARMOR = registerSound("entity.wither.wither_spell_armor");
	
	public static final SoundEvent BUZZ = registerSound("sfx.buzz");
	public static final SoundEvent LASER_BLAST = registerSound("sfx.laser_blast");
	public static final SoundEvent GROUND_SLAM = registerSound("sfx.ground_slam");
	public static final SoundEvent NEUTRALIZE_BOSSES = registerSound("sfx.neutralize_bosses");
	public static final SoundEvent NEUTRALIZE_MOBS = registerSound("sfx.neutralize_mobs");
	public static final SoundEvent NETHER_STAR_GLITTER = registerSound("sfx.nether_star_glitter");
	public static final SoundEvent FAST_MOVE = registerSound("sfx.fast_move");
	
	private static SoundEvent registerSound(String name) {
		ResourceLocation res = new ResourceLocation(EpicFightMod.MODID, name);
		SoundEvent soundEvent = new SoundEvent(res).setRegistryName(name);
		return soundEvent;
	}
	
	public static void registerSoundRegistry(final RegistryEvent.Register<SoundEvent> event) {
		event.getRegistry().registerAll(
	    	BLADE_HIT,
	    	BLUNT_HIT,
	    	BLUNT_HIT_HARD,
	    	CLASH,
	    	EVISCERATE,
	    	BLADE_RUSH_FINISHER,
	    	SWORD_IN,
	    	WHOOSH,
	    	WHOOSH_BIG,
	    	WHOOSH_SHARP,
	    	GROUND_SLAM,
	    	ENDER_DRAGON_BREATH,
	    	ENDER_DRAGON_BREATH_FINALE,
	    	ENDER_DRAGON_CRYSTAL_LINK,
	    	WITHER_SPELL_ARMOR,
	    	NEUTRALIZE_MOBS,
	    	NEUTRALIZE_BOSSES,
	    	NETHER_STAR_GLITTER,
	    	FAST_MOVE,
	    	BUZZ, 
	    	LASER_BLAST
	    );
	}
}