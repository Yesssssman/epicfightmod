package yesman.epicfight.mixin;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.VertexFormat;

import net.irisshaders.iris.gl.blending.AlphaTest;
import net.irisshaders.iris.gl.blending.BlendModeOverride;
import net.irisshaders.iris.gl.blending.BufferBlendOverride;
import net.irisshaders.iris.gl.framebuffer.GlFramebuffer;
import net.irisshaders.iris.gl.image.ImageHolder;
import net.irisshaders.iris.gl.sampler.SamplerHolder;
import net.irisshaders.iris.gl.uniform.DynamicLocationalUniformHolder;
import net.irisshaders.iris.pipeline.IrisRenderingPipeline;
import net.irisshaders.iris.uniforms.custom.CustomUniforms;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import yesman.epicfight.api.exception.ShaderParsingException;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.shader.AnimationShaderInstance;
import yesman.epicfight.client.renderer.shader.IrisAnimationShader;
import yesman.epicfight.client.renderer.shader.ShaderParser;
import yesman.epicfight.compat.IRISCompat;
import yesman.epicfight.main.EpicFightMod;

@Mixin(targets = {"net.irisshaders.iris.pipeline.programs.ExtendedShader"})
public abstract class MixinIrisShaderInstance {
	@Inject(at = @At(value = "TAIL"), method = "<init>", cancellable = true)
	private void epicfight_constructor(ResourceProvider resourceFactory, String name, VertexFormat vertexFormat, boolean usesTessellation, GlFramebuffer writingToBeforeTranslucent, GlFramebuffer writingToAfterTranslucent,
			BlendModeOverride blendModeOverride, AlphaTest alphaTest, Consumer<DynamicLocationalUniformHolder> uniformCreator, BiConsumer<SamplerHolder, ImageHolder> samplerCreator, boolean isIntensity, IrisRenderingPipeline parent,
			@Nullable List<BufferBlendOverride> bufferBlendOverrides, CustomUniforms customUniforms, CallbackInfo info)
	{
		if (!(this instanceof AnimationShaderInstance)) {
			IRISCompat.putIrisShaderProvider(name, () -> {
				try {
					ShaderParser shaderParser = new ShaderParser(resourceFactory, name);
					
					if (shaderParser.hasAttribute("Color")) {
						shaderParser.addUniform("iris_Color", ShaderParser.GLSLType.VEC4, "in vec4 iris_Color;", ShaderParser.InsertPosition.FOLLOWING, Integer.MAX_VALUE, new Double[] {1.0D, 1.0D, 1.0D, 1.0D});
					}
					
					if (shaderParser.hasAttribute("UV1")) {
						shaderParser.addUniform("iris_UV1", ShaderParser.GLSLType.IVEC2, "in ivec2 iris_UV1;", ShaderParser.InsertPosition.FOLLOWING, Integer.MAX_VALUE, new Integer[] {0, 0});
					}
					
					if (shaderParser.hasAttribute("UV2")) {
						shaderParser.addUniform("iris_UV2", ShaderParser.GLSLType.IVEC2, "in ivec2 iris_UV2;", ShaderParser.InsertPosition.FOLLOWING, Integer.MAX_VALUE, new Integer[] {0, 0});
					}
					
					shaderParser.remove("Color", ShaderParser.Usage.ATTRIBUTE, ShaderParser.ExceptionHandler.IGNORE);
					shaderParser.remove("UV1", ShaderParser.Usage.ATTRIBUTE, ShaderParser.ExceptionHandler.IGNORE);
					shaderParser.remove("UV2", ShaderParser.Usage.ATTRIBUTE, ShaderParser.ExceptionHandler.IGNORE);
					shaderParser.remove("iris_Color", ShaderParser.Usage.ATTRIBUTE, ShaderParser.ExceptionHandler.IGNORE);
					shaderParser.remove("iris_UV1", ShaderParser.Usage.ATTRIBUTE, ShaderParser.ExceptionHandler.IGNORE);
					shaderParser.remove("iris_UV2", ShaderParser.Usage.ATTRIBUTE, ShaderParser.ExceptionHandler.IGNORE);
					shaderParser.addAttribute("Joints", ShaderParser.GLSLType.IVEC3);
					shaderParser.addAttribute("Weights", ShaderParser.GLSLType.VEC3);
					
					shaderParser.addUniform("iris_Normal_Mv_Matrix", ShaderParser.GLSLType.MATRIX3F, null);
					shaderParser.addUniformArray("iris_Poses", ShaderParser.GLSLType.MATRIX4F, null, ShaderParser.MAX_JOINTS);
					
					shaderParser.replaceScript("iris_Position", "Position_a", -1, ShaderParser.ExceptionHandler.NO_MATCHES, "in vec3 iris_Position;");
					shaderParser.replaceScript("iris_Normal", "Normal_a", -1, ShaderParser.ExceptionHandler.NO_MATCHES, "in vec3 iris_Normal;", "iris_NormalMat", "uniform mat3 iris_Normal_Mv_Matrix;");
					
					shaderParser.insertToScript("in vec3 iris_Position;", "\nvec3 Position_a = vec3(0.0);", 0, ShaderParser.InsertPosition.FOLLOWING);
					shaderParser.insertToScript("in vec3 iris_Normal;", "\nvec3 Normal_a = vec3(0.0);", 0, ShaderParser.InsertPosition.FOLLOWING);
					
					shaderParser.insertToScript("void main\\(\\) \\{",
											    "\n\nvoid setAnimationPosition() {\n"
											  + "    for(int i=0;i<3;i++)\n"
											  + "    {\n"
											  + "        mat4 jointTransform = iris_Poses[Joints[i]];\n"
											  + "        vec4 posePosition = jointTransform * vec4(iris_Position, 1.0);\n"
											  + "        Position_a += vec3(posePosition.xyz) * Weights[i];\n"
											  + "    }\n"
											  + "}\n"
											  + "\n"
											  + "void setAnimationNormal() {\n"
											  + "    \n"
											  + "    for(int i=0;i<3;i++)\n"
											  + "    {\n"
											  + "        mat4 jointTransform = iris_Poses[Joints[i]];\n"
											  + "        vec4 poseNormal = jointTransform * vec4(iris_Normal, 1.0);\n"
											  + "        Normal_a += vec3(poseNormal.xyz) * Weights[i];\n"
											  + "    }\n"
											  + "    \n"
											  + "    Normal_a = iris_Normal_Mv_Matrix * Normal_a;\n"
											  + "}\n", 0, ShaderParser.InsertPosition.PRECEDING);
					
					shaderParser.insertToScript("void main\\(\\) \\{",
											    "\n    setAnimationPosition();"
											  + "\n    setAnimationNormal();", 0, ShaderParser.InsertPosition.FOLLOWING);
					
					Map<ResourceLocation, Resource> cache = Maps.newHashMap();
					shaderParser.addToResourceCache(cache);
					GameRenderer.ResourceCache resourceProvider = new GameRenderer.ResourceCache(resourceFactory, cache);
					ResourceLocation rl = new ResourceLocation(name);
					VertexFormat animationvertexFormat = EpicFightRenderTypes.getAnimationVertexFormat(vertexFormat);
					
					return new IrisAnimationShader(resourceProvider, EpicFightMod.MODID + ":" + rl.getPath(), animationvertexFormat, usesTessellation, writingToBeforeTranslucent, writingToAfterTranslucent, blendModeOverride, alphaTest, uniformCreator,
													samplerCreator, isIntensity, parent, bufferBlendOverrides, customUniforms);
				} catch (IOException e) {
					throw new ShaderParsingException(e);
				}
			});
		}
	}
}