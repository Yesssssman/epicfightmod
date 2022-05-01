package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.Witch;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public class WitchPatch extends HumanoidMobPatch<Witch> {
	public WitchPatch() {
		super(Faction.NEUTURAL);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, 10.0F, false));
		this.original.goalSelector.addGoal(0, new CombatBehaviorGoal<>(this, MobCombatBehaviors.WITCH.build(this)));
	}
	
	public void setAIAsMounted() {
		
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.ILLAGER_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.DRINK, Animations.WITCH_DRINKING);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
		
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