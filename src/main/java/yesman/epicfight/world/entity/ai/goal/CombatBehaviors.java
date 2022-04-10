package yesman.epicfight.world.entity.ai.goal;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch.AnimationPacketProvider;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class CombatBehaviors<T extends MobPatch<?>> {
	private final List<BehaviorSeries<T>> behaviorSeriesList = Lists.newArrayList();
	private final T mobpatch;
	private int currentBehaviorPointer;
	
	private CombatBehaviors(CombatBehaviors.Builder<T> builder, T mobpatch) {
		builder.behaviorSeriesList.stream().map((behaviorSeriesBuilder) -> behaviorSeriesBuilder.build()).forEach(this.behaviorSeriesList::add);
		this.mobpatch = mobpatch;
		this.currentBehaviorPointer = -1;
	}
	
	private int getRandomCombatBehaviorSeries() {
		List<Integer> candidates = Lists.newArrayList();
		List<Float> rescaledWeight = Lists.newArrayList();
		float weightSum = 0.0F;
		
		for (int i = 0; i < this.behaviorSeriesList.size(); i++) {
			if (this.currentBehaviorPointer != i) {
				BehaviorSeries<T> move = this.behaviorSeriesList.get(i);
				boolean result = move.test(this.mobpatch);
				
				if (result) {
					weightSum += move.weight;
					candidates.add(i);
				}
			}
		}
		
		for (int i : candidates) {
			rescaledWeight.add(this.behaviorSeriesList.get(i).weight / weightSum);
		}
		
		float random = this.mobpatch.getOriginal().getRandom().nextFloat();
		float delta = 0.0F;
		
		for (int i = 0; i < candidates.size(); i++) {
			int index = candidates.get(i);
			delta += rescaledWeight.get(i);
			
			if (random < delta) {
				this.behaviorSeriesList.get(index).resetCooldown(this, true);
				return index;
			}
		}
		
		return -1;
	}
	
	public void execute(int seriesPointer) {
		this.currentBehaviorPointer = seriesPointer;
		BehaviorSeries<T> behaviorSeries = this.behaviorSeriesList.get(seriesPointer);
		Behavior<T> behavior = behaviorSeries.behaviors.get(behaviorSeries.nextBehaviorPointer);
		behaviorSeries.upCounter();
		behavior.execute(this.mobpatch);
	}
	
	public void resetCooldown(int seriesPointer, boolean resetSharingCooldown) {
		this.behaviorSeriesList.get(seriesPointer).resetCooldown(this, resetSharingCooldown);
	}
	
	public Behavior<T> selectRandomBehaviorSeries() {
		int seriesPointer = this.getRandomCombatBehaviorSeries();
		
		if (seriesPointer >= 0) {
			this.currentBehaviorPointer = seriesPointer;
			BehaviorSeries<T> behaviorSeries = this.behaviorSeriesList.get(seriesPointer);
			Behavior<T> behavior = behaviorSeries.behaviors.get(behaviorSeries.nextBehaviorPointer);
			behaviorSeries.upCounter();
			
			if (behaviorSeries.loopFinished && behaviorSeries.volatilePointer) {
				behaviorSeries.loopFinished = false;
				this.currentBehaviorPointer = -1;
			}
			
			return behavior;
		}
		
		return null;
	}
	
	public Behavior<T> tryProceed() {
		BehaviorSeries<T> currentBehaviorSeries = this.behaviorSeriesList.get(this.currentBehaviorPointer);
		
		if (currentBehaviorSeries.canBeInterrupted) {
			int seriesPointer = this.getRandomCombatBehaviorSeries();
			
			if (seriesPointer >= 0 && this.currentBehaviorPointer != seriesPointer) {
				this.currentBehaviorPointer = seriesPointer;
				BehaviorSeries<T> newCombatBehaviorSeries = this.behaviorSeriesList.get(seriesPointer);
				return newCombatBehaviorSeries.behaviors.get(newCombatBehaviorSeries.nextBehaviorPointer);
			}
		}
		
		if (currentBehaviorSeries.loopFinished && currentBehaviorSeries.volatilePointer) {
			currentBehaviorSeries.loopFinished = false;
			this.currentBehaviorPointer = -1;
			return null;
		}
		
		Behavior<T> nextBehavior = currentBehaviorSeries.behaviors.get(currentBehaviorSeries.nextBehaviorPointer);
		
		if (nextBehavior.checkPredicates(this.mobpatch)) {
			currentBehaviorSeries.upCounter();
			return nextBehavior;
		} else {
			this.currentBehaviorPointer = -1;
			
			if (currentBehaviorSeries.volatilePointer) {
				currentBehaviorSeries.nextBehaviorPointer = 0;
			}
			
			return null;
		}
	}
	
	public boolean hasActivatedMove() {
		return this.currentBehaviorPointer >= 0;
	}
	
	public void tick() {
		for (BehaviorSeries<T> behaviorSeries : this.behaviorSeriesList) {
			behaviorSeries.tick();
		}
	}
	
	public static <T extends MobPatch<?>> CombatBehaviors.Builder<T> builder() {
		return new CombatBehaviors.Builder<T>();
	}
	
	public static class Builder<T extends MobPatch<?>> {
		private List<BehaviorSeries.Builder<T>> behaviorSeriesList = Lists.newArrayList();
		
		public Builder<T> newBehaviorSeries(BehaviorSeries.Builder<T> builder) {
			this.behaviorSeriesList.add(builder);
			return this;
		}
		
		public CombatBehaviors<T> build(T mobpatch) {
			return new CombatBehaviors<T>(this, mobpatch);
		}
	}
	
	public static class BehaviorSeries<T extends MobPatch<?>> {
		private final List<Behavior<T>> behaviors = Lists.newArrayList();
		private final boolean volatilePointer;
		private final boolean canBeInterrupted;
		private final float weight;
		private final int maxCooldown;
		private List<Integer> cooldownSharingPointer;
		private int cooldown;
		private int nextBehaviorPointer;
		private boolean loopFinished;
		
		private BehaviorSeries(BehaviorSeries.Builder<T> builder) {
			builder.behaviors.stream().map((motionBuilder) -> motionBuilder.build()).forEach(this.behaviors::add);
			this.volatilePointer = builder.volatilePointer;
			this.canBeInterrupted = builder.canBeInterrupted;
			this.weight = builder.weight;
			this.cooldownSharingPointer = builder.cooldownSharingPointers;
			this.maxCooldown = builder.cooldown;
		}
		
		public boolean test(T mobpatch) {
			if (this.cooldown > 0) {
				return false;
			}
			
			return this.behaviors.get(this.nextBehaviorPointer).checkPredicates(mobpatch);
		}
		
		public void upCounter() {
			++this.nextBehaviorPointer;
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
		
		public void resetCooldown(CombatBehaviors<T> mobBehavior, boolean resetSharingCooldown) {
			this.cooldown = this.maxCooldown;
			
			if (resetSharingCooldown) {
				for (int i : this.cooldownSharingPointer) {
					BehaviorSeries<T> behaviorSeries = mobBehavior.behaviorSeriesList.get(i);
					behaviorSeries.cooldown = behaviorSeries.maxCooldown;
				}
			}
		}
		
		public static <T extends MobPatch<?>> BehaviorSeries.Builder<T> builder() {
			return new BehaviorSeries.Builder<T>();
		}
		
		public static class Builder<T extends MobPatch<?>> {
			private List<Behavior.Builder<T>> behaviors = Lists.newArrayList();
			private boolean volatilePointer = false;
			private boolean canBeInterrupted = true;
			private float weight;
			private int cooldown;
			private List<Integer> cooldownSharingPointers = Lists.newArrayList();
			
			public Builder<T> weight(float weight) {
				this.weight = weight;
				return this;
			}
			
			public Builder<T> cooldown(int cooldown) {
				this.cooldown = cooldown;
				return this;
			}
			
			public Builder<T> simultaneousCooldown(int... cooldownSharingPointers) {
				for (int pointer : cooldownSharingPointers) {
					this.cooldownSharingPointers.add(pointer);
				}
				
				return this;
			}
			
			public BehaviorSeries.Builder<T> nextBehavior(Behavior.Builder<T> motion) {
				this.behaviors.add(motion);
				return this;
			}
			
			public BehaviorSeries.Builder<T> volatileBehaviorPointer(boolean volatilePointer) {
				this.volatilePointer = volatilePointer;
				return this;
			}
			
			public BehaviorSeries.Builder<T> canBeInterrupted(boolean canBeInterrupted) {
				this.canBeInterrupted = canBeInterrupted;
				return this;
			}
			
			public BehaviorSeries<T> build() {
				return new BehaviorSeries<T>(this);
			}
		}
	}
	
	public static class Behavior<T extends MobPatch<?>> {
		private Consumer<T> behavior;
		private final List<NextBehaviorPredicate<T>> predicates;
		
		private Behavior(Behavior.Builder<T> builder) {
			this.behavior = builder.behavior;
			this.predicates = builder.predicate;
		}
		
		private boolean checkPredicates(T mobpatch) {
			for (NextBehaviorPredicate<T> predicate : this.predicates) {
				if (!predicate.test(mobpatch)) {
					return false;
				}
			}
			
			return true;
		}
		
		public void execute(T mobpatch) {
			this.behavior.accept(mobpatch);
        	mobpatch.updateEntityState();
		}
		
		public static <T extends MobPatch<?>> Behavior.Builder<T> builder() {
			return new Behavior.Builder<T>();
		}
		
		public static class Builder<T extends MobPatch<?>> {
			private Consumer<T> behavior;
			private List<NextBehaviorPredicate<T>> predicate = Lists.newArrayList();
			private AnimationPacketProvider packetProvider = SPPlayAnimation::new;
			
			public Behavior.Builder<T> behavior(Consumer<T> behavior) {
				this.behavior = behavior;
				return this;
			}
			
			public Behavior.Builder<T> emptyBehavior() {
				this.behavior = (mobpatch) -> {};
				return this;
			}
			
			public Behavior.Builder<T> animationBehavior(StaticAnimation motion) {
				this.behavior = (mobpatch) -> {
					mobpatch.playAnimationSynchronized(motion, 0.0F, this.packetProvider);
				};
				
				return this;
			}
			
			public Behavior.Builder<T> withinEyeHeight() {
				this.predicate(new TargetWithinEyeHeight<T>());
				return this;
			}
			
			public Behavior.Builder<T> randomChance(float chance) {
				this.predicate(new RandomChance<T>(chance));
				return this;
			}
			
			public Behavior.Builder<T> withinDistance(double minDistance, double maxDistance) {
				this.predicate(new TargetWithinDistance<T>(minDistance * minDistance, maxDistance * maxDistance));
				return this;
			}
			
			public Behavior.Builder<T> withinAngle(double minDegree, double maxDegree) {
				this.predicate(new TargetWithinAngle<T>(minDegree, maxDegree));
				return this;
			}
			
			public Behavior.Builder<T> withinAngleHorizontal(double minDegree, double maxDegree) {
				this.predicate(new TargetWithinAngle.Horizontal<T>(minDegree, maxDegree));
				return this;
			}
			
			public Behavior.Builder<T> health(float health, Health.Comparator comparator) {
				this.predicate(new Health<T>(health, comparator));
				return this;
			}
			
			public Behavior.Builder<T> custom(Function<T, Boolean> customPredicate) {
				this.predicate(new CustomPredicate<T>(customPredicate));
				return this;
			}
			
			public Behavior.Builder<T> predicate(NextBehaviorPredicate<T> predicate) {
				this.predicate.add(predicate);
				return this;
			}
			
			public Behavior.Builder<T> packetProvider(AnimationPacketProvider packetProvider) {
				this.packetProvider = packetProvider;
				return this;
			}
			
			public Behavior<T> build() {
				return new Behavior<T>(this);
			}
		}
	}
	
	public static abstract class NextBehaviorPredicate<T extends MobPatch<?>> {
		public abstract boolean test(T mobpatch);
	}
	
	public static class CustomPredicate<T extends MobPatch<?>> extends NextBehaviorPredicate<T> {
		Function<T, Boolean> test;
		
		public CustomPredicate(Function<T, Boolean> test) {
			this.test = test;
		}
		
		public boolean test(T mobpatch) {
			return this.test.apply(mobpatch);
		}
	}
	
	public static class RandomChance<T extends MobPatch<?>> extends NextBehaviorPredicate<T> {
		private final float chance;
		
		private RandomChance(float chance) {
			this.chance = chance;
		}
		
		public boolean test(T mobpatch) {
			return mobpatch.getOriginal().getRandom().nextFloat() < this.chance;
		}
	}
	
	public static class TargetWithinEyeHeight<T extends MobPatch<?>> extends NextBehaviorPredicate<T> {
		public boolean test(T mobpatch) {
			double veticalDistance = Math.abs(mobpatch.getOriginal().getY() - mobpatch.getAttackTarget().getY());
			return veticalDistance < mobpatch.getOriginal().getEyeHeight();
		}
	}
	
	public static class TargetWithinDistance<T extends MobPatch<?>> extends NextBehaviorPredicate<T> {
		private final double minDistance;
		private final double maxDistance;
		
		private TargetWithinDistance(double minDistance, double maxDistance) {
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
		}
		
		public boolean test(T mobpatch) {
			double distanceSqr = mobpatch.getOriginal().distanceToSqr(mobpatch.getAttackTarget());
			return this.minDistance < distanceSqr && distanceSqr < this.maxDistance;
		}
	}
	
	public static class TargetWithinAngle<T extends MobPatch<?>> extends NextBehaviorPredicate<T> {
		protected final double minDegree;
		protected final double maxDegree;
		
		private TargetWithinAngle(double minDegree, double maxDegree) {
			this.minDegree = minDegree;
			this.maxDegree = maxDegree;
		}
		
		public boolean test(T mobpatch) {
			Mob original = mobpatch.getOriginal();
			Entity target = original.getTarget();
			double degree = mobpatch.getAngleTo(target);
			return this.minDegree < degree && degree < this.maxDegree;
		}
		
		public static class Horizontal<T extends MobPatch<?>> extends TargetWithinAngle<T> {
			private Horizontal(double minDegree, double maxDegree) {
				super(minDegree, maxDegree);
			}
			
			@Override
			public boolean test(T mobpatch) {
				Mob original = mobpatch.getOriginal();
				Entity target = original.getTarget();
				double degree = mobpatch.getAngleToHorizontal(target);
				return this.minDegree < degree && degree < this.maxDegree;
			}
		}
	}
	
	public static class Health<T extends MobPatch<?>> extends NextBehaviorPredicate<T> {
		private final float value;
		private final Comparator comparator;
		
		private Health(float value, Comparator comparator) {
			this.value = value;
			this.comparator = comparator;
		}
		
		public boolean test(T mobpatch) {
			switch (this.comparator) {
			case LESS_ABSOLUTE:
				return this.value > mobpatch.getOriginal().getHealth();
			case GREATER_ABSOLUTE:
				return this.value < mobpatch.getOriginal().getHealth();
			case LESS_RATIO:
				return this.value > mobpatch.getOriginal().getHealth() / mobpatch.getOriginal().getMaxHealth();
			case GREATER_RATIO:
				return this.value < mobpatch.getOriginal().getHealth() / mobpatch.getOriginal().getMaxHealth();
			}
			
			return true;
		}
		
		public static enum Comparator {
			GREATER_ABSOLUTE, LESS_ABSOLUTE, GREATER_RATIO, LESS_RATIO
		}
	}
}