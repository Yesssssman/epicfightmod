package yesman.epicfight.api.animation.types.grappling;

import java.util.List;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class GrapplingTryAnimation extends AttackAnimation {
	private final AnimationProvider<?> grapplingAttackAnimation;
	private final AnimationProvider<?> failAnimation;
	private final AnimationProvider<?> grapplingHitAnimation;
	
	public GrapplingTryAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, Joint colliderJoint, String path, AnimationProvider<?> grapplingHitAnimation, AnimationProvider<?> interactAnimation, AnimationProvider<?> failAnimation, Armature armature) {
		this(convertTime, antic, preDelay, contact, recovery, InteractionHand.MAIN_HAND, collider, colliderJoint, path, grapplingHitAnimation, interactAnimation, failAnimation, armature);
	}
	
	public GrapplingTryAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, Collider collider, Joint colliderJoint, String path, AnimationProvider<?> grapplingHitAnimation, AnimationProvider<?> interactAnimation, AnimationProvider<?> failAnimation, Armature armature) {
		super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
		this.grapplingAttackAnimation = interactAnimation;
		this.failAnimation = failAnimation;
		this.grapplingHitAnimation = grapplingHitAnimation;
		
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		this.addProperty(ActionAnimationProperty.MOVE_ON_LINK, false);
		this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.TRACE_DEST_LOCATION_BEGIN);
		this.addProperty(ActionAnimationProperty.COORD_SET_TICK, MoveCoordFunctions.TRACE_DEST_LOCATION);
		this.addProperty(ActionAnimationProperty.COORD_GET, MoveCoordFunctions.WORLD_COORD);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		if (!entitypatch.isLogicalClient()) {
			LivingEntity hitEntity = entitypatch.getTarget();
			
			if (hitEntity != null) {
				LivingEntityPatch<?> hitEntityPatch = EpicFightCapabilities.getEntityPatch(hitEntity, LivingEntityPatch.class);
				
				if (hitEntityPatch != null) {
					hitEntityPatch.notifyGrapplingWarning();
				}
			}
		}
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		super.end(entitypatch, nextAnimation, isEnd);
		
		if (isEnd && !entitypatch.isLogicalClient()) {
			LivingEntity hitEntity = entitypatch.getTarget();
			
			if (hitEntity != null) {
				Phase phase = this.getPhaseByTime(0.0F);
				AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this);
				float prevPoseTime = player.getPrevElapsedTime();
				float poseTime = player.getElapsedTime();
				List<Entity> list = phase.getCollidingEntities(entitypatch, this, prevPoseTime, poseTime, this.getPlaySpeed(entitypatch, this));
				
				if (list.contains(hitEntity)) {
					DamageSource dmgSource = this.getEpicFightDamageSource(entitypatch, hitEntity, phase);
					
					if (entitypatch.tryHurt(dmgSource, 0.0F).resultType.dealtDamage()) {
						entitypatch.reserveAnimation(this.grapplingAttackAnimation.get());
						entitypatch.setGrapplingTarget(hitEntity);
						LivingEntityPatch<?> hitEntityPatch = EpicFightCapabilities.getEntityPatch(hitEntity, LivingEntityPatch.class);
						
						if (hitEntityPatch != null) {
							hitEntity.lookAt(EntityAnchorArgument.Anchor.FEET, entitypatch.getOriginal().position());
							hitEntityPatch.playAnimationSynchronized(this.grapplingHitAnimation.get(), 0.0F);
						}
						
						return;
					}
				}
			}
			
			entitypatch.reserveAnimation(this.failAnimation.get());
		}
	}
	
	@Override
	protected void attackTick(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
	}
}