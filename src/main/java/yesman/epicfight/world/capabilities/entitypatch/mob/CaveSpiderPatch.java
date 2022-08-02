package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class CaveSpiderPatch<T extends PathfinderMob> extends SpiderPatch<T> {
	@Override
	protected void initAI() {
		super.initAI();
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.SPIDER.build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), 1.0D, false));
	}
	
	@Override
	public void onHurtSomeone(Entity target, InteractionHand handIn, ExtendedDamageSource source, float amount, boolean succeed) {
		if (succeed && target instanceof LivingEntity) {
			int i = 0;
			
            if (this.original.level.getDifficulty() == Difficulty.NORMAL) {
                i = 7;
            } else if (this.original.level.getDifficulty() == Difficulty.HARD) {
                i = 15;
            }
            
            if (i > 0) {
                ((LivingEntity)target).addEffect(new MobEffectInstance(MobEffects.POISON, i * 20, 0));
            }
		}
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(0.7F, 0.7F, 0.7F);
	}
}