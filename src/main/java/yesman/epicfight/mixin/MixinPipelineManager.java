package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.irisshaders.iris.pipeline.WorldRenderingPipeline;
import net.irisshaders.iris.shaderpack.materialmap.NamespacedId;
import yesman.epicfight.compat.IRISCompat;

@Mixin(targets = {"net.irisshaders.iris.pipeline.PipelineManager"})
public abstract class MixinPipelineManager {
	@Inject(at = @At(value = "INVOKE", target = "Lnet/irisshaders/iris/uniforms/SystemTimeUniforms$Timer;reset()V", shift = At.Shift.AFTER), method = "preparePipeline(Lnet/irisshaders/iris/shaderpack/materialmap/NamespacedId;)V", remap = false)
	private void epicfight_preparePipeline(NamespacedId id, CallbackInfoReturnable<WorldRenderingPipeline> info) {
		IRISCompat.clearIrisShaders();
	}
}
