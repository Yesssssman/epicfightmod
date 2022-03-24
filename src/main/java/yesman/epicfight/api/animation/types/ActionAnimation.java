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
import yesman.epicfight.api.animation.property.Property.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.Property.DynamicCoordFunction;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.DynamicActionEntity;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ActionAnimation extends MainFrameAnimation {
	protected float delayTime;
	
	public ActionAnimation(float convertTime, String path, Model model) {
		this(convertTime, Float.MAX_VALUE, path, model);
	}
	
	public ActionAnimation(float convertTime, float postDelay, String path, Model model) {
		super(convertTime, path, model);
		this.delayTime = postDelay;
	}
	
	public <V> ActionAnimation addProperty(ActionAnimationProperty<V> propertyType, V value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		entitypatch.cancelUsingItem();
		
		if (this.getProperty(ActionAnimationProperty.INTERRUPT_PREVIOUS_DELTA_MOVEMENT).orElse(false)) {
			entitypatch.getOriginal().setDeltaMovement(0.0D, entitypatch.getOriginal().getDeltaMovement().y, 0.0D);
		}
		
		DynamicCoordFunction dynamicCoordFunction = this.getProperty(ActionAnimationProperty.DYNAMIC_ACTION_COORD).orElse(null);
		
		if (dynamicCoordFunction != null && entitypatch instanceof DynamicActionEntity) {
			dynamicCoordFunction.set(this, entitypatch, ((DynamicActionEntity)entitypatch).getRootDynamicTransform());
		}
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
		LivingEntity livingentity = entitypatch.getOriginal();
		
		if (entitypatch.isLogicalClient()) {
			if (!(livingentity instanceof LocalPlayer)) {
				return;
			}
		} else {
			if ((livingentity instanceof ServerPlayer)) {
				return;
			}
		}
		
		if (!this.validateMovement(entitypatch, animation)) {
			return;
		}
		
		if (entitypatch.getEntityState().inaction()) {
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
		if (animation instanceof LinkAnimation) {
			if (!this.getProperty(ActionAnimationProperty.MOVE_ON_LINK).orElse(true)) {
				return false;
			} else {
				return this.checkMovementTime(0.0F);
			}
		} else {
			return this.checkMovementTime(entitypatch.getAnimator().getPlayerFor(animation).getElapsedTime());
		}
	}
	
	private boolean checkMovementTime(float currentTime) {
		if (this.properties.containsKey(ActionAnimationProperty.ACTION_TIME)) {
			ActionTime[] actionTimes = this.getProperty(ActionAnimationProperty.ACTION_TIME).get();
			for (ActionTime actionTime : actionTimes) {
				if (currentTime <= actionTime.end) {
					if (actionTime.begin <= currentTime) {
						return true;
					}
				}
			}
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public EntityState getState(float time) {
		if (time <= this.delayTime) {
			return EntityState.PRE_DELAY;
		} else {
			return EntityState.CANCELABLE_RECOVERY;
		}
	}
	
	@Override
	protected void modifyPose(Pose pose, LivingEntityPatch<?> entitypatch, float time) {
		JointTransform jt = pose.getTransformByName("Root");
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
		float totalTime = convertTimeModifier > 0.0F ? convertTimeModifier + this.convertTime : this.convertTime;
		float updatePerTicks = this.getPlaySpeed(entitypatch) * ConfigurationIngame.A_TICK;
		float nextStart = 0;
		for (; nextStart < totalTime; nextStart += updatePerTicks) {
		}
		nextStart -= totalTime;
		
		if (convertTimeModifier < 0.0F) {
			nextStart -= convertTimeModifier;
			dest.startsAt = nextStart;
		}
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Pose pose = this.getPoseByTime(entitypatch, nextStart, 1.0F);
		JointTransform jt = pose.getTransformByName("Root");
		Vec3f withPosition = this.jointTransforms.get("Root").getInterpolatedTransform(nextStart).translation();
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
		DynamicCoordFunction coordFunction = this.getProperty(ActionAnimationProperty.DYNAMIC_ACTION_COORD).orElse(null);
		TransformSheet rootTransforms;
		
		if (coordFunction != null) {
			rootTransforms = (!(animation instanceof LinkAnimation) && entitypatch instanceof DynamicActionEntity) ? ((DynamicActionEntity)entitypatch).getRootDynamicTransform() : animation.jointTransforms.get("Root");
		} else {
			rootTransforms = animation.jointTransforms.get("Root");
		}
		
		if (rootTransforms != null) {
			LivingEntity elb = entitypatch.getOriginal();
			AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
			JointTransform jt = rootTransforms.getInterpolatedTransform(player.getElapsedTime());
			JointTransform prevJt = rootTransforms.getInterpolatedTransform(player.getPrevElapsedTime());
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
				Vec3 motion = elb.getDeltaMovement();
				elb.setDeltaMovement(motion.x, motion.y <= 0 ? (motion.y + 0.08D) : motion.y, motion.z);
			}
			
			return new Vec3f(dx, dy, dz);
		} else {
			return new Vec3f(0, 0, 0);
		}
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