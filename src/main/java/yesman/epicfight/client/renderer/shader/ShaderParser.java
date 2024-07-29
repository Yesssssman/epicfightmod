package yesman.epicfight.client.renderer.shader;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceProvider;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.exception.ShaderParsingException;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class ShaderParser {
	public static final int MAX_JOINTS = 40;
	public static final int MAX_WEIGHTS = 3;
	
	private static final Double[] IDENTY_MATRIX3F = {1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0};
	private static final Double[] IDENTY_MATRIX4F = {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0};
	
	private final String shaderName;
	private final JsonObject propertiesJson;
	private final String vertexShaderScript;
	private final List<Formatter> formatters = Lists.newArrayList();
	private final Resource rProperties;
	private final Resource rVsh;
	private final Resource rFsh;
	
	public ShaderParser(ResourceProvider resourceProvider, String shaderName) throws FileNotFoundException, IOException {
		this.shaderName = shaderName;
		ResourceLocation shaderLocation = new ResourceLocation(shaderName);
		ResourceLocation rlProperties = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".json");
		Resource rProperties = resourceProvider.getResourceOrThrow(rlProperties);
		String properties = new String(rProperties.open().readAllBytes(), StandardCharsets.UTF_8);
		this.propertiesJson = JsonParser.parseString(properties).getAsJsonObject();
		
		ResourceLocation vShaderLocation = new ResourceLocation(GsonHelper.getAsString(this.propertiesJson, "vertex"));
		ResourceLocation fShaderLocation = new ResourceLocation(GsonHelper.getAsString(this.propertiesJson, "fragment"));
		
		ResourceLocation rlVsh = new ResourceLocation(vShaderLocation.getNamespace(), "shaders/core/" + vShaderLocation.getPath() + ".vsh");
		ResourceLocation rlFsh = new ResourceLocation(fShaderLocation.getNamespace(), "shaders/core/" + fShaderLocation.getPath() + ".fsh");
		
		this.propertiesJson.addProperty("vertex", EpicFightMod.MODID + ":" + vShaderLocation.getPath());
		this.propertiesJson.addProperty("fragment", EpicFightMod.MODID + ":" + fShaderLocation.getPath());
		
		Resource rVsh = resourceProvider.getResourceOrThrow(rlVsh);
		Resource rFsh = resourceProvider.getResourceOrThrow(rlFsh);
		
		this.vertexShaderScript = new String(rVsh.open().readAllBytes(), StandardCharsets.UTF_8);
		this.rProperties = rProperties;
		this.rVsh = rVsh;
		this.rFsh = rFsh;
	}
	
	public boolean hasAttribute(String name) {
		JsonArray attributesArray = GsonHelper.getAsJsonArray(this.propertiesJson, "attributes");
		Set<String> attributeNames = new HashSet<> (attributesArray.asList().stream().map(JsonElement::getAsString).toList());
		return attributeNames.contains(name);
	}
	
	public boolean hasUniform(String name) {
		JsonArray uniformsArray = GsonHelper.getAsJsonArray(this.propertiesJson, "uniforms");
		Set<String> uniformNames = new HashSet<> (uniformsArray.asList().stream().map((e) -> GsonHelper.getAsString(e.getAsJsonObject(), "name")).toList());
		
		return uniformNames.contains(name);
	}
	
	public void insertToScript(String regex, String toInsert, int ordinal, InsertPosition insertPosition) {
		this.formatters.add(new ScriptInserter(regex, toInsert, ordinal, insertPosition));
	}
	
	public void replaceScript(String regex, String toReplace, int ordinal, ExceptionHandler exceptionHandler) {
		this.replaceScript(regex, toReplace, ordinal, exceptionHandler, (String[])null);
	}
	
	public void replaceScript(String regex, String toReplace, int ordinal, ExceptionHandler exceptionHandler, String... exceptionsRegex) {
		this.formatters.add(new ScriptReplacer(regex, toReplace, ordinal, exceptionHandler, exceptionsRegex));
	}
	
	public void addAttribute(String name, ExceptionHandler exceptionHandler, GLSLType type) {
		this.formatters.add(new Inserter(name, Usage.ATTRIBUTE, type, exceptionHandler, null));
	}
	
	public void replaceAttribute(String name, String replacedName, GLSLType type, ExceptionHandler exceptionHandler) {
		this.formatters.add(new Replacer(name, replacedName, Usage.ATTRIBUTE, type, exceptionHandler, null));
	}
	
	public void remove(String name, Usage usage, ExceptionHandler exceptionHandler) {
		this.formatters.add(new Remover(name, usage, exceptionHandler));
	}
	
	public void addUniform(String name, GLSLType type, ExceptionHandler exceptionHandler, Number[] defaultUniformValues) {
		this.formatters.add(new Inserter(name, Usage.UNIFORM, type, exceptionHandler, defaultUniformValues));
	}
	
	public void addUniform(String name, GLSLType type, String positionRegex, InsertPosition insertPosition, int ordinal, ExceptionHandler exceptionHandler, Number[] defaultUniformValues) {
		this.formatters.add(new Inserter(name, Usage.UNIFORM, type, exceptionHandler, positionRegex, insertPosition, ordinal, defaultUniformValues, -1));
	}
	
	public void addUniformArray(String name, GLSLType type, ExceptionHandler exceptionHandler, Number[] defaultUniformValues, int arraySize) {
		this.formatters.add(new Inserter(name, Usage.UNIFORM_ARRAY, type, exceptionHandler, defaultUniformValues, arraySize));
	}
	
	public void replaceUniform(String name, String replacedName, GLSLType type, ExceptionHandler exceptionHandler, Number[] defaultUniformValues) {
		this.formatters.add(new Replacer(name, replacedName, Usage.UNIFORM, type, exceptionHandler, defaultUniformValues));
	}
	
	public void addToResourceCache(Map<ResourceLocation, Resource> cache) throws ShaderParsingException {
		ResourceLocation shaderLocation = new ResourceLocation(this.shaderName);
		ResourceLocation rlProperties = new ResourceLocation(shaderLocation.getNamespace(), "shaders/core/" + shaderLocation.getPath() + ".json");
		ResourceLocation vShaderLocation = new ResourceLocation(GsonHelper.getAsString(this.propertiesJson, "vertex"));
		ResourceLocation fShaderLocation = new ResourceLocation(GsonHelper.getAsString(this.propertiesJson, "fragment"));
		ResourceLocation rlVsh = new ResourceLocation(vShaderLocation.getNamespace(), "shaders/core/" + vShaderLocation.getPath() + ".vsh");
		ResourceLocation rlFsh = new ResourceLocation(fShaderLocation.getNamespace(), "shaders/core/" + fShaderLocation.getPath() + ".fsh");
		
		this.propertiesJson.addProperty("vertex", EpicFightMod.MODID + ":" + vShaderLocation.getPath());
		this.propertiesJson.addProperty("fragment", EpicFightMod.MODID + ":" + fShaderLocation.getPath());
		
		Resource rAnimProperties = new Resource(this.rProperties.source(), this.getPropertiesJson(), this.rProperties::metadata);
		Resource rAnimVsh = new Resource(this.rVsh.source(), this.getVertexShaderScript(), this.rVsh::metadata);
		
		cache.put(new ResourceLocation(EpicFightMod.MODID, rlProperties.getPath()), rAnimProperties);
		cache.put(new ResourceLocation(EpicFightMod.MODID, rlVsh.getPath()), rAnimVsh);
		cache.put(new ResourceLocation(EpicFightMod.MODID, rlFsh.getPath()), this.rFsh);
	}
	
	private IoSupplier<InputStream> getPropertiesJson() {
		return () -> {
			this.formatters.forEach((formatter) -> formatter.reformJson(this.propertiesJson));
			return new ByteArrayInputStream(this.propertiesJson.toString().getBytes());
		};
	}
	
	public String getOriginalScript() {
		return this.vertexShaderScript;
	}
	
	private IoSupplier<InputStream> getVertexShaderScript() {
		return () -> {
			StringBuilder sb = new StringBuilder(this.vertexShaderScript);
			this.formatters.forEach((formatter) -> formatter.reformScript(sb));
			
			return new ByteArrayInputStream(sb.toString().getBytes());
		};
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum Usage {
		ATTRIBUTE("in %s %s;"), UNIFORM("uniform %s %s;"), UNIFORM_ARRAY("uniform %s %s[%d];");
		
		String format;
		
		Usage(String format) {
			this.format = format;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum GLSLType {
		IVEC2("ivec2", "int", 2, null),
		VEC2("vec2", "float", 2, null),
		IVEC3("ivec3", "int", 3, null),
		VEC3("vec3", "float", 3, null),
		IVEC4("ivec4", "int", 4, null),
		VEC4("vec4", "float", 4, null),
		MATRIX3F("mat3", "matrix3x3", 9, IDENTY_MATRIX3F),
		MATRIX4F("mat4", "matrix4x4", 16, IDENTY_MATRIX4F);
		
		String typeInScript;
		String typeInProperties;
		int count;
		Number[] uniformDefaultValues;
		
		GLSLType(String typeInScript, String typeInProperties, int count, Number[] defaultValues) {
			this.typeInScript = typeInScript;
			this.typeInProperties = typeInProperties;
			this.count = count;
			this.uniformDefaultValues = defaultValues;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum ExceptionHandler {
		IGNORE, THROW
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum InsertPosition {
		PRECEDING((start, end) -> Math.max(start - 1, 0)), FOLLOWING((start, end) -> end);
		
		BiFunction<Integer, Integer, Integer> positionGetter;
		
		InsertPosition(BiFunction<Integer, Integer, Integer> positionGetter) {
			this.positionGetter = positionGetter;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private abstract class Formatter {
		public abstract void reformJson(JsonObject propertiesJson) throws ShaderParsingException;
		public abstract void reformScript(StringBuilder stringBuilder) throws ShaderParsingException;
	}
	
	@OnlyIn(Dist.CLIENT)
	private class ScriptInserter extends Formatter {
		private final String regex;
		private final String toInsert;
		private final int ordinal;
		private final InsertPosition insertPosition;
		
		private ScriptInserter(String regex, String toEdit, int ordinal, InsertPosition insertPosition) {
			this.regex = regex;
			this.toInsert = toEdit;
			this.ordinal = ordinal;
			this.insertPosition = insertPosition;
		}
		
		@Override
		public void reformJson(JsonObject propertiesJson) {}
		
		@Override
		public void reformScript(StringBuilder stringBuilder) throws ShaderParsingException {
			Pattern pattern = Pattern.compile(this.regex);
			Matcher matcher = pattern.matcher(stringBuilder.toString());
			int start = -1;
			int end = -1;
			int ordinal = 0;
			int correction = 0;
			
			while (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				
				if (ordinal == this.ordinal || this.ordinal == -1) {
					stringBuilder.insert(this.insertPosition.positionGetter.apply(start + correction, end + correction), this.toInsert);
					correction += this.toInsert.length() - (end - start);
				}
				
				ordinal++;
			}
			
			if (ordinal == 0) {
				throw new ShaderParsingException("Can't find matching regular expression " + this.regex + " in the glsl script.");
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class ScriptReplacer extends Formatter {
		private final String regex;
		private final String toReplace;
		private final int ordinal;
		private final ExceptionHandler exceptionHandler;
		private final List<String> exceptionsRegex = Lists.newArrayList();;
		
		private ScriptReplacer(String regex, String toEdit, int ordinal, ExceptionHandler exceptionHandler, String... exceptionsRegex) {
			this.regex = regex;
			this.toReplace = toEdit;
			this.ordinal = ordinal;
			this.exceptionHandler = exceptionHandler;
			
			if (exceptionsRegex != null) {
				this.exceptionsRegex.addAll(Arrays.asList(exceptionsRegex));
			}
		}
		
		@Override
		public void reformJson(JsonObject propertiesJson) {}
		
		@Override
		public void reformScript(StringBuilder stringBuilder) throws ShaderParsingException {
			List<Pair<Integer, Integer>> boundaries = Lists.newArrayList();
			
			if (!this.exceptionsRegex.isEmpty()) {
				for (String regex : this.exceptionsRegex) {
					Pattern pattern = Pattern.compile(regex);
					Matcher matcher = pattern.matcher(stringBuilder.toString());
					
					while (matcher.find()) {
						boundaries.add(Pair.of(matcher.start(), matcher.end()));
					}
				}
			}
			
			Pattern pattern = Pattern.compile(this.regex);
			Matcher matcher = pattern.matcher(stringBuilder.toString());
			int start = -1;
			int end = -1;
			int ordinal = 0;
			int correction = 0;
			
			while (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				
				if (ordinal == this.ordinal || this.ordinal == -1) {
					boolean isOutOfExceptionBoundry = true;
					
					if (!boundaries.isEmpty()) {
						for (Pair<Integer, Integer> boundry : boundaries) {
							if (boundry.getFirst() <= start && boundry.getSecond() >= end) {
								isOutOfExceptionBoundry = false;
								break;
							}
						}
					}
					
					if (isOutOfExceptionBoundry) {
						stringBuilder.replace(start + correction, end + correction, this.toReplace);
						correction += this.toReplace.length() - (end - start);
					}
				}
				
				ordinal++;
			}
			
			if (ordinal == 0 && this.exceptionHandler == ExceptionHandler.THROW) {
				throw new ShaderParsingException("Can't find matching regular expression " + this.regex + " in the glsl script.");
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class Inserter extends Formatter {
		private final String name;
		private final Usage usage;
		private final GLSLType type;
		private final ExceptionHandler exceptionHandler;
		
		private final InsertPosition insertPosition;
		private final int ordinal;
		private String positionRegex;
		
		private final Number[] uniformDefaultValues;
		private final int arraySize;
		
		private Inserter(String name, Usage usage, GLSLType type, ExceptionHandler exceptionHandler, Number[] uniformDefault) {
			this(name, usage, type, exceptionHandler, uniformDefault, -1);
		}
		
		private Inserter(String name, Usage usage, GLSLType type, ExceptionHandler exceptionHandler, Number[] uniformDefault, int arraySize) {
			this(name, usage, type, exceptionHandler, usage == Usage.UNIFORM_ARRAY ? String.format(Usage.UNIFORM.format, ".*", ".*") : String.format(usage.format, ".*", ".*"), InsertPosition.FOLLOWING, Integer.MAX_VALUE, uniformDefault, arraySize);
		}
		
		private Inserter(String name, Usage usage, GLSLType type, ExceptionHandler exceptionHandler, String positionRegex, InsertPosition insertPosition, int ordinal, Number[] uniformDefault, int arraySize) {
			this.name = name;
			this.usage = usage;
			this.type = type;
			this.exceptionHandler = exceptionHandler;
			this.positionRegex = positionRegex;
			this.insertPosition = insertPosition;
			this.ordinal = ordinal;
			
			this.uniformDefaultValues = uniformDefault;
			this.arraySize = arraySize;
		}
		
		@Override
		public void reformJson(JsonObject propertiesJson) {
			switch (this.usage) {
			case ATTRIBUTE -> {
				JsonArray attributesArray = GsonHelper.getAsJsonArray(propertiesJson, "attributes");
				attributesArray.add(this.name);
			}
			case UNIFORM -> {
				JsonArray uniformsArray = GsonHelper.getAsJsonArray(propertiesJson, "uniforms");
				JsonObject uniformObject = new JsonObject();
				uniformObject.addProperty("name", this.name);
				uniformObject.addProperty("type", this.type.typeInProperties);
				uniformObject.addProperty("count", this.type.count);
				
				JsonArray defaultValue = new JsonArray();
				
				for (Number obj : ParseUtil.nvl(this.type.uniformDefaultValues, this.uniformDefaultValues)) {
					defaultValue.add(obj);
				}
				
				uniformObject.add("values", defaultValue);
				uniformsArray.add(uniformObject);
			}
			case UNIFORM_ARRAY -> {
				JsonArray uniformsArray = GsonHelper.getAsJsonArray(propertiesJson, "uniforms");
				
				for (int i = 0; i < this.arraySize; i++) {
					JsonObject uniformObject = new JsonObject();
					uniformObject.addProperty("name", String.format("%s[%d]", this.name, i));
					uniformObject.addProperty("type", this.type.typeInProperties);
					uniformObject.addProperty("count", this.type.count);
					
					JsonArray defaultValue = new JsonArray();
					
					for (Number obj : ParseUtil.nvl(this.type.uniformDefaultValues, this.uniformDefaultValues)) {
						defaultValue.add(obj);
					}
					
					uniformObject.add("values", defaultValue);
					uniformsArray.add(uniformObject);
				}
			}
			}
		}
		
		@Override
		public void reformScript(StringBuilder stringBuilder) throws ShaderParsingException {
			String regex = this.positionRegex;
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stringBuilder.toString());
			int start = -1;
			int end = -1;
			int ordinal = 0;
			
			while (matcher.find()) {
				start = matcher.start();
				end = matcher.end();
				
				ordinal++;
				
				if (ordinal > this.ordinal) {
					break;
				}
			}
			
			if (start == -1 && end == -1) {
				if (this.exceptionHandler == ExceptionHandler.IGNORE) {
					this.positionRegex = String.format(this.usage.format, ".*", ".*");
					this.reformScript(stringBuilder);
					return;
				} else {
					throw new ShaderParsingException("No " + regex + " expression.");
				}
			}
			
			stringBuilder.insert(this.insertPosition.positionGetter.apply(start, end), "\n" + ((this.usage == Usage.UNIFORM_ARRAY) ?
									String.format(this.usage.format, this.type.typeInScript, this.name, this.arraySize) : String.format(this.usage.format, this.type.typeInScript, this.name)));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class Replacer extends Formatter {
		private final String name;
		private final String replacedName;
		private final Usage usage;
		private final GLSLType type;
		private final ExceptionHandler exceptionHandler;
		private final Number[] uniformDefaultValues;
		
		private Replacer(String name, String replacedName, Usage usage, GLSLType type, ExceptionHandler exceptionHandler, Number[] uniformDefault) {
			this.name = name;
			this.replacedName = replacedName;
			this.usage = usage;
			this.type = type;
			this.exceptionHandler = exceptionHandler;
			this.uniformDefaultValues = uniformDefault;
		}
		
		@Override
		public void reformJson(JsonObject propertiesJson) throws ShaderParsingException {
			switch (this.usage) {
			case ATTRIBUTE -> {
				JsonArray attributesArray = GsonHelper.getAsJsonArray(propertiesJson, "attributes");
				
				if (attributesArray.remove(new JsonPrimitive(this.name))) {
					attributesArray.add(this.name);
				} else if (this.exceptionHandler == ExceptionHandler.THROW) {
					throw new ShaderParsingException("No target attribute to replace " + this.name);
				}
			}
			case UNIFORM -> {
				JsonArray uniformsArray = GsonHelper.getAsJsonArray(propertiesJson, "uniforms");
				
				for (JsonElement e : uniformsArray) {
					JsonObject uniformobj = e.getAsJsonObject();
					String attributeName = GsonHelper.getAsString(uniformobj, "name");
					
					if (this.name.equals(attributeName)) {
						uniformobj.asMap().clear();
						uniformobj.addProperty("name", this.name);
						uniformobj.addProperty("type", this.type.typeInProperties);
						uniformobj.addProperty("count", this.type.count);
						
						JsonArray defaultValue = new JsonArray();
						
						for (Number obj : ParseUtil.nvl(this.type.uniformDefaultValues, this.uniformDefaultValues)) {
							defaultValue.add(obj);
						}
						
						uniformobj.add("values", defaultValue);
						
						return;
					}
				}
				
				if (this.exceptionHandler == ExceptionHandler.THROW) {
					throw new ShaderParsingException("No target uniform to replace " + this.name);
				}
			}
			case UNIFORM_ARRAY -> {}
			}
		}
		
		@Override
		public void reformScript(StringBuilder stringBuilder) throws ShaderParsingException {
			String regex = String.format(this.usage.format, ".*", this.name);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stringBuilder.toString());
			
			boolean find = false;
			int start = 0;
			int end = 0;
			
			while (matcher.find()) {
				find = true;
				start = matcher.start();
				end = matcher.end();
			}
			
			if (!find && this.exceptionHandler == ExceptionHandler.THROW) {
				throw new ShaderParsingException("No " + regex + " expression.");
			}
			
			stringBuilder.replace(start, end, String.format(this.usage.format, this.type.typeInScript, this.replacedName));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	private class Remover extends Formatter {
		private final String name;
		private final Usage usage;
		private final ExceptionHandler exceptionHandler;
		
		private Remover(String name, Usage usage, ExceptionHandler exceptionHandler) {
			this.name = name;
			this.usage = usage;
			this.exceptionHandler = exceptionHandler;
		}
		
		@Override
		public void reformJson(JsonObject propertiesJson) throws ShaderParsingException {
			switch (this.usage) {
			case ATTRIBUTE -> {
				GsonHelper.getAsJsonArray(propertiesJson, "attributes").remove(new JsonPrimitive(this.name));
			}
			case UNIFORM -> {
				JsonArray uniformsArray = GsonHelper.getAsJsonArray(propertiesJson, "uniforms");
				
				for (JsonElement e : uniformsArray) {
					JsonObject uniformobj = e.getAsJsonObject();
					String attributeName = GsonHelper.getAsString(uniformobj, "name");
					
					if (this.name.equals(attributeName)) {
						uniformsArray.remove(e);
						break;
					}
				}
				
				if (this.exceptionHandler == ExceptionHandler.THROW) {
					throw new ShaderParsingException("No matching " + this.name + " expression");
				}
			}
			case UNIFORM_ARRAY -> {}
			}
		}
		
		@Override
		public void reformScript(StringBuilder stringBuilder) throws ShaderParsingException {
			String regex = "\n" + String.format(this.usage.format, ".*", this.name);
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(stringBuilder.toString());
			
			boolean find = false;
			int start = 0;
			int end = 0;
			
			while (matcher.find()) {
				find = true;
				start = matcher.start();
				end = matcher.end();
			}
			
			if (find) {
				stringBuilder.replace(start, end, "");
			} else if (this.exceptionHandler == ExceptionHandler.THROW) {
				throw new ShaderParsingException("No matching " + regex + " expression");
			}
		}
	}
}