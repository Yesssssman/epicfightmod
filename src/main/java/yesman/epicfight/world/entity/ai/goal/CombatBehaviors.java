package yesman.epicfight.world.entity.ai.goal;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch.AnimationPacketProvider;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class CombatBehaviors {
	private final List<BehaviorSeries> behaviorSeriesList = Lists.newArrayList();
	private final MobPatch<?> mobpatch;
	private int currentBehaviorPointer;
	
	private CombatBehaviors(CombatBehaviors.Builder builder, MobPatch<?> mobpatch) {
		builder.behaviorSeriesList.stream().map((patternBuilder) -> patternBuilder.build()).forEach(this.behaviorSeriesList::add);
		this.mobpatch = mobpatch;
		this.currentBehaviorPointer = -1;
	}
	
	private int getRandomCombatBehaviorSeries() {
		List<Integer> candidates = Lists.newArrayList();
		List<Float> remappedWeight = Lists.newArrayList();
		float weightSum = 0.0F;
		
		for (int i = 0; i < this.behaviorSeriesList.size(); i++) {
			if (this.currentBehaviorPointer != i) {
				BehaviorSeries move = this.behaviorSeriesList.get(i);
				boolean result = move.test(this.mobpatch, this);
				
				if (result) {
					weightSum += move.weight;
					candidates.add(i);
				}
			}
		}
		
		for (int i : candidates) {
			remappedWeight.add(this.behaviorSeriesList.get(i).weight / weightSum);
		}
		
		float random = this.mobpatch.getOriginal().getRandom().nextFloat();
		float delta = 0.0F;
		
		for (int i = 0; i < candidates.size(); i++) {
			int index = candidates.get(i);
			delta += remappedWeight.get(i);
			
			if (random < delta) {
				this.behaviorSeriesList.get(index).resetCooldown(this);
				return index;
			}
		}
		
		return -1;
	}
	
	public Behavior selectRandomBehaviorSeries() {
		int seriesPointer = this.getRandomCombatBehaviorSeries();
		
		if (seriesPointer >= 0) {
			this.currentBehaviorPointer = seriesPointer;
			BehaviorSeries behaviorSeries = this.behaviorSeriesList.get(seriesPointer);
			Behavior behavior = behaviorSeries.behaviors.get(behaviorSeries.nextBehaviorPointer);
			behaviorSeries.upCounter();
			
			return behavior;
		}
		
		return null;
	}
	
	public Behavior tryProceed() {
		BehaviorSeries currentBehaviorSeries = this.behaviorSeriesList.get(this.currentBehaviorPointer);
		
		if (currentBehaviorSeries.canBeInterrupted) {
			int seriesPointer = this.getRandomCombatBehaviorSeries();
			
			if (seriesPointer >= 0 && this.currentBehaviorPointer != seriesPointer) {
				this.currentBehaviorPointer = seriesPointer;
				BehaviorSeries newCombatBehaviorSeries = this.behaviorSeriesList.get(seriesPointer);
				
				return newCombatBehaviorSeries.behaviors.get(newCombatBehaviorSeries.nextBehaviorPointer);
			}
		}
		
		if (currentBehaviorSeries.loopFinished) {
			if (currentBehaviorSeries.volatilePointer) {
				currentBehaviorSeries.loopFinished = false;
				return null;
			}
		}
		
		Behavior nextBehavior = currentBehaviorSeries.behaviors.get(currentBehaviorSeries.nextBehaviorPointer);
		
		if (nextBehavior.checkPredicates(this.mobpatch, this)) {
			currentBehaviorSeries.upCounter();
			return nextBehavior;
		} else {
			return null;
		}
	}
	
	public void cancel() {
		if (this.currentBehaviorPointer >= 0) {
			this.behaviorSeriesList.get(this.currentBehaviorPointer).onCancel();
			this.currentBehaviorPointer = -1;
		}
	}
	
	public int getCurrentBehaviorPointer() {
		return this.currentBehaviorPointer;
	}
	
	public boolean hasActivatedMove() {
		return this.currentBehaviorPointer >= 0;
	}
	
	public void tick() {
		for (BehaviorSeries behaviorSeries : this.behaviorSeriesList) {
			behaviorSeries.tick();
		}
	}
	
	public static CombatBehaviors.Builder builder() {
		return new CombatBehaviors.Builder();
	}
	
	public static class Builder {
		private List<BehaviorSeries.Builder> behaviorSeriesList = Lists.newArrayList();
		
		public Builder newBehaviorSeries(BehaviorSeries.Builder builder) {
			this.behaviorSeriesList.add(builder);
			return this;
		}
		
		public CombatBehaviors build(MobPatch<?> mobpatch) {
			return new CombatBehaviors(this, mobpatch);
		}
	}
	
	public static class BehaviorSeries {
		private final List<Behavior> behaviors = Lists.newArrayList();
		private final boolean volatilePointer;
		private final boolean canBeInterrupted;
		private final float weight;
		private final int maxCooldown;
		private List<Integer> cooldownSharingPointer;
		private int cooldown;
		private int nextBehaviorPointer;
		private boolean loopFinished;
		
		private BehaviorSeries(BehaviorSeries.Builder builder) {
			builder.behaviors.stream().map((motionBuilder) -> motionBuilder.build()).forEach(this.behaviors::add);
			this.volatilePointer = builder.volatilePointer;
			this.canBeInterrupted = builder.canBeInterrupted;
			this.weight = builder.weight;
			this.cooldownSharingPointer = builder.cooldownSharingPointers;
			this.maxCooldown = builder.cooldown;
		}
		
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			if (this.cooldown > 0) {
				return false;
			}
			
			return this.behaviors.get(this.nextBehaviorPointer).checkPredicates(mobpatch, mobBehavior);
		}
		
		public void upCounter() {
			this.nextBehaviorPointer++;
			this.loopFinished = false;
			int behaviorsNum = this.behaviors.size();
			
			if (this.nextBehaviorPointer >= behaviorsNum) {
				this.nextBehaviorPointer %= behaviorsNum;
				this.loopFinished = true;
			}
		}
		
		public void tick() {
			this.cooldown--;
		}
		
		public void resetCooldown(CombatBehaviors mobBehavior) {
			this.cooldown = this.maxCooldown;
			
			for (int i : this.cooldownSharingPointer) {
				BehaviorSeries behaviorSeries = mobBehavior.behaviorSeriesList.get(i);
				behaviorSeries.cooldown = behaviorSeries.maxCooldown;
			}
		}
		
		public void onCancel() {
			if (this.volatilePointer) {
				this.nextBehaviorPointer = 0;
			}
		}
		
		public static BehaviorSeries.Builder builder() {
			return new BehaviorSeries.Builder();
		}
		
		public static class Builder {
			private List<Behavior.Builder> behaviors = Lists.newArrayList();
			private boolean volatilePointer = false;
			private boolean canBeInterrupted = true;
			private float weight;
			private int cooldown;
			private List<Integer> cooldownSharingPointers = Lists.newArrayList();
			
			public Builder weight(float weight) {
				this.weight = weight;
				return this;
			}
			
			public Builder cooldown(int cooldown) {
				this.cooldown = cooldown;
				return this;
			}
			
			public Builder simultaneousCooldown(int... cooldownSharingPointers) {
				for (int pointer : cooldownSharingPointers) {
					this.cooldownSharingPointers.add(pointer);
				}
				
				return this;
			}
			
			public BehaviorSeries.Builder nextBehavior(Behavior.Builder motion) {
				this.behaviors.add(motion);
				return this;
			}
			
			public BehaviorSeries.Builder volatileBehaviorPointer(boolean volatilePointer) {
				this.volatilePointer = volatilePointer;
				return this;
			}
			
			public BehaviorSeries.Builder canBeInterrupted(boolean canBeInterrupted) {
				this.canBeInterrupted = canBeInterrupted;
				return this;
			}
			
			public BehaviorSeries build() {
				return new BehaviorSeries(this);
			}
		}
	}
	
	public static class Behavior {
		private final StaticAnimation animation;
		private final List<NextBehaviorPredicate> predicates;
		private final AnimationPacketProvider packetProvider;
		
		private Behavior(Behavior.Builder builder) {
			this.animation = builder.animation;
			this.predicates = builder.predicate;
			this.packetProvider = builder.packetProvider;
		}
		
		private boolean checkPredicates(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			for (NextBehaviorPredicate predicate : this.predicates) {
				if (!predicate.test(mobpatch, mobBehavior)) {
					return false;
				}
			}
			
			return true;
		}
		
		public void execute(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			mobpatch.playAnimationSynchronized(this.animation, 0.0F, this.packetProvider);
        	mobpatch.updateEntityState();
        	this.predicates.forEach((predicate) -> predicate.onExecute(mobBehavior));
		}
		
		public static Behavior.Builder builder() {
			return new Behavior.Builder();
		}
		
		public static class Builder {
			private StaticAnimation animation;
			private List<NextBehaviorPredicate> predicate = Lists.newArrayList();
			private AnimationPacketProvider packetProvider = SPPlayAnimation::new;
			
			public Behavior.Builder animation(StaticAnimation motion) {
				this.animation = motion;
				return this;
			}
			
			public Behavior.Builder withinEyeHeight() {
				this.predicate(new TargetWithinEyeHeight());
				return this;
			}
			
			public Behavior.Builder randomChance(float chance) {
				this.predicate(new RandomChance(chance));
				return this;
			}
			
			public Behavior.Builder withinDistance(double minDistance, double maxDistance) {
				this.predicate(new TargetWithinDistance(minDistance * minDistance, maxDistance * maxDistance));
				return this;
			}
			
			public Behavior.Builder withinAngle(double minDegree, double maxDegree) {
				this.predicate(new TargetWithinAngle(minDegree, maxDegree));
				return this;
			}
			
			public Behavior.Builder withinAngleHorizontal(double minDegree, double maxDegree) {
				this.predicate(new TargetWithinAngle.Horizontal(minDegree, maxDegree));
				return this;
			}
			
			public Behavior.Builder health(int health, Health.Comparator comparator) {
				this.predicate(new Health(health, comparator));
				return this;
			}
			
			public Behavior.Builder instance(BiFunction<MobPatch<?>, CombatBehaviors, Boolean> test, Consumer<CombatBehaviors> execute) {
				this.predicate(new InstancePredicate(test, execute));
				return this;
			}
			
			public Behavior.Builder predicate(NextBehaviorPredicate predicate) {
				this.predicate.add(predicate);
				return this;
			}
			
			public Behavior.Builder packetProvider(AnimationPacketProvider packetProvider) {
				this.packetProvider = packetProvider;
				return this;
			}
			
			public Behavior build() {
				return new Behavior(this);
			}
		}
	}
	
	public static abstract class NextBehaviorPredicate {
		public abstract boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior);
		public void onExecute(CombatBehaviors mobBehavior) {}
	}
	
	public static class InstancePredicate extends NextBehaviorPredicate {
		BiFunction<MobPatch<?>, CombatBehaviors, Boolean> test;
		Consumer<CombatBehaviors> execute;
		
		public InstancePredicate(BiFunction<MobPatch<?>, CombatBehaviors, Boolean> test, Consumer<CombatBehaviors> execute) {
			this.test = test;
			this.execute = execute;
		}
		
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			return this.test.apply(mobpatch, mobBehavior);
		}
		
		public void onExecute(CombatBehaviors mobBehavior) {
			this.execute.accept(mobBehavior);
		}
	}
	
	public static class RandomChance extends NextBehaviorPredicate {
		private final float chance;
		
		private RandomChance(float chance) {
			this.chance = chance;
		}
		
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			return mobpatch.getOriginal().getRandom().nextFloat() < this.chance;
		}
	}
	
	public static class TargetWithinEyeHeight extends NextBehaviorPredicate {
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			double veticalDistance = Math.abs(mobpatch.getOriginal().getY() - mobpatch.getAttackTarget().getY());
			return veticalDistance < mobpatch.getOriginal().getEyeHeight();
		}
	}
	
	public static class TargetWithinDistance extends NextBehaviorPredicate {
		private final double minDistance;
		private final double maxDistance;
		
		private TargetWithinDistance(double minDistance, double maxDistance) {
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
		}
		
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			double distanceSqr = mobpatch.getOriginal().distanceToSqr(mobpatch.getAttackTarget());
			return this.minDistance < distanceSqr && distanceSqr < this.maxDistance;
		}
	}
	
	public static class TargetWithinAngle extends NextBehaviorPredicate {
		protected final double minDegree;
		protected final double maxDegree;
		
		private TargetWithinAngle(double minDegree, double maxDegree) {
			this.minDegree = minDegree;
			this.maxDegree = maxDegree;
		}
		
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			Mob original = mobpatch.getOriginal();
			Entity target = original.getTarget();
			double degree = mobpatch.getAngleTo(target);
			
			return this.minDegree < degree && degree < this.maxDegree;
		}
		
		public static class Horizontal extends TargetWithinAngle {
			private Horizontal(double minDegree, double maxDegree) {
				super(minDegree, maxDegree);
			}
			
			@Override
			public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
				Mob original = mobpatch.getOriginal();
				Entity target = original.getTarget();
				double degree = mobpatch.getAngleToHorizontal(target);
				return this.minDegree < degree && degree < this.maxDegree;
			}
		}
	}
	
	public static class Health extends NextBehaviorPredicate {
		private final float value;
		private final Comparator comparator;
		
		private Health(int value, Comparator comparator) {
			this.value = value;
			this.comparator = comparator;
		}
		
		public boolean test(MobPatch<?> mobpatch, CombatBehaviors mobBehavior) {
			switch (this.comparator) {
			case LESS:
				return this.value > mobpatch.getOriginal().getHealth();
			case GREATER:
				return this.value < mobpatch.getOriginal().getHealth();
			}
			
			return true;
		}
		
		public static enum Comparator {
			GREATER, LESS
		}
	}
}