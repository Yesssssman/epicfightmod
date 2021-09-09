package maninhouse.epicfight.capabilities.entity.mob;

import io.netty.buffer.ByteBuf;
import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCLivingMotionChange;
import maninhouse.epicfight.network.server.STCMobInitialSetting;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

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
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void setAIAsArmed() {
		this.orgEntity.goalSelector.addGoal(1, new EntityAIPigmanChase(this, this.orgEntity));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.5D, true, MobAttackPatterns.BIPED_ARMED));
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
		return modelDB.ENTITY_PIGLIN;
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