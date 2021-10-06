package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.RuleKey;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.world.ModGamerules;

public class STCGameruleChange {
	private Gamerules gamerule;
	private int gameruleId;
	private Object object;
	
	public STCGameruleChange(Gamerules gamerule, Object object) {
		this.gamerule = gamerule;
		this.gameruleId = gamerule.id;
		this.object = object;
	}
	
	public static STCGameruleChange fromBytes(PacketBuffer buf) {
		int id = buf.readInt();
		Gamerules gamerule = Gamerules.values()[id];
		Object obj = null;
		
		switch(gamerule.valueType) {
		case INTEGER:
			obj = buf.readInt();
			break;
		case BOOLEAN:
			obj = buf.readBoolean();
			break;
		}
		
		return new STCGameruleChange(gamerule, obj);
	}

	public static void toBytes(STCGameruleChange msg, PacketBuffer buf) {
		buf.writeInt(msg.gameruleId);
		switch(msg.gamerule.valueType) {
		case INTEGER:
			buf.writeInt((int)msg.object);
			break;
		case BOOLEAN:
			buf.writeBoolean((boolean)msg.object);
			break;
		}
	}
	
	public static void handle(STCGameruleChange msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			switch(msg.gamerule.valueType) {
			case INTEGER:
				((GameRules.IntegerValue)Minecraft.getInstance().world.getGameRules().get(msg.gamerule.key)).parseIntValue(msg.object.toString());
				break;
			case BOOLEAN:
				((GameRules.BooleanValue)Minecraft.getInstance().world.getGameRules().get(msg.gamerule.key)).set((boolean)msg.object, null);
				break;
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Gamerules {
		HAS_FALL_ANIMATION(0, ValueType.BOOLEAN, ModGamerules.HAS_FALL_ANIMATION), 
		SPEED_PENALTY_PERCENT(1, ValueType.INTEGER, ModGamerules.WEIGHT_PENALTY);
		
		ValueType valueType;
		RuleKey<?> key;
		int id;
		
		Gamerules(int id, ValueType valueType, RuleKey<?> key) {
			this.id = id;
			this.valueType = valueType;
			this.key = key;
		}
		
		enum ValueType {
			INTEGER, BOOLEAN
		}
	}
}