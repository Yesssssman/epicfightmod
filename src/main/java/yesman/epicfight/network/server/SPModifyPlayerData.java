package yesman.epicfight.network.server;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SPModifyPlayerData {
	
	PacketType packetType;
	private int entityId;
	private Map<String, Object> data;

	public SPModifyPlayerData() {
		this.packetType = null;
		this.entityId = 0;
		this.data = Maps.newHashMap();
	}
	
	public SPModifyPlayerData(int entityId, float yaw) {
		this(PacketType.YAW_CORRECTION, entityId);
		this.addData("yaw", yaw);
	}
	
	public SPModifyPlayerData(int entityId, boolean lastAttackSuccess) {
		this(PacketType.LAST_ATTACK_RESULT, entityId);
		this.addData("lastAttackSuccess", lastAttackSuccess);
	}
	
	public SPModifyPlayerData(int entityId, PlayerPatch.PlayerMode mode) {
		this(PacketType.MODE, entityId);
		this.addData("mode", mode);
	}
	
	public SPModifyPlayerData(int entityId, Entity grapplingTarget) {
		this(PacketType.SET_GRAPPLE_TARGET, entityId);
		this.addData("grapplingTarget", grapplingTarget == null ? -1 : grapplingTarget.getId());
	}
	
	public SPModifyPlayerData(PacketType packetType, int entityId) {
		this.packetType = packetType;
		this.entityId = entityId;
		this.data = Maps.newHashMap();
	}
	
	public SPModifyPlayerData addData(String key, Object val) {
		this.data.put(key, val);
		return this;
	}
	
	public static SPModifyPlayerData fromBytes(FriendlyByteBuf buf) {
		PacketType packetType = PacketType.values()[buf.readInt()];
		SPModifyPlayerData packet = new SPModifyPlayerData(packetType, buf.readInt());
		packetType.decoder.accept(packet, buf);
		
		return packet;
	}

	public static void toBytes(SPModifyPlayerData msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.packetType.ordinal());
		buf.writeInt(msg.entityId);
		msg.packetType.encoder.accept(msg, buf);
	}
	
	public static void handle(SPModifyPlayerData msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(msg.entityId);
			
			if (entity != null) {
				if (entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null) instanceof PlayerPatch<?> playerpatch) {
					switch (msg.packetType) {
					case YAW_CORRECTION:
						playerpatch.changeModelYRot((float)msg.data.get("yaw"));
						break;
					case MODE:
						playerpatch.toMode((PlayerPatch.PlayerMode)msg.data.get("mode"), false);
						break;
					case LAST_ATTACK_RESULT:
						playerpatch.setLastAttackSuccess((boolean)msg.data.get("lastAttackSuccess"));
						break;
					case SET_GRAPPLE_TARGET:
						Entity grapplingTarget = mc.player.level.getEntity((int)msg.data.get("grapplingTarget"));
						
						if (grapplingTarget instanceof LivingEntity) {
							playerpatch.setGrapplingTarget((LivingEntity)grapplingTarget);
						} else {
							playerpatch.setGrapplingTarget((LivingEntity)null);
						}
						
						break;
					}
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static enum PacketType {
		YAW_CORRECTION( (packet, buffer) -> {
			buffer.writeFloat( (float)packet.data.get("yaw") );
		}, (packet, buffer) -> {
			packet.addData("yaw", buffer.readFloat());
		})
		
		,MODE( (packet, buffer) -> {
			buffer.writeInt(((PlayerPatch.PlayerMode)packet.data.get("mode")).ordinal());
		}, (packet, buffer) -> {
			packet.addData("mode", PlayerPatch.PlayerMode.values()[buffer.readInt()]);
		})
		
		,LAST_ATTACK_RESULT( (packet, buffer) -> {
			buffer.writeBoolean( (boolean)packet.data.get("lastAttackSuccess") );
		}, (packet, buffer) -> {
			packet.addData("lastAttackSuccess", buffer.readBoolean());
		})
		,SET_GRAPPLE_TARGET( (packet, buffer) -> {
			buffer.writeInt( (int)packet.data.get("grapplingTarget") );
		}, (packet, buffer) -> {
			packet.addData("grapplingTarget", buffer.readInt());
		});
		
		BiConsumer<SPModifyPlayerData, FriendlyByteBuf> encoder;
		BiConsumer<SPModifyPlayerData, FriendlyByteBuf> decoder;
		
		PacketType(BiConsumer<SPModifyPlayerData, FriendlyByteBuf> encoder, BiConsumer<SPModifyPlayerData, FriendlyByteBuf> decoder) {
			this.encoder = encoder;
			this.decoder = decoder;
		}
	}
}