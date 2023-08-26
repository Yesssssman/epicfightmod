package yesman.epicfight.api.collider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;

public class OBBCollider extends Collider {
	protected final Vec3[] modelVertex;
	protected final Vec3[] modelNormal;
	protected Vec3[] rotatedVertex;
	protected Vec3[] rotatedNormal;
	protected Vec3f scale;
	
	/**
	 * make 3d obb
	 * @param pos1 left_back
	 * @param pos2 left_front
	 * @param pos3 right_front
	 * @param pos4 right_back
	 * @param modelCenter central position
	 */
	public OBBCollider(double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
		this(getInitialAABB(posX, posY, posZ, center_x, center_y, center_z), posX, posY, posZ, center_x, center_y, center_z);
	}
	
	protected OBBCollider(AABB outerAABB, double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
		super(new Vec3(center_x, center_y, center_z), outerAABB);
		this.modelVertex = new Vec3[4];
		this.modelNormal = new Vec3[3];
		this.rotatedVertex = new Vec3[4];
		this.rotatedNormal = new Vec3[3];
		this.modelVertex[0] = new Vec3(posX, posY, -posZ);
		this.modelVertex[1] = new Vec3(posX, posY, posZ);
		this.modelVertex[2] = new Vec3(-posX, posY, posZ);
		this.modelVertex[3] = new Vec3(-posX, posY, -posZ);
		this.modelNormal[0] = new Vec3(1, 0, 0);
		this.modelNormal[1] = new Vec3(0, 1, 0);
		this.modelNormal[2] = new Vec3(0, 0, -1);
		this.rotatedVertex[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[1] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[2] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[3] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[1] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[2] = new Vec3(0.0D, 0.0D, 0.0D);
	}
	
	static AABB getInitialAABB(double posX, double posY, double posZ, double center_x, double center_y, double center_z) {
		double xLength = Math.abs(posX) + Math.abs(center_x);
		double yLength = Math.abs(posY) + Math.abs(center_y);
		double zLength = Math.abs(posZ) + Math.abs(center_z);
		double maxLength = Math.max(xLength, Math.max(yLength, zLength));
		return new AABB(maxLength, maxLength, maxLength, -maxLength, -maxLength, -maxLength);
	}
	
	/**
	 * make 2d obb
	 * @param pos1 left
	 * @param pos2 right
	 * @param modelCenter central position
	 */
	public OBBCollider(AABB entityCallAABB, double pos1_x, double pos1_y, double pos1_z, double pos2_x, double pos2_y, double pos2_z, 
			double norm1_x, double norm1_y, double norm1_z, double norm2_x, double norm2_y, double norm2_z, double center_x, double center_y, double center_z) {
		super(new Vec3(center_x, center_y, center_z), entityCallAABB);
		this.modelVertex = new Vec3[2];
		this.modelNormal = new Vec3[2];
		this.rotatedVertex = new Vec3[2];
		this.rotatedNormal = new Vec3[2];
		this.modelVertex[0] = new Vec3(pos1_x, pos1_y, pos1_z);
		this.modelVertex[1] = new Vec3(pos2_x, pos2_y, pos2_z);
		this.modelNormal[0] = new Vec3(norm1_x,norm1_y,norm1_z);
		this.modelNormal[1] = new Vec3(norm2_x,norm2_y,norm2_z);
		this.rotatedVertex[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedVertex[1] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[0] = new Vec3(0.0D, 0.0D, 0.0D);
		this.rotatedNormal[1] = new Vec3(0.0D, 0.0D, 0.0D);
	}
	
	/**
	 * make obb from aabb
	 * @param aabbCopy
	 */
	public OBBCollider(AABB aabbCopy) {
		super(null, null);
		this.modelVertex = null;
		this.modelNormal = null;
		double xSize = (aabbCopy.maxX - aabbCopy.minX) / 2;
		double ySize = (aabbCopy.maxY - aabbCopy.minY) / 2;
		double zSize = (aabbCopy.maxZ - aabbCopy.minZ) / 2;
		this.worldCenter = new Vec3(-((float)aabbCopy.minX + xSize), (float)aabbCopy.minY + ySize, -((float)aabbCopy.minZ + zSize));
		this.rotatedVertex = new Vec3[4];
		this.rotatedNormal = new Vec3[3];
		this.rotatedVertex[0] = new Vec3(-xSize, ySize, -zSize);
		this.rotatedVertex[1] = new Vec3(-xSize, ySize, zSize);
		this.rotatedVertex[2] = new Vec3(xSize, ySize, zSize);
		this.rotatedVertex[3] = new Vec3(xSize, ySize, -zSize);
		this.rotatedNormal[0] = new Vec3(1, 0, 0);
		this.rotatedNormal[1] = new Vec3(0, 1, 0);
		this.rotatedNormal[2] = new Vec3(0, 0, 1);
	}
	
	/**
	 * Transform every elements of this Bounding Box
	 **/
	@Override
	public void transform(OpenMatrix4f modelMatrix) {
		OpenMatrix4f noTranslation = modelMatrix.removeTranslation();
		
		for (int i = 0; i < this.modelVertex.length; i++) {
			this.rotatedVertex[i] = OpenMatrix4f.transform(noTranslation, this.modelVertex[i]);
		}
		
		for (int i = 0; i < this.modelNormal.length; i++) {
			this.rotatedNormal[i] = OpenMatrix4f.transform(noTranslation, this.modelNormal[i]);
		}
		
		this.scale = noTranslation.toScaleVector();
		
		super.transform(modelMatrix);
	}
	
	@Override
	protected AABB getHitboxAABB() {
		return this.outerAABB.inflate((this.outerAABB.maxX - this.outerAABB.minX) * this.scale.x,
				(this.outerAABB.maxY - this.outerAABB.minY) * this.scale.y,
				(this.outerAABB.maxZ - this.outerAABB.minZ) * this.scale.z).move(-this.worldCenter.x, this.worldCenter.y, -this.worldCenter.z);
	}
	
	public boolean isCollide(OBBCollider opponent) {
		Vec3 toOpponent = opponent.worldCenter.subtract(this.worldCenter);
		
		for (Vec3 seperateAxis : this.rotatedNormal) {
			if (!collisionDetection(seperateAxis, toOpponent, this, opponent)) {
				return false;
			}
		}
		
		for (Vec3 seperateAxis : opponent.rotatedNormal) {
			if (!collisionDetection(seperateAxis, toOpponent, this, opponent)) {
				return false;
			}
		}
		
		/** Below codes detect if the line of each obb collides but it has disabled for better performance
		for(Vector3f norm1 : this.rotatedNormal)
		{
			for(Vector3f norm2 : opponent.rotatedNormal)
			{
				Vector3f seperateAxis = Vector3f.cross(norm1, norm2, null);
				
				if(seperateAxis.x + seperateAxis.y + seperateAxis.z == 0)
				{
					continue;
				}
				
				if(!collisionLogic(seperateAxis, toOpponent, this, opponent))
				{
					return false;
				}
			}
		}**/
		
		return true;
	}
	
	@Override
	public boolean isCollide(Entity entity) {
		OBBCollider obb = new OBBCollider(entity.getBoundingBox());
		return isCollide(obb);
	}
	
	@Override
	public OBBCollider deepCopy() {
		Vec3 xyzVec = this.modelVertex[1];
		return new OBBCollider(xyzVec.x, xyzVec.y, xyzVec.z, this.modelCenter.x, this.modelCenter.y, this.modelCenter.z);
	}
	
	private static boolean collisionDetection(Vec3 seperateAxis, Vec3 toOpponent, OBBCollider box1, OBBCollider box2) {
		Vec3 maxProj1 = null, maxProj2 = null, distance;
		double maxDot1 = -1, maxDot2 = -1;
		distance = seperateAxis.dot(toOpponent) > 0.0F ? toOpponent : toOpponent.scale(-1.0D);
		
		for (Vec3 vertexVector : box1.rotatedVertex) {
			Vec3 temp = seperateAxis.dot(vertexVector) > 0.0F ? vertexVector : vertexVector.scale(-1.0D);
			double dot = seperateAxis.dot(temp);
			
			if (dot > maxDot1) {
				maxDot1 = dot;
				maxProj1 = temp;
			}
		}
		
		for (Vec3 vertexVector : box2.rotatedVertex) {
			Vec3 temp = seperateAxis.dot(vertexVector) > 0.0F ? vertexVector : vertexVector.scale(-1.0D);
			double dot = seperateAxis.dot(temp);
			
			if (dot > maxDot2) {
				maxDot2 = dot;
				maxProj2 = temp;
			}
		}
		
		if (MathUtils.projectVector(distance, seperateAxis).length() > MathUtils.projectVector(maxProj1, seperateAxis).length() + MathUtils.projectVector(maxProj2, seperateAxis).length()) {
			return false;
		}
		
		return true;
	}
	
	@Override
	public String toString() {
		return super.toString() + " worldCenter : " + this.worldCenter + " direction : " + this.rotatedVertex[0];
	}
	
	@OnlyIn(Dist.CLIENT) @Override
	public void drawInternal(PoseStack matrixStackIn, MultiBufferSource buffer, OpenMatrix4f pose, boolean red) {
		VertexConsumer vertexBuilder = buffer.getBuffer(RenderType.lineStrip());
		OpenMatrix4f transpose = new OpenMatrix4f();
		OpenMatrix4f.transpose(pose, transpose);
		matrixStackIn.pushPose();
		MathUtils.translateStack(matrixStackIn, pose);
        MathUtils.rotateStack(matrixStackIn, transpose);
        Matrix4f matrix = matrixStackIn.last().pose();
        Vec3 vec = this.modelVertex[1];
        float maxX = (float)(this.modelCenter.x + vec.x);
        float maxY = (float)(this.modelCenter.y + vec.y);
        float maxZ = (float)(this.modelCenter.z + vec.z);
        float minX = (float)(this.modelCenter.x - vec.x);
        float minY = (float)(this.modelCenter.y - vec.y);
        float minZ = (float)(this.modelCenter.z - vec.z);
        float color = red ? 0.0F : 1.0F;
        vertexBuilder.vertex(matrix, maxX, maxY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, maxY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, maxY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, maxY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, minY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, minY, minZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, minY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, maxY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, minX, minY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        vertexBuilder.vertex(matrix, maxX, minY, maxZ).color(1.0F, color, color, 1.0F).normal(0.0F, 1.0F, 0.0F).endVertex();
        matrixStackIn.popPose();
	}
}