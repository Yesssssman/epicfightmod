package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCPotion {
	private Effect effect;
	private Action action;
	private int entityId;

	public STCPotion() {
		this.effect = null;
		this.entityId = 0;
		this.action = Action.Remove;
	}

	public STCPotion(Effect effect, Action action, int entityId) {
		this.effect = effect;
		this.entityId = entityId;
		this.action = action;
	}

	public static STCPotion fromBytes(PacketBuffer buf) {
		Effect effect = Effect.get(buf.readInt());
		int entityId = buf.readInt();
		Action action = Action.getAction(buf.readInt());

		return new STCPotion(effect, action, entityId);
	}

	public static void toBytes(STCPotion msg, PacketBuffer buf) {
		buf.writeInt(Effect.getId(msg.effect));
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.action.getSymb());
	}
	
	public static void handle(STCPotion msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Entity entity = Minecraft.getInstance().world.getEntityByID(msg.entityId);
			
			if (entity != null && entity instanceof LivingEntity) {
				LivingEntity livEntity = ((LivingEntity)entity);

				switch (msg.action) {
				case Active:
					livEntity.addPotionEffect(new EffectInstance(msg.effect, 0));
					break;
				case Remove:
					livEntity.removePotionEffect(msg.effect);
					break;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Action {
		Active(0), Remove(1);

		int action;

		Action(int action) {
			this.action = action;
		}

		public int getSymb() {
			return action;
		}

		private static Action getAction(int symb) {
			if(symb == 0) return Active;
			else if(symb == 1) return Remove;
			else return null;
		}
	}
}
