package yesman.epicfight.client.gui.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeaponTypeEditScreen extends Screen {
	private static final Gson GSON = (new GsonBuilder()).create();
	
	public WeaponTypeEditScreen(Component component) {
		super(component);
	}
}