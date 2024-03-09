package yesman.epicfight.api.utils;

import java.util.Locale;
import java.util.UUID;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.math.Vec3f;

public class ParseUtil {
	public static int[] toIntArray(JsonArray array) {
		IntList result = new IntArrayList();
		
		for (JsonElement je : array) {
			result.add(je.getAsInt());
		}
		
		return result.toIntArray();
	}
	
	public static float[] toFloatArray(JsonArray array) {
		FloatList result = new FloatArrayList();
		
		for (JsonElement je : array) {
			result.add(je.getAsFloat());
		}
		
		return result.toFloatArray();
	}
	
	public static Vec3f toVector3f(JsonArray array) {
		float[] result = toFloatArray(array);
		
		if (result.length < 3) {
			throw new IllegalArgumentException("Requires more than 3 elements to convert into 3d vector.");
		}
		
		return new Vec3f(result[0], result[1], result[2]);
	}
	
	public static Vec3 toVector3d(JsonArray array) {
		DoubleList result = new DoubleArrayList();
		
		for (JsonElement je : array) {
			result.add(je.getAsDouble());
		}
		
		if (result.size() < 3) {
			throw new IllegalArgumentException("Requires more than 3 elements to convert into 3d vector.");
		}
		
		return new Vec3(result.getDouble(0), result.getDouble(1), result.getDouble(2));
	}
	
	public static AttributeModifier toAttributeModifier(CompoundTag tag) {
		AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(tag.getString("operation").toUpperCase(Locale.ROOT));
		
		return new AttributeModifier(UUID.fromString(tag.getString("uuid")), tag.getString("name"), tag.getDouble("amount"), operation);
	}
	
	public static String makeFirstLetterToUpper(String s) {
		StringBuilder sb = new StringBuilder();
		boolean upperNext = true;
		
		s = s.toLowerCase(Locale.ROOT);
		
		for (String sElement : s.split("")) {
			if (upperNext) {
				sElement = sElement.toUpperCase(Locale.ROOT);
				upperNext = false;
			}
			
			if ("_".equals(sElement)) {
				upperNext = true;
				sb.append(" ");
			} else {
				sb.append(sElement);
			}
		}
		
		return sb.toString();
	}
	
	public static String toStringNvl(Object obj) {
		return obj == null ? "" : obj.toString();
	}

	private ParseUtil() {} // disallow creating new instances of a utility class that has no instance methods
}