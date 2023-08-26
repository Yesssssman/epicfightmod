package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class WitchPatch extends HumanoidMobPatch<Witch> {
	public WitchPatch() {
		super(Faction.NEUTRAL);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		this.original.goalSelector.addGoal(0, new AnimatedAttackGoal<>(this, MobCombatBehaviors.WITCH.build(this)));
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), 1.0F, true, 10.0D));
	}
	
	@Override
	public void updateHeldItem(CapabilityItem fromCap, CapabilityItem toCap, ItemStack from, ItemStack to, InteractionHand hand) {
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.ILLAGER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.ILLAGER_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DRINK, Animations.WITCH_DRINKING);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
		
		if (this.original.isDrinkingPotion()) {
			this.currentCompositeMotion = LivingMotions.DRINK;
		}
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