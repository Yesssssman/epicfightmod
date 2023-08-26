package yesman.epicfight.api.animation.types;

import java.util.function.Function;

import net.minecraft.server.packs.resources.ResourceManager;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.utils.TypeFlexibleHashMap.TypeKey;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SelectiveAnimation extends StaticAnimation {
	public static final TypeKey<Integer> PREVIOUS_STATE = new TypeKey<>() {
		public Integer defaultValue() {
			return -1;
		}
	};
	
	private final Function<LivingEntityPatch<?>, Integer> selector;
	private final StaticAnimation[] animations;
	
	public SelectiveAnimation(Function<LivingEntityPatch<?>, Integer> selector, StaticAnimation... animations) {
		super(0.15F, false, "", null);
		
		this.selector = selector;
		this.animations = animations;
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		int result = this.selector.apply(entitypatch);
		
		entitypatch.getAnimator().playAnimation(this.animations[result], 0.0F);
		entitypatch.getAnimator().putAnimationVariables(PREVIOUS_STATE, result);
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		super.end(entitypatch, nextAnimation, isEnd);
		entitypatch.getAnimator().removeAnimationVariables(PREVIOUS_STATE);
	}
	
	@Override
	public boolean isMetaAnimation() {
		return true;
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager) {
		for (StaticAnimation anim : this.animations) {
			anim.addEvents(StaticAnimationProperty.EVENTS, AnimationEvent.create((entitypatch, animation, params) -> {
				int result = this.selector.apply(entitypatch);
				
				if (entitypatch.getAnimator().getAnimationVariables(PREVIOUS_STATE) != result) {
					entitypatch.getAnimator().playAnimation(this.animations[result], 0.0F);
					entitypatch.getAnimator().putAnimationVariables(PREVIOUS_STATE, result);
				}
				
			}, AnimationEvent.Side.BOTH));
		}
	}
}