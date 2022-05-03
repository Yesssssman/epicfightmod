package yesman.epicfight.world.capabilities.entitypatch.mob;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class VindicatorPatch<T extends PathfinderMob> extends AbstractIllagerPatch<T> {
	public VindicatorPatch() {
		super(Faction.ILLAGER);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		super.initAnimator(clientAnimator);
		clientAnimator.addLivingAnimation(LivingMotion.ANGRY, Animations.VINDICATOR_IDLE_AGGRESSIVE);
		clientAnimator.addLivingAnimation(LivingMotion.CHASE, Animations.VINDICATOR_CHASE);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@Override
	protected void setWeaponMotions() {
		super.setWeaponMotions();
		this.weaponAttackMotions.put(WeaponCategory.AXE, ImmutableMap.of(CapabilityItem.Style.COMMON, MobCombatBehaviors.VINDICATOR_ONEHAND));
		this.weaponAttackMotions.put(WeaponCategory.SWORD, ImmutableMap.of(CapabilityItem.Style.COMMON, MobCombatBehaviors.VINDICATOR_ONEHAND));
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.inaction() && considerInaction) {
			currentLivingMotion = LivingMotion.INACTION;
		} else {
			boolean isAngry = this.original.isAggressive();
			
			if (this.original.getHealth() <= 0.0F) {
				currentLivingMotion = LivingMotion.DEATH;
			} else if (this.original.animationSpeed > 0.01F) {
				currentLivingMotion = isAngry ? LivingMotion.CHASE : LivingMotion.WALK;
			} else {
				currentLivingMotion = isAngry ? LivingMotion.ANGRY : LivingMotion.IDLE;
			}
		}
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {

	}
}