package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.world.capabilities.provider.ProviderItem;

@Mixin(value = ItemStack.class)
public abstract class MixinItemStack {
	@Inject(at = @At(value = "HEAD"), method = "copy()Lnet/minecraft/world/item/ItemStack;", cancellable = true)
	private void epicfight_copy(CallbackInfoReturnable<ItemStack> info) {
		ItemStack myself = ((ItemStack)((Object)this));
		if (ProviderItem.has(myself.getItem())) {
			info.cancel();
			ItemStack itemstack = ItemStack.of(myself.save(new CompoundTag()));
			info.setReturnValue(itemstack);
		}
	}
}