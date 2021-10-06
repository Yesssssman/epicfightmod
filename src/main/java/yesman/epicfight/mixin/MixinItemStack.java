package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import yesman.epicfight.capabilities.provider.ProviderItem;

@Mixin(value = ItemStack.class)
public abstract class MixinItemStack {
	@Inject(at = @At(value = "HEAD"), method = "copy()Lnet/minecraft/item/ItemStack;", cancellable = true)
	private void copyWithCheck(CallbackInfoReturnable<ItemStack> info) {
		ItemStack myself = ((ItemStack)((Object)this));
		if (ProviderItem.has(myself.getItem())) {
			info.cancel();
			ItemStack itemstack = ItemStack.read(myself.write(new CompoundNBT()));
			info.setReturnValue(itemstack);
		}
	}
}