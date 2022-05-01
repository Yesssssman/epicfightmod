package yesman.epicfight.api.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec4f;

@OnlyIn(Dist.CLIENT)
public class ClientModel extends Model {
	private Mesh mesh;

	public ClientModel(ResourceLocation location) {
		super(location);
	}
	
	public ClientModel(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public void loadMeshData(ResourceManager resourceManager) {
		JsonModelLoader loader = new JsonModelLoader(resourceManager, this.location);
		this.mesh = loader.getMesh();
	}
	
	public void drawRawModel(PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord) {
		Matrix4f matrix4f = matrixStackIn.last().pose();
		Matrix3f matrix3f = matrixStackIn.last().normal();
		
		for (VertexIndicator vi : this.mesh.vertexIndicators) {
			int pos = vi.position * 3;
			int norm = vi.normal * 3;
			int uv = vi.uv * 2;
			Vector4f posVec = new Vector4f(this.mesh.positions[pos], this.mesh.positions[pos + 1], this.mesh.positions[pos + 2], 1.0F);
			Vector3f normVec = new Vector3f(this.mesh.noramls[norm], this.mesh.noramls[norm + 1], this.mesh.noramls[norm + 2]);
			posVec.transform(matrix4f);
			normVec.transform(matrix3f);
			builder.vertex(posVec.x(), posVec.y(), posVec.z(), r, g, b, a, this.mesh.uvs[uv], this.mesh.uvs[uv + 1], overlayCoord, packedLightIn, normVec.x(), normVec.y(), normVec.z());
		}
	}
	
	public void drawAnimatedModel(PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
		Matrix4f matrix4f = matrixStackIn.last().pose();
		Matrix3f matrix3f = matrixStackIn.last().normal();
		OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
		
		for (int i = 0; i < poses.length; i++) {
			posesNoTranslation[i] = poses[i].removeTranslation();
		}
		
		for (VertexIndicator vi : this.mesh.vertexIndicators) {
			int pos = vi.position * 3;
			int norm = vi.normal * 3;
			int uv = vi.uv * 2;
			Vec4f position = new Vec4f(this.mesh.positions[pos], this.mesh.positions[pos + 1], this.mesh.positions[pos + 2], 1.0F);
			Vec4f normal = new Vec4f(this.mesh.noramls[norm], this.mesh.noramls[norm + 1], this.mesh.noramls[norm + 2], 1.0F);
			Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			
			for (int i = 0; i < vi.joint.size(); i++) {
				int jointIndex = vi.joint.get(i);
				int weightIndex = vi.weight.get(i);
				float weight = this.mesh.weights[weightIndex];
				Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
				Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
			}
			
			Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
			Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
			posVec.transform(matrix4f);
			normVec.transform(matrix3f);
			builder.vertex(posVec.x(), posVec.y(), posVec.z(), r, g, b, a, this.mesh.uvs[uv], this.mesh.uvs[uv + 1], overlayCoord, packedLightIn, normVec.x(), normVec.y(), normVec.z());
		}
	}
	
	public void drawAnimatedModelNoTexture(PoseStack matrixStackIn, VertexConsumer builder, int packedLightIn, float r, float g, float b, float a, int overlayCoord, OpenMatrix4f[] poses) {
		Matrix4f matrix4f = matrixStackIn.last().pose();
		Matrix3f matrix3f = matrixStackIn.last().normal();
		OpenMatrix4f[] posesNoTranslation = new OpenMatrix4f[poses.length];
		
		for (int i = 0; i < poses.length; i++) {
			posesNoTranslation[i] = poses[i].removeTranslation();
		}
		
		for (VertexIndicator vi : this.mesh.vertexIndicators) {
			int pos = vi.position * 3;
			int norm = vi.normal * 3;
			Vec4f position = new Vec4f(this.mesh.positions[pos], this.mesh.positions[pos + 1], this.mesh.positions[pos + 2], 1.0F);
			Vec4f normal = new Vec4f(this.mesh.noramls[norm], this.mesh.noramls[norm + 1], this.mesh.noramls[norm + 2], 1.0F);
			Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vec4f totalNorm = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			
			for (int i = 0; i < vi.joint.size(); i++) {
				int jointIndex = vi.joint.get(i);
				int weightIndex = vi.weight.get(i);
				float weight = this.mesh.weights[weightIndex];
				Vec4f.add(OpenMatrix4f.transform(poses[jointIndex], position, null).scale(weight), totalPos, totalPos);
				Vec4f.add(OpenMatrix4f.transform(posesNoTranslation[jointIndex], normal, null).scale(weight), totalNorm, totalNorm);
			}
			
			Vector4f posVec = new Vector4f(totalPos.x, totalPos.y, totalPos.z, 1.0F);
			Vector3f normVec = new Vector3f(totalNorm.x, totalNorm.y, totalNorm.z);
			posVec.transform(matrix4f);
			normVec.transform(matrix3f);
			builder.vertex(posVec.x(), posVec.y(), posVec.z());
			builder.color(r, g, b, a);
			builder.uv2(packedLightIn);
			builder.endVertex();
		}
	}
}