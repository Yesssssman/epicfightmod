package yesman.epicfight.compat;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;
import top.theillusivec4.curios.client.render.CuriosLayer;
import yesman.epicfight.api.client.forgeevent.PatchedRenderersEvent;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.client.renderer.patched.layer.ModelRenderLayer;
import yesman.epicfight.compat.curios.RelicsModelProvider;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class CuriosCompat implements ICompatModule {
	@SuppressWarnings("unchecked")
	@Override
	public void onModEventBusClient(IEventBus eventBus) {
		eventBus.<PatchedRenderersEvent.Modify>addListener((event) -> {
			if (event.get(EntityType.PLAYER) instanceof PatchedLivingEntityRenderer patchedlivingrenderer) {
				//patchedlivingrenderer.addPatchedLayer(CuriosLayer.class, new CuriosLayerRenderer());
			}
		});
	}
	
	@Override
	public void onForgeEventBusClient(IEventBus eventBus) {
	}
	
	@Override
	public void onModEventBus(IEventBus eventBus) {
	}
	
	@Override
	public void onForgeEventBus(IEventBus eventBus) {
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class CuriosLayerRenderer extends ModelRenderLayer<LivingEntity, LivingEntityPatch<LivingEntity>, EntityModel<LivingEntity>, CuriosLayer<LivingEntity, EntityModel<LivingEntity>>, AnimatedMesh> {
		private static final List<Function<ItemStack, HumanoidModel<?>>> CURIO_MODEL_GETTERS = Lists.newArrayList();
		private static final Map<String, Map<ItemStack, HumanoidModel<?>>> CURIOS_MODELS_BY_SLOTS = Maps.newHashMap();
		
		private static HumanoidModel<?> getCurioModel(ItemStack itemstack) {
			for (Function<ItemStack, HumanoidModel<?>> modelGetter : CURIO_MODEL_GETTERS) {
				HumanoidModel<?> humanoidModel = modelGetter.apply(itemstack);
				
				if (humanoidModel != null) {
					return humanoidModel;
				}
			}
			
			return null;
		}
		
		public CuriosLayerRenderer() {
			super(null);
			
			if (ModList.get().isLoaded("relics")) {
				CURIO_MODEL_GETTERS.add(RelicsModelProvider::getCuriosModel);
			}
		}
		
		@Override
		protected void renderLayer(LivingEntityPatch<LivingEntity> entitypatch, LivingEntity entityliving, CuriosLayer<LivingEntity, EntityModel<LivingEntity>> vanillaLayer, PoseStack poseStack, MultiBufferSource buffer, int packedLight,
				OpenMatrix4f[] poses, float bob, float yRot, float xRot, float partialTicks) {
			CuriosApi.getCuriosInventory(entityliving).ifPresent((handler) -> {
				handler.getCurios().forEach((id, stacksHandler) -> {
					IDynamicStackHandler stackHandler = stacksHandler.getStacks();
					IDynamicStackHandler cosmeticStacksHandler = stacksHandler.getCosmeticStacks();
					
					//System.out.println(id +" , "+ stackHandler.getSlots());
					
					for (int i = 0; i < stackHandler.getSlots(); i++) {
						ItemStack stack = cosmeticStacksHandler.getStackInSlot(i);
						//boolean cosmetic = true;
						NonNullList<Boolean> renderStates = stacksHandler.getRenders();
						boolean renderable = renderStates.size() > i && renderStates.get(i);
						
						if (stack.isEmpty() && renderable) {
							stack = stackHandler.getStackInSlot(i);
							//cosmetic = false;
						}
						
						if (!stack.isEmpty()) {
							//SlotContext slotContext = new SlotContext(id, entityliving, i, cosmetic, renderable);
							ItemStack finalStack = stack;
							
							Map<ItemStack, HumanoidModel<?>> models = CURIOS_MODELS_BY_SLOTS.computeIfAbsent(id, (k) -> Maps.newHashMap());
							
							CURIOS_MODELS_BY_SLOTS.get(id);
							
							HumanoidModel<?> modele = getCurioModel(finalStack);
							
							if (modele != null) {
								
							}
							
							/**
							CuriosRendererRegistry.getRenderer(stack.getItem()).ifPresent(renderer -> renderer.render(finalStack, slotContext, poseStack, renderLayerParent, renderTypeBuffer, light, limbSwing, limbSwingAmount,
											partialTicks, ageInTicks, netHeadYaw, headPitch));
							**/
						}
					}
				});
			});
		}
	}
}