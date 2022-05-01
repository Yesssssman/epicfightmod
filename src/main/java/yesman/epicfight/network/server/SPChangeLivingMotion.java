package yesman.epicfight.network.server;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPChangeLivingMotion {
	private int entityId;
	private int count;
	private boolean setChangesAsDefault;
	private List<LivingMotion> motionList = Lists.newArrayList();
	private List<StaticAnimation> animationList = Lists.newArrayList();
	
	public SPChangeLivingMotion() {
		this(-1);
	}
	
	public SPChangeLivingMotion(int entityId) {
		this(entityId, 0, false);
	}
	
	public SPChangeLivingMotion(int entityId, boolean setChangesAsDefault) {
		this(entityId, 0, setChangesAsDefault);
	}
	
	private SPChangeLivingMotion(int entityId, int count, boolean setChangesAsDefault) {
		this.entityId = entityId;
		this.count = count;
		this.setChangesAsDefault = setChangesAsDefault;
	}
	
	public SPChangeLivingMotion putPair(LivingMotion motion, StaticAnimation animation) {
		return this.putPair(Pair.of(motion, animation));
	}
	
	public SPChangeLivingMotion putPair(Pair<LivingMotion, StaticAnimation> pair) {
		this.motionList.add(pair.getFirst());
		this.animationList.add(pair.getSecond());
		this.count++;
		return this;
	}
	
	public void putEntries(Set<Map.Entry<LivingMotion, StaticAnimation>> motionSet) {
		this.count += motionSet.size();
		
		motionSet.forEach((entry) -> {
			this.motionList.add(entry.getKey());
			this.animationList.add(entry.getValue());
		});
	}
	
	public static SPChangeLivingMotion fromBytes(FriendlyByteBuf buf) {
		SPChangeLivingMotion msg = new SPChangeLivingMotion(buf.readInt(), buf.readInt(), buf.readBoolean());
		List<LivingMotion> motionList = Lists.newArrayList();
		List<StaticAnimation> animationList = Lists.newArrayList();
		
		for (int i = 0; i < msg.count; i++) {
			motionList.add(LivingMotion.values()[buf.readInt()]);
		}
		
		for (int i = 0; i < msg.count; i++) {
			animationList.add(EpicFightMod.getInstance().animationManager.findAnimationById(buf.readInt(), buf.readInt()));
		}
		
		msg.motionList = motionList;
		msg.animationList = animationList;
		
		return msg;
	}
	
	public static void toBytes(SPChangeLivingMotion msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.count);
		buf.writeBoolean(msg.setChangesAsDefault);
		
		for (LivingMotion motion : msg.motionList) {
			buf.writeInt(motion.getId());
		}
		
		for (StaticAnimation anim : msg.animationList) {
			buf.writeInt(anim.getNamespaceId());
			buf.writeInt(anim.getId());
		}
	}
	
	public static void handle(SPChangeLivingMotion msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(msg.entityId);
			 
			if (entity != null) {
				LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				ClientAnimator animator = entitypatch.getClientAnimator();
				animator.resetMotions();
				animator.resetCompositeMotion();
				
				for (int i = 0; i < msg.count; i++) {
					entitypatch.getClientAnimator().addLivingAnimation(msg.motionList.get(i), msg.animationList.get(i));
				}
				
				if (msg.setChangesAsDefault) {
					animator.setCurrentMotionsAsDefault();
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}