package maninhouse.epicfight.animation.types;

import java.util.Optional;

import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.property.Property;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.BindingOperation;
import maninhouse.epicfight.client.animation.Layer;

public class LinkAnimation extends DynamicAnimation {
	protected DynamicAnimation nextAnimation;
	protected Layer.Priority priority;
	protected float startsAt;
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		this.nextAnimation.updateOnLinkAnimation(entitydata, this);
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		if (!isEnd) {
			this.nextAnimation.onFinish(entitydata, isEnd);
		} else {
			if (this.startsAt > 0.0F) {
				entitydata.getAnimator().getPlayerFor(this).setElapsedTime(this.startsAt);
				entitydata.getAnimator().getPlayerFor(this).markNoReset();
				this.startsAt = 0.0F;
			}
		}
	}
	
	@Override
	public EntityState getState(float time) {
		return this.nextAnimation.getState(0.0F);
	}
	
	@Override
	public Pose getPoseByTime(DynamicAnimation animation, LivingData<?> entitydata, float time) {
		return this.nextAnimation.getPoseByTime(animation, entitydata, time);
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata) {
		return this.nextAnimation.getPlaySpeed(entitydata);
	}
	
	@Override
	public <V> Optional<V> getProperty(Property<V> propertyType) {
		return this.nextAnimation.getProperty(propertyType);
	}
	
	public void setNextAnimation(DynamicAnimation animation) {
		this.nextAnimation = animation;
	}

	public DynamicAnimation getNextAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public boolean isEnabledJoint(String joint) {
		return this.nextAnimation.isEnabledJoint(joint);
	}
	
	@Override
	public BindingOperation getBindingOperation(String joint) {
		return this.nextAnimation.getBindingOperation(joint);
	}
	
	@Override
	public boolean isMainFrameAnimation() {
		return this.nextAnimation.isMainFrameAnimation();
	}
	
	@Override
	public boolean isReboundAnimation() {
		return this.nextAnimation.isReboundAnimation();
	}
	
	@Override
	public DynamicAnimation getRealAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public String toString() {
		return "LinkAnimation " + this.nextAnimation;
	}
}