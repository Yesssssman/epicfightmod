package yesman.epicfight.client.gui.datapack.widgets;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface DataBindingComponent<T> extends ResizableComponent {
	public void setResponder(Consumer<T> responder);
	public void setValue(@Nullable T value);
	public T getValue();
	public void reset();
}