package yesman.epicfight.client.events;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.client.particle.BladeRushParticle;
import yesman.epicfight.client.particle.BloodParticle;
import yesman.epicfight.client.particle.CutParticle;
import yesman.epicfight.client.particle.DustParticle;
import yesman.epicfight.client.particle.EviscerateSkillParticle;
import yesman.epicfight.client.particle.HitBluntParticle;
import yesman.epicfight.client.particle.HitCutParticle;
import yesman.epicfight.client.particle.PortalStraightParticle;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.Particles;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid=EpicFightMod.MODID, value=Dist.CLIENT, bus=EventBusSubscriber.Bus.MOD)
public class RegistryClientEvent {
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onParticleRegistry(final ParticleFactoryRegisterEvent event) {
    	Minecraft.getInstance().particles.registerFactory(Particles.PORTAL_STRAIGHT.get(), PortalStraightParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.HIT_BLUNT.get(), HitBluntParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.HIT_BLADE.get(), new HitCutParticle.Factory());
    	Minecraft.getInstance().particles.registerFactory(Particles.CUT.get(), CutParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.DUST.get(), DustParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.EVISCERATE_SKILL.get(), new EviscerateSkillParticle.Factory());
    	Minecraft.getInstance().particles.registerFactory(Particles.BLOOD.get(), BloodParticle.Factory::new);
    	Minecraft.getInstance().particles.registerFactory(Particles.BLADE_RUSH_SKILL.get(), BladeRushParticle.Factory::new);
    	
    	//Minecraft.getInstance().particles.registerFactory(Particles.FLASH.get(), BlastParticle.Factory::new);
    	//Minecraft.getInstance().particles.registerFactory(Particles.BLAST_PUNCH.get(), new BlastPunchParticle.Factory());
    	//Minecraft.getInstance().particles.registerFactory(Particles.BLAST_PUNCH_HUGE.get(), new BlastPunchHugeParticle.Factory());
    }
	
	/**
	static AtlasTexture particleTexture;
	static Map<ResourceLocation, IBakedModel> modelRegistry;
	static ModelLoader modelLoader;
	
	@SubscribeEvent
	public static void onModelRegistry(final ModelBakeEvent event) {
		modelRegistry = event.getModelRegistry();
		modelLoader = event.getModelLoader();
	}
	
    @SubscribeEvent
    public static void onTextureRegistry(final TextureStitchEvent.Pre event)
    {
    	if(event.getMap().getTextureLocation().getPath() == "textures/particle")
    	{
    		particleTexture = event.getMap();
    		event.addSprite(location("blast_punch_huge"));
    	}
    }
    
    @SubscribeEvent
    public static void onTextureRegistryPost(final TextureStitchEvent.Post event)
    {
    	if(event.getMap().getTextureLocation().getPath() == "textures/particle")
    	{
    		registerParticleOBJModel(modelRegistry, modelLoader, Particles.BLAST_PUNCH_HUGE.get());
    	}
    }
    
    private static ResourceLocation location(String path)
    {
    	return new ResourceLocation(EpicFightMod.MODID, path);
    }**/
}