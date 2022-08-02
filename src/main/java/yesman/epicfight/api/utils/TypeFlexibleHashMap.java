package yesman.epicfight.api.utils;

import java.util.HashMap;

import yesman.epicfight.api.utils.TypeFlexibleHashMap.TypeKey;

public class TypeFlexibleHashMap<A extends TypeKey<?>> extends HashMap<A, Object> {
	
	@SuppressWarnings("unchecked")
	public <T> T put(TypeKey<T> typeKey, T val) {
		return (T)super.put((A) typeKey, val);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(TypeKey<T> typeKey) {
		return (T)super.get(typeKey);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrDefault(TypeKey<T> typeKey, T defaultVal) {
		return (T)super.getOrDefault(typeKey, defaultVal);
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public interface TypeKey<T> {
	}
}