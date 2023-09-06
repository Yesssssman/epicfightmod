package yesman.epicfight.api.animation.property;

import com.mojang.math.Quaternion;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MoveCoordFunctions {
	@FunctionalInterface
	public interface MoveCoordSetter {
		public void set(DynamicAnimation animation, LivingEntityPatch<?> entitypatch, TransformSheet transformSheet);
	}
	
	@FunctionalInterface
	public interface MoveCoordGetter {
		public Vec3f get(DynamicAnimation animation, LivingEntityPatch<?> entitypatch, TransformSheet transformSheet);
	}
	
	public static final MoveCoordGetter DIFF_FROM_PREV_COORD = (animation, entitypatch, coord) -> {
		LivingEntity livingentity = entitypatch.getOriginal();
		AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
		JointTransform jt = coord.getInterpolatedTransform(player.getElapsedTime());
		JointTransform prevJt = coord.getInterpolatedTransform(player.getPrevElapsedTime());
		Vec4f currentpos = new Vec4f(jt.translation());
		Vec4f prevpos = new Vec4f(prevJt.translation());
		OpenMatrix4f rotationTransform = entitypatch.getModelMatrix(1.0F).removeTranslation();
		OpenMatrix4f localTransform = entitypatch.getArmature().searchJointByName("Root").getLocalTrasnform().removeTranslation();
		rotationTransform.mulBack(localTransform);
		currentpos.transform(rotationTransform);
		prevpos.transform(rotationTransform);
		
		boolean hasNoGravity = entitypatch.getOriginal().isNoGravity();
		boolean moveVertical = animation.getProperty(ActionAnimationProperty.MOVE_VERTICAL).orElse(false) || animation.getProperty(ActionAnimationProperty.COORD).isPresent();
		float dx = prevpos.x - currentpos.x;
		float dy = (moveVertical || hasNoGravity) ? currentpos.y - prevpos.y : 0.0F;
		float dz = prevpos.z - currentpos.z;
		dx = Math.abs(dx) > 0.0001F ? dx : 0.0F;
		dz = Math.abs(dz) > 0.0001F ? dz : 0.0F;
		
		BlockPos blockpos = new BlockPos(livingentity.getX(), livingentity.getBoundingBox().minY - 1.0D, livingentity.getZ());
		BlockState blockState = livingentity.level.getBlockState(blockpos);
		AttributeInstance movementSpeed = livingentity.getAttribute(Attributes.MOVEMENT_SPEED);
		boolean soulboost = blockState.is(BlockTags.SOUL_SPEED_BLOCKS) && EnchantmentHelper.getEnchantmentLevel(Enchantments.SOUL_SPEED, livingentity) > 0;
		float speedFactor = (float)(soulboost ? 1.0D : livingentity.level.getBlockState(blockpos).getBlock().getSpeedFactor());
		float moveMultiplier = (float)(animation.getProperty(ActionAnimationProperty.AFFECT_SPEED).orElse(false) ? (movementSpeed.getValue() / movementSpeed.getBaseValue()) : 1.0F);
		
		return new Vec3f(dx * moveMultiplier * speedFactor, dy, dz * moveMultiplier * speedFactor);
	};
	
	public static final MoveCoordGetter WORLD_COORD = (animation, entitypatch, coord) -> {
		LivingEntity livingentity = entitypatch.getOriginal();
		AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
		JointTransform jt = coord.getInterpolatedTransform(player.getElapsedTime());
		Vec3 entityPos = livingentity.position();
		
		return jt.translation().copy().sub(Vec3f.fromDoubleVector(entityPos));
	};
	
	public static final MoveCoordGetter ATTACHED = (animation, entitypatch, coord) -> {
		LivingEntity target = entitypatch.getGrapplingTarget();
		
		if (target == null) {
			return DIFF_FROM_PREV_COORD.get(animation, entitypatch, coord);
		}
		
		TransformSheet rootCoord = animation.getCoord();
		LivingEntity livingentity = entitypatch.getOriginal();
		AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(animation);
		Vec3f model = rootCoord.getInterpolatedTransform(player.getElapsedTime()).translation();
		Vec3f world = OpenMatrix4f.transform3v(OpenMatrix4f.createRotatorDeg(-target.getYRot(), Vec3f.Y_AXIS), model, null);
		Vec3f dst = Vec3f.fromDoubleVector(target.position()).add(world);
		
		livingentity.setYRot(Mth.wrapDegrees(target.getYRot() + 180.0F));
		
		return dst.sub(Vec3f.fromDoubleVector(livingentity.position()));
	};
	
	public static final MoveCoordSetter TRACE_DEST_LOCATION_BEGIN = (self, entitypatch, transformSheet) -> {
		LivingEntity attackTarget = entitypatch.getTarget();
		TransformSheet transform = self.getCoord().copyAll();
		Keyframe[] rootKeyframes = transform.getKeyframes();
		
		if (attackTarget != null && attackTarget.isAlive()) {
			Vec3 start = entitypatch.getOriginal().position();
			Vec3 toTarget = attackTarget.position().subtract(start);
			Vec3f modelDst = rootKeyframes[rootKeyframes.length - 1].transform().translation().copy().multiply(-1.0F, 1.0F, -1.0F);
			float yRot = (float)MathUtils.getYRotOfVector(toTarget);
			
			modelDst.rotate(-yRot, Vec3f.Y_AXIS);
			
			Vec3 dst = attackTarget.position().add(modelDst.x, modelDst.y, modelDst.z);
			float clampedXRot = MathUtils.rotlerp(entitypatch.getOriginal().getXRot(), (float)MathUtils.getXRotOfVector(toTarget), 20.0F);
			float clampedYRot = MathUtils.rotlerp(entitypatch.getOriginal().getYRot(), yRot, entitypatch.getYRotLimit());
			TransformSheet newTransform = transform.getCorrectedWorldCoord(entitypatch, start, dst, -clampedXRot, clampedYRot, 0, rootKeyframes.length);
			
			transformSheet.readFrom(newTransform);
		} else {
			transform.transform((jt) -> {
				Vec3f firstPos = self.getCoord().getKeyframes()[0].transform().translation().copy();
				jt.translation().sub(firstPos);
				
				LivingEntity original = entitypatch.getOriginal();
				Vec3 pos = original.position();
				
				jt.translation().rotate(-original.getYRot(), Vec3f.Y_AXIS);
				jt.translation().multiply(-1.0F, 1.0F, -1.0F);
				jt.translation().add(Vec3f.fromDoubleVector(pos));
			});
			
			transformSheet.readFrom(transform);
		}
	};
	
	public static final MoveCoordSetter TRACE_DEST_LOCATION = (self, entitypatch, transformSheet) -> {
		LivingEntity attackTarget = entitypatch.getTarget();
		
		if (attackTarget != null && attackTarget.isAlive()) {
			TransformSheet transform = self.getCoord().copyAll();
			Keyframe[] rootKeyframes = transform.getKeyframes();
			Vec3 start = entitypatch.getArmature().getActionAnimationCoord().getKeyframes()[0].transform().translation().toDoubleVector();
			Vec3 toTarget = attackTarget.position().subtract(start);
			Vec3f modelDst = rootKeyframes[rootKeyframes.length - 1].transform().translation().copy().multiply(1.0F, 1.0F, -1.0F);
			float yRot = (float)MathUtils.getYRotOfVector(toTarget);
			
			modelDst.rotate(-yRot, Vec3f.Y_AXIS);
			
			Vec3 dst = attackTarget.position().add(modelDst.toDoubleVector());
			float clampedXRot = (float)MathUtils.getXRotOfVector(toTarget);
			float clampedYRot = MathUtils.rotlerp(entitypatch.getOriginal().getYRot(), yRot, entitypatch.getYRotLimit());
			TransformSheet newTransform = transform.getCorrectedWorldCoord(entitypatch, start, dst, -clampedXRot, clampedYRot, 0, rootKeyframes.length);
			
			entitypatch.getOriginal().setYRot(clampedYRot);
			transformSheet.readFrom(newTransform);
		}
	};
	
	public static final MoveCoordSetter TRACE_LOC_TARGET = (self, entitypatch, transformSheet) -> {
		LivingEntity attackTarget = entitypatch.getTarget();
		
		if (attackTarget != null && !self.getRealAnimation().getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).orElse(false)) {
			TransformSheet transform = self.getCoord().copyAll();
			Keyframe[] keyframes = transform.getKeyframes();
			int startFrame = 0; 
			int endFrame = keyframes.length - 1;
			Vec3f keyLast = keyframes[endFrame].transform().translation();
			Vec3 pos = entitypatch.getOriginal().position();
			Vec3 targetpos = attackTarget.position();
			Vec3 toTarget = targetpos.subtract(pos);
			Vec3 viewVec = entitypatch.getOriginal().getViewVector(1.0F);
			float horizontalDistance = Math.max((float)toTarget.horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
			Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
			float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
			
			if (scale > 1.0F) {
				float dot = (float)toTarget.normalize().dot(viewVec.normalize());
				scale = Math.max(scale * dot, 1.0F);
			}
			
			for (int i = startFrame; i <= endFrame; i++) {
				Vec3f translation = keyframes[i].transform().translation();
				
				if (translation.z < 0.0F) {
					translation.z *= scale;
				}
			}
			
			transformSheet.readFrom(transform);
		} else {
			transformSheet.readFrom(self.getCoord().copyAll());
		}
	};
	
	public static final MoveCoordSetter TRACE_LOCROT_TARGET = (self, entitypatch, transformSheet) -> {
		LivingEntity attackTarget = entitypatch.getTarget();
		
		if (attackTarget != null) {
			TransformSheet transform = self.getCoord().copyAll();
			Keyframe[] keyframes = transform.getKeyframes();
			int startFrame = 0; 
			int endFrame = keyframes.length - 1;
			Vec3f keyLast = keyframes[endFrame].transform().translation();
			Vec3 pos = entitypatch.getOriginal().position();
			Vec3 targetpos = attackTarget.position();
			Vec3 toTarget = targetpos.subtract(pos);
			float horizontalDistance = Math.max((float)toTarget.horizontalDistance() - (attackTarget.getBbWidth() + entitypatch.getOriginal().getBbWidth()) * 0.75F, 0.0F);
			Vec3f worldPosition = new Vec3f(keyLast.x, 0.0F, -horizontalDistance);
			float scale = Math.min(worldPosition.length() / keyLast.length(), 2.0F);
			
			float yRot = (float)MathUtils.getYRotOfVector(toTarget);
			float clampedYRot = MathUtils.rotlerp(entitypatch.getOriginal().getYRot(), yRot, entitypatch.getYRotLimit());
			
			entitypatch.getOriginal().setYRot(clampedYRot);
			
			for (int i = startFrame; i <= endFrame; i++) {
				Vec3f translation = keyframes[i].transform().translation();
				
				if (translation.z < 0.0F) {
					translation.z *= scale;
				}
			}
			
			transformSheet.readFrom(transform);
		} else {
			transformSheet.readFrom(self.getCoord().copyAll());
		}
	};
	
	public static final MoveCoordSetter RAW_COORD = (self, entitypatch, transformSheet) -> {
		transformSheet.readFrom(self.getCoord().copyAll());
	};
	
	public static final MoveCoordSetter RAW_COORD_WITH_X_ROT = (self, entitypatch, transformSheet) -> {
		float xRot = entitypatch.getOriginal().getXRot();
		TransformSheet sheet = self.getCoord().copyAll();
		
		for (Keyframe kf : sheet.getKeyframes()) {
			kf.transform().translation().rotate(-xRot, Vec3f.X_AXIS);
		}
		
		transformSheet.readFrom(sheet);
	};
	
	public static final MoveCoordSetter VEX_TRACE = (self, entitypatch, transformSheet) -> {
		TransformSheet transform = self.getCoord().copyAll();
		Keyframe[] keyframes = transform.getKeyframes();
		int startFrame = 0;
		int endFrame = 6;
		Vec3 pos = entitypatch.getOriginal().position();
		Vec3 targetpos = entitypatch.getTarget().position();
		float verticalDistance = (float) (targetpos.y - pos.y);
		Quaternion rotator = Vec3f.getRotatorBetween(new Vec3f(0.0F, -verticalDistance, (float)targetpos.subtract(pos).horizontalDistance()), new Vec3f(0.0F, 0.0F, 1.0F));
		
		for (int i = startFrame; i <= endFrame; i++) {
			Vec3f translation = keyframes[i].transform().translation();
			OpenMatrix4f.transform3v(OpenMatrix4f.fromQuaternion(rotator), translation, translation);
		}
		
		transformSheet.readFrom(transform);
	};
}