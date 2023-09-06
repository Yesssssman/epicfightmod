package yesman.epicfight.api.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.network.chat.TranslatableComponent;
import yesman.epicfight.main.EpicFightMod;

public class ExtendableEnumManager<T> {
	private final Map<Integer, T> enumMapByOrdinal = Maps.newLinkedHashMap();
	private final Map<String, T> enumMapByName = Maps.newLinkedHashMap();
	private final String namespace;
	private int lastOrdinal = 0;
	
	public ExtendableEnumManager(String namespace) {
		this.namespace = namespace;
	}
	
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
		String enumName = value.toString().toLowerCase(Locale.ROOT);
		
		if (this.enumMapByName.containsKey(enumName)) {
			throw new IllegalArgumentException("Enum name " + enumName + " already exists");
		}
		
		this.enumMapByOrdinal.put(lastOrdinal, value);
		this.enumMapByName.put(enumName, value);
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
	
	public String toTranslated(ExtendableEnum e) {
		TranslatableComponent t = new TranslatableComponent(EpicFightMod.MODID + "." + this.namespace + "." + e.toString().toLowerCase(Locale.ROOT));
		return t.getString();
	}
}