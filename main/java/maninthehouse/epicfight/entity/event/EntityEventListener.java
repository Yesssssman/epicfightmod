package maninthehouse.epicfight.entity.event;

import java.util.Collection;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import maninthehouse.epicfight.capabilities.entity.player.PlayerData;

public class EntityEventListener
{
	private Multimap<Event, PlayerEvent> map;
	private final PlayerData<?> player;
	
	public EntityEventListener(PlayerData<?> player)
	{
		this.player = player;
		this.map = HashMultimap.create();
	}
	
	public void addEventListener(Event event, PlayerEvent function)
	{
		map.put(event, function);
	}
	
	public void removeListener(Event event, UUID functionUUID)
	{
		Collection<PlayerEvent> c = map.get(event);
		PlayerEvent wantToRemove = null;
		
		for(PlayerEvent e : c)
		{
			if(e.is(functionUUID))
			{
				wantToRemove = e;
				break;
			}
		}
		
		if(wantToRemove!=null)
			c.remove(wantToRemove);
	}
	
	public boolean activateEvents(Event event) {
		boolean cancel = false;
		for(PlayerEvent function : map.get(event)) {
			if(event.isRemote == this.player.isRemote()) {
				cancel |= function.doIt(this.player);
			}
		}	
		
		return cancel;
	}
	
	public enum Event
	{
		ON_ACTION_SERVER_EVENT(false), ON_ATTACK_CLIENT_EVENT(true);
		boolean isRemote;
		
		Event(boolean isRemote)
		{
			this.isRemote = isRemote;
		}
		
		public boolean getDist()
		{
			return isRemote;
		}
	}
}