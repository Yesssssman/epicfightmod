package yesman.epicfight.client.gui.datapack.screen;

import java.util.function.Supplier;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PackEntry<K, T> {
	public static <K, T> PackEntry<K, T> of(K packKey, Supplier<T> valueGetter) {
		return new PackEntry<>(packKey, valueGetter.get());
	}
	
	public static <K, T> PackEntry<K, T> ofValue(K packKey, T valueGetter) {
		return new PackEntry<>(packKey, valueGetter);
	}
	
	private K packKey;
	private T packValue;
	
	private PackEntry(K packKey, T valueGetter) {
		this.packKey = (packKey);
		this.packValue = valueGetter;
	}
	
	public void setPackKey(K packKey) {
		this.packKey = packKey;
	}
	
	public void setPackValue(T packValue) {
		this.packValue = packValue;
	}
	
	public K getPackKey() {
		return this.packKey;
	}
	
	public T getPackValue() {
		return this.packValue;
	}
}
