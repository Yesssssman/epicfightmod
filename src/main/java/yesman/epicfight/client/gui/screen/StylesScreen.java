package yesman.epicfight.client.gui.screen;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StylesScreen extends Screen {
	private final Screen parentScreen;
	
	public StylesScreen(Screen parentScreen) {
		super(Component.translatable("gui.epicfight.styles"));
		
		this.parentScreen = parentScreen;
	}
}