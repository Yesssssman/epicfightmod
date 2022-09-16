package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EnderDragonRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraftforge.common.MinecraftForge;
import yesman.epicfight.api.client.forgeevent.RenderEnderDragonEvent;

@Mixin(value = EnderDragonRenderer.class)
public abstract class MixinEnderDragonRenderer {
	@Inject(at = @At(value = "HEAD"), method = "render(Lnet/minecraft/entity/Entity;FFLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer;I)V", cancellable = true)
	private void epicfight_render(Entity enderdragon, float yRot, float partialTicks, MatrixStack poseStack, IRenderTypeBuffer multiSourceBuffer, int packedLight, CallbackInfo info) {
		RenderEnderDragonEvent renderDragonEvent = new RenderEnderDragonEvent((EnderDragonEntity)enderdragon, (EnderDragonRenderer)((Object)this), partialTicks, poseStack, multiSourceBuffer, packedLight);
		
		if (MinecraftForge.EVENT_BUS.post(renderDragonEvent)) {
			info.cancel();
		}
	}
}