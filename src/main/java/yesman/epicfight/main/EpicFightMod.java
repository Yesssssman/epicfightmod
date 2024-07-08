package yesman.epicfight.main;

import java.nio.file.Path;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.client.animation.property.JointMaskReloadListener;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
import yesman.epicfight.client.gui.screen.config.IngameConfigurationScreen;
import yesman.epicfight.client.renderer.patched.item.EpicFightItemProperties;
import yesman.epicfight.compat.AzureLibArmorCompat;
import yesman.epicfight.compat.AzureLibCompat;
import yesman.epicfight.compat.GeckolibCompat;
import yesman.epicfight.compat.ICompatModule;
import yesman.epicfight.config.ConfigManager;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.data.conditions.EpicFightConditions;
import yesman.epicfight.data.loot.EpicFightLootTables;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.EpicFightDataSerializers;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.server.commands.PlayerModeCommand;
import yesman.epicfight.server.commands.PlayerSkillCommand;
import yesman.epicfight.server.commands.arguments.EpicFightCommandArgumentTypes;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;
import yesman.epicfight.world.capabilities.provider.EntityPatchProvider;
import yesman.epicfight.world.capabilities.provider.ItemCapabilityProvider;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.effect.EpicFightPotions;
import yesman.epicfight.world.entity.EpicFightEntities;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.decoration.EpicFightPaintingVariants;
import yesman.epicfight.world.gamerule.EpicFightGamerules;
import yesman.epicfight.world.item.EpicFightCreativeTabs;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;
import yesman.epicfight.world.level.block.EpicFightBlocks;
import yesman.epicfight.world.level.block.entity.EpicFightBlockEntities;

/**
 *  Changes from 20.7.4 -> 20.8.1
 *  
 *  1. Datapack Edit Screen added
 *  
 *  2. Skillbook screen revamped
 *  
 *  3. Skill Consume event changed
 *  
 *  4. Armor Negation calculation changed
 *  
 *  5. Weight base value calculation changed to (entity dimension width * height * {@link LivingEntityPatch#WEIGHT_CORRECTION})
 *  
 *  6. Animations now won't be interpolated from between the previous and current pose but from animation clip resulting in increased animation accuracy
 *  
 *  7. Skill registration changed
 *  
 *  8. Added {@link EpicFightExtensions}. Now you can decide a creative tab that you want to display your skills of your mod. (see the usage below)
 *  
 *  9. Fixed crash when item broken in player's offhand by attacking any entities
 *  
 *  10. Added a selectable built-in resource pack with legacy weapon textures
 *
 *  11. Fixed Player head go through the body when using demolition leap
 *  
 *  --- 20.8.1.2 ---
 *
 *  1. Fixed Freecam mod disabling Epic Fight battle mode (Release fix)
 *  
 *  2. Fixed Ender dragon breath hitting a player in creative mode (Release fix)
 *  
 *  --- 20.8.1.2 ---
 *  
 *  1. Fixed Demolition leap unable to jump forward when you cast it with a wall behind (Release fix)
 *  
 *  2. Fixed the skills are not removed even tho keepSkills gamerule is set to false (Release fix)
 *  
 *  3. Added translations for sound subtitles. (Release fix)
 *  
 *  4. Fixed players dealing a weapon's damage when they drop a weapon and attack at the same time (Release fix)
 *  
 *  --- 20.8.1.3 ---
 *  
 *  1. Fixed epic fight legacy resource pack not loaded properly
 *  
 *  2. Fixed trail textures broken when trails that have different textures from each other are on the screen (Release fix)
 *  
 *  3. Added missing translations for attributes (GitHub issues #1678) (Release fix)
 *  
 *  4. Fixed Armor negation dealing more damage when it exceeds 100
 *  
 *  5. Deactivated Bloom effect
 *  
 *  6. Now players can select any skills in the skill edit screen(Default Keybind: K) when they're in creative mode.
 *  
 *  7. Fixed epic fight attributes not being applied to entities via commands (Release fix)
 *  
 *  8. Fixed epic fight attributes being reset when joining the world (GitHub issues #1354) (Release fix)
 *  
 *  9. Fixed Gecko & Azurelib based entities' texture broken when the health bar is activated (Release fix)
 *  
 *  10. Fixed server loading crash (GitHub issues #1680) (Release fix)
 *  
 *  11. Fixed a crash when spawning Witches in a dedicated server
 *  
 *  --- 20.8.1.4 ---
 *  
 *  1. Fixed a crash when loading weapon types from datapack
 *  
 *  2. Datapack editor screen automatically updates the old condition format
 *  
 *  --- 20.8.1.5 ---
 *  
 *  1. Fixed Weapon innate skills remaining after the player respawns
 *  
 *  2. Fixed custom animations not shown on the animation select screen
 *  
 *  3. Now Epic Fight is compatible with the latest Azurelib (release fix)
 *  
 *  4. Now Armor trims work normally in the animated models (release fix) (GitHub issues #1689)
 *  
 *  --- TO DO ---
 *  
 *  1. Crash because {@link PlayerPatch#STAMINA} is unregistered at SynchedEntityData (Most likely a mod compatibility issue)
 *  
 *  2. Add an reach property to attack animation (idea)
 *  
 *  3. Add an alert function when an entity targeting the player tries grappling or execution attack
 *  
 *  4. Add UI for execution resistance
 *  
 *  5. Add functionality to blooming effect (resists wither effect)
 *  
 *  6. First person animation system by adding /data/ folder in the path, and few samples
 *  
 *  @author yesman
 */
