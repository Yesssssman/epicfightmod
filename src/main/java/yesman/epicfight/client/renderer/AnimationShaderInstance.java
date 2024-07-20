package yesman.epicfight.client.renderer;

import java.io.IOException;

import javax.annotation.Nullable;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimationShaderInstance extends ShaderInstance {
	@Nullable
	public final Uniform[] POSES;
	
	public AnimationShaderInstance(ResourceProvider resourceProvider, ResourceLocation resourceLocation, VertexFormat vertexFormat) throws IOException {
		super(resourceProvider, resourceLocation, vertexFormat);
		
		this.POSES = new Uniform[AnimationShaderTransformer.MAX_JOINTS];
		
		for (int i = 0; i < AnimationShaderTransformer.MAX_JOINTS; i++) {
			this.POSES[i] = this.getUniform("Poses" + String.valueOf(i));
		}
	}
}
