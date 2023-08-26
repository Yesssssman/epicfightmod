package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.Util;
import net.minecraft.client.KeyboardHandler;
import yesman.epicfight.client.ClientEngine;


@Mixin(value = KeyboardHandler.class)
public abstract class MixinKeyboardHandler {
	@Shadow
	private long debugCrashKeyTime = -1L;
	
	@Inject(at = @At(value = "HEAD"), method = "handleDebugKeys(I)Z", cancellable = true)
	private void epicfight_handleDebugKeys(int key, CallbackInfoReturnable<Boolean> info) {
		if (!(this.debugCrashKeyTime > 0L && this.debugCrashKeyTime < Util.getMillis() - 100L)) {
			switch (key) {
			case 89:
				boolean flag = ClientEngine.getInstance().switchArmorModelDebuggingMode();
				this.debugFeedbackTranslated(flag ? "debug.armor_model_debugging.on" : "debug.armor_model_debugging.off");
				info.cancel();
				info.setReturnValue(true);
			}
		}
	}
	
	@Shadow
	private void debugFeedbackTranslated(String p_90914_, Object... p_90915_) {}
}