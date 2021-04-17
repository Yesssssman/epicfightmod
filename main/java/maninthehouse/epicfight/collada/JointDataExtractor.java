package maninthehouse.epicfight.collada;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maninthehouse.epicfight.animation.Joint;
import maninthehouse.epicfight.collada.xml.XmlNode;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;

public class JointDataExtractor
{
	private static final VisibleMatrix4f CORRECTION = new VisibleMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0, 0));
	
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
		VisibleMatrix4f jointTransform = convertStringToMatrix(matrixData);
		VisibleMatrix4f.mul(CORRECTION, jointTransform, jointTransform);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);//new Joint(name, index++, jointTransform);
		joints.put(joint.getId(), joint);
		
		return joint;
	}
	
	private Joint getJoint(XmlNode node)
	{
		jointNumber++;
		
		String name = node.getAttribute("sid");
		String[] matrixData = node.getChild("matrix").getData().split(" ");
		VisibleMatrix4f jointTransform = convertStringToMatrix(matrixData);
		Joint joint = new Joint(name, rawJointMap.get(name), jointTransform);//new Joint(name, index++, jointTransform);
		joints.put(joint.getId(), joint);
		
		return joint;
	}
	
	private VisibleMatrix4f convertStringToMatrix(String[] data)
	{
		float[] mat4 = new float[16];
		for(int i = 0; i < 16; i++)
		{
			mat4[i] = Float.parseFloat(data[i]);
		}
		FloatBuffer floatbuffer = FloatBuffer.allocate(16);
		floatbuffer.put(mat4);
		floatbuffer.flip();
		VisibleMatrix4f transform = new VisibleMatrix4f();
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