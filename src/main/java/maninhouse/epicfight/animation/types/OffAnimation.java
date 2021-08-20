package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.capabilities.entity.LivingData;

public class OffAnimation extends StaticAnimation {
	public OffAnimation(int id) {
		super(id, 0.0F, false, null);
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
}