package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCPlayAnimationTP extends STCPlayAnimationTarget {
	protected double posX;
	protected double posY;
	protected double posZ;
	protected float yaw;
	
	public STCPlayAnimationTP() {
		super();
		posX = 0;
		posY = 0;
		posZ = 0;
		yaw = 0;
	}
	
	public STCPlayAnimationTP(int namespaceId, int animation, int entityId, float modifyTime, int targetId, double posX, double posY, double posZ, float yaw) {
		super(namespaceId, animation, entityId, modifyTime, targetId);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.yaw = yaw;
	}
	
	@Override
	public void onArrive() {
		super.onArrive();
		Entity entity = Minecraft.getInstance().player.world.getEntityByID(this.entityId);
		entity.setPositionAndUpdate(this.posX, this.posY, this.posZ);
		entity.prevPosX = entity.getPosX();
		entity.prevPosY = entity.getPosY();
		entity.prevPosZ = entity.getPosZ();
		entity.lastTickPosX = entity.getPosX();
		entity.lastTickPosY = entity.getPosY();
		entity.lastTickPosZ = entity.getPosZ();
		entity.prevRotationYaw = yaw;
		entity.rotationYaw = yaw;
	}
	
	public static STCPlayAnimationTP fromBytes(PacketBuffer buf) {
		return new STCPlayAnimationTP(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
	}
	
	public static void toBytes(STCPlayAnimationTP msg, PacketBuffer buf) {
		buf.writeInt(msg.namespaceId);
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.modifyTime);
		buf.writeInt(msg.targetId);
		buf.writeDouble(msg.posX);
		buf.writeDouble(msg.posY);
		buf.writeDouble(msg.posZ);
		buf.writeFloat(msg.yaw);
	}

	public static void handler(STCPlayAnimationTP msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}