package yesman.epicfight.world.capabilities.entitypatch.mob;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class ZombiePatch<T extends PathfinderMob> extends HumanoidMobPatch<T> {
	public ZombiePatch() {
		super(Faction.UNDEAD);
	}
	
	@Override
	public void onStartTracking(ServerPlayer trackingPlayer) {
		if (!this.getHoldingItemCapability(InteractionHand.MAIN_HAND).isEmpty()) {
			SPSpawnData packet = new SPSpawnData(this.original.getId());
			EpicFightNetworkManager.sendToPlayer(packet, trackingPlayer);
		}
		
		super.onStartTracking(trackingPlayer);
	}
	
	@Override
	public void processSpawnData(ByteBuf buf) {
		ClientAnimator animator = this.getClientAnimator();
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
		animator.addLivingAnimation(LivingMotions.CHASE, Animations.BIPED_WALK);
		animator.setCurrentMotionsAsDefault();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0D);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.ZOMBIE_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.ZOMBIE_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.CHASE, Animations.ZOMBIE_CHASE);
		clientAnimator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
}