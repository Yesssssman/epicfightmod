package yesman.epicfight.network.client;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCLivingMotionChange;
import yesman.epicfight.network.server.STCToggleMode;

public class CTSReqPlayerInfo {
	private int entityId;

	public CTSReqPlayerInfo() {
		this.entityId = 0;
	}
	
	public CTSReqPlayerInfo(int entityId) {
		this.entityId = entityId;
	}
	
	public static CTSReqPlayerInfo fromBytes(PacketBuffer buf) {
		return new CTSReqPlayerInfo(buf.readInt());
	}
	
	public static void toBytes(CTSReqPlayerInfo msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CTSReqPlayerInfo msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Entity entity = ctx.get().getSender().world.getEntityByID(msg.entityId);
			if (entity != null && entity instanceof ServerPlayerEntity) {
				ServerPlayerData playerdata = (ServerPlayerData) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (playerdata != null) {
					List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
					List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
					int i = 0;
					for (Map.Entry<LivingMotion, StaticAnimation> entry : playerdata.getLivingMotionEntrySet()) {
						if (entry.getValue() != null) {
							motions.add(entry.getKey());
							animations.add(entry.getValue());
							i++;
						}
					}
					
					LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
					StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
					STCLivingMotionChange mg = new STCLivingMotionChange(playerdata.getOriginalEntity().getEntityId(), i);
					mg.setMotions(motionarr);
					mg.setAnimations(animationarr);
					ModNetworkManager.sendToPlayer(mg, ctx.get().getSender());
					ModNetworkManager.sendToPlayer(new STCToggleMode(playerdata.getOriginalEntity().getEntityId(), playerdata.isBattleMode()), ctx.get().getSender());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
