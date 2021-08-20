package maninhouse.epicfight.collada;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import maninhouse.epicfight.collada.xml.XmlNode;
import maninhouse.epicfight.utils.math.Vec3f;

public class SkinDataExtractor {
	private static final int MAX_JOINT_LIMIT = 3;
	private XmlNode skin;

	public SkinDataExtractor(XmlNode skin) {
		this.skin = skin;
	}

	public Map<String, Integer> getRawJoints() {
		Map<String, Integer> map = Maps.<String, Integer>newHashMap();
		String[] weightData = getJointLists(skin);

		for (int i = 0; i < weightData.length; i++) {
			map.put(weightData[i], i);
		}

		return map;
	}
	
	public void extractSkinData(List<VertexData> vertices) {
		String[] weightData = getWeights(skin);
		String[] effectiveJointNumber = getEffectiveJointNumber(skin);
		String[] indices = getIndices(skin);

		int currentIndice = 0;
		int currentVertex = 0;

		for (int i = 0; i < effectiveJointNumber.length; i++) {
			VertexData vertexData = vertices.get(currentVertex);
			Vec3f jointIndices = new Vec3f();
			Vec3f jointWeights = new Vec3f();
			int jointNumber = Integer.parseInt(effectiveJointNumber[i]);

			for (int j = 0; j < jointNumber; j++) {
				if (j < MAX_JOINT_LIMIT) {
					float index = Integer.parseInt(indices[currentIndice]);
					int weightIndex = Integer.parseInt(indices[currentIndice + 1]);
					float weight = Float.parseFloat(weightData[weightIndex]);

					switch (j) {
					case 0:
						jointIndices.x = index;
						jointWeights.x = weight;
						break;
					case 1:
						jointIndices.y = index;
						jointWeights.y = weight;
						break;
					case 2:
						jointIndices.z = index;
						jointWeights.z = weight;
						break;
					default:
						break;
					}
				}

				currentIndice += 2;
			}

			float total = jointWeights.x + jointWeights.y + jointWeights.z;
			float expandRatio = 1.0f / total;
			jointWeights.scale(expandRatio);

			vertexData.setEffectiveJointIDs(jointIndices);
			vertexData.setEffectiveJointWeights(jointWeights);
			vertexData.setEffectiveJointNumber(jointNumber);
			currentVertex++;
		}
	}

	private static String[] getWeights(XmlNode node) {
		String weightID = node.getChild("vertex_weights").getChildWithAttribute("input", "semantic", "WEIGHT").getAttribute("source").substring(1);
		XmlNode weightData = node.getChildWithAttribute("source", "id", weightID).getChild("float_array");

		return weightData.getData().split(" ");
	}

	private static String[] getEffectiveJointNumber(XmlNode node) {
		XmlNode vertexNumberData = node.getChild("vertex_weights").getChild("vcount");

		return vertexNumberData.getData().split(" ");
	}

	private static String[] getIndices(XmlNode node) {
		XmlNode vertexNumberData = node.getChild("vertex_weights").getChild("v");

		return vertexNumberData.getData().split(" ");
	}

	private static String[] getJointLists(XmlNode node) {
		XmlNode jointData = node.getChildWithAttribute("source", "id", "Armature_Cube-skin-joints").getChild("Name_array");

		return jointData.getData().split(" ");
	}
}
