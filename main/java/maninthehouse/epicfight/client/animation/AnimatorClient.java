package maninthehouse.epicfight.client.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maninthehouse.epicfight.animation.AnimationPlayer;
import maninthehouse.epicfight.animation.Animator;
import maninthehouse.epicfight.animation.Joint;
import maninthehouse.epicfight.animation.JointTransform;
import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.animation.types.MirrorAnimation;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;

public class AnimatorClient extends Animator {
	private final Map<LivingMotion, StaticAnimation> livingAnimations = new HashMap<LivingMotion, StaticAnimation>();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
	private List<LivingMotion> modifiedLivingMotions;
	public final BaseLayer baseLayer;
	public final MixLayer mixLayer;
	private LivingMotion currentMotion;
	private LivingMotion currentMixMotion;
	public boolean reversePlay = false;
	public boolean mixLayerActivated = false;
	
	public AnimatorClient(LivingData<?> entitydata) {
		this.entitydata = entitydata;
		this.baseLayer = new BaseLayer(Animations.DUMMY_ANIMATION);
		this.mixLayer = new MixLayer(Animations.DUMMY_ANIMATION);
		this.currentMotion = LivingMotion.IDLE;
		this.currentMixMotion = LivingMotion.NONE;
		this.defaultLivingAnimations = new HashMap<LivingMotion, StaticAnimation>();
		this.modifiedLivingMotions = new ArrayList<LivingMotion>();
	}

