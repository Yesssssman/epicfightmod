package yesman.epicfight.api.client.forgeevent;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.client.renderer.patched.entity.PatchedEntityRenderer;
import yesman.epicfight.client.renderer.patched.item.RenderItemBase;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("rawtypes")
public abstract class PatchedRenderersEvent extends Event implements IModBusEvent {
	public static class Add extends PatchedRenderersEvent {
		private final Map<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> entityRendererProvider;
		private final Map<Item, RenderItemBase> itemRenerers;
		private final EntityRendererProvider.Context context;
		
		public Add(Map<EntityType<?>, Function<EntityType<?>, PatchedEntityRenderer>> entityRendererProvider, Map<Item, RenderItemBase> itemRenerers, EntityRendererProvider.Context context) {
			this.entityRendererProvider = entityRendererProvider;
			this.itemRenerers = itemRenerers;
			this.context = context;
		}
		
		public void addPatchedEntityRenderer(EntityType<?> entityType, Function<EntityType<?>, PatchedEntityRenderer> provider) {
			this.entityRendererProvider.put(entityType, provider);
		}
		
		public void addItemRenderer(Item item, RenderItemBase renderer) {
			this.itemRenerers.put(item, renderer);
		}
		
		public EntityRendererProvider.Context getContext() {
			return this.context;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Modify extends PatchedRenderersEvent {
		private final Map<EntityType<?>, PatchedEntityRenderer> renderers;
		
		public Modify(Map<EntityType<?>, PatchedEntityRenderer> renderers) {
			this.renderers = renderers;
		}
		
		public PatchedEntityRenderer get(EntityType<?> entityType) {
			return this.renderers.get(entityType);
		}
	}
}