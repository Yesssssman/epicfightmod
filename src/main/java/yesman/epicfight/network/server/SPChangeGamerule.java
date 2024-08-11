package yesman.epicfight.network.server;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.world.gamerule.EpicFightGamerules;

public class SPChangeGamerule {
	private final SynchronizedGameRules gamerule;
	private final int gameruleId;
	private final Object object;
	
	public SPChangeGamerule() {
		this.gamerule = null;
		this.gameruleId = -1;
		this.object = 0;
	}
	
	public SPChangeGamerule(SynchronizedGameRules gamerule, Object object) {
		this.gamerule = gamerule;
		this.gameruleId = gamerule.ordinal();
		this.object = object;
	}
	
	public static SPChangeGamerule fromBytes(FriendlyByteBuf buf) {
		int id = buf.readInt();
		SynchronizedGameRules gamerule = SynchronizedGameRules.values()[id];
		Object obj = gamerule.decoder.apply(buf);
		
		return new SPChangeGamerule(gamerule, obj);
	}

	public static void toBytes(SPChangeGamerule msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.gameruleId);
		msg.gamerule.encoder.accept(buf, msg.object);
	}
	
	public static void handle(SPChangeGamerule msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> msg.gamerule.setRule.accept(Minecraft.getInstance().level, msg.object));
		ctx.get().setPacketHandled(true);
	}
	
	public enum SynchronizedGameRules {
		HAS_FALL_ANIMATION((level) -> level.getGameRules().getBoolean(EpicFightGamerules.HAS_FALL_ANIMATION), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.HAS_FALL_ANIMATION).set((boolean)value, null), (buf, val) -> buf.writeBoolean((boolean)val), ByteBuf::readBoolean),
		WEIGHT_PENALTY((level) -> level.getGameRules().getInt(EpicFightGamerules.WEIGHT_PENALTY), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.WEIGHT_PENALTY).tryDeserialize(value.toString()), (buf, val) -> buf.writeInt((int)val), ByteBuf::readInt),
		DIABLE_ENTITY_UI((level) -> level.getGameRules().getBoolean(EpicFightGamerules.DISABLE_ENTITY_UI), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.DISABLE_ENTITY_UI).set((boolean)value, null), (buf, val) -> buf.writeBoolean((boolean)val), ByteBuf::readBoolean),
		CAN_SWITCH_COMBAT((level) -> level.getGameRules().getBoolean(EpicFightGamerules.CAN_SWITCH_COMBAT), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.CAN_SWITCH_COMBAT).set((boolean)value, null), (buf, val) -> buf.writeBoolean((boolean)val), ByteBuf::readBoolean),
		STIFF_COMBO_ATTACKS((level) -> level.getGameRules().getBoolean(EpicFightGamerules.STIFF_COMBO_ATTACKS), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.STIFF_COMBO_ATTACKS).set((boolean)value, null), (buf, val) -> buf.writeBoolean((boolean)val), ByteBuf::readBoolean),
		NO_MOBS_IN_BOSSFIGHT((level) -> level.getGameRules().getBoolean(EpicFightGamerules.NO_MOBS_IN_BOSSFIGHT), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.NO_MOBS_IN_BOSSFIGHT).set((boolean)value, null), (buf, val) -> buf.writeBoolean((boolean)val), ByteBuf::readBoolean),
		INITIAL_PLAYER_MODE((level) -> level.getGameRules().getInt(EpicFightGamerules.INITIAL_PLAYER_MODE), (level, value) -> level.getGameRules().getRule(EpicFightGamerules.INITIAL_PLAYER_MODE).tryDeserialize(value.toString()), (buf, val) -> buf.writeInt((int)val), ByteBuf::readInt);
		
		public Function<Level, Object> getRule;
		public BiConsumer<Level, Object> setRule;
		public BiConsumer<ByteBuf, Object> encoder;
		public Function<ByteBuf, Object> decoder;
		
		SynchronizedGameRules(Function<Level, Object> getRule, BiConsumer<Level, Object> setRule, BiConsumer<ByteBuf, Object> encoder, Function<ByteBuf, Object > decoder) {
			this.getRule = getRule;
			this.setRule = setRule;
			this.encoder = encoder;
			this.decoder = decoder;
		}
	}
}