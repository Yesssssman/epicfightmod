package maninhouse.epicfight.collada;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.collada.xml.XmlNode;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.OpenMatrix4f;

public class JointDataExtractor
{
	private static final OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0, 0));
	
	private int jointNumber = 1;
	
	private XmlNode skeleton;
	private Map<String, Integer> rawJointMap;
	private Map<Integer, Joint> joints = new HashMap<Integer, Joint> ();
	
	public JointDataExtractor(XmlNode skeleton, Map<String, Integer> rawJointMap)
	{
		this.skeleton = skeleton;
		this.rawJointMap = rawJointMap;
	}
	
	public Joint extractSkeletonData()
	{
		XmlNode rootNode = skeleton.getChild("node");
		Joint root = getRootJoint(rootNode);
		bindJointData(root, rootNode.getChildren("node"));
		
		return root;
	}
	
	private void bindJointData(Joint root, List<XmlNode> nodes)
	{
		for(XmlNode node : nodes)
		{
			Joint joint = getJoint(node);
			root.addSubJoint(joint);
			bindJointData(joint, node.getChildren("node"));
		}
	}
	
	private Joint getRootJoint(XmlNode node)
	{
		String name = node.getAttribute("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		OpenMatrix4f jointTransform = convertStringToMatrix(matrixData);
		OpenMatrix4f.mul(CORRECTION, jointTransform, jointTransform);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);//new Joint(name, index++, jointTransform);
		joints.put(joint.getId(), joint);
		
		return joint;
	}
	
	private Joint getJoint(XmlNode node)
	{
		jointNumber++;
		
		String name = node.getAttribute("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		OpenMatrix4f jointTransform = convertStringToMatrix(matrixData);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);//new Joint(name, index++, jointTransform);
		joints.put(joint.getId(), joint);
		
		return joint;
	}
	
	private OpenMatrix4f convertStringToMatrix(String[] data)
	{
		float[] mat4 = new float[16];
		for(int i = 0; i < 16; i++)
		{
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

	public Map<Integer, Joint> getJointTable()
	{
		return this.joints;
	}
	
	public int getJointNumber()
	{
		return jointNumber;
	}
}