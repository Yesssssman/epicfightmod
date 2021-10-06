package yesman.epicfight.physics;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;
import yesman.epicfight.utils.math.Vec4f;

public class LineCollider extends Collider {
	protected Vec3f modelVec;
	protected Vec3f worldVec;
	
	public LineCollider(float posX, float posY, float posZ, float vecX, float vecY, float vecZ) {
		this(getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ), posX, posY, posZ, vecX, vecY, vecZ);
	}
	
	public LineCollider(AxisAlignedBB outerAABB, float posX, float posY, float posZ, float vecX, float vecY, float vecZ) {
		super(new Vec3f(posX, posY, posZ), outerAABB);
		this.modelVec = new Vec3f(vecX, vecY, vecZ);
		this.worldVec = new Vec3f();
	}
	
	static AxisAlignedBB getInitialAABB(float posX, float posY, float posZ, float vecX, float vecY, float vecZ) {
		Vec3f start = new Vec3f(posX, posY, posZ);
		Vec3f end = new Vec3f(vecX + posX, vecY + posY, vecZ + posZ);
		float length = Math.max(start.length(), end.length());
		return new AxisAlignedBB(length, length, length, -length, -length, -length);
	}
	
	@Override
	public void transform(OpenMatrix4f mat) {
		Vec4f tempVector = new Vec4f(0, 0, 0, 1);
		OpenMatrix4f rotationMatrix = new OpenMatrix4f(mat);
		rotationMatrix.m30 = 0;
		rotationMatrix.m31 = 0;
		rotationMatrix.m32 = 0;
		tempVector.x = this.modelVec.x;
		tempVector.y = this.modelVec.y;
		tempVector.z = this.modelVec.z;
		OpenMatrix4f.transform(rotationMatrix, tempVector, tempVector);
		this.worldVec.x = tempVector.x;
		this.worldVec.y = tempVector.y;
		this.worldVec.z = tempVector.z;
		super.transform(mat);
	}
	
	@Override
	public boolean isCollideWith(Entity entity) {
		AxisAlignedBB opponent = entity.getBoundingBox();
		float maxStart;
		float minEnd;
		float startX;
		float startY;
		float startZ;
		float endX;
		float endY;
		float endZ;
		
		if (this.worldVec.x == 0) {
			if (this.worldCenter.x < opponent.minX || this.worldCenter.x > opponent.maxX) {
				return false;
			}
		}
		
		startX = MathHelper.clamp((float)(opponent.minX + this.worldCenter.x) / -this.worldVec.x, 0, 1);
		endX = MathHelper.clamp((float)(opponent.maxX + this.worldCenter.x) / -this.worldVec.x, 0, 1);

		if (startX > endX) {
			float temp = startX;
			startX = endX;
			endX = temp;
		}
		
		maxStart = startX;
		minEnd = endX;
		
		if (minEnd == maxStart) {
			return false;
		}
		
		if (this.worldVec.y == 0) {
			if (this.worldCenter.y < opponent.minY || this.worldCenter.y > opponent.maxY) {
				return false;
			}
		}
		
		startY = MathHelper.clamp((float)(opponent.minY - this.worldCenter.y) / this.worldVec.y, 0, 1);
		endY = MathHelper.clamp((float)(opponent.maxY - this.worldCenter.y) / this.worldVec.y, 0, 1);
		
		if (startY > endY) {
			float temp = startY;
			startY = endY;
			endY = temp;
		}
		
		maxStart = maxStart < startY ? startY : maxStart;
		minEnd = minEnd > endY ? endY : minEnd;
		
		if (maxStart >= minEnd) {
			return false;
		}
		
		if (this.worldVec.z == 0) {
			if (this.worldCenter.z < opponent.minZ || this.worldCenter.z > opponent.maxZ) {
				return false;
			}
		}
		
		startZ = MathHelper.clamp((float)(opponent.minZ + this.worldCenter.z) / -this.worldVec.z, 0, 1);
		endZ = MathHelper.clamp((float)(opponent.maxZ + this.worldCenter.z) / -this.worldVec.z, 0, 1);
		
		if (startZ > endZ) {
			float temp = startZ;
			startZ = endZ;
			endZ = temp;
		}
		
		maxStart = maxStart < startZ ? startZ : maxStart;
		minEnd = minEnd > endZ ? endZ : minEnd;
		
		if (maxStart >= minEnd) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void drawInternal(MatrixStack matrixStackIn, IRenderTypeBuffer buffer, OpenMatrix4f pose, boolean red) {
		IVertexBuilder vertexBuilder = buffer.getBuffer(ModRenderTypes.getLine());
		OpenMatrix4f transpose = new OpenMatrix4f();
		OpenMatrix4f.transpose(pose, transpose);
		MathUtils.translateStack(matrixStackIn, pose);
        MathUtils.rotateStack(matrixStackIn, transpose);
        Matrix4f matrix = matrixStackIn.getLast().getMatrix();
        float startX = this.modelCenter.x;
        float startY = this.modelCenter.y;
        float startZ = this.modelCenter.z;
        float endX = this.modelCenter.x + this.modelVec.x;
        float endY = this.modelCenter.y + this.modelVec.y;
        float endZ = this.modelCenter.z + this.modelVec.z;
        float color = red ? 0.0F : 1.0F;
        vertexBuilder.pos(matrix, startX, startY, startZ).color(1.0F, color, color, 1.0F).endVertex();
        vertexBuilder.pos(matrix, endX, endY, endZ).color(1.0F, color, color, 1.0F).endVertex();
	}
}