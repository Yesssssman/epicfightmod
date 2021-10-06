package yesman.epicfight.client.input;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class ModKeys {
	public static final KeyBinding SPECIAL_SKILL_TOOLTIP = new KeyBinding("key." + EpicFightMod.MODID + ".show_tooltip", 80, "key." + EpicFightMod.MODID + ".gui");
	public static final KeyBinding SWITCH_MODE = new KeyBinding("key." + EpicFightMod.MODID + ".switch_mode", 82, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyBinding DODGE = new KeyBinding("key." + EpicFightMod.MODID + ".dodge", 340, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyBinding SPECIAL_SKILL = new SpecialAttackKeyBinding("key." + EpicFightMod.MODID + ".weapon_special_skill", InputMappings.Type.MOUSE, 0, "key." + EpicFightMod.MODID + ".combat");
	
	public static void registerKeys() {
		ClientRegistry.registerKeyBinding(SPECIAL_SKILL_TOOLTIP);
		ClientRegistry.registerKeyBinding(SWITCH_MODE);
		ClientRegistry.registerKeyBinding(DODGE);
		ClientRegistry.registerKeyBinding(SPECIAL_SKILL);
	}
}