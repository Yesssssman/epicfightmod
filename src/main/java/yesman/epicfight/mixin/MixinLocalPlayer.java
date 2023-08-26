package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;

@Mixin(value = LocalPlayer.class)
public abstract class MixinLocalPlayer {
	@Shadow
	private void sendPosition() {throw new AbstractMethodError("Shadow");}
	
	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;sendPosition()V"), method = "tick()V")
	private void epicfight_tick(LocalPlayer entity) {
		entity.connection.send(new ServerboundPlayerInputPacket(entity.xxa, entity.zza, entity.input.jumping, entity.input.shiftKeyDown));
		 
		this.sendPosition();
	}
}