package yesman.epicfight.main;

import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DataSerializerEntry;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.screen.IngameConfigurationScreen;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.data.loot.EpicFightLootModifiers;
import yesman.epicfight.events.CapabilityEvent;
import yesman.epicfight.events.EntityEvents;
import yesman.epicfight.events.ModBusEvents;
import yesman.epicfight.events.PlayerEvents;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.network.EpicFightDataSerializers;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.server.commands.arguments.SkillArgument;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCapabilityPresets;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.provider.ProviderEntity;
import yesman.epicfight.world.capabilities.provider.ProviderItem;
import yesman.epicfight.world.capabilities.provider.ProviderProjectile;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.effect.EpicFightPotions;
import yesman.epicfight.world.entity.EpicFightEntities;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.gamerule.EpicFightGamerules;
import yesman.epicfight.world.item.EpicFightItems;

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
	private Function<LivingEntityPatch<?>, Animator> animatorProvider;
	private Models<?> model;
	
    public EpicFightMod() {
    	this.animationManager = new AnimationManager();
    	instance = this;
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
    	
    	IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    	bus.addListener(this::doClientStuff);
    	bus.addListener(this::doCommonStuff);
    	bus.addListener(this::doServerStuff);
    	bus.addListener(EpicFightAttributes::registerNewMobs);
    	bus.addListener(EpicFightAttributes::modifyExistingMobs);
    	bus.addListener(EpicFightCapabilities::registerCapabilities);
    	bus.addListener(Animations::registerAnimations);
    	bus.addGenericListener(DataSerializerEntry.class, EpicFightDataSerializers::register);
    	bus.addGenericListener(GlobalLootModifierSerializer.class, EpicFightLootModifiers::register);
    	
    	LivingMotion.ENUM_MANAGER.loadPreemptive(LivingMotions.class);
    	SkillCategory.ENUM_MANAGER.loadPreemptive(SkillCategories.class);
    	Style.ENUM_MANAGER.loadPreemptive(Styles.class);
    	WeaponCategory.ENUM_MANAGER.loadPreemptive(WeaponCategories.class);
    	
    	EpicFightMobEffects.EFFECTS.register(bus);
    	EpicFightPotions.POTIONS.register(bus);
        EpicFightAttributes.ATTRIBUTES.register(bus);
        EpicFightItems.ITEMS.register(bus);
        EpicFightParticles.PARTICLES.register(bus);
        EpicFightEntities.ENTITIES.register(bus);
        
        MinecraftForge.EVENT_BUS.addListener(this::reloadListnerEvent);
        MinecraftForge.EVENT_BUS.register(EntityEvents.class);
        MinecraftForge.EVENT_BUS.register(ModBusEvents.class);
        MinecraftForge.EVENT_BUS.register(CapabilityEvent.class);
        MinecraftForge.EVENT_BUS.register(PlayerEvents.class);
        
        ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml").toString());
        ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());
        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class, () -> new ConfigGuiHandler.ConfigGuiFactory(IngameConfigurationScreen::new));
    }
    
	private void doClientStuff(final FMLClientSetupEvent event) {
    	new ClientEngine();
    	
    	CLIENT_INGAME_CONFIG = new ConfigurationIngame();
        this.animatorProvider = ClientAnimator::getAnimator;
        this.model = ClientModels.LOGICAL_CLIENT;
    	
		ProviderEntity.registerEntityPatchesClient();
		ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
		ClientModels.LOGICAL_CLIENT.loadModels(resourceManager);
		ClientModels.LOGICAL_CLIENT.loadArmatures(resourceManager);
		Models.LOGICAL_SERVER.loadArmatures(resourceManager);
		this.animationManager.loadAnimationsInit(resourceManager);
		Animations.buildClient();
		EpicFightKeyMappings.registerKeys();
        ((ReloadableResourceManager)resourceManager).registerReloadListener(ClientModels.LOGICAL_CLIENT);
        ((ReloadableResourceManager)resourceManager).registerReloadListener(this.animationManager);
    }
	
	private void doServerStuff(final FMLDedicatedServerSetupEvent event) {
		Models.LOGICAL_SERVER.loadArmatures(null);
		this.animationManager.loadAnimationsInit(null);
		this.animatorProvider = ServerAnimator::getAnimator;
		this.model = Models.LOGICAL_SERVER;
	}
	
	private void doCommonStuff(final FMLCommonSetupEvent event) {
		event.enqueueWork(this.animationManager::registerAnimations);
		event.enqueueWork(Skills::registerSkills);
		event.enqueueWork(SkillArgument::registerArgumentTypes);
		event.enqueueWork(EpicFightPotions::addRecipes);
		event.enqueueWork(EpicFightNetworkManager::registerPackets);
		event.enqueueWork(ProviderItem::registerWeaponTypesByClass);
		event.enqueueWork(ProviderEntity::registerEntityPatches);
		event.enqueueWork(ProviderProjectile::registerPatches);
		event.enqueueWork(EpicFightGamerules::registerRules);
		event.enqueueWork(EpicFightEntities::registerSpawnPlacements);
		event.enqueueWork(WeaponCapabilityPresets::register);
    }
	
	private void reloadListnerEvent(final AddReloadListenerEvent event) {
		event.addListener(new ItemCapabilityReloadListener());
		event.addListener(new MobPatchReloadListener());
	}
	
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return EpicFightMod.getInstance().animatorProvider.apply(entitypatch);
	}
	
	public static Models<?> getModelContainer(boolean isLogicalClient) {
		return EpicFightMod.getInstance().model.getModels(isLogicalClient);
	}
	
	public static boolean isPhysicalClient() {
    	return FMLEnvironment.dist == Dist.CLIENT;
    }
}