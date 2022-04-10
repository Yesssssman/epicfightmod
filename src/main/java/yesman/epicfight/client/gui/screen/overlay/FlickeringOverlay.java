package yesman.epicfight.client.gui.screen.overlay;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public class FlickeringOverlay extends OverlayManager.Overlay {
	private float time = (float)-Math.PI;
	private final float deltaTime;
	private final float strength;
	private final double initialGamma;
	
	public FlickeringOverlay(float deltaTime, float strength) {
		this.deltaTime = deltaTime;
		this.strength = strength;
		Minecraft minecraft = Minecraft.getInstance();
		this.initialGamma = minecraft.options.gamma;
	}
	
	@Override
	public void render(int xResolution, int yResolution) {
		this.time += this.deltaTime;
		float darkenAmount = Mth.clamp((float)Math.sin(this.time), -1.0F, 0.0F);
		
		Minecraft minecraft = Minecraft.getInstance();
		minecraft.options.gamma = this.initialGamma + darkenAmount * strength;
		
		if (this.time >= 0) {
			this.isRemoved = true;
		}
	}
}