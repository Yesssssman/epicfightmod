package yesman.epicfight.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.particle.AirBurstParticle;
import yesman.epicfight.client.particle.BladeRushParticle;
import yesman.epicfight.client.particle.BloodParticle;
import yesman.epicfight.client.particle.CutParticle;
import yesman.epicfight.client.particle.DustParticle;
import yesman.epicfight.client.particle.EnderParticle;
import yesman.epicfight.client.particle.EntityAfterImageParticle;
import yesman.epicfight.client.particle.EviscerateParticle;
import yesman.epicfight.client.particle.FeatherParticle;
import yesman.epicfight.client.particle.ForceFieldEndParticle;
import yesman.epicfight.client.particle.ForceFieldParticle;
import yesman.epicfight.client.particle.GroundSlamParticle;
import yesman.epicfight.client.particle.HitBluntParticle;
import yesman.epicfight.client.particle.HitCutParticle;
import yesman.epicfight.client.particle.LaserParticle;
import yesman.epicfight.client.particle.TrailParticle;
import yesman.epicfight.client.particle.TsunamiSplashParticle;
import yesman.epicfight.client.renderer.blockentity.FractureBlockRenderer;
import yesman.epicfight.client.renderer.entity.DroppedNetherStarRenderer;
import yesman.epicfight.client.renderer.entity.WitherGhostRenderer;
import yesman.epicfight.client.renderer.entity.WitherSkeletonMinionRenderer;
import yesman.epicfight.client.renderer.patched.layer.WearableItemLayer;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.entity.EpicFightEntities;
import yesman.epicfight.world.level.block.entity.EpicFightBlockEntities;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid=EpicFightMod.MODID, value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public class ClientModBusEvent {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onParticleRegistry(final ParticleFactoryRegisterEvent event) {
		Minecraft mc = Minecraft.getInstance();
		ParticleEngine particleEngine = mc.particleEngine;
    	particleEngine.register(EpicFightParticles.ENDERMAN_DEATH_EMIT.get(), EnderParticle.EndermanDeathEmitProvider::new);
    	particleEngine.register(EpicFightParticles.HIT_BLUNT.get(), HitBluntParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.HIT_BLADE.get(), new HitCutParticle.Provider());
    	particleEngine.register(EpicFightParticles.CUT.get(), CutParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.NORMAL_DUST.get(), DustParticle.NormalDustProvider::new);
    	particleEngine.register(EpicFightParticles.DUST_EXPANSIVE.get(), DustParticle.ExpansiveDustProvider::new);
    	particleEngine.register(EpicFightParticles.DUST_CONTRACTIVE.get(), DustParticle.ContractiveDustProvider::new);
    	particleEngine.register(EpicFightParticles.EVISCERATE.get(), new EviscerateParticle.Provider());
    	particleEngine.register(EpicFightParticles.BLOOD.get(), BloodParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.BLADE_RUSH_SKILL.get(), BladeRushParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.GROUND_SLAM.get(), new GroundSlamParticle.Provider());
    	particleEngine.register(EpicFightParticles.BREATH_FLAME.get(), EnderParticle.BreathFlameProvider::new);
    	particleEngine.register(EpicFightParticles.FORCE_FIELD.get(), new ForceFieldParticle.Provider());
    	particleEngine.register(EpicFightParticles.FORCE_FIELD_END.get(), new ForceFieldEndParticle.Provider());
    	particleEngine.register(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), new EntityAfterImageParticle.Provider());
    	particleEngine.register(EpicFightParticles.LASER.get(), new LaserParticle.Provider());
    	particleEngine.register(EpicFightParticles.NEUTRALIZE.get(), new DustParticle.ExpansiveMetaParticle.Provider());
    	particleEngine.register(EpicFightParticles.BOSS_CASTING.get(), new DustParticle.ContractiveMetaParticle.Provider());
    	particleEngine.register(EpicFightParticles.TSUNAMI_SPLASH.get(), TsunamiSplashParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.SWING_TRAIL.get(), TrailParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.FEATHER.get(), FeatherParticle.Provider::new);
    	particleEngine.register(EpicFightParticles.AIR_BURST.get(), new AirBurstParticle.Provider());
    }
	
	@SubscribeEvent
	public static void registerRenderersEvent(RegisterRenderers event) {
		event.registerEntityRenderer(EpicFightEntities.AREA_EFFECT_BREATH.get(), NoopRenderer::new);
		event.registerEntityRenderer(EpicFightEntities.DROPPED_NETHER_STAR.get(), DroppedNetherStarRenderer::new);
		event.registerEntityRenderer(EpicFightEntities.DEATH_HARVEST_ORB.get(), NoopRenderer::new);
		event.registerEntityRenderer(EpicFightEntities.DODGE_LEFT.get(), NoopRenderer::new);
		event.registerEntityRenderer(EpicFightEntities.WITHER_GHOST_CLONE.get(), WitherGhostRenderer::new);
		event.registerEntityRenderer(EpicFightEntities.WITHER_SKELETON_MINION.get(), WitherSkeletonMinionRenderer::new);
		
		event.registerBlockEntityRenderer(EpicFightBlockEntities.FRACTURE.get(), FractureBlockRenderer::new);
	}
	
	@SubscribeEvent
	public static void reloadEvent(EntityRenderersEvent.AddLayers event) {
		ClientEngine.getInstance().renderEngine.registerRenderer();
		WearableItemLayer.clear();
	}
}