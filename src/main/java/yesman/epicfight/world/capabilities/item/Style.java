package yesman.epicfight.world.capabilities.item;

import java.util.Map;

import com.google.common.collect.Maps;

public interface Style {
	static final Map<String, Style> STYLES = Maps.newHashMap();
	
	public static void put(String name, Style style) {
		STYLES.put(name, style);
	}
	
	public static Style get(String name) {
		return STYLES.get(name);
	}
	
	public boolean canUseOffhand();
}