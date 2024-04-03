package yesman.epicfight.api.client.model;

import java.util.List;

import com.google.common.collect.Lists;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VertexIndicator {
	
	public static List<VertexIndicator> create(int[] drawingIndices) {
		List<VertexIndicator> vertexIndicators = Lists.newArrayList();
		
		for (int i = 0; i < drawingIndices.length / 3; i++) {
			int k = i * 3;
			int position = drawingIndices[k];
			int uv = drawingIndices[k + 1];
			int normal = drawingIndices[k + 2];
			VertexIndicator vi = new VertexIndicator(position, uv, normal);
			vertexIndicators.add(vi);
		}
		
		return vertexIndicators;
	}
	
	@SuppressWarnings("unchecked")
	public static List<AnimatedVertexIndicator> createAnimated(int[] drawingIndices, int[] vCounts, int[] animationIndices) {
		List<AnimatedVertexIndicator> vertexIndicators = Lists.newArrayList();
		IntList[] listedAnimationData = new IntList[vCounts.length];
		int indexPointer = 0;
		
		for (int i = 0; i < vCounts.length; i++) {
			int count = vCounts[i];
			IntList list = new IntArrayList();
			
			for (int j = 0; j < count; j++) {
				list.add(animationIndices[indexPointer * 2]);
				list.add(animationIndices[indexPointer * 2 + 1]);
				indexPointer++;
			}
			
			listedAnimationData[i] = list;
		}
		
		for (int i = 0; i < drawingIndices.length / 3; i++) {
			int k = i * 3;
			int position = drawingIndices[k];
			int uv = drawingIndices[k + 1];
			int normal = drawingIndices[k + 2];
			AnimatedVertexIndicator vi = new AnimatedVertexIndicator(position, uv, normal);
			IntList list = listedAnimationData[position];
			
			for (int j = 0; j < list.size() / 2; j++) {
				vi.addAnimationData(list.getInt(j * 2), list.getInt(j * 2 + 1));
			}
			
			vertexIndicators.add(vi);
		}
		
		return vertexIndicators;
	}
	
	public final int position;
	public final int uv;
	public final int normal;
	
	public VertexIndicator(int position, int uv, int normal) {
		this.position = position;
		this.uv = uv;
		this.normal = normal;
	}
	
	public static class AnimatedVertexIndicator extends VertexIndicator {
		public final IntList joint;
		public final IntList weight;
		
		public AnimatedVertexIndicator(int position, int uv, int normal) {
			super(position, uv, normal);
			this.joint = new IntArrayList();
			this.weight = new IntArrayList();
		}
		
		public void addAnimationData(int jointId, int weight) {
			this.joint.add(jointId);
			this.weight.add(weight);
		}
	}
}