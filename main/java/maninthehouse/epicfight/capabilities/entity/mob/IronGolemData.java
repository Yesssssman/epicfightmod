package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.entity.ai.EntityAIPatternWithChance;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;

public class IronGolemData extends BipedMobData<EntityIronGolem> {
	private int deathTimerExt;

	public IronGolemData() {
		super(Faction.VILLAGER);
	}
	
	@Override
	public void onEntityJoinWorld(EntityIronGolem entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.entityCollisionReduction = 0.2F;
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getEntityAttribute(ModAttributes.MAX_STRIKES).setBaseValue(4.0D);
		this.orgEntity.getEntityAttribute(ModAttributes.IMPACT).setBaseValue(10.0D);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		animatorClient.mixLayer.setJointMask("Root", "Torso");
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.GOLEM_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.GOLEM_WALK);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.GOLEM_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}

	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void update() {
		if (orgEntity.getHealth() <= 0.0F) {
			orgEntity.rotationPitch = 0;

			if (orgEntity.deathTime > 1 && this.deathTimerExt < 20) {
				deathTimerExt++;
				orgEntity.deathTime--;
			}
		}

		super.update();
	}

	@Override
	public void setAIAsUnarmed() {
		orgEntity.tasks.addTask(0, new EntityAIPatternWithChance(this, this.orgEntity, 0.0D, 1.5D, 0.3F, true, MobAttackPatterns.GOLEM_PATTERN1));
		orgEntity.tasks.addTask(0, new EntityAIPatternWithChance(this, this.orgEntity, 1.0D, 2.5D, 0.15F, true, MobAttackPatterns.GOLEM_PATTERN2));
		orgEntity.tasks.addTask(0, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 2.0D, true, MobAttackPatterns.GOLEM_PATTERN3));
		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.0D, false));
	}
	
	@Override
	public void setAIAsArmed() {
		this.setAIAsUnarmed();
	}

	@Override
	public SoundEvent getWeaponHitSound(EnumHand hand) {
		return Sounds.BLUNT_HIT_HARD;
	}

	@Override
	public SoundEvent getSwingSound(EnumHand hand) {
		return Sounds.WHOOSH_BIG;
	}

	@Override
	public float getDamageToEntity(Entity targetEntity, EnumHand hand) {
		return (float) (7 + this.orgEntity.getRNG().nextInt(15));
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return null;
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_GOLEM;
	}
}