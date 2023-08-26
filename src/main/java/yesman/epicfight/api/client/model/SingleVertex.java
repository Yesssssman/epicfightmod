package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Mesh.RenderProperties;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class SingleVertex {
	private Vec3f position;
	private Vec3f normal;
	private Vec2f textureCoordinate;
	private Vec3f effectiveJointIDs;
	private Vec3f effectiveJointWeights;
	private int effectiveJointNumber;
	
	public SingleVertex() {
		this.position = null;
		this.normal = null;
		this.textureCoordinate = null;
	}
	
	public SingleVertex(SingleVertex vertex) {
		this.position = vertex.position;
		this.effectiveJointIDs = vertex.effectiveJointIDs;
		this.effectiveJointWeights = vertex.effectiveJointWeights;
		this.effectiveJointNumber = vertex.effectiveJointNumber;
	}
	
	public SingleVertex setPosition(Vec3f position) {
		this.position = position;
		return this;
	}
	
	public SingleVertex setNormal(Vec3f vector) {
		this.normal = vector;
		return this;
	}
	
	public SingleVertex setTextureCoordinate(Vec2f vector) {
		this.textureCoordinate = vector;
		return this;
	}
	
	public SingleVertex setEffectiveJointIDs(Vec3f effectiveJointIDs) {
		this.effectiveJointIDs = effectiveJointIDs;
		return this;
	}
	
	public SingleVertex setEffectiveJointWeights(Vec3f effectiveJointWeights) {
		this.effectiveJointWeights = effectiveJointWeights;
		return this;
	}
	
	public SingleVertex setEffectiveJointNumber(int count) {
		this.effectiveJointNumber = count;
		return this;
	}
	
	public State compareTextureCoordinateAndNormal(Vec3f normal, Vec2f textureCoord) {
		if (this.textureCoordinate == null) {
			return State.EMPTY;
		} else if (this.textureCoordinate.equals(textureCoord) && this.normal.equals(normal)) {
			return State.EQUAL;
		} else {
			return State.DIFFERENT;
		}
	}
	
	public static AnimatedMesh loadVertexInformation(List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
		List<Float> positions = Lists.<Float>newArrayList();
		List<Float> normals = Lists.<Float>newArrayList();
		List<Float> texCoords = Lists.<Float>newArrayList();
		List<Integer> animationIndices = Lists.<Integer>newArrayList();
		List<Float> jointWeights = Lists.<Float>newArrayList();
		List<Integer> affectCountList = Lists.<Integer>newArrayList();
		
		for (int i = 0; i < vertices.size(); i++) {
			SingleVertex vertex = vertices.get(i);
			Vec3f position = vertex.position;
			Vec3f normal = vertex.normal;
			Vec2f texCoord = vertex.textureCoordinate;
			positions.add(position.x);
			positions.add(position.y);
			positions.add(position.z);
			normals.add(normal.x);
			normals.add(normal.y);
			normals.add(normal.z);
			texCoords.add(texCoord.x);
			texCoords.add(texCoord.y);
			
			Vec3f effectIDs = vertex.effectiveJointIDs;
			Vec3f weights = vertex.effectiveJointWeights;
			int count = Math.min(vertex.effectiveJointNumber, 3);
			affectCountList.add(count);
			
			for (int j = 0; j < count; j++) {
				switch (j) {
				case 0:
					animationIndices.add((int) effectIDs.x);
					jointWeights.add(weights.x);
					animationIndices.add(jointWeights.size() - 1);
					break;
				case 1:
					animationIndices.add((int) effectIDs.y);
					jointWeights.add(weights.y);
					animationIndices.add(jointWeights.size() - 1);
					break;
				case 2:
					animationIndices.add((int) effectIDs.z);
					jointWeights.add(weights.z);
					animationIndices.add(jointWeights.size() - 1);
					break;
				default:
				}
			}
		}
		
		float[] positionList = ArrayUtils.toPrimitive(positions.toArray(new Float[0]));
		float[] normalList = ArrayUtils.toPrimitive(normals.toArray(new Float[0]));
		float[] texCoordList = ArrayUtils.toPrimitive(texCoords.toArray(new Float[0]));
		int[] animationIndexList = ArrayUtils.toPrimitive(animationIndices.toArray(new Integer[0]));
		float[] jointWeightList = ArrayUtils.toPrimitive(jointWeights.toArray(new Float[0]));
		int[] affectJointCounts = ArrayUtils.toPrimitive(affectCountList.toArray(new Integer[0]));
		Map<String, float[]> arrayMap = Maps.newHashMap();
		Map<String, ModelPart<AnimatedVertexIndicator>> meshMap = Maps.newHashMap();
		
		arrayMap.put("positions", positionList);
		arrayMap.put("normals", normalList);
		arrayMap.put("uvs", texCoordList);
		arrayMap.put("weights", jointWeightList);
		
		for (Map.Entry<String, List<Integer>> e : indices.entrySet()) {
			meshMap.put(e.getKey(), new ModelPart<AnimatedVertexIndicator>(VertexIndicator.createAnimated(ArrayUtils.toPrimitive(e.getValue().toArray(new Integer[0])), affectJointCounts, animationIndexList)));
		}
		
		return new AnimatedMesh(arrayMap, null, RenderProperties.DEFAULT, meshMap);
	}
	
	public enum State {
		EMPTY, EQUAL, DIFFERENT;
	}
}