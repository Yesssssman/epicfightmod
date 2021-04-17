package maninthehouse.epicfight.client.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;

import net.minecraft.client.gui.GuiButton;

public class OptionButton<T extends Object> extends GuiButton {
	private final Consumer<OptionButton> onClicked;
	private final Supplier<String> refreshText;
	
	public OptionButton(int buttonId, int x, int y, int widthIn, int heightIn,
			Consumer<OptionButton> onClicked, Supplier<String> refreshText) {
		super(buttonId, x, y, widthIn, heightIn, refreshText.get());
		this.onClicked = onClicked;
		this.refreshText = refreshText;
	}
	
	public void onClicked() {
		this.onClicked.accept(this);
		this.refreshText();
	}
	
	public void refreshText() {
		this.displayString = this.refreshText.get();
	}
}