@Mod("epicfight")
public class EpicFightMod {
	public static final String MODID = "epicfight";
	public static final String CONFIG_FILE_PATH = EpicFightMod.MODID + ".toml";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	public static EpicFightOptions CLIENT_CONFIGS;
	private static EpicFightMod instance;
	
	public static EpicFightMod getInstance() {
		return instance;
	}
	
	private Function<LivingEntityPatch<?>, Animator> animatorProvider;
	
    public EpicFightMod() {
    	instance = this;
    	
    	ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ConfigManager.CLIENT_CONFIG);
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		
		bus.addListener(this::constructMod);
    	bus.addListener(this::doClientStuff);
    	bus.addListener(this::doCommonStuff);
    	bus.addListener(this::doServerStuff);
    	bus.addListener(this::addPackFindersEvent);
    	bus.addListener(this::registerResourcepackReloadListnerEvent);
    	bus.addListener(this::buildCreativeTabWithSkillBooks);
    	bus.addListener(EpicFightAttributes::entityAttributeCreationEvent);
    	bus.addListener(EpicFightAttributes::entityAttributeModificationEvent);
    	bus.addListener(SkillManager::createSkillRegistry);
    	bus.addListener(SkillManager::registerSkills);
    	bus.addListener(EpicFightCapabilities::registerCapabilities);
    	bus.addListener(EpicFightEntities::onSpawnPlacementRegister);
    	
    	MinecraftForge.EVENT_BUS.addListener(this::command);
        MinecraftForge.EVENT_BUS.addListener(this::registerDatapackReloadListnerEvent);
    	
    	LivingMotion.ENUM_MANAGER.registerEnumCls(EpicFightMod.MODID, LivingMotions.class);
    	SkillCategory.ENUM_MANAGER.registerEnumCls(EpicFightMod.MODID, SkillCategories.class);
    	SkillSlot.ENUM_MANAGER.registerEnumCls(EpicFightMod.MODID, SkillSlots.class);
    	Style.ENUM_MANAGER.registerEnumCls(EpicFightMod.MODID, Styles.class);
    	WeaponCategory.ENUM_MANAGER.registerEnumCls(EpicFightMod.MODID, WeaponCategories.class);
    	
    	EpicFightMobEffects.EFFECTS.register(bus);
    	EpicFightPotions.POTIONS.register(bus);
        EpicFightAttributes.ATTRIBUTES.register(bus);
        EpicFightCreativeTabs.TABS.register(bus);
        EpicFightItems.ITEMS.register(bus);
        EpicFightParticles.PARTICLES.register(bus);
        EpicFightEntities.ENTITIES.register(bus);
        EpicFightBlocks.BLOCKS.register(bus);
        EpicFightBlockEntities.BLOCK_ENTITIES.register(bus);
		EpicFightLootTables.LOOT_MODIFIERS.register(bus);
		EpicFightSounds.SOUNDS.register(bus);
		EpicFightDataSerializers.ENTITY_DATA_SERIALIZER.register(bus);
		EpicFightConditions.CONDITIONS.register(bus);
		SkillDataKeys.DATA_KEYS.register(bus);
		EpicFightPaintingVariants.PAINTING_VARIANTS.register(bus);
		EpicFightCommandArgumentTypes.COMMAND_ARGUMENT_TYPES.register(bus);
        
