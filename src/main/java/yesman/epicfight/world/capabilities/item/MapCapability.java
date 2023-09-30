package yesman.epicfight.world.capabilities.item;

import java.util.Map;

import net.minecraft.world.InteractionHand;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MapCapability extends CapabilityItem {
	public MapCapability(CapabilityItem.Builder builder) {
		super(builder);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> playerpatch, InteractionHand hand) {
		Map<LivingMotion, StaticAnimation> livingModifier = super.getLivingMotionModifier(playerpatch, hand);
		
		livingModifier.put(LivingMotions.IDLE, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.KNEEL, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.WALK, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.CHASE, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.RUN, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.SNEAK, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.SWIM, Animations.BIPED_HOLD_MAP);
		livingModifier.put(LivingMotions.FLOAT, Animations.BIPED_HOLD_MAP);
		
		return livingModifier;
	}
	
	public static CapabilityItem.Builder builder() {
		return new CapabilityItem.Builder().constructor(MapCapability::new);
	}
}