package yesman.epicfight.collada;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import yesman.epicfight.animation.Joint;
import yesman.epicfight.collada.xml.XmlNode;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class JointDataExtractor {
	private static final OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0, 0));
	private int jointNumber = 1;
	private XmlNode skeleton;
	private Map<String, Integer> rawJointMap;
	private Map<String, Joint> joints = Maps.newHashMap();
	
	public JointDataExtractor(XmlNode skeleton, Map<String, Integer> rawJointMap) {
		this.skeleton = skeleton;
		this.rawJointMap = rawJointMap;
	}

	public Joint extractSkeletonData() {
		XmlNode rootNode = this.skeleton.getChild("node");
		Joint root = getRootJoint(rootNode);
		bindJointData(root, rootNode.getChildren("node"));

		return root;
	}

	private void bindJointData(Joint root, List<XmlNode> nodes) {
		for (XmlNode node : nodes) {
			Joint joint = getJoint(node);
			root.addSubJoint(joint);
			bindJointData(joint, node.getChildren("node"));
		}
	}

	private Joint getRootJoint(XmlNode node) {
		String name = node.getAttribute("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		OpenMatrix4f jointTransform = convertStringToMatrix(matrixData);
		OpenMatrix4f.mul(CORRECTION, jointTransform, jointTransform);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);//new Joint(name, index++, jointTransform);
		this.joints.put(joint.getName(), joint);
		
		return joint;
	}
	
	private Joint getJoint(XmlNode node) {
		this.jointNumber++;
		
		String name = node.getAttribute("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		OpenMatrix4f jointTransform = convertStringToMatrix(matrixData);
		Joint joint = new Joint(name, this.rawJointMap.get(name), jointTransform);//new Joint(name, index++, jointTransform);
		this.joints.put(joint.getName(), joint);
		
		return joint;
	}
	
	private OpenMatrix4f convertStringToMatrix(String[] data) {
		float[] mat4 = new float[16];
		for (int i = 0; i < 16; i++) {
			mat4[i] = Float.parseFloat(data[i]);
		}
		FloatBuffer floatbuffer = FloatBuffer.allocate(16);
		floatbuffer.put(mat4);
		floatbuffer.flip();
		OpenMatrix4f transform = new OpenMatrix4f();
		transform.load(floatbuffer);
		transform.transpose();
		return transform;
	}

	public Map<String, Joint> getJointMap() {
		return this.joints;
	}

	public int getJointNumber() {
		return this.jointNumber;
	}
}