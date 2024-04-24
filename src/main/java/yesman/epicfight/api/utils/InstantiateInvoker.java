package yesman.epicfight.api.utils;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.main.EpicFightMod;

public class InstantiateInvoker {
	private static final Map<String, Class<?>> PRIMITIVE_KEYWORDS = Maps.newHashMap();
	private static final Map<Class<?>, Function<String, Object>> STRING_TO_OBJECT_PARSER = Maps.newHashMap();
	
	static {
		PRIMITIVE_KEYWORDS.put("B", byte.class);
		PRIMITIVE_KEYWORDS.put("C", char.class);
		PRIMITIVE_KEYWORDS.put("D", double.class);
		PRIMITIVE_KEYWORDS.put("F", float.class);
		PRIMITIVE_KEYWORDS.put("I", int.class);
		PRIMITIVE_KEYWORDS.put("J", long.class);
		PRIMITIVE_KEYWORDS.put("S", short.class);
		PRIMITIVE_KEYWORDS.put("Z", boolean.class);
		
		registerKeyword(byte.class, Byte::parseByte);
		registerKeyword(char.class, (s) -> s.charAt(0));
		registerKeyword(double.class, Double::parseDouble);
		registerKeyword(float.class, Float::parseFloat);
		registerKeyword(int.class, Integer::parseInt);
		registerKeyword(long.class, Long::parseLong);
		registerKeyword(short.class, Short::parseShort);
		registerKeyword(boolean.class, Boolean::parseBoolean);
		registerKeyword(String.class, (s) -> s);
		registerKeyword(Collider.class, (s) -> ColliderPreset.get(new ResourceLocation(s)));
		registerKeyword(OBBCollider.class, (s) -> {
			String[] colliderArgs = s.split(",");
			return new OBBCollider(Double.valueOf(colliderArgs[0]), Double.valueOf(colliderArgs[1]), Double.valueOf(colliderArgs[2]), Double.valueOf(colliderArgs[3]), Double.valueOf(colliderArgs[4]), Double.valueOf(colliderArgs[5]));
		});
		registerKeyword(MultiOBBCollider.class, (s) -> {
			String[] colliderArgs = s.split(",");
			return new MultiOBBCollider(Integer.valueOf(colliderArgs[0]), Double.valueOf(colliderArgs[1]), Double.valueOf(colliderArgs[2]), Double.valueOf(colliderArgs[3]), Double.valueOf(colliderArgs[4]), Double.valueOf(colliderArgs[5]), Double.valueOf(colliderArgs[6]));
		});
		registerKeyword(Joint.class, (s) -> {
			String[] armature$joint = s.split("\\.");
			Armature armature = Armatures.getOrCreateArmature(EpicFightMod.getResourceManager(), new ResourceLocation(armature$joint[0]), Armature::new);
			Joint joint = armature.searchJointByName(armature$joint[1]);
			
			return joint;
		});
		registerKeyword(Armature.class, (s) -> Armatures.getOrCreateArmature(EpicFightMod.getResourceManager(), new ResourceLocation(s), Armature::new));
		registerKeyword(InteractionHand.class, InteractionHand::valueOf);
	}
	
	public static void registerKeyword(Class<?> clz, Function<String, Object> parser) {
		STRING_TO_OBJECT_PARSER.put(clz, parser);
	}
	
	public static class Result<T> {
		final Class<T> type;
		final T result;
		
		Result(Class<T> type, T result) {
			this.type = type;
			this.result = result;
		}
		
		public static <T> Result<T> of(Class<T> type, T value) {
			return new Result<>(type, value);
		}
		
		public Class<T> getType() {
			return this.type;
		}
		
