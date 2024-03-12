package yesman.epicfight.client.gui.screen;

import java.util.function.Consumer;

import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.registries.IForgeRegistry;

public class SelectAnimationScreen extends SelectFromRegistryScreen {

	public SelectAnimationScreen(Screen parentScreen, IForgeRegistry registry, Consumer selectCallback) {
		super(parentScreen, registry, selectCallback);
	}
}
