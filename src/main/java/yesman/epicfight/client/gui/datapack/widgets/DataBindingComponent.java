package yesman.epicfight.client.gui.datapack.widgets;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface DataBindingComponent<T> extends ResizableComponent {
	public T getValue();
	public void setValue(@Nullable T value);
	public void reset();
	public void setResponder(Consumer<T> responder);
}