package yesman.epicfight.api.client.model;

import java.util.List;

import org.joml.Vector3i;

import com.google.common.collect.Lists;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlenderVertexBuilder {
	public static List<BlenderVertexBuilder> createVertexIndicator(int[] drawingIndices) {
		List<BlenderVertexBuilder> vertexIndicators = Lists.newArrayList();
		
		for (int i = 0; i < drawingIndices.length / 3; i++) {
			int k = i * 3;
			int position = drawingIndices[k];
			int uv = drawingIndices[k + 1];
			int normal = drawingIndices[k + 2];
			BlenderVertexBuilder vi = new BlenderVertexBuilder(position, uv, normal);
			vertexIndicators.add(vi);
		}
		
		return vertexIndicators;
	}
	
	public static List<BlenderAnimatedVertexBuilder> createAnimated(int[] drawingIndices, int[] affectingJointCount, int[] animationIndices) {
		List<BlenderAnimatedVertexBuilder> vertexIndicators = Lists.newArrayList();
		Vector3i[] aJointId = new Vector3i[affectingJointCount.length];
		Vector3i[] aWeights = new Vector3i[affectingJointCount.length];
		int[] counts = new int[affectingJointCount.length];
		int indexPointer = 0;
		
		for (int i = 0; i < affectingJointCount.length; i++) {
			int count = affectingJointCount[i];
			Vector3i joinId = new Vector3i();
			Vector3i weights = new Vector3i();
			
			for (int j = 0; j < count; j++) {
				switch (j) {
				case 0 -> {
					joinId.x = animationIndices[indexPointer * 2];
					weights.x = animationIndices[indexPointer * 2 + 1];
				}
				case 1 -> {
					joinId.y = animationIndices[indexPointer * 2];
					weights.y = animationIndices[indexPointer * 2 + 1];
				}
				case 2 -> {
					joinId.z = animationIndices[indexPointer * 2];
					weights.z = animationIndices[indexPointer * 2 + 1];
				}
				}
				
				indexPointer++;
			}
			
			counts[i] = count;
			aJointId[i] = joinId;
			aWeights[i] = weights;
		}
		
		for (int i = 0; i < drawingIndices.length / 3; i++) {
			int k = i * 3;
			int position = drawingIndices[k];
			int uv = drawingIndices[k + 1];
			int normal = drawingIndices[k + 2];
			BlenderAnimatedVertexBuilder vi = new BlenderAnimatedVertexBuilder(position, uv, normal, aJointId[position], aWeights[position], counts[i]);
			vertexIndicators.add(vi);
		}
		
		return vertexIndicators;
	}
	
	public final int position;
	public final int uv;
	public final int normal;
	
	public BlenderVertexBuilder(int position, int uv, int normal) {
		this.position = position;
		this.uv = uv;
		this.normal = normal;
	}
}