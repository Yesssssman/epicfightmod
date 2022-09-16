package yesman.epicfight.api.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;

import yesman.epicfight.main.EpicFightMod;

public class ExtendableEnumManager<T> {
	private int lastOrdinal = 0;
	private Map<Integer, T> enumMapByOrdinal = Maps.newLinkedHashMap();
	private Map<String, T> enumMapByName = Maps.newLinkedHashMap();
	
	public void loadPreemptive(Class<?> targetClss) {
		try {
			Method m = targetClss.getMethod("values");
			m.invoke(null);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			EpicFightMod.LOGGER.warn("Error when loading extendable enum " + targetClss);
			e.printStackTrace();
		}
	}
	
	public int assign(T value) {
		int lastOrdinal = this.lastOrdinal;
		this.enumMapByOrdinal.put(lastOrdinal, value);
		this.enumMapByName.put(value.toString().toLowerCase(Locale.ROOT), value);
		++this.lastOrdinal;
		return lastOrdinal;
	}
	
	public T get(int id) {
		return this.enumMapByOrdinal.get(id);
	}
	
	public T get(String name) {
		return this.enumMapByName.get(name.toLowerCase(Locale.ROOT));
	}
	
	public Collection<T> universalValues() {
		return this.enumMapByOrdinal.values();
	}
}