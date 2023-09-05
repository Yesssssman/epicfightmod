package yesman.epicfight.api.animation;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.api.utils.TypeFlexibleHashMap.TypeKey;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class Animator {
	protected final Map<LivingMotion, StaticAnimation> livingAnimations = Maps.newHashMap();
	protected final TypeFlexibleHashMap<TypeKey<?>> animationVariables = new TypeFlexibleHashMap<TypeKey<?>>(false);
	protected LivingEntityPatch<?> entitypatch;
	
	public Animator() {
		// Put default variables
		this.animationVariables.put(AttackAnimation.HIT_ENTITIES, Lists.newArrayList());
		this.animationVariables.put(AttackAnimation.HURT_ENTITIES, Lists.newArrayList());
	}
	
	public abstract void playAnimation(StaticAnimation nextAnimation, float convertTimeModifier);
	public abstract void playAnimationInstantly(StaticAnimation nextAnimation);
	public abstract void tick();
	/** Standby until the current animation is completely end. Mostly used for link two animations having the same last & first keyframe pose on {@link DynamicAnimation#end(LivingEntityPatch, boolean)} **/
	public abstract void reserveAnimation(StaticAnimation nextAnimation);
	public abstract EntityState getEntityState();
	/** Give a null value as a parameter to get an animation that is highest priority on client **/
	public abstract AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation);
	public abstract void init();
	public abstract void poseTick();
	
	public final void playAnimation(int namespaceId, int id, float convertTimeModifier) {
		this.playAnimation(EpicFightMod.getInstance().animationManager.findAnimationById(namespaceId, id), convertTimeModifier);
	}
	
	public final void playAnimationInstantly(int namespaceId, int id) {
		this.playAnimationInstantly(EpicFightMod.getInstance().animationManager.findAnimationById(namespaceId, id));
	}
	
	public boolean isReverse() {
		return false;
	}
	
	public void playDeathAnimation() {
		this.playAnimation(Animations.BIPED_DEATH, 0);
	}
	
	public void addLivingAnimation(LivingMotion livingMotion, StaticAnimation animation) {
		this.livingAnimations.put(livingMotion, animation);
	}
	
	public StaticAnimation getLivingAnimation(LivingMotion livingMotion, StaticAnimation defaultGetter) {
		return this.livingAnimations.getOrDefault(livingMotion, defaultGetter);
	}
	
	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingAnimationEntrySet() {
		return this.livingAnimations.entrySet();
	}
	
	public void removeAnimationVariables(TypeKey<?> typeKey) {
		this.animationVariables.remove(typeKey);
	}
	
	public <T> void putAnimationVariables(TypeKey<T> typeKey, T value) {
		if (this.animationVariables.containsKey(typeKey)) {
			this.animationVariables.replace(typeKey, value);
		} else {
			this.animationVariables.put(typeKey, value);
		}
	}
	
	public <T> T getAnimationVariables(TypeKey<T> key) {
		return (T)this.animationVariables.get(key);
	}
	
	public void resetLivingAnimations() {
		this.livingAnimations.clear();
	}
}