package yesman.epicfight.api.animation.types;

import java.util.Map;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordGetter;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordSetter;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.TimePairList;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ActionAnimation extends MainFrameAnimation {
	public ActionAnimation(float convertTime, String path, Armature armature) {
		this(convertTime, Float.MAX_VALUE, path, armature);
	}
	
	public ActionAnimation(float convertTime, float postDelay, String path, Armature armature) {
		super(convertTime, path, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, postDelay)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.addState(EntityState.CAN_SKILL_EXECUTION, false)
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.INACTION, true);
	}
	
	public <V> ActionAnimation addProperty(ActionAnimationProperty<V> propertyType, V value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		entitypatch.cancelAnyAction();
		
		if (entitypatch.shouldMoveOnCurrentSide(this)) {
			if (this.getProperty(ActionAnimationProperty.STOP_MOVEMENT).orElse(false)) {
				entitypatch.getOriginal().setDeltaMovement(0.0D, entitypatch.getOriginal().getDeltaMovement().y, 0.0D);
			}
			
			entitypatch.correctRotation();
			
			MoveCoordSetter moveCoordSetter = this.getProperty(ActionAnimationProperty.COORD_SET_BEGIN).orElse(MoveCoordFunctions.RAW_COORD);
			moveCoordSetter.set(this, entitypatch, entitypatch.getArmature().getActionAnimationCoord());
		}
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		this.move(entitypatch, this);
	}
	
	@Override
	public void linkTick(LivingEntityPatch<?> entitypatch, DynamicAnimation linkAnimation) {
		this.move(entitypatch, linkAnimation);
	};
	
	protected void move(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		if (!this.validateMovement(entitypatch, animation)) {
			return;
		}
		
		EntityState state = this.getState(entitypatch, entitypatch.getAnimator().getPlayerFor(this).getElapsedTime());
		
		if (state.inaction()) {
			LivingEntity livingentity = entitypatch.getOriginal();
			Vec3 vec3 = this.getCoordVector(entitypatch, animation);
			livingentity.move(MoverType.SELF, vec3);
		}
	}
	
	protected boolean validateMovement(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		if (!entitypatch.shouldMoveOnCurrentSide(this)) {
			return false;
		}
		
		if (animation instanceof LinkAnimation) {
			if (!this.getProperty(ActionAnimationProperty.MOVE_ON_LINK).orElse(true)) {
				return false;
			} else {
				return this.shouldMove(0.0F);
			}
		} else {
			return this.shouldMove(entitypatch.getAnimator().getPlayerFor(animation).getElapsedTime());
		}
	}
	
	protected boolean shouldMove(float currentTime) {
		if (this.properties.containsKey(ActionAnimationProperty.MOVE_TIME)) {
			TimePairList moveTimes = this.getProperty(ActionAnimationProperty.MOVE_TIME).get();
			return moveTimes.isTimeInPairs(currentTime);
		} else {
			return true;
		}
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		if (this.getProperty(ActionAnimationProperty.COORD).isEmpty()) {
			JointTransform jt = pose.getOrDefaultTransform("Root");
			Vec3f jointPosition = jt.translation();
			OpenMatrix4f toRootTransformApplied = entitypatch.getArmature().searchJointByName("Root").getLocalTrasnform().removeTranslation();
			OpenMatrix4f toOrigin = OpenMatrix4f.invert(toRootTransformApplied, null);
			Vec3f worldPosition = OpenMatrix4f.transform3v(toRootTransformApplied, jointPosition, null);
			worldPosition.x = 0.0F;
			worldPosition.y = (this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false) && worldPosition.y > 0.0F) ? 0.0F : worldPosition.y;
			worldPosition.z = 0.0F;
			OpenMatrix4f.transform3v(toOrigin, worldPosition, worldPosition);
			jointPosition.x = worldPosition.x;
			jointPosition.y = worldPosition.y;
			jointPosition.z = worldPosition.z;
		}
		
		super.modifyPose(animation, pose, entitypatch, time, partialTicks);
	}
	
	@Override
	public void setLinkAnimation(Pose pose1, float convertTimeModifier, LivingEntityPatch<?> entitypatch, LinkAnimation dest) {
		float totalTime = convertTimeModifier > 0.0F ? convertTimeModifier : 0.0F + this.convertTime;
		float nextStart = 0.0F;
		
		if (convertTimeModifier < 0.0F) {
			nextStart -= convertTimeModifier;
			dest.startsAt = nextStart;
		}
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Pose pose = this.getPoseByTime(entitypatch, nextStart, 1.0F);
		
		if (entitypatch.shouldMoveOnCurrentSide(this) && this.getProperty(ActionAnimationProperty.MOVE_ON_LINK).orElse(true)) {
			JointTransform jt = pose.getOrDefaultTransform("Root");
			
			if (this.getProperty(ActionAnimationProperty.COORD).isEmpty()) {
				Vec3f withPosition = entitypatch.getArmature().getActionAnimationCoord().getInterpolatedTranslation(nextStart);
				jt.translation().set(withPosition);
			} else {
				TransformSheet coordTransform = this.getProperty(ActionAnimationProperty.COORD).get();
				Vec3f nextCoord = coordTransform.getKeyframes()[0].transform().translation();
				jt.translation().add(0.0F, 0.0F, nextCoord.z);
			}
		}
		
		Map<String, JointTransform> data2 = pose.getJointTransformData();
		
		for (String jointName : data2.keySet()) {
			Keyframe[] keyframes = new Keyframe[2];
			keyframes[0] = new Keyframe(0, data1.getOrDefault(jointName, JointTransform.empty()));
			keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
			
			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}
	
	protected Vec3 getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
		TimePairList coordUpdateTime = this.getProperty(ActionAnimationProperty.COORD_UPDATE_TIME).orElse(null);
		boolean isCoordUpdateTime = true;
		
		if (coordUpdateTime != null && !coordUpdateTime.isTimeInPairs(player.getElapsedTime())) {
			isCoordUpdateTime = false;
		}
		
		MoveCoordSetter moveCoordsetter = isCoordUpdateTime ? this.getProperty(ActionAnimationProperty.COORD_SET_TICK).orElse(null) : MoveCoordFunctions.RAW_COORD;
		
		if (moveCoordsetter != null) {
			TransformSheet transformSheet = (animation instanceof LinkAnimation) ? animation.getCoord() : entitypatch.getArmature().getActionAnimationCoord();
			moveCoordsetter.set(animation, entitypatch, transformSheet);
		}
		
		TransformSheet rootCoord;
		
		if (animation instanceof LinkAnimation) {
			rootCoord = animation.getCoord();
		} else {
			rootCoord = entitypatch.getArmature().getActionAnimationCoord();
			
			if (rootCoord == null) {
				rootCoord = animation.getCoord();
			}
		}
		
		boolean hasNoGravity = entitypatch.getOriginal().isNoGravity();
		boolean moveVertical = this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(this.getProperty(ActionAnimationProperty.COORD).isPresent());
		MoveCoordGetter moveGetter = isCoordUpdateTime ? this.getProperty(ActionAnimationProperty.COORD_GET).orElse(MoveCoordFunctions.DIFF_FROM_PREV_COORD) : MoveCoordFunctions.DIFF_FROM_PREV_COORD;
		Vec3f move = moveGetter.get(animation, entitypatch, rootCoord);
		
		LivingEntity livingentity = entitypatch.getOriginal();
		Vec3 motion = livingentity.getDeltaMovement();
		
		this.getProperty(ActionAnimationProperty.NO_GRAVITY_TIME).ifPresentOrElse((noGravityTime) -> {
			if (noGravityTime.isTimeInPairs(animation instanceof LinkAnimation ? 0.0F : player.getElapsedTime())) {
				livingentity.setDeltaMovement(motion.x, 0.0D, motion.z);
			} else {
				move.y = 0.0F;
			}
		}, () -> {
			if (moveVertical && move.y > 0.0F && !hasNoGravity) {
				double gravity = livingentity.getAttribute(ForgeMod.ENTITY_GRAVITY.get()).getValue();
				livingentity.setDeltaMovement(motion.x, motion.y <= 0.0F ? (motion.y + gravity) : motion.y, motion.z);
			}
		});
		
		return move.toDoubleVector();
	}
}