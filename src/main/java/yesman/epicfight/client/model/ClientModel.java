package yesman.epicfight.client.model;

import java.io.IOException;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.collada.ColladaModelLoader;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec4f;

@OnlyIn(Dist.CLIENT)
public class ClientModel extends Model {
	private Mesh mesh;

	public ClientModel(ResourceLocation location) {
		super(location);
	}
	
	public ClientModel(Mesh mesh) {
		super(null);
		this.mesh = mesh;
	}
	
	public void loadMeshData(IResourceManager resourceManager) {
		try {
			this.mesh = ColladaModelLoader.getMeshData(resourceManager, this.location);
		} catch (IOException e) {
			System.err.println(location.getNamespace() + " failed to load!");
		}
	}

	public void draw(MatrixStack matrixStackIn, IVertexBuilder builderIn, int packedLightIn, float r, float g, float b, float a, OpenMatrix4f[] poses) {
		float[] animatedPosition = this.mesh.positionList.clone();
		float[] animatedNormal = this.mesh.noramlList.clone();		
		int weightIndex = 0;
		
		for(int i = 0; i < this.mesh.vertexCount; i++) {
			int k = i * 3;
			Vec4f totalPos = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vec4f totalNormal = new Vec4f(0.0F, 0.0F, 0.0F, 0.0F);
			Vec4f pos = new Vec4f(animatedPosition[k], animatedPosition[k + 1], animatedPosition[k + 2], 1.0F);
			Vec4f normal = new Vec4f(animatedNormal[k], animatedNormal[k + 1], animatedNormal[k + 2], 1.0F);
			
			for(int j = 0; j < this.mesh.weightCountList[i]; j++) {
				if(weightIndex < this.mesh.weightList.length) {
					float weight = this.mesh.weightList[weightIndex];
					OpenMatrix4f pose = poses[this.mesh.jointIdList[weightIndex++]];
					Vec4f.add(OpenMatrix4f.transform(pose, pos, null).scale(weight), totalPos, totalPos);
					Vec4f.add(OpenMatrix4f.transform(pose, normal, null).scale(weight), totalNormal, totalNormal);
				}
			}
			
			animatedPosition[k] = totalPos.x;
			animatedPosition[k + 1] = totalPos.y;
			animatedPosition[k + 2] = totalPos.z;
			animatedNormal[k] = totalNormal.x;
			animatedNormal[k + 1] = totalNormal.y;
			animatedNormal[k + 2] = totalNormal.z;
		}
		
		Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
		Matrix3f matrix3f = matrixStackIn.getLast().getNormal();
		
		for(int i = 0; i < this.mesh.indexCount; i++) {
			int index = this.mesh.indexList[i];
			int im2 = index * 2;
			int im3 = index * 3;
			Vector4f position = new Vector4f(animatedPosition[im3], animatedPosition[im3 + 1], animatedPosition[im3 + 2], 1.0F);
			Vector3f normal = new Vector3f(animatedNormal[im3], animatedNormal[im3 + 1], animatedNormal[im3 + 2]);
			position.transform(matrix4f);
			normal.transform(matrix3f);
			
			builderIn.addVertex(position.getX(), position.getY(), position.getZ(), r, g, b, a, this.mesh.textureList[im2], this.mesh.textureList[im2 + 1],
					OverlayTexture.NO_OVERLAY, packedLightIn, normal.getX(), normal.getY(), normal.getZ());
		}
	}
}