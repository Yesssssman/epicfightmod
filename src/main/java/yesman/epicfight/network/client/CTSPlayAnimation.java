package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCPlayAnimation;

public class CTSPlayAnimation {
	private int namespaceId;
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
		this(animation.getNamespaceId(), animation.getId(), modifyTime, clinetOnly, resendToSender);
	}

	public CTSPlayAnimation(int namespaceId, int animationId, float modifyTime, boolean clinetOnly, boolean resendToSender) {
		this.namespaceId = namespaceId;
		this.animationId = animationId;
		this.modifyTime = modifyTime;
		this.isClientSideAnimation = clinetOnly;
		this.resendToSender = resendToSender;
	}
	
	public static CTSPlayAnimation fromBytes(PacketBuffer buf) {
		return new CTSPlayAnimation(buf.readInt(), buf.readInt(), buf.readFloat(), buf.readBoolean(), buf.readBoolean());
	}

	public static void toBytes(CTSPlayAnimation msg, PacketBuffer buf) {
		buf.writeInt(msg.namespaceId);
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
				playerdata.getAnimator().playAnimation(msg.namespaceId, msg.animationId, msg.modifyTime);
			}
			
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(msg.namespaceId, msg.animationId, serverPlayer.getEntityId(), msg.modifyTime), serverPlayer);
			
			if (msg.resendToSender) {
				ModNetworkManager.sendToPlayer(new STCPlayAnimation(msg.namespaceId, msg.animationId, serverPlayer.getEntityId(), msg.modifyTime), serverPlayer);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}