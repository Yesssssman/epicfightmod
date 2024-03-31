package yesman.epicfight.client.gui.datapack.widgets;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface DataBindingComponent<T> extends ResizableComponent {
	public void setValue(@Nullable T value);
	public T getValue();
	public void reset();
}