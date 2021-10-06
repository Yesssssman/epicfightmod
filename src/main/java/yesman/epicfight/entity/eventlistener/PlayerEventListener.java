package yesman.epicfight.entity.eventlistener;

import java.util.UUID;

import com.google.common.base.Function;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import net.minecraftforge.fml.LogicalSide;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;

public class PlayerEventListener {
	private Multimap<EventType<? extends PlayerEvent<?>>, EventTrigger<? extends PlayerEvent<?>>> events;
	private PlayerData<?> player;

	public PlayerEventListener(PlayerData<?> player) {
		this.player = player;
		this.events = HashMultimap.create();
	}

	public <T extends PlayerEvent<?>> void addEventListener(EventType<T> eventType, UUID uuid, Function<T, Boolean> function) {
		if (eventType.shouldActive(this.player.isRemote())) {
			this.removeListener(eventType, uuid);
			this.events.put(eventType, EventTrigger.makeEvent(eventType, uuid, function));
		}
	}
	
	public <T extends PlayerEvent<?>> void removeListener(EventType<T> eventType, UUID uuid) {
		this.events.get(eventType).removeIf((trigger) -> trigger.compareTo(uuid) == 0);
	}

	@SuppressWarnings("unchecked")
	public <T extends PlayerEvent<?>> boolean activateEvents(EventType<T> eventType, T event) {
		boolean cancel = false;
		for (EventTrigger<?> eventTrigger : this.events.get(eventType)) {
			if (eventType.shouldActive(this.player.isRemote())) {
				cancel |= ((EventTrigger<T>) eventTrigger).trigger(event);
			}
		}
		return cancel;
	}
	
	public static class EventType<T extends PlayerEvent<?>> {
		public static final EventType<ActionEvent> ACTION_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<GetAttackSpeedEvent> ATTACK_SPEED_GET_EVENT = new EventType<>(null);
		public static final EventType<DealtDamageEvent<PlayerData<?>>> DEALT_DAMAGE_PRE_EVENT = new EventType<>(null);
		public static final EventType<DealtDamageEvent<ServerPlayerData>> DEALT_DAMAGE_POST_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<TakeDamageEvent> TAKE_DAMAGE_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<AttackEndEvent> ATTACK_ANIMATION_END_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<HitEvent> HIT_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<BasicAttackEvent> BASIC_ATTACK_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<MovementInputEvent> MOVEMENT_INPUT_EVENT = new EventType<>(LogicalSide.CLIENT);
		public static final EventType<RightClickItemEvent<ClientPlayerData>> CLIENT_ITEM_USE_EVENT = new EventType<>(LogicalSide.CLIENT);
		public static final EventType<RightClickItemEvent<ServerPlayerData>> SERVER_ITEM_USE_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<ItemUseEndEvent> SERVER_ITEM_STOP_EVENT = new EventType<>(LogicalSide.SERVER);
		
		LogicalSide side;
		
		EventType(LogicalSide side) {
			this.side = side;
		}
		
		public boolean shouldActive(boolean isRemote) {
			return side == null ? true : this.side.isClient() == isRemote;
		}
	}
}