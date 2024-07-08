package yesman.epicfight.compat;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import software.bernie.geckolib.event.GeoRenderEvent;
import yesman.epicfight.api.client.model.armor.CustomModelBakery;
import yesman.epicfight.api.client.model.armor.GeoArmor;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.gui.EntityIndicator;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class GeckolibCompat implements ICompatModule {
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		CustomModelBakery.registerNewTransformer(new GeoArmor());
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
		eventBus.addListener(GeoArmor::getGeoArmorTexturePath);
		eventBus.addListener(this::geoEntityRenderPreEvent);
		eventBus.addListener(this::geoEntityRenderPostEvent);
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
	}
	
	@OnlyIn(Dist.CLIENT)
	public void geoEntityRenderPreEvent(GeoRenderEvent.Entity.Pre event) {
		Entity entity = event.getEntity();
		
		if (entity.level() == null) {
			return;
		}
		
		if (entity instanceof LivingEntity livingentity) {
			RenderEngine renderEngine = ClientEngine.getInstance().renderEngine;
			
			if (renderEngine.hasRendererFor(livingentity)) {
				LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(livingentity, LivingEntityPatch.class);
				LocalPlayerPatch playerpatch = null;
				float originalYRot = 0.0F;
				
				if ((event.getPartialTick() == 0.0F || event.getPartialTick() == 1.0F) && entitypatch instanceof LocalPlayerPatch localPlayerPatch) {
					playerpatch = localPlayerPatch;
					originalYRot = playerpatch.getModelYRot();
					playerpatch.setModelYRotInGui(livingentity.getYRot());
					event.getPoseStack().translate(0, 0.1D, 0);
				}
				
				if (entitypatch != null && entitypatch.overrideRender()) {
					event.setCanceled(true);
					renderEngine.renderEntityArmatureModel(livingentity, entitypatch, event.getRenderer(), event.getBufferSource(), event.getPoseStack(), event.getPackedLight(), event.getPartialTick());
					
					if (ClientEngine.getInstance().getPlayerPatch() != null && !renderEngine.minecraft.options.hideGui && !livingentity.level().getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI)) {
						for (EntityIndicator entityIndicator : EntityIndicator.ENTITY_INDICATOR_RENDERERS) {
							if (entityIndicator.shouldDraw(livingentity, entitypatch, ClientEngine.getInstance().getPlayerPatch())) {
								entityIndicator.drawIndicator(livingentity, entitypatch, ClientEngine.getInstance().getPlayerPatch(), event.getPoseStack(), event.getBufferSource(), event.getPartialTick());
							}
						}
					}
				}
				
				if (playerpatch != null) {
					playerpatch.disableModelYRotInGui(originalYRot);
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public void geoEntityRenderPostEvent(GeoRenderEvent.Entity.Post event) {
		Entity entity = event.getEntity();
		
		if (entity.level() == null) {
			return;
		}
		
		if (entity instanceof LivingEntity livingentity) {
			RenderEngine renderEngine = ClientEngine.getInstance().renderEngine;
			
			if (ClientEngine.getInstance().getPlayerPatch() != null && !renderEngine.minecraft.options.hideGui && !livingentity.level().getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI)) {
				LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(livingentity, LivingEntityPatch.class);
				
				for (EntityIndicator entityIndicator : EntityIndicator.ENTITY_INDICATOR_RENDERERS) {
					if (entityIndicator.shouldDraw(livingentity, entitypatch, ClientEngine.getInstance().getPlayerPatch())) {
						entityIndicator.drawIndicator(livingentity, entitypatch, ClientEngine.getInstance().getPlayerPatch(), event.getPoseStack(), event.getBufferSource(), event.getPartialTick());
					}
				}
			}
		}
	}
}