package yesman.epicfight.client.gui.screen.overlay;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayManager {
	private final Map<String, OverlayManager.Overlay> overlays = Maps.newHashMap();
	
	public void renderTick(int xResolution, int yResolution) {
		List<String> toRemove = Lists.newArrayList();
		
		for (Map.Entry<String, OverlayManager.Overlay> entry : this.overlays.entrySet()) {
			OverlayManager.Overlay overlay = entry.getValue();
			overlay.render(xResolution, yResolution);
			
			if (overlay.isRemoved) {
				toRemove.add(entry.getKey());
			}
		}
		
		toRemove.forEach(this.overlays::remove);
		this.overlays.values().forEach(overlay -> overlay.render(xResolution, yResolution));
	}
	
	public void remove(String overlayId) {
		this.overlays.remove(overlayId);
	}
	
	public void blendingTexture(String overlayId, ResourceLocation texture) {
		if (Minecraft.renderNames()) {
			this.overlays.put(overlayId, new BlendingTextureOverlay(texture));
		}
	}
	
	public void flickering(String overlayId, float deltaTime, float strength) {
		if (Minecraft.renderNames()) {
			this.overlays.put(overlayId, new FlickeringOverlay(deltaTime, strength));
		}
	}
	
	public abstract static class Overlay {
		protected boolean isRemoved;
		
		public abstract void render(int xResolution, int yResolution);
	}
}