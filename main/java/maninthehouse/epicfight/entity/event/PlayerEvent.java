package maninthehouse.epicfight.entity.event;

import java.util.UUID;
import java.util.function.Function;

import maninthehouse.epicfight.capabilities.entity.player.PlayerData;

public class PlayerEvent implements Comparable<UUID>
{
	private UUID uuid;
	private Function<PlayerData<?>, Boolean> function;
	
	public PlayerEvent(UUID uuid, Function<PlayerData<?>, Boolean> function)
	{
		this.uuid = uuid;
		this.function = function;
	}
	
	public boolean is(UUID uuid)
	{
		return this.uuid.equals(uuid);
	}
	
	public boolean doIt(PlayerData<?> player)
	{
		return this.function.apply(player);
	}
	
	@Override
	public int compareTo(UUID o)
	{
		if(o.equals(this.uuid))
			return 0;
		else
			return -1;
	}
	
	public static PlayerEvent makeEvent(UUID uuid, Function<PlayerData<?>, Boolean> function)
	{
		return new PlayerEvent(uuid, function);
	}
}