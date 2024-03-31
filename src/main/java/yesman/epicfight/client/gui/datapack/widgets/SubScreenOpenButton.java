package yesman.epicfight.client.gui.datapack.widgets;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubScreenOpenButton extends ResizableButton {
	protected final Screen owner;
	protected final Supplier<CompoundTag> compoundTagProvider;
	protected final BiFunction<Screen, CompoundTag, Screen> subScreenProvider;
	
	public SubScreenOpenButton(Builder builder) {
		super(builder);
		
		this.owner = builder.owner;
		this.compoundTagProvider = builder.compoundTagProvider;
		this.subScreenProvider = builder.subScreenProvider;
	}
	
	@Override
	public void onPress() {
		Minecraft.getInstance().setScreen(this.subScreenProvider.apply(this.owner, this.compoundTagProvider.get()));
	}
	
	public static SubScreenOpenButton.Builder builder(Screen parent) {
		return new SubScreenOpenButton.Builder(parent);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Builder extends ResizableButton.Builder {
		Screen owner;
		Supplier<CompoundTag> compoundTagProvider;
		BiFunction<Screen, CompoundTag, Screen> subScreenProvider;
		
		public Builder(Screen owner) {
			super(CommonComponents.ELLIPSIS, null);
			
			this.owner = owner;
		}
		
		public SubScreenOpenButton.Builder subScreen(BiFunction<Screen, CompoundTag, Screen> subScreenProvider) {
			this.subScreenProvider = subScreenProvider;
			return this;
		}
		
		public SubScreenOpenButton.Builder compoundTag(Supplier<CompoundTag> compoundTagProvider) {
			this.compoundTagProvider = compoundTagProvider;
			return this;
		}
		
		@Override
		public SubScreenOpenButton build() {
			return new SubScreenOpenButton(this);
		}
	}
}
