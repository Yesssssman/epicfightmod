package yesman.epicfight.client.input;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpecialAttackKeyMapping extends KeyBinding {
	public SpecialAttackKeyMapping(String description, InputMappings.Type type, int code, String category) {
		super(description, type, code, category);
	}
	
	@Override
	public boolean isActiveAndMatches(InputMappings.Input keyCode) {
        return super.isActiveAndMatches(keyCode) && !keyCode.equals(Minecraft.getInstance().options.keyAttack.getKey());
    }
}