package yesman.epicfight.api.animation.types;

import java.util.Map;
import java.util.Optional;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class DynamicAnimation {
	protected final boolean isRepeat;
	protected final float convertTime;
	
	public DynamicAnimation() {
		this(EpicFightOptions.GENERAL_ANIMATION_CONVERT_TIME, false);
	}
	
	public DynamicAnimation(float convertTime, boolean isRepeat) {
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}
	
	public final Pose getRawPose(float time) {
		return this.getAnimationClip().getPoseInTime(time);
	}
	
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose pose = this.getRawPose(time);
		this.modifyPose(this, pose, entitypatch, time, partialTicks);
		
		return pose;
	}
	
	/** Modify the pose both this and link animation. **/
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
	}
	
	public void putOnPlayer(AnimationPlayer animationPlayer, LivingEntityPatch<?> entitypatch) {
		animationPlayer.setPlayAnimation(this);
		animationPlayer.tick(entitypatch);
		animationPlayer.begin(this, entitypatch);
	}
	
	public void begin(LivingEntityPatch<?> entitypatch) {}
	public void tick(LivingEntityPatch<?> entitypatch) {}
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {}
	public void linkTick(LivingEntityPatch<?> entitypatch, DynamicAnimation linkAnimation) {};
	
	public boolean hasTransformFor(String joint) {
		return this.getTransfroms().containsKey(joint);
	}
	
	@OnlyIn(Dist.CLIENT)
	public Optional<JointMaskEntry> getJointMaskEntry(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion) {
		return Optional.empty();
	}
	
	public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
		return EntityState.DEFAULT_STATE;
	}
	
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		return new TypeFlexibleHashMap<> (false);
	}

	public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
		return stateFactor.defaultValue();
	}
	
	public abstract AnimationClip getAnimationClip();
	
	public Map<String, TransformSheet> getTransfroms() {
		return this.getAnimationClip().getJointTransforms();
	}
	
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch, DynamicAnimation animation) {
		return 1.0F;
	}
	
	public TransformSheet getCoord() {
		return this.getTransfroms().containsKey("Root") ? this.getTransfroms().get("Root") : ActionAnimation.EMPTY_SHEET;
	}
	
	public DynamicAnimation getRealAnimation() {
		return this;
	}
	
	public void setTotalTime(float totalTime) {
		this.getAnimationClip().setClipTime(totalTime);
	}
	
	public float getTotalTime() {
		return this.getAnimationClip().getClipTime();
	}
	
	public float getConvertTime() {
		return this.convertTime;
	}
	
	public boolean isRepeat() {
		return this.isRepeat;
	}
	
	public boolean canBePlayedReverse() {
		return false;
	}
	
	public ResourceLocation getRegistryName() {
		return new ResourceLocation(EpicFightMod.MODID, "");
	}
	
	public int getId() {
		return -1;
	}
	
	public <V> Optional<V> getProperty(AnimationProperty<V> propertyType) {
		return Optional.empty();
	}
	
	public boolean isBasicAttackAnimation() {
		return false;
	}

	public boolean isMainFrameAnimation() {
		return false;
	}
	
	public boolean isReboundAnimation() {
		return false;
	}
	
	public boolean isMetaAnimation() {
		return false;
	}
	
	public boolean isClientAnimation() {
		return false;
	}
	
	public boolean isStaticAnimation() {
		return false;
	}
	
	public boolean isLinkAnimation() {
		return false;
	}
	
	public boolean doesHeadRotFollowEntityHead() {
		return false;
	}
	
	public DynamicAnimation getThis() {
		return this;
	}

	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, float playTime, float partialTicks) {
	}
}