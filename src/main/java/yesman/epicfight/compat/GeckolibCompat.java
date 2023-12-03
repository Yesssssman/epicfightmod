package yesman.epicfight.compat;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;
import yesman.epicfight.api.client.model.armor.GeoArmor;

public class GeckolibCompat implements ICompatModule {
	@Override
	public void onModEventBus(IEventBus eventBus) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ()-> {
			CustomModelBakery.registerNewTransformer(new GeoArmor());
		});
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ()-> {
			eventBus.addListener(GeoArmor::getGeoArmorTexturePath);
		});
	}
}