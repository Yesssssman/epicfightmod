package yesman.epicfight.api.animation.property;

import java.util.function.Predicate;

import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AnimationEvent {
	final AnimationEvent.Side executionSide;
	final AnimationEventConsumer event;
	Object[] params;
	
	private AnimationEvent(AnimationEvent.Side executionSide, AnimationEventConsumer event) {
		this.executionSide = executionSide;
		this.event = event;
	}
	
	public void executeIfRightSide(LivingEntityPatch<?> entitypatch, StaticAnimation animation) {
		if (this.executionSide.predicate.test(entitypatch.isLogicalClient())) {
			this.event.fire(entitypatch, animation, this.params);
		}
	}
	
	public AnimationEvent params(Object... params) {
		this.params = params;
		return this;
	}
	
	public static AnimationEvent create(AnimationEventConsumer event, AnimationEvent.Side isRemote) {
		return new AnimationEvent(isRemote, event);
	}
	
	public static class TimeStampedEvent extends AnimationEvent implements Comparable<TimeStampedEvent> {
		final float time;
		
		private TimeStampedEvent(float time, AnimationEvent.Side executionSide, AnimationEventConsumer event) {
			super(executionSide, event);
			this.time = time;
		}
		
		public void executeIfRightSide(LivingEntityPatch<?> entitypatch, StaticAnimation animation, float prevElapsed, float elapsed) {
			if (this.time >= prevElapsed && this.time < elapsed) {
				super.executeIfRightSide(entitypatch, animation);
			}
		}
		
		public static TimeStampedEvent create(float time, AnimationEventConsumer event, AnimationEvent.Side isRemote) {
			return new TimeStampedEvent(time, isRemote, event);
		}
		
		public TimeStampedEvent withParams(Object... params) {
			TimeStampedEvent event = new TimeStampedEvent(this.time, this.executionSide, this.event);
			event.params = params;
			
			return event;
		}
		
		@Override
		public int compareTo(TimeStampedEvent arg0) {
			if(this.time == arg0.time) {
				return 0;
			} else {
				return this.time > arg0.time ? 1 : -1;
			}
		}
		
		@Override
		public TimeStampedEvent params(Object... params) {
			this.params = params;
			return this;
		}
	}
	
	public static class TimePeriodEvent extends AnimationEvent implements Comparable<TimePeriodEvent> {
		final float start;
		final float end;
		
		private TimePeriodEvent(float start, float end, AnimationEvent.Side executionSide, AnimationEventConsumer event) {
			super(executionSide, event);
			this.start = start;
			this.end = end;
		}
		
		public void executeIfRightSide(LivingEntityPatch<?> entitypatch, StaticAnimation animation, float prevElapsed, float elapsed) {
			if (this.start <= elapsed && this.end > elapsed) {
				super.executeIfRightSide(entitypatch, animation);
			}
		}
		
		public static TimePeriodEvent create(float start, float end, AnimationEventConsumer event, AnimationEvent.Side isRemote) {
			return new TimePeriodEvent(start, end, isRemote, event);
		}
		
		public TimePeriodEvent withParams(Object... params) {
			TimePeriodEvent event = new TimePeriodEvent(this.start, this.end, this.executionSide, this.event);
			event.params = params;
			
			return event;
		}
		
		@Override
		public int compareTo(TimePeriodEvent arg0) {
			if(this.start == arg0.start) {
				return 0;
			} else {
				return this.start > arg0.start ? 1 : -1;
			}
		}
		
		@Override
		public TimePeriodEvent params(Object... params) {
			this.params = params;
			return this;
		}
	}
	
	public static enum Side {
		CLIENT((isLogicalClient) -> isLogicalClient), SERVER((isLogicalClient) -> !isLogicalClient), BOTH((isLogicalClient) -> true);
		
		Predicate<Boolean> predicate;
		
		Side(Predicate<Boolean> predicate) {
			this.predicate = predicate;
		}
	}
	
	@FunctionalInterface
	public static interface AnimationEventConsumer {
		public abstract void fire(LivingEntityPatch<?> entitypatch, StaticAnimation animation, Object... params);
	}
}