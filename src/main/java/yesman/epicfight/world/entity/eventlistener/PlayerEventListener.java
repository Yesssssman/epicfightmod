package yesman.epicfight.world.entity.eventlistener;

import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import net.minecraftforge.fml.LogicalSide;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class PlayerEventListener {
	private Map<EventType<? extends PlayerEvent<?>>, TreeMultimap<Integer, EventTrigger<? extends PlayerEvent<?>>>> events;
	private PlayerPatch<?> playerpatch;
	
	public PlayerEventListener(PlayerPatch<?> playerpatch) {
		this.playerpatch = playerpatch;
		this.events = Maps.newHashMap();
	}
	
	public <T extends PlayerEvent<?>> void addEventListener(EventType<T> eventType, UUID uuid, Consumer<T> function) {
		this.addEventListener(eventType, uuid, function, -1);
	}
	
	public <T extends PlayerEvent<?>> void addEventListener(EventType<T> eventType, UUID uuid, Consumer<T> function, int priority) {
		if (eventType.shouldActive(this.playerpatch.isLogicalClient())) {
			if (!this.events.containsKey(eventType)) {
				this.events.put(eventType, TreeMultimap.create());
			}
			
			priority = Math.max(priority, -1);
			this.removeListener(eventType, uuid, priority);
			TreeMultimap<Integer, EventTrigger<? extends PlayerEvent<?>>> map = this.events.get(eventType);
			map.put(priority, EventTrigger.makeEvent(uuid, function, priority));
		}
	}
	
	public <T extends PlayerEvent<?>> void removeListener(EventType<T> eventType, UUID uuid) {
		this.removeListener(eventType, uuid, -1);
	}
	
	public <T extends PlayerEvent<?>> void removeListener(EventType<T> eventType, UUID uuid, int priority) {
		Multimap<Integer, EventTrigger<? extends PlayerEvent<?>>> map = this.events.get(eventType);
		
		if (map != null) {
			priority = Math.max(priority, -1);
			map.get(priority).removeIf((trigger) -> trigger.is(uuid));
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends PlayerEvent<?>> boolean triggerEvents(EventType<T> eventType, T event) {
		boolean cancel = false;
		TreeMultimap<Integer, EventTrigger<? extends PlayerEvent<?>>> map = this.events.get(eventType);
		
		if (map != null) {
			for (int i : map.keySet().descendingSet()) {
				if (!cancel || i == -1) {
					for (EventTrigger<?> eventTrigger : map.get(i)) {
						if (eventType.shouldActive(this.playerpatch.isLogicalClient())) {
							EventTrigger<T> castedTrigger = ((EventTrigger<T>)eventTrigger);
							castedTrigger.trigger(event);
							cancel |= event.isCanceled();
						}
					}
				}
			}
		}
		
		return cancel;
	}
	
	public static class EventType<T extends PlayerEvent<?>> {
		public static final EventType<ActionEvent<LocalPlayerPatch>> ACTION_EVENT_CLIENT = new EventType<>(null);
		public static final EventType<ActionEvent<ServerPlayerPatch>> ACTION_EVENT_SERVER = new EventType<>(null);
		public static final EventType<AttackSpeedModifyEvent> ATTACK_SPEED_MODIFY_EVENT = new EventType<>(null);
		public static final EventType<DealtDamageEvent<PlayerPatch<?>>> DEALT_DAMAGE_EVENT_PRE = new EventType<>(null);
		public static final EventType<DealtDamageEvent<ServerPlayerPatch>> DEALT_DAMAGE_EVENT_POST = new EventType<>(LogicalSide.SERVER);
		public static final EventType<HurtEvent.Pre> HURT_EVENT_PRE = new EventType<>(LogicalSide.SERVER);
		public static final EventType<HurtEvent.Post> HURT_EVENT_POST = new EventType<>(LogicalSide.SERVER);
		public static final EventType<AttackEndEvent> ATTACK_ANIMATION_END_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<BasicAttackEvent> BASIC_ATTACK_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<MovementInputEvent> MOVEMENT_INPUT_EVENT = new EventType<>(LogicalSide.CLIENT);
		public static final EventType<RightClickItemEvent<LocalPlayerPatch>> CLIENT_ITEM_USE_EVENT = new EventType<>(LogicalSide.CLIENT);
		public static final EventType<RightClickItemEvent<ServerPlayerPatch>> SERVER_ITEM_USE_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<ItemUseEndEvent> SERVER_ITEM_STOP_EVENT = new EventType<>(LogicalSide.SERVER);
		public static final EventType<ProjectileHitEvent> PROJECTILE_HIT_EVENT = new EventType<>(LogicalSide.SERVER);
		
		LogicalSide side;
		
		EventType(LogicalSide side) {
			this.side = side;
		}
		
		public boolean shouldActive(boolean isRemote) {
			return this.side == null ? true : this.side.isClient() == isRemote;
		}
	}
}