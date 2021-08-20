package maninhouse.epicfight.collada;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

import maninhouse.epicfight.client.model.Mesh;
import maninhouse.epicfight.utils.math.Vec2f;
import maninhouse.epicfight.utils.math.Vec3f;

public class VertexData {
	private Vec3f position;
	private Vec3f normal;
	private Vec2f textureCoordinate;
	private Vec3f effectiveJointIDs;
	private Vec3f effectiveJointWeights;
	private int effectiveJointNumber;
	
	public VertexData() {
		this.position = null;
		this.normal = null;
		this.textureCoordinate = null;
	}

	public VertexData(VertexData vertex) {
		this.position = vertex.position;
		this.effectiveJointIDs = vertex.effectiveJointIDs;
		this.effectiveJointWeights = vertex.effectiveJointWeights;
		this.effectiveJointNumber = vertex.effectiveJointNumber;
	}

	public VertexData setPosition(Vec3f position) {
		this.position = position;
		return this;
	}

	public VertexData setNormal(Vec3f vector) {
		this.normal = vector;
		return this;
	}

	public VertexData setTextureCoordinate(Vec2f vector) {
		this.textureCoordinate = vector;
		return this;
	}

	public VertexData setEffectiveJointIDs(Vec3f effectiveJointIDs) {
		this.effectiveJointIDs = effectiveJointIDs;
		return this;
	}

	public VertexData setEffectiveJointWeights(Vec3f effectiveJointWeights) {
		this.effectiveJointWeights = effectiveJointWeights;
		return this;
	}
	
	public VertexData setEffectiveJointNumber(int count) {
		this.effectiveJointNumber = count;
		return this;
	}
	
	public State compareTextureCoordinateAndNormal(Vec3f normal, Vec2f textureCoord) {
		if (textureCoordinate == null) {
			return State.EMPTY;
		} else if (textureCoordinate.equals(textureCoord) && this.normal.equals(normal)) {
			return State.EQUAL;
		} else {
			return State.DIFFERENT;
		}
	}
	
	public static Mesh loadVertexInformation(List<VertexData> vertices, int[] indices, boolean animated) {
		List<Float> positions = Lists.<Float>newArrayList();
		List<Float> normals = Lists.<Float>newArrayList();
		List<Float> texCoords = Lists.<Float>newArrayList();
		List<Integer> jointIndices = Lists.<Integer>newArrayList();
		List<Float> jointWeights = Lists.<Float>newArrayList();
		List<Integer> effectJointCount = Lists.<Integer>newArrayList();
		
		for (int i = 0; i < vertices.size(); i++) {
			VertexData vertex = vertices.get(i);
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

			if (animated) {
				Vec3f effectIDs = vertex.effectiveJointIDs;
				Vec3f weights = vertex.effectiveJointWeights;
				int count = Math.min(vertex.effectiveJointNumber, 3);
				effectJointCount.add(count);
				for(int j = 0; j < count; j++) {
					switch(j) {
					case 0:
						jointIndices.add((int) effectIDs.x);
						jointWeights.add(weights.x);
						break;
					case 1:
						jointIndices.add((int) effectIDs.y);
						jointWeights.add(weights.y);
						break;
					case 2:
						jointIndices.add((int) effectIDs.z);
						jointWeights.add(weights.z);
						break;
					default:
					}
				}
			}
		}
		
		float[] positionList = ArrayUtils.toPrimitive(positions.toArray(new Float[0]));
		float[] normalList = ArrayUtils.toPrimitive(normals.toArray(new Float[0]));
		float[] texCoordList = ArrayUtils.toPrimitive(texCoords.toArray(new Float[0]));
		int[] jointIndexList = ArrayUtils.toPrimitive(jointIndices.toArray(new Integer[0]));
		float[] jointWeightList = ArrayUtils.toPrimitive(jointWeights.toArray(new Float[0]));
		int[] jointCountList = ArrayUtils.toPrimitive(effectJointCount.toArray(new Integer[0]));
		
		return new Mesh(positionList, normalList, texCoordList, jointIndexList, jointWeightList, indices, jointCountList,
				positionList.length / 3, indices.length);
	}

	public enum State {
		EMPTY, EQUAL, DIFFERENT;
	}
}