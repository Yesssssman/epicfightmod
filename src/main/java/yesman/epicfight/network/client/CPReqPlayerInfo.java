package yesman.epicfight.network.client;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.network.server.SPTogglePlayerMode;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPReqPlayerInfo {
	private int entityId;

	public CPReqPlayerInfo() {
		this.entityId = 0;
	}
	
	public CPReqPlayerInfo(int entityId) {
		this.entityId = entityId;
	}
	
	public static CPReqPlayerInfo fromBytes(FriendlyByteBuf buf) {
		return new CPReqPlayerInfo(buf.readInt());
	}
	
	public static void toBytes(CPReqPlayerInfo msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CPReqPlayerInfo msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Entity entity = ctx.get().getSender().level.getEntity(msg.entityId);
			
			if (entity != null && entity instanceof ServerPlayer) {
				ServerPlayerPatch playerpatch = (ServerPlayerPatch) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (playerpatch != null) {
					List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
					List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
					int i = 0;
					
					for (Map.Entry<LivingMotion, StaticAnimation> entry : playerpatch.getCompositeLivingMotions()) {
						if (entry.getValue() != null) {
							motions.add(entry.getKey());
							animations.add(entry.getValue());
							i++;
						}
					}
					
					LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
					StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
					SPChangeLivingMotion mg = new SPChangeLivingMotion(playerpatch.getOriginal().getId(), i, SPPlayAnimation.Layer.COMPOSITE_LAYER);
					mg.setMotions(motionarr);
					mg.setAnimations(animationarr);
					EpicFightNetworkManager.sendToPlayer(mg, ctx.get().getSender());
					EpicFightNetworkManager.sendToPlayer(new SPTogglePlayerMode(playerpatch.getOriginal().getId(), playerpatch.isBattleMode()), ctx.get().getSender());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
