package yesman.epicfight.world.capabilities.item;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ShieldCapability extends CapabilityItem {
	protected StaticAnimation blockingMotion;
	
	protected ShieldCapability(CapabilityItem.Builder builder) {
		super(builder);
	}
	
	/*
	 * Avoid duplicated usage with guard skill
	 */
	@Override
	public UseAction getUseAnimation(LivingEntityPatch<?> entitypatch) {
		return UseAction.NONE;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> playerdata, Hand hand) {
		return ImmutableMap.of(LivingMotions.BLOCK_SHIELD, Animations.BIPED_BLOCK);
	}
}