	/** Play an animation by animation id **/
	@Override
	public void playAnimation(int id, float modifyTime) {
		this.playAnimation(Animations.findAnimationDataById(id), modifyTime);
	}

	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime) {
		this.baseLayer.pause = false;
		this.mixLayer.pause = false;
		this.reversePlay = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitydata, modifyTime);
	}
	
	@Override
	public void vacateCurrentPlay() {
		this.baseLayer.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
	}
	
	/** Add new Living animation of entity **/
	public void addLivingAnimation(LivingMotion motion, StaticAnimation animation) {
		this.livingAnimations.put(motion, animation);
		
		if (motion == this.currentMotion) {
			if (!this.entitydata.isInaction()) {
				playAnimation(animation, 0);
			}
		}
	}
	
	public void addLivingMixAnimation(LivingMotion motion, StaticAnimation animation) {
		this.livingAnimations.put(motion, animation);

		if (motion == this.currentMotion) {
			if (!this.entitydata.isInaction()) {
				if (animation instanceof MirrorAnimation) {
					playMixLayerAnimation(((MirrorAnimation)animation).checkHandAndReturnAnimation(this.entitydata.getOriginalEntity().getActiveHand()));
				} else {
					playMixLayerAnimation(animation);
				}
			}
		}
	}
	
	public void addModifiedLivingMotion(LivingMotion motion, StaticAnimation animation) {
		if (!this.modifiedLivingMotions.contains(motion)) {
			this.modifiedLivingMotions.add(motion);
		}
		
		this.addLivingAnimation(motion, animation);
	}
	
	public void resetModifiedLivingMotions() {
		if (this.modifiedLivingMotions != null) {
			for (LivingMotion livingMotion : this.modifiedLivingMotions) {
				this.addLivingAnimation(livingMotion, this.defaultLivingAnimations.get(livingMotion));
			}
			
			this.modifiedLivingMotions.clear();
		}
	}
	
	public void setCurrentLivingMotionsToDefault() {
		this.defaultLivingAnimations.clear();
		this.defaultLivingAnimations.putAll(this.livingAnimations);
	}

	public void playLoopMotion() {
		this.currentMotion = this.entitydata.currentMotion;
		if(this.livingAnimations.containsKey(this.entitydata.currentMotion)) {
			this.playAnimation(this.livingAnimations.get(this.entitydata.currentMotion), 0.0F);
		}
	}
	
	public void playMixLoopMotion() {
		if(this.entitydata.currentMixMotion == LivingMotion.NONE) {
			this.offMixLayer(false);
		} else {
			StaticAnimation animation = this.livingAnimations.get(this.entitydata.currentMixMotion);
			
			if(animation instanceof MirrorAnimation) {
				this.playMixLayerAnimation(((MirrorAnimation)animation).checkHandAndReturnAnimation(this.entitydata.getOriginalEntity().getActiveHand()));
			} else {
				this.playMixLayerAnimation(animation);
			}
		}
		this.mixLayer.pause = false;
		this.currentMixMotion = this.entitydata.currentMixMotion;
	}

	public void playMixLayerAnimation(int id) {
		playMixLayerAnimation(Animations.findAnimationDataById(id));
	}
	
	public void playMixLayerAnimation(StaticAnimation nextAnimation) {
		if (!this.mixLayerActivated) {
			this.mixLayerActivated = true;
			this.mixLayer.animationPlayer.synchronize(this.baseLayer.animationPlayer);
		}
		this.mixLayer.linkEndPhase = false;
		this.mixLayer.playAnimation(nextAnimation, this.entitydata, 0);
	}
	
	public void offMixLayer(boolean byForce) {
		if (this.mixLayerActivated && (byForce || this.mixLayer.animationPlayer.getPlay().getState(this.mixLayer.animationPlayer.getElapsedTime()) != EntityState.POST_DELAY)) {
			this.mixLayer.linkEndPhase = true;
			this.mixLayer.setMixLinkAnimation(entitydata, 0);
			this.mixLayer.playAnimation(this.mixLayer.mixLinkAnimation, this.entitydata);
			this.mixLayer.nextPlaying = null;
			this.mixLayer.pause = false;
		}
	}
	
	public void disableMixLayer() {
		this.mixLayerActivated = false;

		if (this.mixLayer.animationPlayer.getPlay() != null) {
			this.mixLayer.animationPlayer.getPlay().onFinish(this.entitydata, true);
			this.mixLayer.animationPlayer.setEmpty();
		}

		this.mixLayer.animationPlayer.resetPlayer();
	}
	
	public void setPoseToModel(float partialTicks) {
		if(this.mixLayerActivated) {
			applyPoseToJoint(getCurrentPose(this.baseLayer, partialTicks), getCurrentPose(this.mixLayer, partialTicks), this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy(), new VisibleMatrix4f());
		} else {
			applyPoseToJoint(getCurrentPose(this.baseLayer, partialTicks), this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy(), new VisibleMatrix4f());
		}
	}
	
	private void applyPoseToJoint(Pose base, Pose mix, Joint joint, VisibleMatrix4f parentTransform) {
		if (this.mixLayer.jointMasked(joint.getName())) {
			VisibleMatrix4f currentLocalTransformBase = base.getTransformByName(joint.getName()).toTransformMatrix();
			VisibleMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformBase, currentLocalTransformBase);
			VisibleMatrix4f bindTransformBase = VisibleMatrix4f.mul(parentTransform, currentLocalTransformBase, null);
			
			VisibleMatrix4f currentLocalTransformMix = mix.getTransformByName(joint.getName()).toTransformMatrix();
			VisibleMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransformMix, currentLocalTransformMix);
			VisibleMatrix4f bindTransformMix = VisibleMatrix4f.mul(parentTransform, currentLocalTransformMix, null);
			
			bindTransformMix.m31 = bindTransformBase.m31;
			joint.setAnimatedTransform(bindTransformMix);
			
			for(Joint joints : joint.getSubJoints())
			{
				if(this.mixLayer.jointMasked(joints.getName()) || this.currentMotion == LivingMotion.IDLE)
					applyPoseToJoint(mix, joints, bindTransformMix);
				else
					applyPoseToJoint(base, joints, bindTransformBase);
			}
		} else {
			VisibleMatrix4f currentLocalTransform = base.getTransformByName(joint.getName()).toTransformMatrix();
			VisibleMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
			VisibleMatrix4f bindTransform = VisibleMatrix4f.mul(parentTransform, currentLocalTransform, null);
			joint.setAnimatedTransform(bindTransform);
			
			for(Joint joints : joint.getSubJoints()) {
				applyPoseToJoint(base, mix, joints, bindTransform);
			}
		}
	}
	
	private void applyPoseToJoint(Pose pose, Joint joint, VisibleMatrix4f parentTransform) {
		JointTransform jt = pose.getTransformByName(joint.getName());
		VisibleMatrix4f currentLocalTransform = jt.toTransformMatrix();
		VisibleMatrix4f.mul(joint.getLocalTrasnform(), currentLocalTransform, currentLocalTransform);
		VisibleMatrix4f bindTransform = VisibleMatrix4f.mul(parentTransform, currentLocalTransform, null);
		VisibleMatrix4f.mul(bindTransform, joint.getAnimatedTransform(), bindTransform);
		
		if (jt.getCustomRotation() != null) {
			float x = bindTransform.m30;
			float y = bindTransform.m31;
			float z = bindTransform.m32;
			bindTransform.m30 = 0;
			bindTransform.m31 = 0;
			bindTransform.m32 = 0;
			VisibleMatrix4f.mul(jt.getCustomRotation().toRotationMatrix(), bindTransform, bindTransform);
			bindTransform.m30 = x;
			bindTransform.m31 = y;
			bindTransform.m32 = z;
		}
		
		joint.setAnimatedTransform(bindTransform);
		
		for (Joint joints : joint.getSubJoints()) {
			applyPoseToJoint(pose, joints, bindTransform);
		}
	}
	
	public void update() {
		this.baseLayer.update(this.entitydata, this.reversePlay);
		if (this.baseLayer.animationPlayer.isEnd()) {
			if (this.baseLayer.nextPlaying == null && this.currentMotion != LivingMotion.DEATH) {
				this.entitydata.updateMotion();
				playLoopMotion();
			}
		}
		
		if (this.mixLayerActivated) {
			this.mixLayer.update(this.entitydata, false);
			if (this.mixLayer.animationPlayer.isEnd()) {
				if (this.mixLayer.linkEndPhase) {
					if (this.mixLayer.nextPlaying == null) {
						disableMixLayer();
						this.mixLayer.linkEndPhase = false;
					}
				} else {
					this.mixLayer.animationPlayer.getPlay().onFinish(this.entitydata, this.mixLayer.animationPlayer.isEnd());
					if (!this.mixLayer.pause) {
						this.mixLayer.setMixLinkAnimation(this.entitydata, 0);
						this.mixLayer.playAnimation(this.mixLayer.mixLinkAnimation, this.entitydata);
						this.mixLayer.linkEndPhase = true;
					}
				}
			}
		}
	}
	
	@Override
	public void playDeathAnimation() {
		this.playAnimation(livingAnimations.get(LivingMotion.DEATH), 0);
		this.currentMotion = LivingMotion.DEATH;
	}
	
	public StaticAnimation getJumpAnimation() {
		return this.livingAnimations.get(LivingMotion.JUMPING);
	}
	
	@Override
	public void onEntityDeath() {
		this.baseLayer.clear(this.entitydata);
		this.mixLayer.clear(this.entitydata);
	}

	public Pose getCurrentPose(BaseLayer layer, float partialTicks) {
		return layer.animationPlayer.getCurrentPose(this.entitydata, this.baseLayer.pause ? 1 : partialTicks);
	}

	public boolean compareMotion(LivingMotion motion) {
		return this.currentMotion == motion;
	}

	public boolean compareMixMotion(LivingMotion motion) {
		return this.currentMixMotion == motion;
	}

	public void resetMotion() {
		this.currentMotion = LivingMotion.IDLE;
	}

	public void resetMixMotion() {
		this.currentMixMotion = LivingMotion.NONE;
	}

	public boolean prevAiming() {
		return this.currentMixMotion == LivingMotion.AIMING;
	}

	public void playReboundAnimation() {
		this.playMixLayerAnimation(this.livingAnimations.get(LivingMotion.SHOTING));
		this.entitydata.resetLivingMixLoop();
	}

	@Override
	public AnimationPlayer getPlayer() {
		return this.baseLayer.animationPlayer;
	}
	
	@Override
	public AnimationPlayer getPlayerFor(StaticAnimation animation) {
		AnimationPlayer player = this.baseLayer.animationPlayer;
		
		if(player.getPlay().equals(animation)) {
			return player;
		} else {
			player = this.mixLayer.animationPlayer;
		}
		
		if(player.getPlay().equals(animation)) {
			return player;
		} else {
			return null;
		}
	}
	
	public AnimationPlayer getMixLayerPlayer() {
		return this.mixLayer.animationPlayer;
	}
}