package yesman.epicfight.world.capabilities.entitypatch.mob;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPReqSpawnInfo;
import yesman.epicfight.network.server.SPMobInitialize;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.AttackBehaviorGoal;

public class ZombiePatch<T extends Zombie> extends HumanoidMobPatch<T> {
	public ZombiePatch() {
		super(Faction.UNDEAD);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		if (!this.isLogicalClient()) {
			if (!this.original.canPickUpLoot()) {
				this.original.setCanPickUpLoot(this.isArmed());
			}
		} else {
			EpicFightNetworkManager.sendToServer(new CPReqSpawnInfo(this.original.getId()));
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0D);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.ZOMBIE_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
	}
	
	@Override
	public SPMobInitialize sendInitialInformationToClient() {
		SPMobInitialize packet = new SPMobInitialize(this.original.getId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.original.canPickUpLoot());
        
		return packet;
	}
	
	@Override
	public void clientInitialSettings(ByteBuf buf) {
		ClientAnimator animator = this.getClientAnimator();
		
		if (buf.readBoolean()) {
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
			animator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		}
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false, Animations.ZOMBIE_CHASE, Animations.ZOMBIE_WALK, !this.original.isBaby()));
		this.original.goalSelector.addGoal(0, new AttackBehaviorGoal(this, MobCombatBehaviors.ZOMBIE_ATTACKS.build(this)));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.bipedOldTexture;
	}
}