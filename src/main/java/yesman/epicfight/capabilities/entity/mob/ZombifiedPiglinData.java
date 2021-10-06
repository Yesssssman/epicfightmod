package yesman.epicfight.capabilities.entity.mob;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCLivingMotionChange;
import yesman.epicfight.network.server.STCMobInitialSetting;

public class ZombifiedPiglinData extends BipedMobData<ZombifiedPiglinEntity> {
	public ZombifiedPiglinData() {
		super(Faction.NATURAL);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_IDLE_ZOMBIE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.PIGLIN_WALK_ZOMBIE);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public STCMobInitialSetting sendInitialInformationToClient() {
		STCMobInitialSetting packet = new STCMobInitialSetting(this.orgEntity.getEntityId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.orgEntity.canPickUpLoot());
		return packet;
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonCreatureUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsArmed() {
		this.orgEntity.goalSelector.addGoal(1, new EntityAIPigmanChase(this, this.orgEntity));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.5D, true, AttackCombos.BIPED_ARMED));
	}
	
	@Override
	public boolean hurtBy(LivingAttackEvent event) {
		if (event.getSource().getTrueSource() instanceof ZombifiedPiglinEntity) {
			return false;
		}
		return true;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.piglin;
	}
	
	static class EntityAIPigmanChase extends ChasingGoal {
		boolean angry;
		
		public EntityAIPigmanChase(BipedMobData<?> entitydata, MobEntity creature) {
			super(entitydata, creature, 1.35D, false, Animations.PIGLIN_CHASE_ZOMBIE, Animations.PIGLIN_WALK_ZOMBIE);
		}
		
		@Override
		public void tick() {
			super.tick();

			if (!((ZombifiedPiglinEntity)this.attacker).isAggressive()) {
				if (this.angry) {
					STCLivingMotionChange msg = new STCLivingMotionChange(this.attacker.getEntityId(), 1);
					msg.setMotions(LivingMotion.WALK);
					msg.setAnimations(walkingAnimation);
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
					this.angry = false;
				}
			} else {
				if (!this.angry) {
					STCLivingMotionChange msg = new STCLivingMotionChange(this.attacker.getEntityId(), 1);
					msg.setMotions(LivingMotion.WALK);
					msg.setAnimations(this.chasingAnimation);
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.attacker);
					this.angry = true;
				}
			}
		}
		
		@Override
		public void startExecuting() {
	        super.startExecuting();
	        this.angry = true;
	    }
		
		@Override
		public void resetTask() {
	        super.resetTask();
	        this.angry = false;
	    }
	}
}