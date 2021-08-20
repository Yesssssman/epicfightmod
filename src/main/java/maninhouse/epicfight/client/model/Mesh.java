package maninhouse.epicfight.client.model;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Mesh {
	final float[] positionList;
	final float[] noramlList;
	final float[] textureList;
	final int[] jointIdList;
	final float[] weightList;
	final int[] indexList;
	final int[] weightCountList;
	final int vertexCount;
	final int indexCount;
	
	public Mesh(float[] positionList, float[] noramlList, float[] textureList, int[] jointIdList, float[] weightList,
			int[] indexList, int[] weightCountList, int vertexCount, int drawCount) {
		this.positionList = positionList;
		this.noramlList = noramlList;
		this.textureList = textureList;
		this.jointIdList = jointIdList;
		this.weightList = weightList;
		this.indexList = indexList;
		this.weightCountList = weightCountList;
		this.vertexCount = vertexCount;
		this.indexCount = drawCount;
	}
}