package yesman.epicfight.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import yesman.epicfight.animation.AnimationManager;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.item.ItemCapabilityManager;
import yesman.epicfight.capabilities.provider.ProviderEntity;
import yesman.epicfight.capabilities.provider.ProviderItem;
import yesman.epicfight.capabilities.provider.ProviderProjectile;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.ClientEvents;
import yesman.epicfight.client.events.RegistryClientEvent;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.gui.screen.IngameConfigurationScreen;
import yesman.epicfight.client.input.ModKeys;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.config.CapabilityConfig;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.effects.ModEffects;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.events.CapabilityEvent;
import yesman.epicfight.events.EntityEvents;
import yesman.epicfight.events.PlayerEvents;
import yesman.epicfight.events.RegistryEvents;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.item.EpicFightItems;
import yesman.epicfight.loot.LootModifiers;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.world.ModGamerules;

@Mod("epicfight")
public class EpicFightMod {
	public static final String MODID = "epicfight";
	public static final String CONFIG_FILE_PATH = EpicFightMod.MODID + ".toml";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static ConfigurationIngame CLIENT_INGAME_CONFIG;
	private static EpicFightMod instance;
	
	public static EpicFightMod getInstance() {
		return instance;
	}
	
	public final AnimationManager animationManager;
	
    public EpicFightMod() {
    	this.animationManager = new AnimationManager();
    	instance = this;
    	
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG, CONFIG_FILE_PATH);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    	
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::doClientStuff);
    	bus.addListener(this::doCommonStuff);
    	bus.addListener(this::doServerStuff);
    	bus.addListener(EpicFightAttributes::modifyAttributeMap);
    	bus.addListener(Animations::registerAnimations);
    	
    	this.animationManager.registerAnimations();
    	Skills.init();
    	
    	ModEffects.EFFECTS.register(bus);
        EpicFightAttributes.ATTRIBUTES.register(bus);
        EpicFightItems.ITEMS.register(bus);
        Particles.PARTICLES.register(bus);
        LootModifiers.SERIALIZERS.register(bus);
        
        MinecraftForge.EVENT_BUS.addListener(this::reloadListnerEvent);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);
        MinecraftForge.EVENT_BUS.register(RegistryEvents.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvent.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        
        ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml").toString());
        ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());
        CapabilityConfig.buildEntityMap();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> IngameConfigurationScreen::new);
    }
    
	private void doClientStuff(final FMLClientSetupEvent event) {
    	new ClientEngine();
		ProviderEntity.makeMapClient();
		
		IResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		ClientModels.LOGICAL_CLIENT.loadMeshData(resourceManager);
		ClientModels.LOGICAL_CLIENT.loadArmatureData(resourceManager);
		Models.LOGICAL_SERVER.loadArmatureData(resourceManager);
		this.animationManager.loadAnimationsInit(resourceManager);
		Animations.buildClient();
		
		ClientEngine.instance.renderEngine.buildRenderer();
		ModKeys.registerKeys();
		MinecraftForge.EVENT_BUS.register(ControllEngine.Events.class);
        MinecraftForge.EVENT_BUS.register(RenderEngine.Events.class);
        MinecraftForge.EVENT_BUS.register(RegistryClientEvent.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        ((IReloadableResourceManager)resourceManager).addReloadListener(ClientModels.LOGICAL_CLIENT);
        ((IReloadableResourceManager)resourceManager).addReloadListener(this.animationManager);
        CLIENT_INGAME_CONFIG = new ConfigurationIngame();
    }
	
	private void doServerStuff(final FMLDedicatedServerSetupEvent event) {
		Models.LOGICAL_SERVER.loadArmatureData(null);
		this.animationManager.loadAnimationsInit(null);
	}
	
	private void doCommonStuff(final FMLCommonSetupEvent event) {
    	ModCapabilities.registerCapabilities();
    	ModNetworkManager.registerPackets();
    	ProviderItem.makeMap();
    	ProviderEntity.makeMap();
    	ProviderProjectile.makeMap();
    	ModGamerules.registerRules();
    }
	
	private void reloadListnerEvent(final AddReloadListenerEvent event) {
		event.addListener(new ItemCapabilityManager());
	}
	
	public static boolean isPhysicalClient() {
    	return FMLEnvironment.dist == Dist.CLIENT;
    }
}