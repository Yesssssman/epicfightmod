package yesman.epicfight.api.animation.types.procedural;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.MoveCoordFunctions;
import yesman.epicfight.api.animation.property.MoveCoordFunctions.MoveCoordSetter;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.client.renderer.RenderingTool;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;

public class EnderDragonAttackAnimation extends AttackAnimation implements ProceduralAnimation {
	private final IKInfo[] ikInfos;
	private Map<String, TransformSheet> tipPointTransform;
	
	public EnderDragonAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, Collider collider, Joint colliderJoint, String path, Armature armature, IKInfo[] ikInfos) {
		super(convertTime, antic, preDelay, contact, recovery, collider, colliderJoint, path, armature);
		this.ikInfos = ikInfos;
		
		this.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, MoveCoordFunctions.RAW_COORD);
		this.addProperty(ActionAnimationProperty.COORD_SET_TICK, null);
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager) {
		try {
			JsonModelLoader modelLoader = (new JsonModelLoader(resourceManager, this.resourceLocation));
			AnimationManager.getInstance().loadAnimationClip(this, modelLoader::loadAllJointsClipForAnimation);
			
			this.tipPointTransform = Maps.newHashMap();
			this.setIKInfo(this.ikInfos, this.getTransfroms(), this.tipPointTransform, this.getArmature(), false, true);
		} catch (Exception e) {
			EpicFightMod.LOGGER.warn("Failed to load animation: " + this.resourceLocation);
			e.printStackTrace();
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose pose = super.getPoseByTime(entitypatch, time, partialTicks);
		
		if (entitypatch instanceof EnderDragonPatch enderdragonpatch) {
	    	float x = (float)entitypatch.getOriginal().getX();
	    	float y = (float)entitypatch.getOriginal().getY();
	    	float z = (float)entitypatch.getOriginal().getZ();
	    	float xo = (float)entitypatch.getOriginal().xo;
	    	float yo = (float)entitypatch.getOriginal().yo;
	    	float zo = (float)entitypatch.getOriginal().zo;
	    	OpenMatrix4f toModelPos = OpenMatrix4f.mul(OpenMatrix4f.translate(new Vec3f(xo + (x - xo) * partialTicks, yo + (y - yo) * partialTicks, zo + (z - zo) * partialTicks), new OpenMatrix4f(), null), entitypatch.getModelMatrix(partialTicks), null).invert();
	    	this.correctRootRotation(pose.getJointTransformData().get("Root"), enderdragonpatch, partialTicks);
	    	
	    	for (IKInfo ikInfo : this.ikInfos) {
		    	TipPointAnimation tipAnim = enderdragonpatch.getTipPointAnimation(ikInfo.endJoint.getName());
	    		JointTransform jt = tipAnim.getTipTransform(partialTicks);
		    	Vec3f jointModelpos = OpenMatrix4f.transform3v(toModelPos, jt.translation(), null);
		    	this.applyFabrikToJoint(jointModelpos.multiply(-1.0F, 1.0F, -1.0F), pose, entitypatch.getArmature(), ikInfo.startJoint, ikInfo.endJoint, jt.rotation());
	    	}
		}
		
		return pose;
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		// Load if null
		this.getAnimationClip();
		
		MoveCoordSetter actionAnimCoordSetter = this.getProperty(ActionAnimationProperty.COORD_SET_BEGIN).orElse((self, entitypatch$2, transformSheet) -> {
			transformSheet.readFrom(self.getTransfroms().get("Root"));
		});
		
		actionAnimCoordSetter.set(this, entitypatch, entitypatch.getArmature().getActionAnimationCoord());
		
		if (entitypatch instanceof EnderDragonPatch enderdragonpatch) {
			Vec3 entitypos = enderdragonpatch.getOriginal().position();
			OpenMatrix4f toWorld = OpenMatrix4f.mul(OpenMatrix4f.createTranslation((float)entitypos.x, (float)entitypos.y, (float)entitypos.z), enderdragonpatch.getModelMatrix(1.0F), null);
			enderdragonpatch.resetTipAnimations();
			
			for (IKInfo ikInfo : this.ikInfos) {
				TransformSheet tipAnim = this.getFirstPart(this.tipPointTransform.get(ikInfo.endJoint.getName()));
				Keyframe[] keyframes = tipAnim.getKeyframes();
				JointTransform firstposeTransform = keyframes[0].transform();
				firstposeTransform.translation().multiply(-1.0F, 1.0F, -1.0F);
				
				if (!ikInfo.clipAnimation || ikInfo.touchingGround[0]) {
					Vec3f rayResultPosition = this.getRayCastedTipPosition(firstposeTransform.translation().add(0.0F, 2.5F, 0.0F), toWorld, enderdragonpatch, 8.0F, ikInfo.rayLeastHeight);
					firstposeTransform.translation().set(rayResultPosition);
				} else {
					firstposeTransform.translation().set(OpenMatrix4f.transform3v(toWorld, firstposeTransform.translation(), null));
				}
				
				for (Keyframe keyframe : keyframes) {
					keyframe.transform().translation().set(firstposeTransform.translation());
				}
				
				enderdragonpatch.addTipPointAnimation(ikInfo.endJoint.getName(), firstposeTransform.translation(), tipAnim, ikInfo);
			}
		}
		
		if (entitypatch.isLogicalClient()) {
			entitypatch.getClientAnimator().resetMotion();
			entitypatch.getClientAnimator().resetCompositeMotion();
		}
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		
		if (entitypatch instanceof EnderDragonPatch enderdragonpatch) {
			Vec3 entitypos = enderdragonpatch.getOriginal().position();
			OpenMatrix4f toWorld = OpenMatrix4f.mul(OpenMatrix4f.createTranslation((float)entitypos.x, (float)entitypos.y, (float)entitypos.z), enderdragonpatch.getModelMatrix(1.0F), null);
			float elapsedTime = entitypatch.getAnimator().getPlayerFor(this).getElapsedTime();
			
			for (IKInfo ikInfo : this.ikInfos) {
				if (ikInfo.clipAnimation) {
					Keyframe[] keyframes = this.getTransfroms().get(ikInfo.endJoint.getName()).getKeyframes();
					float startTime = keyframes[ikInfo.startFrame].time();
					float endTime = keyframes[ikInfo.endFrame - 1].time();
					
					if (startTime <= elapsedTime && elapsedTime < endTime) {
						TipPointAnimation tipAnim = enderdragonpatch.getTipPointAnimation(ikInfo.endJoint.getName());
						Vec3f clipStart = ikInfo.endpos.copy().add(0.0F, 2.5F, 0.0F).multiply(-1.0F, 1.0F, -1.0F);
						Vec3f finalTargetpos = (!ikInfo.clipAnimation || ikInfo.touchingGround[ikInfo.touchingGround.length - 1]) ? 
							this.getRayCastedTipPosition(clipStart, toWorld, enderdragonpatch, 8.0F, ikInfo.rayLeastHeight) : 
								OpenMatrix4f.transform3v(toWorld, ikInfo.endpos.multiply(-1.0F, 1.0F, -1.0F), null);
						
						if (tipAnim.isOnWorking()) {
							tipAnim.newTargetPosition(finalTargetpos);
						} else {
							this.startPartAnimation(ikInfo, tipAnim, this.clipAnimation(this.tipPointTransform.get(ikInfo.endJoint.getName()), ikInfo), finalTargetpos);
						}
					}
				}
			}
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, float playTime, float partialTicks) {
		super.renderDebugging(poseStack, buffer, entitypatch, playTime, partialTicks);
		
		if (entitypatch instanceof EnderDragonPatch enderdragonpatch) {
			OpenMatrix4f modelmat = enderdragonpatch.getModelMatrix(partialTicks);
			LivingEntity originalEntity = entitypatch.getOriginal();
			Vec3 entitypos = originalEntity.position();
			float x = (float)entitypos.x;
	       	float y = (float)entitypos.y;
	       	float z = (float)entitypos.z;
	       	float xo = (float)originalEntity.xo;
	       	float yo = (float)originalEntity.yo;
	       	float zo = (float)originalEntity.zo;
	       	OpenMatrix4f toModelPos = OpenMatrix4f.mul(OpenMatrix4f.createTranslation(xo + (x - xo) * partialTicks, yo + (y - yo) * partialTicks, zo + (z - zo) * partialTicks), modelmat, null).invert();
	       	
			for (IKInfo ikInfo : this.ikInfos) {
				VertexConsumer vertexBuilder = buffer.getBuffer(EpicFightRenderTypes.debugQuads());
				Vec3f worldtargetpos = enderdragonpatch.getTipPointAnimation(ikInfo.endJoint.getName()).getTargetPosition();
				Vec3f modeltargetpos = OpenMatrix4f.transform3v(toModelPos, worldtargetpos, null).multiply(-1.0F, 1.0F, -1.0F);
				RenderingTool.drawQuad(poseStack, vertexBuilder, modeltargetpos, 0.5F, 1.0F, 0.0F, 0.0F);
				
		       	Vec3f jointWorldPos = enderdragonpatch.getTipPointAnimation(ikInfo.endJoint.getName()).getTipPosition(partialTicks);
		       	Vec3f jointModelpos = OpenMatrix4f.transform3v(toModelPos, jointWorldPos, null).multiply(-1.0F, 1.0F, -1.0F);
		       	RenderingTool.drawQuad(poseStack, vertexBuilder, jointModelpos, 0.4F, 0.0F, 0.0F, 1.0F);
		       	Pose pose = new Pose();
		       	
				for (String jointName : this.getTransfroms().keySet()) {
					pose.putJointData(jointName, this.getTransfroms().get(jointName).getInterpolatedTransform(playTime));
				}
			}
		}
	}
}