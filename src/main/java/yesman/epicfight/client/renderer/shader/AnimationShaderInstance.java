package yesman.epicfight.client.renderer.shader;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface AnimationShaderInstance {
	public Uniform getModelViewMatrixUniform();
	
	public Uniform getProjectionMatrixUniform();
	
	public Uniform getInverseViewRotationMatrixUniform();
	
	public Uniform getColorModulatorUniform();
	
	public Uniform getGlintAlphaUniform();
	
	public Uniform getFogStartUniform();
	
	public Uniform getFogEndUniform();
	
	public Uniform getFogColorUniform();
	
	public Uniform getFogShapeUniform();
	
	public Uniform getTextureMatrixUniform();
	
	public Uniform getGameTimeUniform();
	
	public Uniform getScreenSizeUniform();
	
	public Uniform getColorUniform();
	
	public Uniform getOverlayUniform();
	
	public Uniform getLightUniform();
	
	public Uniform getNormalMatrixUniform();
	
	public Uniform getPoses(int i);
	
	public void setSampler(String samplerName, Object texture);
	
	default void setupShaderLights() {
		RenderSystem.setupShaderLights((ShaderInstance)this);
	}
	
	public String _getName();
	
	public void _apply();
	
	public void _clear();
	
	public void _close();
	
	public VertexFormat _getVertexFormat();
}
