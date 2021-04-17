package maninthehouse.epicfight.main;

import org.apache.logging.log4j.Logger;

import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.config.ConfigurationCapability;
import maninthehouse.epicfight.effects.ModEffects;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.gamedata.Skills;
import maninthehouse.epicfight.main.proxy.IProxy;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = EpicFightMod.MODID, name = EpicFightMod.MODNAME, version = EpicFightMod.VERSION, guiFactory = "maninthehouse.epicfight.client.gui.IngameConfigurationGui")
public class EpicFightMod {
	public static final String MODID = "epicfight";
    public static final String MODNAME = "Epic Fight Mod";
    public static final String VERSION = "1.12.2-2.0.0";
	public static final String CLIENT_PROXY_CLASS = "maninthehouse.epicfight.main.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "maninthehouse.epicfight.main.proxy.CommonProxy";
    public static final String CONFIG_FILE_PATH = EpicFightMod.MODID + ".toml";
	
	public static Logger LOGGER;
	
    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = SERVER_PROXY_CLASS, modId = MODID)
    public static IProxy proxy;
	
    @EventHandler
	public void preInit(FMLPreInitializationEvent event) {
    	LOGGER = event.getModLog();
    	
    	ModEffects.registerModPotions();
    	if(isPhysicalClient()) {
    		ClientModels.LOGICAL_CLIENT.buildArmatureData();
    		Models.LOGICAL_SERVER.buildArmatureData();
    	} else {
    		Models.LOGICAL_SERVER.buildArmatureData();
    	}
    	
    	Animations.registerAnimations(FMLCommonHandler.instance().getSide());
    	Skills.init();
    }
    
	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
    
	public static boolean isPhysicalClient() {
		return FMLCommonHandler.instance().getSide() == Side.CLIENT;
	}
}