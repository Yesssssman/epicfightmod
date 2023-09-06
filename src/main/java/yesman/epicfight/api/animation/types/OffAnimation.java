package yesman.epicfight.api.animation.types;

import net.minecraft.server.packs.resources.ResourceManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class OffAnimation extends StaticAnimation {
	public OffAnimation(String path) {
		super(0.0F, false, path, null);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		if (entitypatch.isLogicalClient()) {
			AnimationPlayer player = entitypatch.getClientAnimator().getCompositeLayer(this.getPriority()).animationPlayer;
			
			if (!player.isEmpty() && !(player.getAnimation() instanceof OffAnimation)) {
				entitypatch.getClientAnimator().getCompositeLayer(this.getPriority()).off(entitypatch);
			}
		} else {
			entitypatch.getAnimator().playAnimation(Animations.DUMMY_ANIMATION, 0.0F);
		}
	}
	
	@Override
	public boolean isMetaAnimation() {
		return true;
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager) {
		;
	}
}