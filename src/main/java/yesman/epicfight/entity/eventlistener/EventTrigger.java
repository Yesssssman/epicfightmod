package yesman.epicfight.entity.eventlistener;

import java.util.UUID;

import com.google.common.base.Function;

import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;

public class EventTrigger<T extends PlayerEvent<?>> implements Comparable<UUID> {
	private UUID uuid;
	private Function<T, Boolean> function;

	public EventTrigger(UUID uuid, Function<T, Boolean> function) {
		this.uuid = uuid;
		this.function = function;
	}
	
	public boolean is(UUID uuid) {
		return this.uuid.equals(uuid);
	}
	
	public boolean trigger(T args) {
		return this.function.apply(args);
	}
	
	@Override
	public int compareTo(UUID o) {
		if(o.equals(this.uuid)) {
			return 0;
		} else {
			return -1;
		}
	}
	
	public static <T extends PlayerEvent<?>> EventTrigger<T> makeEvent(EventType<T> type, UUID uuid, Function<T, Boolean> function) {
		return new EventTrigger<T>(uuid, function);
	}
}