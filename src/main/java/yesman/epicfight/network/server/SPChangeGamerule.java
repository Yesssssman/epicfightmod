package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameRules;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class SPChangeGamerule {
	private SynchronizedGameRules gamerule;
	private int gameruleId;
	private Object object;
	
	public SPChangeGamerule(SynchronizedGameRules gamerule, Object object) {
		this.gamerule = gamerule;
		this.gameruleId = gamerule.ordinal();
		this.object = object;
	}
	
	public static SPChangeGamerule fromBytes(PacketBuffer buf) {
		int id = buf.readInt();
		SynchronizedGameRules gamerule = SynchronizedGameRules.values()[id];
		Object obj = null;
		
		switch (gamerule.valueType) {
		case INTEGER:
			obj = buf.readInt();
			break;
		case BOOLEAN:
			obj = buf.readBoolean();
			break;
		}
		
		return new SPChangeGamerule(gamerule, obj);
	}

	public static void toBytes(SPChangeGamerule msg, PacketBuffer buf) {
		buf.writeInt(msg.gameruleId);
		switch (msg.gamerule.valueType) {
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
			
			switch (msg.gamerule.valueType) {
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
	
	public static enum SynchronizedGameRules {
		HAS_FALL_ANIMATION(ValueType.BOOLEAN, EpicFightGamerules.HAS_FALL_ANIMATION), 
		WEIGHT_PENALTY(ValueType.INTEGER, EpicFightGamerules.WEIGHT_PENALTY),
		DIABLE_ENTITY_UI(ValueType.BOOLEAN, EpicFightGamerules.DISABLE_ENTITY_UI);
		
		ValueType valueType;
		GameRules.RuleKey<?> key;
		
		SynchronizedGameRules(ValueType valueType, GameRules.RuleKey<?> key) {
			this.valueType = valueType;
			this.key = key;
		}
		
		enum ValueType {
			INTEGER, BOOLEAN
		}
	}
}