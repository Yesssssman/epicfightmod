package yesman.epicfight.physics;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class MultiOBBCollider extends MultiCollider<OBBCollider> {
	public MultiOBBCollider(int arrayLength, float posX, float posY, float posZ, float vecX, float vecY, float vecZ) {
		super(arrayLength, vecX, vecY, vecZ, OBBCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ));
		this.bigCollider = new OBBCollider(this.outerAABB, posX, posY, posZ, vecX, vecY, vecZ);
	}
	
	@Override
	public OBBCollider createCollider() {
		return new OBBCollider(this.outerAABB, this.bigCollider.modelVertex[1].x, this.bigCollider.modelVertex[1].y, this.bigCollider.modelVertex[1].z,
				this.modelCenter.x, this.modelCenter.y, this.modelCenter.z);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, LivingData<?> entitydata, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.max(Math.round((this.numberOfColliders + animation.getProperty(AttackAnimationProperty.COLLIDER_ADDER).orElse(0)) * attackSpeed), 1);
		float partialScale = 1.0F / (numberOf - 1);
		float interpolation = 0.0F;
		int jointIndex = animation.getIndexer(elapsedTime);
		boolean red = entitydata.getEntityState().shouldDetectCollision();
		List<OBBCollider> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.createCollider());
		}
		
		for (OBBCollider obbCollider : colliders) {
			matrixStackIn.push();
			OpenMatrix4f mat = null;
			Armature armature = entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature();
			armature.initializeTransform();
			float partialTime = MathHelper.lerp(interpolation, prevElapsedTime, elapsedTime);
			
			if (jointIndex == -1) {
				mat = new OpenMatrix4f();
			} else {
				mat = entitydata.getClientAnimator().getJointTransformByIndex(animation.getPoseByTime(entitydata, partialTime),
						armature.getJointHierarcy(), new OpenMatrix4f(), jointIndex);
			}
			
			obbCollider.drawInternal(matrixStackIn, buffer, mat, red);
			obbCollider.transform(mat);
			
			interpolation += partialScale;
			matrixStackIn.pop();
		}
	}
}