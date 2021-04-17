package maninthehouse.epicfight.network;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.network.client.*;
import maninthehouse.epicfight.network.server.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetworkManager {
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(EpicFightMod.MODID);

	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(CTSExecuteSkill.Handler.class, CTSExecuteSkill.class, id++, Side.SERVER);
		INSTANCE.registerMessage(CTSPlayAnimation.Handler.class, CTSPlayAnimation.class, id++, Side.SERVER);
		INSTANCE.registerMessage(CTSReqSpawnInfo.Handler.class, CTSReqSpawnInfo.class, id++, Side.SERVER);
		INSTANCE.registerMessage(CTSRotatePlayerYaw.Handler.class, CTSRotatePlayerYaw.class, id++, Side.SERVER);
		INSTANCE.registerMessage(CTSReqPlayerInfo.Handler.class, CTSReqPlayerInfo.class, id++, Side.SERVER);
		
		INSTANCE.registerMessage(STCExecuteSkill.Handler.class, STCExecuteSkill.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCMobInitialSetting.Handler.class, STCMobInitialSetting.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCLivingMotionChange.Handler.class, STCLivingMotionChange.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCSetSkillValue.Handler.class, STCSetSkillValue.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCNotifyPlayerYawChanged.Handler.class, STCNotifyPlayerYawChanged.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCPlayAnimation.Handler.class, STCPlayAnimation.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCPlayAnimationTarget.Handler.class, STCPlayAnimationTarget.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCPlayAnimationTP.Handler.class, STCPlayAnimationTP.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCPotion.Handler.class, STCPotion.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCResetBasicAttackCool.Handler.class, STCResetBasicAttackCool.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCModifySkillVariable.Handler.class, STCModifySkillVariable.class, id++, Side.CLIENT);
		INSTANCE.registerMessage(STCGameruleChange.Handler.class, STCGameruleChange.class, id++, Side.CLIENT);
	}
	
	public static void sendToServer(IMessage message) {
		INSTANCE.sendToServer(message);
	}

	public static void sendToAll(IMessage message) {
		INSTANCE.sendToAll(message);
	}

	public static void sendToAllPlayerTrackingThisEntity(IMessage message, Entity entity) {
		INSTANCE.sendToAllTracking(message, entity);
	}

	public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
		INSTANCE.sendTo(message, player);
	}

	public static void sendToAllPlayerTrackingThisEntityWithSelf(IMessage message, EntityPlayerMP entity) {
		sendToPlayer(message, entity);
		sendToAllPlayerTrackingThisEntity(message, entity);
	}
	
	private static final int MAX_LENGTH = 32767;
	
	public static void writeString(String string, ByteBuf buffer) {
		byte[] abyte = string.getBytes(StandardCharsets.UTF_8);

		if (abyte.length > MAX_LENGTH) {
			throw new EncoderException(
					"String too big (was " + abyte.length + " bytes encoded, max " + MAX_LENGTH + ")");
		} else {
			buffer.writeInt(abyte.length);
			buffer.writeBytes(abyte);
		}
	}
	
	public static String readString(ByteBuf buffer) {
		int i = buffer.readInt();
		if (i > MAX_LENGTH * 4) {
			throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + i + " > " + MAX_LENGTH * 4 + ")");
		} else if (i < 0) {
			throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
		} else {
			String s = buffer.toString(buffer.readerIndex(), i, StandardCharsets.UTF_8);
			buffer.readerIndex(buffer.readerIndex() + i);
			if (s.length() > MAX_LENGTH) {
				throw new DecoderException("The received string length is longer than maximum allowed (" + i + " > " + MAX_LENGTH + ")");
			} else {
				return s;
			}
		}
	}
}