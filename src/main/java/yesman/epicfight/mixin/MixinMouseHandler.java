package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.Blaze3D;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.util.SmoothDouble;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@Mixin(value = MouseHandler.class)
public abstract class MixinMouseHandler {
	
	@Shadow private Minecraft minecraft;
	@Final @Shadow private SmoothDouble smoothTurnX;
	@Final @Shadow private SmoothDouble smoothTurnY;
	@Shadow private double accumulatedDX;
	@Shadow private double accumulatedDY;
	@Shadow private double lastMouseEventTime;
	
	@Inject(at = @At(value = "HEAD"), method = "turnPlayer()V", cancellable = true)
	private void epicfight_turnPlayer(CallbackInfo info) {
		double d0 = Blaze3D.getTime();
		double d1 = d0 - this.lastMouseEventTime;
		this.lastMouseEventTime = d0;
		
		MouseHandler self = (MouseHandler)((Object)this);
		
		if (self.isMouseGrabbed() && this.minecraft.isWindowActive()) {
			double d4 = this.minecraft.options.sensitivity * (double) 0.6F + (double) 0.2F;
			double d5 = d4 * d4 * d4;
			double d6 = d5 * 8.0D;
			double d2;
			double d3;
			
			if (this.minecraft.options.smoothCamera) {
				double d7 = this.smoothTurnX.getNewDeltaValue(this.accumulatedDX * d6, d1 * d6);
				double d8 = this.smoothTurnY.getNewDeltaValue(this.accumulatedDY * d6, d1 * d6);
				d2 = d7;
				d3 = d8;
			} else if (this.minecraft.options.getCameraType().isFirstPerson() && this.minecraft.player.isScoping()) {
				this.smoothTurnX.reset();
				this.smoothTurnY.reset();
				d2 = this.accumulatedDX * d5;
				d3 = this.accumulatedDY * d5;
			} else {
				this.smoothTurnX.reset();
				this.smoothTurnY.reset();
				d2 = this.accumulatedDX * d6;
				d3 = this.accumulatedDY * d6;
			}
			
			this.accumulatedDX = 0.0D;
			this.accumulatedDY = 0.0D;
			int i = 1;
			
			if (this.minecraft.options.invertYMouse) {
				i = -1;
			}
			
			this.minecraft.getTutorial().onMouse(d2, d3);
			
			if (this.minecraft.player != null) {
				LocalPlayerPatch playerpatch = (LocalPlayerPatch)this.minecraft.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
				RenderEngine renderEngine = ClientEngine.instance.renderEngine;
				
				if (!playerpatch.getEntityState().turningLocked() || this.minecraft.player.isRidingJumpable()) {
					
					if (renderEngine.isPlayerRotationLocked()) {
						renderEngine.unlockRotation(this.minecraft.player);
					}
					
					this.minecraft.player.turn(d2, d3 * (double)i);
				} else {
					renderEngine.setCameraRotation((float)(d3 * i), (float)d2);
				}
			}
		} else {
			this.accumulatedDX = 0.0D;
			this.accumulatedDY = 0.0D;
		}
	}
}