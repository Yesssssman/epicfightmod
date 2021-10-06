package yesman.epicfight.client.gui.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RewindableButton extends Button {
	protected final Button.IPressable onRewindPress;
	
	public RewindableButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction, IPressable rewindPressedAction, ITooltip onTooltip) {
		super(x, y, width, height, title, pressedAction, onTooltip);
		this.onRewindPress = rewindPressedAction;
	}
	
	@Override
	protected boolean isValidClickButton(int button) {
		return button == 0 || button == 1;
	}
	
	public void onClick(double mouseX, double mouseY, int button) {
		if (button == 0) {
			super.onClick(mouseX, mouseY);
		} else {
			this.onRewindPress.onPress(this);
		}
	}
	
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				boolean flag = this.clicked(mouseX, mouseY);
				if (flag) {
					this.playDownSound(Minecraft.getInstance().getSoundHandler());
					this.onClick(mouseX, mouseY, button);
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}
}