package yesman.epicfight.client.input;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class EpicFightKeyMappings {
	public static final KeyMapping WEAPON_INNATE_SKILL_TOOLTIP = new KeyMapping("key." + EpicFightMod.MODID + ".show_tooltip", InputConstants.KEY_LSHIFT, "key." + EpicFightMod.MODID + ".gui");
	public static final KeyMapping SWITCH_MODE = new KeyMapping("key." + EpicFightMod.MODID + ".switch_mode", InputConstants.KEY_R, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyMapping DODGE = new KeyMapping("key." + EpicFightMod.MODID + ".dodge", InputConstants.KEY_LALT, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyMapping WEAPON_INNATE_SKILL = new WeaponInnateSkillKeyMapping("key." + EpicFightMod.MODID + ".weapon_innate_skill", InputConstants.Type.MOUSE, 0, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyMapping MOVER_SKILL = new WeaponInnateSkillKeyMapping("key." + EpicFightMod.MODID + ".mover_skill", InputConstants.Type.KEYSYM, InputConstants.KEY_SPACE, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyMapping SKILL_EDIT = new KeyMapping("key." + EpicFightMod.MODID + ".skill_gui", InputConstants.KEY_K, "key." + EpicFightMod.MODID + ".gui");
	public static final KeyMapping LOCK_ON = new KeyMapping("key." + EpicFightMod.MODID + ".lock_on", InputConstants.Type.MOUSE, 2, "key." + EpicFightMod.MODID + ".combat");
	public static final KeyMapping CONFIG = new KeyMapping("key." + EpicFightMod.MODID + ".config", -1, "key." + EpicFightMod.MODID + ".gui");

	public static void registerKeys(RegisterKeyMappingsEvent event) {
		event.register(WEAPON_INNATE_SKILL_TOOLTIP);
		event.register(SWITCH_MODE);
		event.register(DODGE);
		event.register(WEAPON_INNATE_SKILL);
		event.register(MOVER_SKILL);
		event.register(SKILL_EDIT);
		event.register(LOCK_ON);
		event.register(CONFIG);
	}
}