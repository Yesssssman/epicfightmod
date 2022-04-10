package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.entity.ai.goal.RangeAttackMobGoal;

public class WitchPatch extends HumanoidMobPatch<Witch> {
	public WitchPatch() {
		super(Faction.NATURAL);
	}

	@Override
	public void postInit() {
		super.resetCombatAI();
		this.original.goalSelector.addGoal(0, new RangeAttackMobGoal(this.original, this, Animations.BIPED_MOB_THROW, 1.0D, 60, 10.0F, 5));
	}
	
	@Override
	public void setAIAsUnarmed() {

	}
	
	@Override
	public void setAIAsArmed() {

	}

	public void setAIAsMounted() {

	}

	public void setAIAsRanged() {

	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingMotion(LivingMotion.DEATH, Animations.BIPED_DEATH);
		clientAnimator.addLivingMotion(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		clientAnimator.addLivingMotion(LivingMotion.WALK, Animations.ILLAGER_WALK);
		clientAnimator.addCompositeAnimation(LivingMotion.DRINK, Animations.WITCH_DRINKING);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
		
		if (this.original.isDrinkingPotion()) {
			this.currentCompositeMotion = LivingMotion.DRINK;
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.witch;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public OpenMatrix4f getHeadMatrix(float partialTicks) {
		if (this.original.isDrinkingPotion()) {
			return new OpenMatrix4f();
		} else {
			return super.getHeadMatrix(partialTicks);
		}
	}
}