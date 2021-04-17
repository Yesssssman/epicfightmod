package maninthehouse.epicfight.client.model;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import maninthehouse.epicfight.collada.ColladaModelLoader;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientModel extends Model {
	private Mesh mesh;

	public ClientModel(ResourceLocation location) {
		super(location);
	}
	
	public ClientModel(Mesh mesh) {
		super(null);
		this.mesh = mesh;
	}
	
	public void loadMeshData() {
		try {
			this.mesh = ColladaModelLoader.getMeshData(location);
		} catch (IOException e) {
			System.err.println(location.getResourcePath() + " failed to load!");
		}
	}

	public void draw(VisibleMatrix4f[] poses) {
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
					VisibleMatrix4f pose = poses[this.mesh.jointIdList[weightIndex++]];
					Vec4f.add(VisibleMatrix4f.transform(pose, pos, null).scale(weight), totalPos, totalPos);
					Vec4f.add(VisibleMatrix4f.transform(pose, normal, null).scale(weight), totalNormal, totalNormal);
				}
			}
			
			totalNormal.normalise();
			animatedPosition[k] = totalPos.x;
			animatedPosition[k + 1] = totalPos.y;
			animatedPosition[k + 2] = totalPos.z;
			animatedNormal[k] = totalNormal.x;
			animatedNormal[k + 1] = totalNormal.y;
			animatedNormal[k + 2] = totalNormal.z;
		}
		
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		
		bufferbuilder.begin(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
		
		for(int i = 0; i < this.mesh.indexCount; i++) {
			int index = this.mesh.indexList[i];
			int im2 = index * 2;
			int im3 = index * 3;
			
			bufferbuilder.pos(animatedPosition[im3], animatedPosition[im3 + 1], animatedPosition[im3 + 2])
				.tex(this.mesh.textureList[im2], this.mesh.textureList[im2 + 1])
				.normal(animatedNormal[im3], animatedNormal[im3 + 1],animatedNormal[im3 + 2])
				.endVertex();
		}
		
		tessellator.draw();
	}
}