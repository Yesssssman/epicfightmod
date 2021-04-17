package maninthehouse.epicfight.capabilities.entity.mob;

import java.util.Iterator;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.entity.ai.EntityAIPatternWithChance;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILeapAtTarget;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;

public class SpiderData<T extends EntitySpider> extends MobData<T> {
	public SpiderData() {
		super(Faction.NATURAL);
	}
	
	@Override
	protected void initAI() {
		super.initAI();

		Iterator<EntityAITasks.EntityAITaskEntry> iterator = orgEntity.tasks.taskEntries.iterator();

		while (iterator.hasNext()) {
			EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
			EntityAIBase entityAI = entityaitasks$entityaitaskentry.action;

			if (entityAI instanceof EntityAILeapAtTarget) {
				iterator.remove();
			}
		}

		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.0D, false));
		orgEntity.tasks.addTask(1, new EntityAIPatternWithChance(this, this.orgEntity, 0.0D, 2.0D, 0.5F, true,
				MobAttackPatterns.SPIDER_PATTERN));
		orgEntity.tasks.addTask(0, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 2.5D, true,
				MobAttackPatterns.SPIDER_JUMP_PATTERN));
	}
	
	@Override
	public void postInit() {
		super.postInit();
	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.SPIDER_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.SPIDER_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.SPIDER_CRAWL);
		animatorClient.setCurrentLivingMotionsToDefault();
	}

	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.SPIDER_HIT;
	}

	@Override
	public SoundEvent getSwingSound(EnumHand hand) {
		return SoundEvents.ENTITY_SPIDER_HURT;
	}

	@Override
	public SoundEvent getWeaponHitSound(EnumHand hand) {
		return super.getWeaponHitSound(hand);
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_SPIDER;
	}
}