        ConfigManager.loadConfig(ConfigManager.CLIENT_CONFIG, FMLPaths.CONFIGDIR.get().resolve(MODID + "-client.toml").toString());
        ConfigManager.loadConfig(ConfigManager.COMMON_CONFIG, FMLPaths.CONFIGDIR.get().resolve(CONFIG_FILE_PATH).toString());
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(IngameConfigurationScreen::new));
        
        //
        ModLoadingContext.get().registerExtensionPoint(EpicFightExtensions.class, () -> new EpicFightExtensions(EpicFightCreativeTabs.ITEMS.get()));
        
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
        	if (ModList.get().isLoaded("geckolib")) {
    			ICompatModule.loadCompatModule(GeckolibCompat.class);
    		}
    		
    		if (ModList.get().isLoaded("azurelib")) {
    			ICompatModule.loadCompatModule(AzureLibCompat.class);
    		}
    		
    		if (ModList.get().isLoaded("azurelibarmor")) {
    			ICompatModule.loadCompatModule(AzureLibArmorCompat.class);
    		}
		});
	}
    
    /**
     * FML Lifecycle Events
     */
    private void constructMod(final FMLConstructModEvent event) {
    	LivingMotion.ENUM_MANAGER.loadEnum();
    	SkillCategory.ENUM_MANAGER.loadEnum();
    	SkillSlot.ENUM_MANAGER.loadEnum();
    	Style.ENUM_MANAGER.loadEnum();
    	WeaponCategory.ENUM_MANAGER.loadEnum();
    }
    
	private void doClientStuff(final FMLClientSetupEvent event) {
		CLIENT_CONFIGS = new EpicFightOptions();
    	new ClientEngine();
    	
        this.animatorProvider = ClientAnimator::getAnimator;
		EntityPatchProvider.registerEntityPatchesClient();
		EpicFightItemProperties.registerItemProperties();
		SkillBookScreen.registerIconItems();
    }
	
	private void doServerStuff(final FMLDedicatedServerSetupEvent event) {
		this.animatorProvider = ServerAnimator::getAnimator;
	}
	
	private void doCommonStuff(final FMLCommonSetupEvent event) {
		event.enqueueWork(EpicFightCommandArgumentTypes::registerArgumentTypes);
		event.enqueueWork(EpicFightPotions::addRecipes);
		event.enqueueWork(EpicFightNetworkManager::registerPackets);
		event.enqueueWork(ItemCapabilityProvider::registerWeaponTypesByClass);
		event.enqueueWork(EntityPatchProvider::registerEntityPatches);
		event.enqueueWork(EpicFightGamerules::registerRules);
		event.enqueueWork(WeaponTypeReloadListener::registerDefaultWeaponTypes);
		event.enqueueWork(EpicFightMobEffects::addOffhandModifier);
    }
	
	/**
	 * Register Etc
	 */
	private void command(final RegisterCommandsEvent event) {
		PlayerModeCommand.register(event.getDispatcher());
		PlayerSkillCommand.register(event.getDispatcher(), event.getBuildContext());
    }
	
	public void addPackFindersEvent(AddPackFindersEvent event) {
		if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            Path resourcePath = ModList.get().getModFileById(EpicFightMod.MODID).getFile().findResource("packs/epicfight_legacy");
            PathPackResources pack = new PathPackResources(ModList.get().getModFileById(EpicFightMod.MODID).getFile().getFileName() + ":" + resourcePath, resourcePath, false);
            Pack.ResourcesSupplier resourcesSupplier = (string) -> pack;
            Pack.Info info = Pack.readPackInfo("epicfight_legacy", resourcesSupplier);
            
            if (info != null) {
                event.addRepositorySource((source) ->
    			source.accept(Pack.create("epicfight_legacy", Component.translatable("pack.epicfight_legacy.title"), false, resourcesSupplier, info, PackType.CLIENT_RESOURCES, Pack.Position.TOP, false, PackSource.BUILT_IN)));
            }
        }
    }
	
	private void registerResourcepackReloadListnerEvent(final RegisterClientReloadListenersEvent event) {
		event.registerReloadListener(new JointMaskReloadListener());
		event.registerReloadListener(Meshes.INSTANCE);
		event.registerReloadListener(AnimationManager.getInstance());
		event.registerReloadListener(ItemSkins.INSTANCE);
	}
	
	private void registerDatapackReloadListnerEvent(final AddReloadListenerEvent event) {
		if (!isPhysicalClient()) {
			event.addListener(AnimationManager.getInstance());
		}
		
		event.addListener(new ColliderPreset());
		event.addListener(new SkillManager());
		event.addListener(new WeaponTypeReloadListener());
		event.addListener(new ItemCapabilityReloadListener());
		event.addListener(new MobPatchReloadListener());
	}
	
	private void buildCreativeTabWithSkillBooks(final BuildCreativeModeTabContentsEvent event) {
		/**
		 * Accept learnable skills for each mod by {@link EpicFightExtensions#skillBookCreativeTab}.
		 * If the extension doesn't exist, add them to {@link EpicFightCreativeTabs.ITEMS} tab.
		 */
		SkillManager.getNamespaces().forEach((modid) -> {
			ModList.get().getModContainerById(modid).flatMap((mc) -> mc.getCustomExtension(EpicFightExtensions.class)).ifPresentOrElse((extension) -> {
				if (extension.skillBookCreativeTab() == event.getTab()) {
					SkillManager.getSkillNames((skill) -> skill.getCategory().learnable() && skill.getRegistryName().getNamespace() == modid).forEach((rl) -> {
						ItemStack stack = new ItemStack(EpicFightItems.SKILLBOOK.get());
						SkillBookItem.setContainingSkill(rl.toString(), stack);
						event.accept(stack);
					});
				}
			}, () -> {
				if (event.getTab() == EpicFightCreativeTabs.ITEMS.get()) {
					SkillManager.getSkillNames((skill) -> skill.getCategory().learnable() && skill.getRegistryName().getNamespace() == modid).forEach((rl) -> {
						ItemStack stack = new ItemStack(EpicFightItems.SKILLBOOK.get());
						SkillBookItem.setContainingSkill(rl.toString(), stack);
						event.accept(stack);
					});
				}
			});
		});
	}
	
	/**
	 * Epic Fight utils
	 */
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return EpicFightMod.getInstance().animatorProvider.apply(entitypatch);
	}
	
	public static boolean isPhysicalClient() {
    	return FMLEnvironment.dist == Dist.CLIENT;
    }
}