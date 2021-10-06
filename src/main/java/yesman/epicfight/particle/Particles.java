package yesman.epicfight.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

public class Particles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EpicFightMod.MODID);
	
	//public static final RegistryObject<HitParticleType> BLAST_PUNCH = PARTICLES.register("blast_punch", () -> new HitParticleType(true, HitParticleType.ARGUMENT_ATTACKER_DIRECTION));
	//public static final RegistryObject<HitParticleType> BLAST_PUNCH_HUGE = PARTICLES.register("blast_punch_huge", () -> new HitParticleType(true, HitParticleType.ARGUMENT_ATTACKER_DIRECTION));
	//public static final RegistryObject<BasicParticleType> FLASH = PARTICLES.register("flash", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> BLOOD = PARTICLES.register("blood", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> CUT = PARTICLES.register("cut", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> DUST = PARTICLES.register("dust", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> PORTAL_STRAIGHT = PARTICLES.register("portal_straight", () -> new BasicParticleType(true));
	public static final RegistryObject<HitParticleType> HIT_BLUNT = PARTICLES.register("blunt", () ->
				new HitParticleType(true, HitParticleType.POSITION_RANDOM_IN_TARGET_SIZE, HitParticleType.ARGUMENT_ZERO));
	public static final RegistryObject<HitParticleType> HIT_BLADE = PARTICLES.register("hit_cut", () ->
				new HitParticleType(true, HitParticleType.POSITION_RANDOM_IN_TARGET_SIZE, HitParticleType.ARGUMENT_ZERO));
	public static final RegistryObject<HitParticleType> EVISCERATE_SKILL = PARTICLES.register("eviscerate_skill", () -> 
				new HitParticleType(true, HitParticleType.POSITION_MIDDLE_OF_TARGET, HitParticleType.ARGUMENT_ATTACKER_DIRECTION));
	public static final RegistryObject<HitParticleType> BLADE_RUSH_SKILL = PARTICLES.register("blade_rush", () -> 
				new HitParticleType(true, HitParticleType.POSITION_RANDOM_IN_TARGET_SIZE, HitParticleType.ARGUMENT_ZERO));
}