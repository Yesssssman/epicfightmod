package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class SPSetSkillValue {
	private float floatType;
	private boolean booleanType;
	private int index;
	private Target target;

	public SPSetSkillValue() {
		this.floatType = 0;
		this.index = 0;
	}

	public SPSetSkillValue(Target target, int slot, float amount, boolean boolset) {
		this.target = target;
		this.floatType = amount;
		this.booleanType = boolset;
		this.index = slot;
	}
	
	public static SPSetSkillValue fromBytes(PacketBuffer buf) {
		return new SPSetSkillValue(Target.values()[buf.readInt()], buf.readInt(), buf.readFloat(), buf.readBoolean());
	}
	
	public static void toBytes(SPSetSkillValue msg, PacketBuffer buf) {
		buf.writeInt(msg.target.ordinal());
		buf.writeInt(msg.index);
		buf.writeFloat(msg.floatType);
		buf.writeBoolean(msg.booleanType);
	}
	
	public static void handle(SPSetSkillValue msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayerPatch playerpatch = (LocalPlayerPatch) mc.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerpatch != null) {
				switch (msg.target) {
				case COOLDOWN:
					playerpatch.getSkill(msg.index).setResource(msg.floatType);
					break;
				case DURATION:
					playerpatch.getSkill(msg.index).setDuration((int) msg.floatType);
					break;
				case MAX_DURATION:
					playerpatch.getSkill(msg.index).setMaxDuration((int) msg.floatType);
					break;
				case STACK:
					playerpatch.getSkill(msg.index).setStack((int) msg.floatType);
					break;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Target {
		COOLDOWN, DURATION, MAX_DURATION, STACK;
	}
}