package yesman.epicfight.api.utils;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

import yesman.epicfight.api.utils.TypeFlexibleHashMap.TypeKey;

@SuppressWarnings("serial")
public class TypeFlexibleHashMap<A extends TypeKey<?>> extends HashMap<A, Object> {
	final boolean immutable;
	
    public TypeFlexibleHashMap(boolean immutable) {
        this.immutable = immutable;
    }
	
	@SuppressWarnings("unchecked")
	public <T> T put(TypeKey<T> typeKey, T val) {
		if (this.immutable) {
			throw new UnsupportedOperationException();
		}
		
		return (T)super.put((A) typeKey, val);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T get(TypeKey<T> typeKey) {
		
		ImmutableMap.of();
		
		return (T)super.get(typeKey);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getOrDefault(TypeKey<T> typeKey) {
		return (T)super.getOrDefault(typeKey, typeKey.defaultValue());
	}
	
	public interface TypeKey<T> {
		public T defaultValue();
	}
}