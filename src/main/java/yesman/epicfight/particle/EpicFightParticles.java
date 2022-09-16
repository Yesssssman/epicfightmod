package yesman.epicfight.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightParticles {
	public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, EpicFightMod.MODID);
	
	public static final RegistryObject<BasicParticleType> BLOOD = PARTICLES.register("blood", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> CUT = PARTICLES.register("cut", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> DUST_EXPANSIVE = PARTICLES.register("dust_expansive", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> DUST_CONTRACTIVE = PARTICLES.register("dust_contractive", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> NORMAL_DUST = PARTICLES.register("dust_normal", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> ENDERMAN_DEATH_EMIT = PARTICLES.register("enderman_death_emit", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> GROUND_SLAM = PARTICLES.register("ground_slam", () -> new BasicParticleType(true));
	public static final RegistryObject<HitParticleType> HIT_BLUNT = PARTICLES.register("hit_blunt", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO));
	public static final RegistryObject<HitParticleType> HIT_BLADE = PARTICLES.register("hit_blade", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO));
	public static final RegistryObject<HitParticleType> EVISCERATE = PARTICLES.register("eviscerate", () -> new HitParticleType(true, HitParticleType.CENTER_OF_TARGET, HitParticleType.ATTACKER_XY_ROTATION));
	public static final RegistryObject<HitParticleType> BLADE_RUSH_SKILL = PARTICLES.register("blade_rush", () -> new HitParticleType(true, HitParticleType.RANDOM_WITHIN_BOUNDING_BOX, HitParticleType.ZERO));
	public static final RegistryObject<BasicParticleType> BREATH_FLAME = PARTICLES.register("breath_flame", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> FORCE_FIELD = PARTICLES.register("force_field", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> FORCE_FIELD_END = PARTICLES.register("force_field_end", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> ENTITY_AFTER_IMAGE = PARTICLES.register("after_image", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> LASER = PARTICLES.register("laser", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> NEUTRALIZE = PARTICLES.register("neutralize", () -> new BasicParticleType(true));
	public static final RegistryObject<BasicParticleType> BOSS_CASTING = PARTICLES.register("boss_casting", () -> new BasicParticleType(true));
}