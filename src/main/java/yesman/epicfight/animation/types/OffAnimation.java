package yesman.epicfight.animation.types;

import net.minecraft.resources.IResourceManager;
import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.capabilities.entity.LivingData;

public class OffAnimation extends StaticAnimation {
	public OffAnimation() {
		super(0.0F, false, null, null);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		if (entitydata.isRemote()) {
			AnimationPlayer player = entitydata.getClientAnimator().getLayer(this.getPriority()).animationPlayer;
			if (!player.isEmpty() && !(player.getPlay() instanceof OffAnimation)) {
				entitydata.getClientAnimator().getLayer(this.getPriority()).off(entitydata);
			}
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {}
	
	@Override
	public boolean isMetaAnimation() {
		return true;
	}
	
	@Override
	public void loadAnimation(IResourceManager resourceManager) {
		;
	}
}