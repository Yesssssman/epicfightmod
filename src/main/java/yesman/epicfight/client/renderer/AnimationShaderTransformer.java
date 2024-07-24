package yesman.epicfight.client.renderer;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class AnimationShaderTransformer {
	public static final int MAX_JOINTS = 40;
	public static final int MAX_WEIGHTS = 3;
	private static final double[] IDENTY_MATRIX3F = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0};
	private static final double[] IDENTY_MATRIX4F = {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
	private static final Map<String, AnimationShaderInstance> ANINMATION_SHADERS = Maps.newConcurrentMap();
	private static Map<ResourceLocation, Resource> SHADER_LIBS;
	
	public static AnimationShaderInstance getAnimationShader(ShaderInstance shaderInstance) {
		if (shaderInstance instanceof AnimationShaderInstance animationShaderInstance) {
			return animationShaderInstance;
		}
		
		return ANINMATION_SHADERS.computeIfAbsent(shaderInstance.getName(), (name) -> {
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			ResourceLocation shaderLocation = new ResourceLocation(shaderInstance.getName());
			ResourceLocation rlProperties = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".json");
			ResourceLocation rlVsh = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".vsh");
			ResourceLocation rlFsh = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".fsh");
			
			Map<ResourceLocation, Resource> cache = Maps.newHashMap();
			cache.putAll(SHADER_LIBS);
			
			try {
				final Resource rProperties = resourceManager.getResourceOrThrow(rlProperties);
				final Resource rVsh = resourceManager.getResourceOrThrow(rlVsh);
				final Resource rFsh = resourceManager.getResourceOrThrow(rlFsh);
				
				final Set<String> attrToUniforms = Sets.newHashSet();
				
				Resource rAnimProperties = new Resource(rProperties.source(), () -> {
					String properties = new String(rProperties.open().readAllBytes(), StandardCharsets.UTF_8);
					JsonObject propertiesJson = JsonParser.parseString(properties).getAsJsonObject();
					String vShaderLocation = GsonHelper.getAsString(propertiesJson, "vertex");
					String fShaderLocation = GsonHelper.getAsString(propertiesJson, "fragment");
					propertiesJson.addProperty("vertex", EpicFightMod.MODID + ":" + new ResourceLocation(vShaderLocation).getPath());
					propertiesJson.addProperty("fragment", EpicFightMod.MODID + ":" + new ResourceLocation(fShaderLocation).getPath());
					
					JsonArray attributesArray = GsonHelper.getAsJsonArray(propertiesJson, "attributes");
					JsonArray refreshedAttributesArray = new JsonArray();
					
					for (JsonElement e : attributesArray) {
						switch (e.getAsString()) {
						case "Position" -> {
							refreshedAttributesArray.add(new JsonPrimitive("Position_a"));
						}
						case "Color", "UV1", "UV2" -> {
							attrToUniforms.add(e.getAsString());
						}
						case "Normal" -> {
							refreshedAttributesArray.add(new JsonPrimitive("Normal_a"));
						}
						default -> {
							refreshedAttributesArray.add(e);
						}
						}
					}
					
					refreshedAttributesArray.add("Joints");
					refreshedAttributesArray.add("Weights");
					propertiesJson.add("attributes", refreshedAttributesArray);
					
					JsonArray uniformsArray = GsonHelper.getAsJsonArray(propertiesJson, "uniforms");
					JsonArray matArr = new JsonArray(9);
					
					for (double d : IDENTY_MATRIX3F) {
						matArr.add(d);
					}
					
					JsonObject matrix3fUniform = new JsonObject();
					matrix3fUniform.addProperty("name", "Normal_Mv_Matrix");
					matrix3fUniform.addProperty("type", "matrix3x3");
					matrix3fUniform.addProperty("count", 9);
					matrix3fUniform.add("values", matArr);
					
					uniformsArray.add(matrix3fUniform);
					
					for (String attrRemoved : attrToUniforms) {
						switch (attrRemoved) {
						case "Color" -> {
							JsonArray jsonArr = new JsonArray(4);
							jsonArr.add(1.0F);
							jsonArr.add(1.0F);
							jsonArr.add(1.0F);
							jsonArr.add(1.0F);
							
							JsonObject colorUniform = new JsonObject();
							colorUniform.addProperty("name", "Color");
							colorUniform.addProperty("type", "float");
							colorUniform.addProperty("count", 4);
							colorUniform.add("values", jsonArr);
							
							uniformsArray.add(colorUniform);
						}
						case "UV1" -> {
							JsonArray jsonArr = new JsonArray(2);
							jsonArr.add(0);
							jsonArr.add(0);
							
							JsonObject uv1Uniform = new JsonObject();
							uv1Uniform.addProperty("name", "UV1");
							uv1Uniform.addProperty("type", "int");
							uv1Uniform.addProperty("count", 2);
							uv1Uniform.add("values", jsonArr);
							
							uniformsArray.add(uv1Uniform);
						}
						case "UV2" -> {
							JsonArray jsonArr = new JsonArray(2);
							jsonArr.add(0);
							jsonArr.add(0);
							
							JsonObject uv2Uniform = new JsonObject();
							uv2Uniform.addProperty("name", "UV2");
							uv2Uniform.addProperty("type", "int");
							uv2Uniform.addProperty("count", 2);
							uv2Uniform.add("values", jsonArr);
							
							uniformsArray.add(uv2Uniform);
						}
						}
					}
					
					for (int i = 0; i < MAX_JOINTS; i++) {
						JsonObject posesUniform = new JsonObject();
						posesUniform.addProperty("name", "Poses[" + i + "]");
						posesUniform.addProperty("type", "matrix4x4");
						posesUniform.addProperty("count", 16);
						
						JsonArray jsonArr = new JsonArray(16);
						
						for (double d : IDENTY_MATRIX4F) {
							jsonArr.add(d);
						}
						
						posesUniform.add("values", jsonArr);
						uniformsArray.add(posesUniform);
					}
					
					return new ByteArrayInputStream(propertiesJson.toString().getBytes());
				}, rProperties::metadata);
				
				Resource rAnimVsh = new Resource(rVsh.source(), () -> {
					String shaderSource = new String(rVsh.open().readAllBytes(), StandardCharsets.UTF_8);
					StringBuilder sb = new StringBuilder(shaderSource);
					
					try {
						replaceLastMatching(sb, "in vec3 Position;", "in vec3 Position_a;");
						replaceLastMatching(sb, "in vec3 Normal;", "in vec3 Normal_a;");
						removeMatching(sb, "in vec4 Color;\n");
						removeMatching(sb, "in ivec2 UV1;\n");
						removeMatching(sb, "in ivec2 UV2;\n");
						
						//Insert attributes
						insertLastMatching(sb, "in .* .*;", "\r\nin ivec3 Joints;\r\nin vec3 Weights;\r\n");
						
						//Insert uv2 uniform
						insertLastMatching(sb, "uniform .* .*;", "\nuniform mat3 Normal_Mv_Matrix;");
						
						//Insert Color uniform
						if (attrToUniforms.contains("Color")) {
							insertLastMatching(sb, "uniform .* .*;", "\nuniform vec4 Color;");
						}
						
						//Insert uv1 uniform
						if (attrToUniforms.contains("UV1")) {
							insertLastMatching(sb, "uniform .* .*;", "\nuniform ivec2 UV1;");
						}
						
						//Insert uv2 uniform
						if (attrToUniforms.contains("UV2")) {
							insertLastMatching(sb, "uniform .* .*;", "\nuniform ivec2 UV2;");
						}
						
						//Insert poses uniforms
						insertLastMatching(sb, "uniform .* .*;", "\nuniform mat4 Poses[" + String.valueOf(MAX_JOINTS) + "];");
						
						//Insert normal
						insertLastMatching(sb, "void main\\(\\) \\{", "\r\n    vec3 Normal = vec3(0.0);\r\n"
																	    + "    \r\n"
																	    + "    for(int i=0;i<" + MAX_WEIGHTS + ";i++)\r\n"
																	    + "    {\r\n"
																	    + "        mat4 jointTransform = Poses[Joints[i]];\r\n"
																	    + "        vec4 poseNormal = jointTransform * vec4(Normal_a, 1.0);\r\n"
																	    + "        Normal += vec3(poseNormal.xyz) * Weights[i];\r\n"
																	    + "    }\r\n"
																	    + "    Normal = Normal_Mv_Matrix * Normal;");
						
						//Insert position
						insertLastMatching(sb, "void main\\(\\) \\{", "\r\n    vec3 Position = vec3(0.0);\r\n"
																	    + "    \r\n"
																	    + "    for(int i=0;i<" + MAX_WEIGHTS + ";i++)\r\n"
																	    + "    {\r\n"
																	    + "        mat4 jointTransform = Poses[Joints[i]];\r\n"
																	    + "        vec4 posePosition = jointTransform * vec4(Position_a, 1.0);\r\n"
																	    + "        Position += vec3(posePosition.xyz) * Weights[i];\r\n"
																	    + "    }\r\n");
					} catch (NoSuchElementException e) {
						throw new RuntimeException("Can't transform shader " + shaderInstance.getName(), e);
					}
					
					return new ByteArrayInputStream(sb.toString().getBytes());
				}, rVsh::metadata);
				
				cache.put(new ResourceLocation(EpicFightMod.MODID, rlProperties.getPath()), rAnimProperties);
				cache.put(new ResourceLocation(EpicFightMod.MODID, rlVsh.getPath()), rAnimVsh);
				cache.put(new ResourceLocation(EpicFightMod.MODID, rlFsh.getPath()), rFsh);
			} catch (FileNotFoundException e) {
				new RuntimeException("Can't create animation shader " + shaderInstance.getName(), e);
			}
			
			GameRenderer.ResourceCache resourceProvider = new GameRenderer.ResourceCache(resourceManager, cache);
			
			try {
				return new AnimationShaderInstance(resourceProvider, new ResourceLocation(EpicFightMod.MODID, shaderLocation.getPath()), EpicFightRenderTypes.getAnimationVertexFormat(shaderInstance.getVertexFormat()));
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't create animation shader", e);
			}
		});
	}
	
	private static void removeMatching(StringBuilder sb, String regex) throws NoSuchElementException {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sb.toString());
		
		boolean find = false;
		int start = 0;
		int end = 0;
		
		while (matcher.find()) {
			find = true;
			start = matcher.start();
			end = matcher.end();
		}
		
		if (!find) {
			throw new NoSuchElementException("No " + regex + " expression.");
		}
		
		sb.replace(start, end, "");
	}
	
	private static void insertLastMatching(StringBuilder sb, String regex, String toInsert) throws NoSuchElementException {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sb.toString());
		
		boolean find = false;
		int idx = 0;
		
		while (matcher.find()) {
			find = true;
			idx = matcher.end();
		}
		
		if (!find) {
			throw new NoSuchElementException("No " + regex + " expression.");
		}
		
		sb.insert(idx, toInsert);
	}
	
	private static void replaceLastMatching(StringBuilder sb, String regex, String toReplace) throws NoSuchElementException {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sb.toString());
		
		boolean find = false;
		int start = 0;
		int end = 0;
		
		while (matcher.find()) {
			find = true;
			start = matcher.start();
			end = matcher.end();
		}
		
		if (!find) {
			throw new NoSuchElementException("No " + regex + " expression.");
		}
		
		sb.replace(start, end, toReplace);
	}
	
	@SubscribeEvent
	public static void registerShadersEvent(RegisterShadersEvent event) throws IOException {
		ANINMATION_SHADERS.clear();
		
		Map<ResourceLocation, Resource> shaderLibs = ((ResourceManager)((GameRenderer.ResourceCache) event.getResourceProvider()).original()).listResources("shaders/include", (rl) -> {
			String s = rl.getPath();
			return s.endsWith(".glsl");
		});
		
		SHADER_LIBS = ImmutableMap.copyOf(shaderLibs);
	}
}