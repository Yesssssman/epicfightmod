package yesman.epicfight.client.events;

import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
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
	public static void onParticleRegistry(final RegisterParticleProvidersEvent event) {
    	event.registerSpriteSet(EpicFightParticles.ENDERMAN_DEATH_EMIT.get(), EnderParticle.EndermanDeathEmitProvider::new);
    	event.registerSpriteSet(EpicFightParticles.HIT_BLUNT.get(), HitBluntParticle.Provider::new);
    	event.registerSpecial(EpicFightParticles.HIT_BLADE.get(), new HitCutParticle.Provider());
    	event.registerSpriteSet(EpicFightParticles.CUT.get(), CutParticle.Provider::new);
    	event.registerSpriteSet(EpicFightParticles.NORMAL_DUST.get(), DustParticle.NormalDustProvider::new);
    	event.registerSpriteSet(EpicFightParticles.DUST_EXPANSIVE.get(), DustParticle.ExpansiveDustProvider::new);
    	event.registerSpriteSet(EpicFightParticles.DUST_CONTRACTIVE.get(), DustParticle.ContractiveDustProvider::new);
    	event.registerSpecial(EpicFightParticles.EVISCERATE.get(), new EviscerateParticle.Provider());
    	event.registerSpriteSet(EpicFightParticles.BLOOD.get(), BloodParticle.Provider::new);
    	event.registerSpriteSet(EpicFightParticles.BLADE_RUSH_SKILL.get(), BladeRushParticle.Provider::new);
    	event.registerSpecial(EpicFightParticles.GROUND_SLAM.get(), new GroundSlamParticle.Provider());
    	event.registerSpriteSet(EpicFightParticles.BREATH_FLAME.get(), EnderParticle.BreathFlameProvider::new);
    	event.registerSpecial(EpicFightParticles.FORCE_FIELD.get(), new ForceFieldParticle.Provider());
    	event.registerSpecial(EpicFightParticles.FORCE_FIELD_END.get(), new ForceFieldEndParticle.Provider());
    	event.registerSpecial(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), new EntityAfterImageParticle.Provider());
    	event.registerSpecial(EpicFightParticles.LASER.get(), new LaserParticle.Provider());
    	event.registerSpecial(EpicFightParticles.NEUTRALIZE.get(), new DustParticle.ExpansiveMetaParticle.Provider());
    	event.registerSpecial(EpicFightParticles.BOSS_CASTING.get(), new DustParticle.ContractiveMetaParticle.Provider());
    	event.registerSpriteSet(EpicFightParticles.TSUNAMI_SPLASH.get(), TsunamiSplashParticle.Provider::new);
    	event.registerSpriteSet(EpicFightParticles.SWING_TRAIL.get(), TrailParticle.Provider::new);
    	event.registerSpriteSet(EpicFightParticles.FEATHER.get(), FeatherParticle.Provider::new);
    	event.registerSpecial(EpicFightParticles.AIR_BURST.get(), new AirBurstParticle.Provider());
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