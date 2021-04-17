package maninthehouse.epicfight.client.input;

import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModKeys {
	public static final KeyBinding SPECIAL_ATTACK_TOOLTIP = new KeyBinding("key." + EpicFightMod.MODID + ".show_tooltip", 25, "key." + EpicFightMod.MODID + ".gui");
	public static final KeyBinding SWITCH_MODE = new KeyBinding("key." + EpicFightMod.MODID + ".switch_mode", 19, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyBinding DODGE = new KeyBinding("key." + EpicFightMod.MODID + ".dodge", 42, "key." + EpicFightMod.MODID + ".combat");
	
	public static void registerKeys() {
		ClientRegistry.registerKeyBinding(SPECIAL_ATTACK_TOOLTIP);
		ClientRegistry.registerKeyBinding(SWITCH_MODE);
		ClientRegistry.registerKeyBinding(DODGE);
	}
}