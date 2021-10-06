package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.PlayerData;

public class STCToggleMode {
	private int entityId;
	private boolean battleMode;

	public STCToggleMode() {
		this.entityId = 0;
		this.battleMode = false;
	}

	public STCToggleMode(int entityId, boolean battleMode) {
		this.entityId = entityId;
		this.battleMode = battleMode;
	}

	public static STCToggleMode fromBytes(PacketBuffer buf) {
		return new STCToggleMode(buf.readInt(), buf.readBoolean());
	}

	public static void toBytes(STCToggleMode msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
		buf.writeBoolean(msg.battleMode);
	}
	
	public static void handle(STCToggleMode msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Entity entity = Minecraft.getInstance().player.world.getEntityByID(msg.entityId);
			
			if (entity != null) {
				PlayerData<?> entitydata = (PlayerData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (entitydata != null) {
					entitydata.setBattleMode(msg.battleMode);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}