package yesman.epicfight.world.capabilities.entitypatch.mob;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class ZombiePatch<T extends Zombie> extends HumanoidMobPatch<T> {
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
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		animator.addLivingAnimation(LivingMotion.CHASE, Animations.BIPED_WALK);
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
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.ZOMBIE_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.CHASE, Animations.ZOMBIE_CHASE);
		clientAnimator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonAggressiveMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		super.setAIAsInfantry(holdingRanedWeapon);
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, true));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.bipedOldTexture;
	}
}