package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.network.ModNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCGameruleChange implements IMessage {
	private String ruleName;
	private String value;
	
	public STCGameruleChange() {
		this.ruleName = "";
		this.value = "";
	}
	
	public STCGameruleChange(String ruleName, String value) {
		this.ruleName = ruleName;
		this.value = value;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.ruleName = ModNetworkManager.readString(buf);
		this.value = ModNetworkManager.readString(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ModNetworkManager.writeString(this.ruleName, buf);
		ModNetworkManager.writeString(this.value, buf);
	}
	
	public static class Handler implements IMessageHandler<STCGameruleChange, IMessage> {
		@Override
		public IMessage onMessage(STCGameruleChange message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				Minecraft.getMinecraft().world.getGameRules().setOrCreateGameRule(message.ruleName, message.value);
		    });
			
			return null;
		}
	}
}