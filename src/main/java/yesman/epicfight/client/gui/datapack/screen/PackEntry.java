package yesman.epicfight.client.gui.datapack.screen;

import java.util.function.Supplier;

import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PackEntry<K, T extends Tag> {
	public static <K, T extends Tag> PackEntry<K, T> of(K packName, Supplier<T> tagGetter) {
		return new PackEntry<>(packName, tagGetter);
	}
	
	private K packName;
	private T packTag;
	
	private PackEntry(K packName, Supplier<T> tagGetter) {
		this.packName = (packName);
		this.packTag = tagGetter.get();
	}
	
	public void setPackName(K packName) {
		this.packName = (packName);
	}
	
	public K getPackName() {
		return this.packName;
	}
	
	public T getTag() {
		return this.packTag;
	}
}
