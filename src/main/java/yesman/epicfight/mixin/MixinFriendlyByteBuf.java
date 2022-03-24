package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

@Mixin(value = FriendlyByteBuf.class)
public abstract class MixinFriendlyByteBuf {
	@Inject(at = @At(value = "HEAD"), method = "readItem()Lnet/minecraft/world/item/ItemStack;", cancellable = true)
	private void epicfight_readItem(CallbackInfoReturnable<ItemStack> info) {
		info.cancel();
		FriendlyByteBuf pb = (FriendlyByteBuf)((Object)this);
		
		if (!pb.readBoolean()) {
			info.setReturnValue(ItemStack.EMPTY);
		} else {
			int i = pb.readVarInt();
			int j = pb.readByte();
			Item item = Item.byId(i);
			CompoundTag constructTag = new CompoundTag();
			CompoundTag customTag = pb.readNbt();
			if (customTag != null) {
				constructTag.put("tag", customTag);
			}
			constructTag.putInt("Count", j);
			constructTag.putString("id", item.getRegistryName().toString());
			ItemStack itemstack = ItemStack.of(constructTag);
			info.setReturnValue(itemstack);
		}
	}
}