		public T getResult() {
			return this.result;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Result<T> invoke(String invocationCommand, @Nullable Class<T> hint) throws Exception {
		if (invocationCommand.matches("\\(.+\\)") || invocationCommand.matches("\\(.+\\)\\#.+")) { // invoke instance
			return (Result<T>)invokeInstance(invocationCommand, hint);
		} else if (invocationCommand.matches("\\[.+\\]") || invocationCommand.matches("\\[.+\\]\\#.+")) { // invoke array
			return (Result<T>)invokeArray(invocationCommand, hint);
		} else {
			String[] param = splitExceptWrapper(invocationCommand, '#', true);
			String sValue = param[0];
			String sType = param[1];
			
			if (PRIMITIVE_KEYWORDS.containsKey(sType)) {
				Class<T> type = (Class<T>)PRIMITIVE_KEYWORDS.get(sType);
				return Result.of(type, (T)STRING_TO_OBJECT_PARSER.get(type).apply(sValue));
			}
			
			Class<T> type = (Class<T>)Class.forName(sType);
			
			if (STRING_TO_OBJECT_PARSER.containsKey(type)) {
				return Result.of(type, (T)STRING_TO_OBJECT_PARSER.get(type).apply(sValue));
			}
			
			throw new IllegalArgumentException("Can't find the matching type for the command " + invocationCommand);
		}
	}
	
	/**
	 * @param hint has a higher priority than class specified in invocation command
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Result<T> invokeInstance(String invocationCommand, @Nullable Class<?> hint) throws Exception {
		if (!invocationCommand.matches("\\(.+\\)") && !invocationCommand.matches("\\(.+\\)\\#.+")) {
			throw new IllegalStateException("Invalid instantiate invocation command: " + invocationCommand);
		}
		
		String[] param$type = splitExceptWrapper(invocationCommand, '#', true);
		String sParams = param$type[0];
		Class<?> type = param$type.length > 1 ? Class.forName(param$type[1]) : hint;
		
		if (type == null) {
			throw new IllegalArgumentException("Can't find the type in command " + invocationCommand);
		}
		
		String[] params = splitExceptWrapper(sParams, ',', false);
		Object[] oArgs = new Object[params.length];
		Class[] oArgClss = new Class[params.length];
		
		for (int i = 0; i < params.length; i++) {
			Result<?> result = invoke(params[i], null);
			oArgs[i] = result.result;
			oArgClss[i] = result.type;
		}
		
		Constructor constructor = null;
		
		try {
			if (hint == null) {
				throw new NoSuchMethodException();
			}
			constructor = hint.getConstructor(oArgClss);
			EpicFightMod.LOGGER.debug("Can't find the matching constructor for the hint class. Using the one given by command");
		} catch (NoSuchMethodException e) {
			constructor = type.getConstructor(oArgClss);
		}
		
		return Result.of((Class<T>)type, (T)constructor.newInstance(oArgs));
	}
	
	/**
	 * @param hint has a higher priority than class specified in invocation command
	 */
	@SuppressWarnings("unchecked")
	public static <T> Result<T[]> invokeArray(String invocationCommand, @Nullable Class<?> hint) throws Exception {
		if (!invocationCommand.matches("\\[.+\\]") && !invocationCommand.matches("\\[.+\\]\\#.+")) {
			throw new IllegalStateException("Invalid array invocation command: " + invocationCommand);
		}
		
		String[] param$type = splitExceptWrapper(invocationCommand, '#', true);
		String sParams = param$type[0];
		Class<?> type = param$type.length > 1 ? Class.forName(param$type[1]) : hint;
		
		if (type == null) {
			throw new IllegalArgumentException("Can't find the type in command " + invocationCommand);
		}
		
		String[] params = splitExceptWrapper(sParams, ',', false);
		List<T> resultArray = Lists.newArrayList();
		T[] result = (T[]) Array.newInstance(type, params.length);
		
		for (int i = 0; i < params.length; i++) {
			T obj = (T)invoke(params[i], type).result;
			
			if (obj.getClass() != type) {
				throw new IllegalStateException("Heterogeneous array elements for the command " + invocationCommand);
			}
			
			resultArray.add(obj);
			result[i] = obj;
		}
		
		return Result.of((Class<T[]>)type.arrayType(), resultArray.toArray(result));
	}
	
	private static String[] splitExceptWrapper(String sArgs, char keyword, boolean skipWrapper) {
		List<String> sArgsList = Lists.newArrayList();
		
		int arrayNestCounter = 0;
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < sArgs.length(); i++) {
			char c = sArgs.charAt(i);
			
			if (c == keyword) {
				if (arrayNestCounter < 1) {
					sArgsList.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(c);
				}
			} else if (c == '[' || c == '(') {
				if (!skipWrapper || arrayNestCounter > 0) {
					sb.append(c);
				}
				
				arrayNestCounter++;
			} else if (c == ']' || c == ')') {
				arrayNestCounter--;
				
				if (!skipWrapper || arrayNestCounter > 0) {
					sb.append(c);
				}
			} else {
				sb.append(c);
			}
		}
		
		if (!sb.isEmpty()) {
			sArgsList.add(sb.toString());
		}
		
		return sArgsList.toArray(new String[0]);
	}
}