package yesman.epicfight.client.input;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WeaponInnateSkillKeyMapping extends KeyMapping {
	public WeaponInnateSkillKeyMapping(String description, InputConstants.Type type, int code, String category) {
		super(description, type, code, category);
	}
	
	@Override
	public boolean isActiveAndMatches(InputConstants.Key keyCode) {
        return super.isActiveAndMatches(keyCode) && !keyCode.equals(Minecraft.getInstance().options.keyAttack.getKey());
    }
}