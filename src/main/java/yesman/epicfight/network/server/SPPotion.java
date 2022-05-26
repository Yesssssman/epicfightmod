package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public class SPPotion {
	private MobEffect effect;
	private Action action;
	private int entityId;

	public SPPotion() {
		this.effect = null;
		this.entityId = 0;
		this.action = Action.REMOVE;
	}

	public SPPotion(MobEffect effect, Action action, int entityId) {
		this.effect = effect;
		this.entityId = entityId;
		this.action = action;
	}

	public static SPPotion fromBytes(FriendlyByteBuf buf) {
		MobEffect effect = MobEffect.byId(buf.readInt());
		int entityId = buf.readInt();
		Action action = Action.getAction(buf.readInt());

		return new SPPotion(effect, action, entityId);
	}

	public static void toBytes(SPPotion msg, FriendlyByteBuf buf) {
		buf.writeInt(MobEffect.getId(msg.effect));
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.action.getSymb());
	}
	
	public static void handle(SPPotion msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.level.getEntity(msg.entityId);
			
			if (entity != null && entity instanceof LivingEntity) {
				LivingEntity livEntity = ((LivingEntity)entity);

				switch (msg.action) {
				case ACTIVATE:
					livEntity.addEffect(new MobEffectInstance(msg.effect, 0));
					break;
				case REMOVE:
					livEntity.removeEffect(msg.effect);
					break;
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Action {
		ACTIVATE(0), REMOVE(1);

		int action;

		Action(int action) {
			this.action = action;
		}

		public int getSymb() {
			return action;
		}

		private static Action getAction(int symb) {
			if(symb == 0) return ACTIVATE;
			else if(symb == 1) return REMOVE;
			else return null;
		}
	}
}