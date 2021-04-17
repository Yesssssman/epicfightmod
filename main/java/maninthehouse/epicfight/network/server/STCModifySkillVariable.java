package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.skill.SkillSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCModifySkillVariable implements IMessage {
	private String nbtName;
	private Object value;
	private int index;
	private int type;
	
	public STCModifySkillVariable() {
		this.nbtName = "";
		this.value = null;
	}

	public STCModifySkillVariable(VariableType type, SkillSlot slot, String nbtName, Object value) {
		this(type.id, slot.getIndex(), nbtName, value);
	}

	public STCModifySkillVariable(int type, int slot, String nbtName, Object value) {
		this.type = type;
		this.index = slot;
		this.nbtName = nbtName;
		this.value = value;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.type = buf.readInt();
		this.index = buf.readInt();
		this.nbtName = ModNetworkManager.readString(buf);
		
		switch (type) {
		case 0:
			this.value = buf.readBoolean();
			break;
		case 1:
			this.value = buf.readInt();
			break;
		case 2:
			this.value = buf.readFloat();
			break;
		default:
			break;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.type);
		buf.writeInt(this.index);
		ModNetworkManager.writeString(this.nbtName, buf);

		switch (this.type) {
		case 0:
			buf.writeBoolean((boolean) this.value);
			break;
		case 1:
			buf.writeInt((int) this.value);
			break;
		case 2:
			buf.writeFloat((float) this.value);
			break;
		default:
			break;
		}
	}
	
	public static class Handler implements IMessageHandler<STCModifySkillVariable, IMessage> {
		@Override
		public IMessage onMessage(STCModifySkillVariable message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();
				
				if (playerdata != null) {
					NBTTagCompound nbt = playerdata.getSkill(message.index).getVariableNBT();

					switch (message.type) {
					case 0:
						nbt.setBoolean(message.nbtName, (boolean) message.value);
						break;
					case 1:
						nbt.setInteger(message.nbtName, (int) message.value);
						break;
					case 2:
						nbt.setFloat(message.nbtName, (float) message.value);
						break;
					default:
						break;
					}
				}
		    });
			
			return null;
		}
	}
	
	public static enum VariableType {
		BOOLEAN(0), INTEGER(1), FLOAT(2);

		final int id;

		VariableType(int id) {
			this.id = id;
		}
	}
}