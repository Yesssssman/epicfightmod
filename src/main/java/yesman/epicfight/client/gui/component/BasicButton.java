package yesman.epicfight.client.gui.component;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class BasicButton extends Button {
    public static final OnTooltip NO_TOOLTIP = (button, guiGraphices, mouseX, mouseY) -> {
    };
    
    protected OnTooltip onTooltip;
    
    public BasicButton(int x, int y, int width, int height, Component message, OnPress press) {
        this(x, y, width, height, message, press, NO_TOOLTIP);
    }
    
    public BasicButton(int x, int y, int width, int height, Component message, OnPress press, OnTooltip onTooltip) {
        super(x, y, width, height, message, press, onTooltip);
        this.onTooltip = onTooltip;
    }
    
    public void renderToolTip(GuiGraphics guiGraphics, int i, int j) {
        this.onTooltip.onTooltip(this, guiGraphics, i, j);
    }
    
    public interface OnTooltip extends Button.CreateNarration {
    	
        void onTooltip(Button var1, GuiGraphics guiGraphics, int var3, int var4);
        
        default MutableComponent createNarrationMessage(Supplier<MutableComponent> supplier) {
            return supplier.get();
        }
    }
}
