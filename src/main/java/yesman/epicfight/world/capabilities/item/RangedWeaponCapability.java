package yesman.epicfight.world.capabilities.item;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RangedWeaponCapability extends CapabilityItem {
	protected Map<LivingMotion, StaticAnimation> rangeAnimationModifiers;
	
	protected RangedWeaponCapability(CapabilityItem.Builder builder) {
		super(builder);
		
		RangedWeaponCapability.Builder rangedBuilder = (RangedWeaponCapability.Builder)builder;
		this.rangeAnimationModifiers = rangedBuilder.rangeAnimationModifiers;
	}
	
	@Override
	public void setConfigFileAttribute(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributes(Styles.RANGED, armorNegation1, impact1, maxStrikes1);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> playerdata, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND) {
			return this.rangeAnimationModifiers;
		}
		
		return super.getLivingMotionModifier(playerdata, hand);
	}

	@Override
	public boolean availableOnHorse() {
		return true;
	}
	
	@Override
	public boolean canBePlacedOffhand() {
		return false;
	}
	
	public static RangedWeaponCapability.Builder builder() {
		return new RangedWeaponCapability.Builder();
	}
	
	public static class Builder extends CapabilityItem.Builder {
		Map<LivingMotion, StaticAnimation> rangeAnimationModifiers;
		
		protected Builder() {
			this.category = WeaponCategories.RANGED;
			this.constructor = RangedWeaponCapability::new;
			this.rangeAnimationModifiers = Maps.newHashMap();
		}
		
		public Builder addAnimationsModifier(LivingMotion livingMotion, StaticAnimation animations) {
			this.rangeAnimationModifiers.put(livingMotion, animations);
			return this;
		}
	}
}