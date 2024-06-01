package yesman.epicfight.compat;

import java.lang.reflect.Constructor;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import yesman.epicfight.main.EpicFightMod;

public interface ICompatModule {
	public static void loadCompatModule(Class<? extends ICompatModule> compatModule) {
		try {
			Constructor<? extends ICompatModule> constructor = compatModule.getConstructor();
			ICompatModule compatModuleInstance = constructor.newInstance();
			compatModuleInstance.onModEventBus(FMLJavaModLoadingContext.get().getModEventBus());
			compatModuleInstance.onForgeEventBus(MinecraftForge.EVENT_BUS);
			
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
				compatModuleInstance.onModEventBusClient(FMLJavaModLoadingContext.get().getModEventBus());
				compatModuleInstance.onForgeEventBusClient(MinecraftForge.EVENT_BUS);
			});
			
			EpicFightMod.LOGGER.info("Loaded mod compat: " + compatModule.getSimpleName());
		} catch (Exception e) {
			EpicFightMod.LOGGER.error("Failed to load mod compat: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	void onModEventBus(IEventBus eventBus);
	
	void onForgeEventBus(IEventBus eventBus);
	
	@OnlyIn(Dist.CLIENT)
	void onModEventBusClient(IEventBus eventBus);
	@OnlyIn(Dist.CLIENT)
	void onForgeEventBusClient(IEventBus eventBus);
}