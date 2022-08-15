package yesman.epicfight.api.animation.types;

import java.util.Map;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationCoordSetter;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ActionAnimation extends MainFrameAnimation {
	
	public ActionAnimation(float convertTime, String path, Model model) {
		this(convertTime, Float.MAX_VALUE, path, model);
	}
	
	public ActionAnimation(float convertTime, float postDelay, String path, Model model) {
		super(convertTime, path, model);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, postDelay)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.addState(EntityState.CAN_SKILL_EXECUTION, false)
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.INACTION, true);
	}
	
	public <V> ActionAnimation addProperty(ActionAnimationProperty<V> propertyType, V value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		entitypatch.cancelUsingItem();
		
		if (this.getProperty(ActionAnimationProperty.STOP_MOVEMENT).orElse(false)) {
			entitypatch.getOriginal().setDeltaMovement(0.0D, entitypatch.getOriginal().getDeltaMovement().y, 0.0D);
		}
		
		ActionAnimationCoordSetter actionCoordSetter = this.getProperty(ActionAnimationProperty.COORD_SET_BEGIN).orElse((self, entitypatch$2, transformSheet) -> {
			transformSheet.readFrom(self.jointTransforms.get("Root"));
		});
		
		entitypatch.getAnimator().getPlayerFor(this).setActionAnimationCoord(this, entitypatch, actionCoordSetter);
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		this.move(entitypatch, this);
	}
	
	@Override
	public void linkTick(LivingEntityPatch<?> entitypatch, LinkAnimation linkAnimation) {
		this.move(entitypatch, linkAnimation);
	};
	
	private void move(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		if (!this.validateMovement(entitypatch, animation)) {
			return;
		}
		
		EntityState state = this.getState(entitypatch.getAnimator().getPlayerFor(this).getElapsedTime());
		
		if (state.inaction()) {
			LivingEntity livingentity = entitypatch.getOriginal();
			Vec3f vec3 = this.getCoordVector(entitypatch, animation);
			BlockPos blockpos = new BlockPos(livingentity.getX(), livingentity.getBoundingBox().minY - 1.0D, livingentity.getZ());
			BlockState blockState = livingentity.level.getBlockState(blockpos);
			AttributeInstance movementSpeed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
			boolean soulboost = blockState.is(BlockTags.SOUL_SPEED_BLOCKS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, livingentity) > 0;
			double speedFactor = soulboost ? 1.0D : livingentity.level.getBlockState(blockpos).getBlock().getSpeedFactor();
			double moveMultiplier = this.getProperty(ActionAnimationProperty.AFFECT_SPEED).orElse(false) ? (movementSpeed.getValue() / movementSpeed.getBaseValue()) : 1.0F;
			livingentity.move(MoverType.SELF, new Vec3(vec3.x * moveMultiplier, vec3.y, vec3.z * moveMultiplier * speedFactor));
		}
	}
	
	private boolean validateMovement(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		LivingEntity livingentity = entitypatch.getOriginal();
		
		if (entitypatch.isLogicalClient()) {
			if (!(livingentity instanceof LocalPlayer)) {
				return false;
			}
		} else {
			if ((livingentity instanceof ServerPlayer)) {
				return false;
			}
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
	
	private boolean shouldMove(float currentTime) {
		if (this.properties.containsKey(ActionAnimationProperty.MOVE_TIME)) {
			ActionTime[] actionTimes = this.getProperty(ActionAnimationProperty.MOVE_TIME).get();
			for (ActionTime actionTime : actionTimes) {
				if (actionTime.begin <= currentTime && currentTime <= actionTime.end) {
					return true;
				}
			}
			
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	protected void modifyPose(Pose pose, LivingEntityPatch<?> entitypatch, float time) {
		JointTransform jt = pose.getOrDefaultTransform("Root");
		Vec3f jointPosition = jt.translation();
		OpenMatrix4f toRootTransformApplied = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature().searchJointByName("Root").getLocalTrasnform().removeTranslation();
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
		JointTransform jt = pose.getOrDefaultTransform("Root");
		Vec3f withPosition = entitypatch.getAnimator().getPlayerFor(this).getActionAnimationCoord().getInterpolatedTranslation(nextStart);
		
		jt.translation().set(withPosition);
		Map<String, JointTransform> data2 = pose.getJointTransformData();
		
		for (String jointName : data1.keySet()) {
			if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
				Keyframe[] keyframes = new Keyframe[2];
				keyframes[0] = new Keyframe(0, data1.get(jointName));
				keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.addSheet(jointName, sheet);
			}
		}
	}
	
	protected Vec3f getCoordVector(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		if (!this.getProperty(ActionAnimationProperty.COORD_SET_TICK).isEmpty()) {
			ActionAnimationCoordSetter actionAnimationCoordSetter = this.getProperty(ActionAnimationProperty.COORD_SET_TICK).orElse(null);
			
			if (animation instanceof LinkAnimation) {
				actionAnimationCoordSetter.set(animation, entitypatch, animation.jointTransforms.get("Root"));
			} else {
				entitypatch.getAnimator().getPlayerFor(this).setActionAnimationCoord(this, entitypatch, actionAnimationCoordSetter);
			}
		}
		
		TransformSheet rootCoord;
		
		if (animation instanceof LinkAnimation) {
			rootCoord = animation.jointTransforms.get("Root");
		} else {
			rootCoord = entitypatch.getAnimator().getPlayerFor(this).getActionAnimationCoord();
			
			if (rootCoord == null) {
				rootCoord = animation.jointTransforms.get("Root");
			}
		}
		
		LivingEntity livingentity = entitypatch.getOriginal();
		AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
		JointTransform jt = rootCoord.getInterpolatedTransform(player.getElapsedTime());
		JointTransform prevJt = rootCoord.getInterpolatedTransform(player.getPrevElapsedTime());
		Vec4f currentpos = new Vec4f(jt.translation().x, jt.translation().y, jt.translation().z, 1.0F);
		Vec4f prevpos = new Vec4f(prevJt.translation().x, prevJt.translation().y, prevJt.translation().z, 1.0F);
		OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation();
		OpenMatrix4f localTransform = entitypatch.getEntityModel(Models.LOGICAL_SERVER).getArmature().searchJointByName("Root").getLocalTrasnform().removeTranslation();
		rotationTransform.mulBack(localTransform);
		currentpos.transform(rotationTransform);
		prevpos.transform(rotationTransform);
		boolean hasNoGravity = entitypatch.getOriginal().isNoGravity();
		boolean moveVertical = this.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false);
		float dx = prevpos.x - currentpos.x;
		float dy = (moveVertical || hasNoGravity) ? currentpos.y - prevpos.y : 0.0F;
		float dz = prevpos.z - currentpos.z;
		dx = Math.abs(dx) > 0.0000001F ? dx : 0.0F;
		dz = Math.abs(dz) > 0.0000001F ? dz : 0.0F;
		
		if (moveVertical && currentpos.y > 0.0F && !hasNoGravity) {
			Vec3 motion = livingentity.getDeltaMovement();
			livingentity.setDeltaMovement(motion.x, motion.y <= 0 ? (motion.y + 0.08D) : motion.y, motion.z);
		}
		
		return new Vec3f(dx, dy, dz);
	}
	
	public static class ActionTime {
		private float begin;
		private float end;
		
		private ActionTime(float begin, float end) {
			this.begin = begin;
			this.end = end;
		}
		
		public static ActionTime crate(float begin, float end) {
			return new ActionTime(begin, end);
		}
	}
}