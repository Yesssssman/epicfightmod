package maninhouse.epicfight.network.client;

import java.util.function.Supplier;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class CTSPlayAnimation {
	private int animationId;
	private float modifyTime;
	private boolean isClientSideAnimation;
	private boolean resendToSender;
	
	public CTSPlayAnimation() {
		this.animationId = 0;
		this.modifyTime = 0;
		this.resendToSender = false;
	}

	public CTSPlayAnimation(StaticAnimation animation, float modifyTime, boolean clinetOnly, boolean resendToSender) {
		this(animation.getId(), modifyTime, clinetOnly, resendToSender);
	}

	public CTSPlayAnimation(int animation, float modifyTime, boolean clinetOnly, boolean resendToSender) {
		this.animationId = animation;
		this.modifyTime = modifyTime;
		this.isClientSideAnimation = clinetOnly;
		this.resendToSender = resendToSender;
	}
	
	public static CTSPlayAnimation fromBytes(PacketBuffer buf) {
		return new CTSPlayAnimation(buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readBoolean());
	}

	public static void toBytes(CTSPlayAnimation msg, PacketBuffer buf) {
		buf.writeInt(msg.animationId);
		buf.writeFloat(msg.modifyTime);
		buf.writeBoolean(msg.isClientSideAnimation);
		buf.writeBoolean(msg.resendToSender);
	}
	
	public static void handle(CTSPlayAnimation msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()-> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (!msg.isClientSideAnimation) {
				playerdata.getAnimator().playAnimation(msg.animationId, msg.modifyTime);
			}
			
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(msg.animationId, serverPlayer.getEntityId(), msg.modifyTime), serverPlayer);
			
			if (msg.resendToSender) {
				ModNetworkManager.sendToPlayer(new STCPlayAnimation(msg.animationId, serverPlayer.getEntityId(), msg.modifyTime), serverPlayer);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}