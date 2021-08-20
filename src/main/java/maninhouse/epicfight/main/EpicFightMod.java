package maninhouse.epicfight.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.provider.ProviderEntity;
import maninhouse.epicfight.capabilities.provider.ProviderItem;
import maninhouse.epicfight.capabilities.provider.ProviderProjectile;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.client.events.ClientEvents;
import maninhouse.epicfight.client.events.RegistryClientEvent;
import maninhouse.epicfight.client.events.engine.ControllEngine;
import maninhouse.epicfight.client.events.engine.RenderEngine;
import maninhouse.epicfight.client.gui.IngameConfigurationGui;
import maninhouse.epicfight.client.input.ModKeys;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.config.CapabilityConfig;
import maninhouse.epicfight.config.ConfigManager;
import maninhouse.epicfight.config.ConfigurationIngame;
import maninhouse.epicfight.effects.ModEffects;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.events.CapabilityEvent;
import maninhouse.epicfight.events.EntityEvents;
import maninhouse.epicfight.events.PlayerEvents;
import maninhouse.epicfight.events.RegistryEvents;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.item.ModItems;
import maninhouse.epicfight.loot.LootModifiers;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.world.ModGamerules;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;

@Mod("epicfight")
public class EpicFightMod {
	public static final String MODID = "epicfight";
	public static final String CONFIG_FILE_PATH = EpicFightMod.MODID + ".toml";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static ConfigurationIngame CLIENT_INGAME_CONFIG;
	
    public EpicFightMod() {
    	ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigManager.COMMON_CONFIG, CONFIG_FILE_PATH);
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    	
    	if(isPhysicalClient()) {
    		ClientModels.LOGICAL_CLIENT.buildArmatureData();
    		Models.LOGICAL_SERVER.buildArmatureData();
    	} else {
    		Models.LOGICAL_SERVER.buildArmatureData();
    	}
    	
    	Animations.registerAnimations(FMLEnvironment.dist);
    	Skills.init();
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::doClientStuff);
    	bus.addListener(this::doCommonStuff);
    	bus.addListener(ModAttributes::modifyAttributeMap);
    	ModEffects.EFFECTS.register(bus);
        ModAttributes.ATTRIBUTES.register(bus);
        ModItems.ITEMS.register(bus);
        Particles.PARTICLES.register(bus);
        LootModifiers.SERIALIZERS.register(bus);
        
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);
        MinecraftForge.EVENT_BUS.register(RegistryEvents.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvent.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        
        ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml").toString());
        ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());
        CapabilityConfig.buildEntityMap();
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> IngameConfigurationGui::new);
    }
    
	private void doClientStuff(final FMLClientSetupEvent event) {
    	new ClientEngine();
    	ClientEngine.initAnimations();
		ProviderEntity.makeMapClient();
		ClientModels.LOGICAL_CLIENT.buildMeshData();
		ClientEngine.INSTANCE.renderEngine.buildRenderer();
		ModKeys.registerKeys();
		MinecraftForge.EVENT_BUS.register(ControllEngine.Events.class);
        MinecraftForge.EVENT_BUS.register(RenderEngine.Events.class);
        MinecraftForge.EVENT_BUS.register(RegistryClientEvent.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
        
        CLIENT_INGAME_CONFIG = new ConfigurationIngame();
    }
    
	private void doCommonStuff(final FMLCommonSetupEvent event) {
    	ModCapabilities.registerCapabilities();
    	ModNetworkManager.registerPackets();
    	ProviderItem.makeMap();
    	ProviderEntity.makeMap();
    	ProviderProjectile.makeMap();
    	ModGamerules.registerRules();
    }
	
	public static boolean isPhysicalClient() {
    	return FMLEnvironment.dist == Dist.CLIENT;
    }
}