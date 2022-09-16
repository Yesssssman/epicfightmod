package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPPlayAnimationAndSyncTransform extends SPPlayAnimationAndSetTarget {
	protected double posX;
	protected double posY;
	protected double posZ;
	protected float yRot;
	
	public SPPlayAnimationAndSyncTransform(int namespaceId, int animation, int entityId, float modifyTime, int targetId, double posX, double posY, double posZ, float yRot) {
		super(namespaceId, animation, entityId, modifyTime, targetId);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.yRot = yRot;
	}
	
	public SPPlayAnimationAndSyncTransform(StaticAnimation animation, float modifyTime, LivingEntityPatch<?> entitypatch) {
		super(animation, modifyTime, entitypatch);
		
		Vector3d position = entitypatch.getOriginal().position();
		this.posX = position.x;
		this.posY = position.y;
		this.posZ = position.z;
		this.yRot = entitypatch.getOriginal().yRotO;
	}
	
	@Override
	public void onArrive() {
		super.onArrive();
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.player.level.getEntity(this.entityId);
		entity.setPos(this.posX, this.posY, this.posZ);
		entity.xo = entity.getX();
		entity.yo = entity.getY();
		entity.zo = entity.getZ();
		entity.xOld = entity.getX();
		entity.yOld = entity.getY();
		entity.zOld = entity.getZ();
		entity.yRotO = this.yRot;
		entity.yRot = this.yRot;
	}
	
	public static SPPlayAnimationAndSyncTransform fromBytes(PacketBuffer buf) {
		return new SPPlayAnimationAndSyncTransform(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readFloat());
	}
	
	public static void toBytes(SPPlayAnimationAndSyncTransform msg, PacketBuffer buf) {
		buf.writeInt(msg.namespaceId);
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.convertTimeModifier);
		buf.writeInt(msg.targetId);
		buf.writeDouble(msg.posX);
		buf.writeDouble(msg.posY);
		buf.writeDouble(msg.posZ);
		buf.writeFloat(msg.yRot);
	}

	public static void handler(SPPlayAnimationAndSyncTransform msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			msg.onArrive();
		});
		
		ctx.get().setPacketHandled(true);
	}
}