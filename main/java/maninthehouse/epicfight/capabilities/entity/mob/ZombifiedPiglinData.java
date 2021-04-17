package maninthehouse.epicfight.capabilities.entity.mob;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.entity.DataKeys;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCLivingMotionChange;
import maninthehouse.epicfight.network.server.STCMobInitialSetting;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.util.DamageSource;

public class ZombifiedPiglinData extends BipedMobData<EntityPigZombie> {
	public ZombifiedPiglinData() {
		super(Faction.NATURAL);
	}
	
	@Override
	public void onEntityJoinWorld(EntityPigZombie entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ZOMBIE_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public STCMobInitialSetting sendInitialInformationToClient() {
		STCMobInitialSetting packet = new STCMobInitialSetting(this.orgEntity.getEntityId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.orgEntity.canPickUpLoot());
		return packet;
	}
	
	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.registerIfAbsent(ModAttributes.MAX_STUN_ARMOR);
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void setAIAsArmed() {
        orgEntity.tasks.addTask(1, new EntityAIPigmanChase(this, this.orgEntity));
        orgEntity.tasks.addTask(0, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 1.5D, true, MobAttackPatterns.BIPED_ARMED_ONEHAND));
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount) {
		if (damageSource.getTrueSource() instanceof EntityPigZombie) {
			return false;
		}
		return true;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_BIPED;
	}
	
	static class EntityAIPigmanChase extends EntityAIChase {
		boolean angry;

		public EntityAIPigmanChase(BipedMobData<?> entitydata, EntityCreature creature) {
			super(entitydata, creature, 1.35D, false, Animations.BIPED_RUN, Animations.BIPED_WALK);
		}
		
		@Override
		public void updateTask() {
			super.updateTask();

			if (!((EntityPigZombie) attacker).isAngry()) {
				if (this.angry) {
					STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getEntityId(), 1);
					msg.setMotions(LivingMotion.WALKING);
					msg.setAnimations(walkingAnimation);
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
					this.angry = false;
				}
			} else {
				if (!this.angry) {
					STCLivingMotionChange msg = new STCLivingMotionChange(attacker.getEntityId(), 1);
					msg.setMotions(LivingMotion.WALKING);
					msg.setAnimations(chasingAnimation);
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(msg, attacker);
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