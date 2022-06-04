package yesman.epicfight.api.utils.game;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;

import com.google.common.collect.Maps;

public class EnumerateAssignmentManager<T> {
	private int lastOrdinal = 0;
	private Map<Integer, T> enumMapByOrdinal = Maps.newLinkedHashMap();
	private Map<String, T> enumMapByName = Maps.newLinkedHashMap();
	
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
		return this.enumMapByName.get(name);
	}
	
	public Collection<T> universalValues() {
		return this.enumMapByOrdinal.values();
	}
}