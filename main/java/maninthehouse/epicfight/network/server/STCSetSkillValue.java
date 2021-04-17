package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCSetSkillValue implements IMessage {
	private float floatSet;
	private boolean boolset;
	private int index;
	private int target;

	public STCSetSkillValue() {
		this.floatSet = 0;
		this.index = 0;
	}
	
	public STCSetSkillValue(Target target, int slot, float amount, boolean boolset) {
		this.target = target.id;
		this.floatSet = amount;
		this.boolset = boolset;
		this.index = slot;
	}

	public STCSetSkillValue(int target, int slot, float amount, boolean boolset) {
		this.target = target;
		this.floatSet = amount;
		this.boolset = boolset;
		this.index = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.target = buf.readInt();
		this.index = buf.readInt();
		this.floatSet = buf.readFloat();
		this.boolset = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.target);
		buf.writeInt(this.index);
		buf.writeFloat(this.floatSet);
		buf.writeBoolean(this.boolset);
	}

	public static class Handler implements IMessageHandler<STCSetSkillValue, IMessage> {
		@Override
		public IMessage onMessage(STCSetSkillValue message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();

				if (playerdata != null) {
					if (message.target == Target.COOLDOWN.id) {
						if (message.floatSet == 0) {
							playerdata.getSkill(message.index).reset(!playerdata.getOriginalEntity().isCreative());
						} else {
							playerdata.getSkill(message.index).setCooldown(message.floatSet);
						}
					} else if (message.target == Target.DURATION.id) {
						playerdata.getSkill(message.index).setDuration((int) message.floatSet);
					} else if (message.target == Target.DURATION_CONSUME.id) {
						playerdata.getSkill(message.index).setDurationConsume(message.boolset);
					}
				}
			});

			return null;
		}
	}

	public static enum Target {
		COOLDOWN(0), DURATION(1), DURATION_CONSUME(2);

		public final int id;

		Target(int id) {
			this.id = id;
		}
	}
}