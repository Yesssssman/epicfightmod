package yesman.epicfight.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;

public class SpecialAttackKeyMapping extends KeyMapping {
	public SpecialAttackKeyMapping(String description, InputConstants.Type type, int code, String category) {
		super(description, type, code, category);
	}
	
	@Override
	public boolean isActiveAndMatches(InputConstants.Key keyCode) {
        return super.isActiveAndMatches(keyCode) && !keyCode.equals(Minecraft.getInstance().options.keyAttack.getKey());
    }
}