package maninhouse.epicfight.capabilities.entity.mob;

import io.netty.buffer.ByteBuf;
import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSReqSpawnInfo;
import maninhouse.epicfight.network.server.STCMobInitialSetting;
import net.minecraft.entity.MobEntity;

public class ZombieData<T extends MobEntity> extends BipedMobData<T> {
	public ZombieData() {
		super(Faction.UNDEAD);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		if (!this.isRemote()) {
			if (!this.orgEntity.canPickUpLoot()) {
				this.orgEntity.setCanPickUpLoot(this.isArmed());
			}
		} else {
			ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.0D);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.ZOMBIE_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public STCMobInitialSetting sendInitialInformationToClient() {
		STCMobInitialSetting packet = new STCMobInitialSetting(this.orgEntity.getEntityId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.orgEntity.canPickUpLoot());
        
		return packet;
	}
	
	@Override
	public void clientInitialSettings(ByteBuf buf) {
		AnimatorClient animator = this.getClientAnimator();
		
		if (buf.readBoolean()) {
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
			animator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		}
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false, Animations.ZOMBIE_CHASE, Animations.ZOMBIE_WALK, !orgEntity.isChild()));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.88D, true, MobAttackPatterns.BIPED_UNARMED));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}
}