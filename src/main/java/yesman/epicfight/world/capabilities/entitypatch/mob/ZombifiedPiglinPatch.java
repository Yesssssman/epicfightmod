package yesman.epicfight.world.capabilities.entitypatch.mob;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.AttackResult;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPMobInitialize;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.world.entity.ai.goal.AttackBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class ZombifiedPiglinPatch extends HumanoidMobPatch<ZombifiedPiglin> {
	public ZombifiedPiglinPatch() {
		super(Faction.NATURAL);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_ZOMBIFIED_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.PIGLIN_ZOMBIFIED_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public SPMobInitialize sendInitialInformationToClient() {
		SPMobInitialize packet = new SPMobInitialize(this.original.getId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.original.canPickUpLoot());
		return packet;
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsArmed() {
		this.original.goalSelector.addGoal(1, new EntityAIPigmanChase(this, this.original));
		this.original.goalSelector.addGoal(0, new AttackBehaviorGoal(this, MobCombatBehaviors.BIPED_ARMED_ATTACKS.build(this)));
	}
	
	@Override
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		if (damageSource.getEntity() instanceof ZombifiedPiglin) {
			return new AttackResult(AttackResult.ResultType.FAILED, amount);
		}
		
		return super.tryHurt(damageSource, amount);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.piglin;
	}
	
	static class EntityAIPigmanChase extends ChasingGoal {
		boolean angry;
		
		public EntityAIPigmanChase(HumanoidMobPatch<?> entitypatch, Mob creature) {
			super(entitypatch, creature, 1.35D, false, Animations.PIGLIN_ZOMBIFIED_CHASE, Animations.PIGLIN_ZOMBIFIED_WALK);
		}
		
		@Override
		public void tick() {
			super.tick();

			if (!((ZombifiedPiglin)this.attacker).isAggressive()) {
				if (this.angry) {
					SPChangeLivingMotion msg = new SPChangeLivingMotion(this.attacker.getId(), 1, SPPlayAnimation.Layer.BASE_LAYER);
					msg.setMotions(LivingMotion.WALK);
					msg.setAnimations(walkingAnimation);
					EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
					this.angry = false;
				}
			} else {
				if (!this.angry) {
					SPChangeLivingMotion msg = new SPChangeLivingMotion(this.attacker.getId(), 1, SPPlayAnimation.Layer.BASE_LAYER);
					msg.setMotions(LivingMotion.WALK);
					msg.setAnimations(this.chasingAnimation);
					EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
					this.angry = true;
				}
			}
		}
		
		@Override
		public void start() {
	        super.start();
	        this.angry = true;
	    }
		
		@Override
		public void stop() {
	        super.stop();
	        this.angry = false;
	    }
	}
}