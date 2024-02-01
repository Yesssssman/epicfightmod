package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LocalPlayer.class)
public abstract class MixinLocalPlayer {

	@Unique
	private final LocalPlayer epicfight$entity = (LocalPlayer)(Object)this;
	
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendPosition()V", shift = At.Shift.BEFORE), method = "tick()V")
	private void epicfight_tick(CallbackInfo ci) {
		epicfight$entity.connection.send(new ServerboundPlayerInputPacket(epicfight$entity.xxa, epicfight$entity.zza, epicfight$entity.input.jumping, epicfight$entity.input.shiftKeyDown));
	}
}