package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.world.EpicFightGamerules;

public class SPChangeGamerule {
	private Gamerules gamerule;
	private int gameruleId;
	private Object object;
	
	public SPChangeGamerule(Gamerules gamerule, Object object) {
		this.gamerule = gamerule;
		this.gameruleId = gamerule.id;
		this.object = object;
	}
	
	public static SPChangeGamerule fromBytes(FriendlyByteBuf buf) {
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
		
		return new SPChangeGamerule(gamerule, obj);
	}

	public static void toBytes(SPChangeGamerule msg, FriendlyByteBuf buf) {
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
	
	public static void handle(SPChangeGamerule msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			switch(msg.gamerule.valueType) {
			case INTEGER:
				((GameRules.IntegerValue)Minecraft.getInstance().level.getGameRules().getRule(msg.gamerule.key)).tryDeserialize(msg.object.toString());
				break;
			case BOOLEAN:
				((GameRules.BooleanValue)Minecraft.getInstance().level.getGameRules().getRule(msg.gamerule.key)).set((boolean)msg.object, null);
				break;
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Gamerules {
		HAS_FALL_ANIMATION(0, ValueType.BOOLEAN, EpicFightGamerules.HAS_FALL_ANIMATION), 
		SPEED_PENALTY_PERCENT(1, ValueType.INTEGER, EpicFightGamerules.WEIGHT_PENALTY);
		
		ValueType valueType;
		GameRules.Key<?> key;
		int id;
		
		Gamerules(int id, ValueType valueType, GameRules.Key<?> key) {
			this.id = id;
			this.valueType = valueType;
			this.key = key;
		}
		
		enum ValueType {
			INTEGER, BOOLEAN
		}
	}
}