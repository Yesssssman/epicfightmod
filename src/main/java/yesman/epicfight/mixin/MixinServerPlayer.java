package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.level.ServerPlayer;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPUpdatePlayerInput;

@Mixin(value = ServerPlayer.class)
public abstract class MixinServerPlayer {
	@Inject(at = @At(value = "HEAD"), method = "setPlayerInput(FFZZ)V", cancellable = true)
	private void epicfight_setPlayerInput(float xxa, float zza, boolean jump, boolean shift, CallbackInfo info) {
		ServerPlayer self = (ServerPlayer)((Object)this);
		
		if (xxa >= -1.0F && xxa <= 1.0F) {
			self.xxa = xxa;
		}
		
		if (zza >= -1.0F && zza <= 1.0F) {
			self.zza = zza;
		}
		
		self.setJumping(jump);
		self.setShiftKeyDown(shift);
		
		SPUpdatePlayerInput packet = new SPUpdatePlayerInput(self.getId(), xxa, zza);
		EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(packet, self);
		
		info.cancel();
	}
}