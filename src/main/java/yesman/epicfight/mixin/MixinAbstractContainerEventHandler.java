package yesman.epicfight.mixin;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.world.entity.Mob;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.HurtableEntityPatch;

@Mixin(value = AbstractContainerEventHandler.class)
public abstract class MixinAbstractContainerEventHandler {
	@Shadow
	private GuiEventListener focused;
	
	@Inject(at = @At(value = "HEAD"), method = "serverAiStep()V", cancellable = true)
	private void epicfight_serverAiStep(CallbackInfo info) {
		Mob self = (Mob)((Object)this);
		HurtableEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(self, HurtableEntityPatch.class);
		
		if (entitypatch != null && entitypatch.isStunned()) {
			info.cancel();
		}
	}
	
	@Inject(at = @At(value = "HEAD"), method = "setFocused()V", cancellable = true)
	private void epicfight_setFocused(@Nullable GuiEventListener widget, CallbackInfo info) {
		if (this.focused == widget) {
			info.cancel();
		}
	}
}