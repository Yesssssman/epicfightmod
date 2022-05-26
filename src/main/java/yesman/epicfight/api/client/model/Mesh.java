package yesman.epicfight.api.client.model;

import java.util.List;
import java.util.Map;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.api.utils.math.Vec4f;

@OnlyIn(Dist.CLIENT)
public class Mesh {
	final float[] positions;
	final float[] uvs;
	final float[] noramls;
	final float[] weights;
	final List<VertexIndicator> vertexIndicators;
	
	public Mesh(float[] positions, float[] noramls, float[] uvs, int[] animationIndices, float[] weights, int[] drawingIndices, int[] vCounts) {
		this.positions = positions;
		this.noramls = noramls;
		this.uvs = uvs;
		this.weights = weights;
		this.vertexIndicators = VertexIndicator.create(drawingIndices, vCounts, animationIndices);
	}
	
	public JsonObject toJsonObject() {
		JsonObject root = new JsonObject();
		JsonObject vertices = new JsonObject();
		float[] positions = this.positions.clone();
		float[] normals = this.noramls.clone();
		
		OpenMatrix4f toBlenderCoord = OpenMatrix4f.createRotatorDeg(90.0F, Vec3f.X_AXIS);
		
		for (int i = 0; i < positions.length / 3; i++) {
			int k = i * 3;
			Vec4f posVector = new Vec4f(positions[k], positions[k+1], positions[k+2], 1.0F);
			OpenMatrix4f.transform(toBlenderCoord, posVector, posVector);
			positions[k] = posVector.x;
			positions[k+1] = posVector.y;
			positions[k+2] = posVector.z;
		}
		
		for (int i = 0; i < normals.length / 3; i++) {
			int k = i * 3;
			Vec4f normVector = new Vec4f(normals[k], normals[k+1], normals[k+2], 1.0F);
			OpenMatrix4f.transform(toBlenderCoord, normVector, normVector);
			normals[k] = normVector.x;
			normals[k+1] = normVector.y;
			normals[k+2] = normVector.z;
		}
		
		int count = this.vertexIndicators.size();
		int[] indices = new int[count * 3];
		int[] vcounts = new int[positions.length / 3];
		List<Integer> vIndexList = Lists.newArrayList();
		Map<Integer, VertexIndicator> positionMap = Maps.newHashMap();
		int[] vIndices;
		
		for (int i = 0; i < this.vertexIndicators.size(); i++) {
			VertexIndicator vertexIndicator = this.vertexIndicators.get(i);
			indices[i * 3] = vertexIndicator.position;
			indices[i * 3 + 1] = vertexIndicator.uv;
			indices[i * 3 + 2] = vertexIndicator.normal;
			vcounts[vertexIndicator.position] = vertexIndicator.joint.size();
			positionMap.put(vertexIndicator.position, vertexIndicator);
		}
		
		for (int i = 0; i < vcounts.length; i++) {
			for (int j = 0; j < vcounts[i]; j++) {
				VertexIndicator vi = positionMap.get(i);
				vIndexList.add(vi.joint.get(j));
				vIndexList.add(vi.weight.get(j));
			}
		}
		
		vIndices = vIndexList.stream().mapToInt(i -> i).toArray();
		vertices.add("positions", arrayToJsonObject(positions, 3));
		vertices.add("uvs", arrayToJsonObject(this.uvs, 2));
		vertices.add("normals", arrayToJsonObject(normals, 3));
		vertices.add("indices", arrayToJsonObject(indices, 3));
		vertices.add("vcounts", arrayToJsonObject(vcounts, 1));
		vertices.add("weights", arrayToJsonObject(this.weights, 1));
		vertices.add("vindices", arrayToJsonObject(vIndices, 1));
		root.add("vertices", vertices);
		
		return root;
	}
	
	public static JsonObject arrayToJsonObject(float[] array, int stride) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("stride", stride);
		jsonObject.addProperty("count", array.length / stride);
		JsonArray jsonArray = new JsonArray();
		
		for (float element : array) {
			jsonArray.add(element);
		}
		
		jsonObject.add("array", jsonArray);
		
		return jsonObject;
	}
	
	public static JsonObject arrayToJsonObject(int[] array, int stride) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("stride", stride);
		jsonObject.addProperty("count", array.length / stride);
		JsonArray jsonArray = new JsonArray();
		
		for (int element : array) {
			jsonArray.add(element);
		}
		
		jsonObject.add("array", jsonArray);
		
		return jsonObject;
	}
}