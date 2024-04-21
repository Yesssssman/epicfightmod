package yesman.epicfight.client.gui.datapack.widgets;

import java.util.function.BiFunction;
import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubScreenOpenButton<T> extends ResizableButton {
	protected final Screen owner;
	protected final Supplier<T> compoundTagProvider;
	protected final BiFunction<Screen, T, Screen> subScreenProvider;
	
	public SubScreenOpenButton(Builder<T> builder) {
		super(builder);
		
		this.owner = builder.owner;
		this.compoundTagProvider = builder.compoundTagProvider;
		this.subScreenProvider = builder.subScreenProvider;
	}
	
	@Override
	public void onPress() {
		Minecraft.getInstance().setScreen(this.subScreenProvider.apply(this.owner, this.compoundTagProvider.get()));
	}
	
	public static <T> SubScreenOpenButton.Builder<T> builder(Screen parent) {
		return new SubScreenOpenButton.Builder<>(parent);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Builder<T> extends ResizableButton.Builder {
		Screen owner;
		Supplier<T> compoundTagProvider;
		BiFunction<Screen, T, Screen> subScreenProvider;
		
		public Builder(Screen owner) {
			super(CommonComponents.ELLIPSIS, null);
			
			this.owner = owner;
		}
		
		public SubScreenOpenButton.Builder<T> subScreen(BiFunction<Screen, T, Screen> subScreenProvider) {
			this.subScreenProvider = subScreenProvider;
			return this;
		}
		
		public SubScreenOpenButton.Builder<T> compoundTag(Supplier<T> compoundTagProvider) {
			this.compoundTagProvider = compoundTagProvider;
			return this;
		}
		
		@Override
		public SubScreenOpenButton<T> build() {
			return new SubScreenOpenButton<> (this);
		}
	}
}
