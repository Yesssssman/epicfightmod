package yesman.epicfight.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.client.util.InputMappings.Type;

public class SpecialAttackKeyBinding extends KeyBinding {
	public SpecialAttackKeyBinding(String description, Type type, int code, String category) {
		super(description, type, code, category);
	}
	
	@Override
	public boolean isActiveAndMatches(InputMappings.Input keyCode) {
        return super.isActiveAndMatches(keyCode) && !keyCode.equals(Minecraft.getInstance().gameSettings.keyBindAttack.getKey());
    }
}