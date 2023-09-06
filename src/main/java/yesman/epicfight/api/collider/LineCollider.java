package yesman.epicfight.api.collider;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;

public class LineCollider extends Collider {
	protected Vec3 modelVec;
	protected Vec3 worldVec;
	
	public LineCollider(double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		this(getInitialAABB(posX, posY, posZ, vecX, vecY, vecZ), posX, posY, posZ, vecX, vecY, vecZ);
	}
	
	protected LineCollider(AABB outerAABB, double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		super(new Vec3(posX, posY, posZ), outerAABB);
		this.modelVec = new Vec3(vecX, vecY, vecZ);
		this.worldVec = new Vec3(0.0D, 0.0D, 0.0D);
	}
	
	static AABB getInitialAABB(double posX, double posY, double posZ, double vecX, double vecY, double vecZ) {
		Vec3 start = new Vec3(posX, posY, posZ);
		Vec3 end = new Vec3(vecX + posX, vecY + posY, vecZ + posZ);
		double length = Math.max(start.length(), end.length());
		return new AABB(length, length, length, -length, -length, -length);
	}
	
	@Override
	public void transform(OpenMatrix4f mat) {
		this.worldVec = OpenMatrix4f.transform(mat.removeTranslation(), this.modelVec);
		super.transform(mat);
	}
	
	@Override
	public boolean isCollide(Entity entity) {
		AABB opponent = entity.getBoundingBox();
		double maxStart;
		double minEnd;
		double startX;
		double startY;
		double startZ;
		double endX;
		double endY;
		double endZ;
		
		if (this.worldVec.x == 0) {
			if (this.worldCenter.x < opponent.minX || this.worldCenter.x > opponent.maxX) {
				return false;
			}
		}
		
		startX = Mth.clamp((opponent.minX + this.worldCenter.x) / -this.worldVec.x, 0, 1);
		endX = Mth.clamp((opponent.maxX + this.worldCenter.x) / -this.worldVec.x, 0, 1);

		if (startX > endX) {
			double temp = startX;
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
		
		startY = Mth.clamp((float)(opponent.minY - this.worldCenter.y) / this.worldVec.y, 0, 1);
		endY = Mth.clamp((float)(opponent.maxY - this.worldCenter.y) / this.worldVec.y, 0, 1);
		
		if (startY > endY) {
			double temp = startY;
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
		
		startZ = Mth.clamp((float)(opponent.minZ + this.worldCenter.z) / -this.worldVec.z, 0, 1);
		endZ = Mth.clamp((float)(opponent.maxZ + this.worldCenter.z) / -this.worldVec.z, 0, 1);
		
		if (startZ > endZ) {
			double temp = startZ;
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
	public LineCollider deepCopy() {
		return new LineCollider(this.modelCenter.x, this.modelCenter.y, this.modelCenter.z, this.modelVec.x, this.modelVec.y, this.modelVec.z);
	}
	
	@Override
	public void drawInternal(PoseStack matrixStackIn, MultiBufferSource buffer, OpenMatrix4f pose, boolean red) {
		VertexConsumer vertexBuilder = buffer.getBuffer(EpicFightRenderTypes.debugCollider());
		OpenMatrix4f transpose = new OpenMatrix4f();
		OpenMatrix4f.transpose(pose, transpose);
		MathUtils.translateStack(matrixStackIn, pose);
        MathUtils.rotateStack(matrixStackIn, transpose);
        Matrix4f matrix = matrixStackIn.last().pose();
        float startX = (float)this.modelCenter.x;
        float startY = (float)this.modelCenter.y;
        float startZ = (float)this.modelCenter.z;
        float endX = (float)(this.modelCenter.x + this.modelVec.x);
        float endY = (float)(this.modelCenter.y + this.modelVec.y);
        float endZ = (float)(this.modelCenter.z + this.modelVec.z);
        float color = red ? 0.0F : 1.0F;
        vertexBuilder.vertex(matrix, startX, startY, startZ).color(1.0F, color, color, 1.0F).endVertex();
        vertexBuilder.vertex(matrix, endX, endY, endZ).color(1.0F, color, color, 1.0F).endVertex();
	}
}