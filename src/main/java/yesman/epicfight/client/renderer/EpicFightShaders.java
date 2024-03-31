package yesman.epicfight.client.renderer;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.ShaderInstance;

public class EpicFightShaders {
	public static ShaderInstance positionColorNormalShader;
	
	@Nullable
	public static ShaderInstance getPositionColorNormalShader() {
		return positionColorNormalShader;
	}
}
