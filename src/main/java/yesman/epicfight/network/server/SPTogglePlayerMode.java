package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SPTogglePlayerMode {
	private int entityId;
	private boolean battleMode;

	public SPTogglePlayerMode() {
		this.entityId = 0;
		this.battleMode = false;
	}

	public SPTogglePlayerMode(int entityId, boolean battleMode) {
		this.entityId = entityId;
		this.battleMode = battleMode;
	}

	public static SPTogglePlayerMode fromBytes(FriendlyByteBuf buf) {
		return new SPTogglePlayerMode(buf.readInt(), buf.readBoolean());
	}

	public static void toBytes(SPTogglePlayerMode msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
		buf.writeBoolean(msg.battleMode);
	}
	
	public static void handle(SPTogglePlayerMode msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Entity entity = Minecraft.getInstance().player.level.getEntity(msg.entityId);
			if (entity != null) {
				PlayerPatch<?> playerpatch = (PlayerPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerpatch != null) {
					playerpatch.setBattleMode(msg.battleMode);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}