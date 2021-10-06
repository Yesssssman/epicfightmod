package yesman.epicfight.physics;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class MultiLineCollider extends MultiCollider<LineCollider> {
	public MultiLineCollider(int arrayLength, float posX, float posY, float posZ, float vecX, float vecY, float vecZ) {
		super(arrayLength, posX, posY, posZ, LineCollider.getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ));
		this.bigCollider = new LineCollider(this.outerAABB, posX, posY, posZ, vecX, vecY, vecZ);
	}
	
	@Override
	public LineCollider createCollider() {
		return new LineCollider(this.outerAABB, this.modelCenter.x, this.modelCenter.y, this.modelCenter.z, this.bigCollider.modelVec.x, this.bigCollider.modelVec.y, this.bigCollider.modelVec.z);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void draw(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, LivingData<?> entitydata, AttackAnimation animation, float prevElapsedTime, float elapsedTime, float partialTicks, float attackSpeed) {
		int numberOf = Math.round(this.numberOfColliders * attackSpeed);
		float partialScale = 1.0F / numberOf;
		float interpolation = partialScale;
		int jointIndex = animation.getIndexer(elapsedTime);
		boolean red = entitydata.getEntityState().shouldDetectCollision();
		List<LineCollider> colliders = Lists.newArrayList();
		
		for (int i = 0; i < numberOf; i++) {
			colliders.add(this.createCollider());
		}
		
		for(LineCollider lineCollider : colliders) {
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
			
			lineCollider.drawInternal(matrixStackIn, buffer, mat, red);
			interpolation += partialScale;
			matrixStackIn.pop();
		}
	}
}