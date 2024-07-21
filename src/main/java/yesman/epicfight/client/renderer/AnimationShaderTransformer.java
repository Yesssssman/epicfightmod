package yesman.epicfight.client.renderer;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
	private static final double[] IDENTY_MATRIX = {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
	private static final Map<String, ShaderInstance> ANINMATION_SHADERS = Maps.newConcurrentMap();
	private static Map<ResourceLocation, Resource> SHADER_LIBS;
	
	public static ShaderInstance getAnimationShader(String shaderName) {
		return ANINMATION_SHADERS.computeIfAbsent(shaderName, (name) -> {
			ShaderInstance shaderInstance = Minecraft.getInstance().gameRenderer.getShader(shaderName);
			ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
			ResourceLocation shaderLocation = new ResourceLocation(shaderName);
			ResourceLocation rlProperties = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".json");
			ResourceLocation rlVsh = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".vsh");
			ResourceLocation rlFsh = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".fsh");
			
			Map<ResourceLocation, Resource> cache = Maps.newHashMap();
			cache.putAll(SHADER_LIBS);
			
			try {
				final Resource rProperties = resourceManager.getResourceOrThrow(rlProperties);
				final Resource rVsh = resourceManager.getResourceOrThrow(rlVsh);
				final Resource rFsh = resourceManager.getResourceOrThrow(rlFsh);
				
				Resource rAnimProperties = new Resource(rProperties.source(), () -> {
					String properties = new String(rProperties.open().readAllBytes(), StandardCharsets.UTF_8);
					JsonObject propertiesJson = JsonParser.parseString(properties).getAsJsonObject();
					String vShaderLocation = GsonHelper.getAsString(propertiesJson, "vertex");
					String fShaderLocation = GsonHelper.getAsString(propertiesJson, "fragment");
					propertiesJson.addProperty("vertex", EpicFightMod.MODID + ":" + new ResourceLocation(vShaderLocation).getPath());
					propertiesJson.addProperty("fragment", EpicFightMod.MODID + ":" + new ResourceLocation(fShaderLocation).getPath());
					
					JsonArray attributesArray = GsonHelper.getAsJsonArray(propertiesJson, "attributes");
					attributesArray.add("Joints");
					attributesArray.add("Weights");
					
					JsonArray uniformsArray = GsonHelper.getAsJsonArray(propertiesJson, "uniforms");
					
					for (int i = 0; i < MAX_JOINTS; i++) {
						JsonObject posesUniform = new JsonObject();
						posesUniform.addProperty("name", "Poses[" + i + "]");
						posesUniform.addProperty("type", "matrix4x4");
						posesUniform.addProperty("count", 16);
						
						JsonArray jsonArr = new JsonArray(16);
						
						for (double d : IDENTY_MATRIX) {
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
						//Insert attributes
						insertLastMatching(sb, "in .* .*;", "\r\nin ivec3 Joints;\r\nin vec3 Weights;\r\n");
						//Insert uniforms
						insertLastMatching(sb, "uniform .* .*;", "uniform mat4 Poses[" + String.valueOf(MAX_JOINTS) + "];");
						
						//Replace vertex position
						replaceLastMatching(sb, "gl_Position = .*Position.*;", "vec4 totalLocalPos = vec4(0.0);\r\n"
																			 + "    \r\n"
																			 + "    for(int i=0;i<" + MAX_WEIGHTS + ";i++)\r\n"
																			 + "    {\r\n"
																			 + "        mat4 jointTransform = Poses[Joints[i]];\r\n"
																			 + "        vec4 posePosition = jointTransform * vec4(Position, 1.0);\r\n"
																			 + "        totalLocalPos += posePosition * Weights[i];\r\n"
																			 + "    }\r\n"
																			 + "    \r\n"
																			 + "    gl_Position = ProjMat * ModelViewMat * totalLocalPos;");
						
						//Replace vertex normal
						replaceLastMatching(sb, "normal = .*Normal.*;", "vec4 totalNormalPos = vec4(0.0);\r\n"
																	  + "    \r\n"
																	  + "    for(int i=0;i<" + MAX_WEIGHTS + ";i++)\r\n"
																	  + "    {\r\n"
																	  + "        mat4 jointTransform = Poses[Joints[i]];\r\n"
																	  + "        vec4 posePosition = jointTransform * vec4(Normal, 1.0);\r\n"
																	  + "        totalNormalPos += posePosition * Weights[i];\r\n"
																	  + "    }\r\n"
																	  + "    \r\n"
																	  + "    normal = ProjMat * ModelViewMat * vec4(normalize(totalNormalPos.xyz), 1.0);");
					} catch (NoSuchElementException e) {
						throw new RuntimeException("Can't transform shader " + shaderName, e);
					}
					
					return new ByteArrayInputStream(sb.toString().getBytes());
				}, rVsh::metadata);
				
				cache.put(new ResourceLocation(EpicFightMod.MODID, rlProperties.getPath()), rAnimProperties);
				cache.put(new ResourceLocation(EpicFightMod.MODID, rlVsh.getPath()), rAnimVsh);
				cache.put(new ResourceLocation(EpicFightMod.MODID, rlFsh.getPath()), rFsh);
			} catch (FileNotFoundException e) {
				new RuntimeException("Can't create animation shader " + shaderName, e);
			}
			
			GameRenderer.ResourceCache resourceProvider = new GameRenderer.ResourceCache(resourceManager, cache);
			
			try {
				return new AnimationShaderInstance(resourceProvider, new ResourceLocation(EpicFightMod.MODID, shaderLocation.getPath()), shaderInstance.getVertexFormat());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Can't create animation shader", e);
			}
		});
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