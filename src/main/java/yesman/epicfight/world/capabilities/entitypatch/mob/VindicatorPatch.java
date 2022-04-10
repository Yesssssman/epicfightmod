package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.AbstractIllager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AttackBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class VindicatorPatch<T extends AbstractIllager> extends AbstractIllagerPatch<T> {
	public VindicatorPatch() {
		super(Faction.ILLAGER);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		super.initAnimator(clientAnimator);
		clientAnimator.addLivingMotion(LivingMotion.ANGRY, Animations.VINDICATOR_IDLE_AGGRESSIVE);
		clientAnimator.addLivingMotion(LivingMotion.CHASE, Animations.VINDICATOR_CHASE);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.inaction() && considerInaction) {
			currentMotion = LivingMotion.INACTION;
		} else {
			boolean isAngry = this.original.isAggressive();
			
			if(this.original.getHealth() <= 0.0F) {
				currentMotion = LivingMotion.DEATH;
			} else if (original.animationSpeed > 0.01F) {
				currentMotion = isAngry ? LivingMotion.CHASE : LivingMotion.WALK;
			} else {
				currentMotion = isAngry ? LivingMotion.ANGRY : LivingMotion.IDLE;
			}
		}
	}
	
	@Override
	public void setAIAsArmed() {
		this.original.goalSelector.addGoal(0, new AttackBehaviorGoal<>(this, MobCombatBehaviors.VINDICATOR_BEHAVIORS.build(this)));
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false));
	}

	@Override
	public void setAIAsUnarmed() {

	}

	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}