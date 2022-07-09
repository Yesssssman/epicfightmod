package yesman.epicfight.world.capabilities.item;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ShieldCapability extends CapabilityItem {
	protected StaticAnimation blockingMotion;
	
	public ShieldCapability(Item item) {
		super(item, WeaponCategories.SHIELD);
		this.blockingMotion = Animations.BIPED_BLOCK;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> playerdata, InteractionHand hand) {
		return ImmutableMap.of(LivingMotions.BLOCK_SHIELD, this.blockingMotion);
	}
}