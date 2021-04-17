package maninthehouse.epicfight.collada;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import maninthehouse.epicfight.animation.Joint;
import maninthehouse.epicfight.animation.JointKeyframe;
import maninthehouse.epicfight.animation.JointTransform;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.animation.Quaternion;
import maninthehouse.epicfight.animation.TransformSheet;
import maninthehouse.epicfight.animation.types.MixLinkAnimation;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.collada.xml.XmlNode;
import maninthehouse.epicfight.collada.xml.XmlParser;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.util.ResourceLocation;

public class AnimationDataExtractor
{
	private static final VisibleMatrix4f CORRECTION = new VisibleMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0,0));
	
	private static TransformSheet getTransformSheet(String[] times, String[] trasnformMatrix, VisibleMatrix4f invLocalTransform, boolean correct)
	{
		List<JointKeyframe> keyframeList = new ArrayList<JointKeyframe> ();
		
		for(int i = 0; i < times.length; i++)
		{
			float timeStamp = Float.parseFloat(times[i]);
			
			if(timeStamp < 0)
			{
				continue;
			}
			
			float[] matrixValue = new float[16];
			for(int j = 0; j < 16; j++)
			{
				matrixValue[j] = Float.parseFloat(trasnformMatrix[i*16 + j]);
			}
			
			FloatBuffer buffer = FloatBuffer.allocate(16);
			buffer.put(matrixValue);
			buffer.flip();
			
			VisibleMatrix4f matrix = new VisibleMatrix4f();
			matrix.load(buffer);
			matrix.transpose();
			
			if(correct)
			{
				VisibleMatrix4f.mul(CORRECTION, matrix, matrix);
			}
			
			VisibleMatrix4f.mul(invLocalTransform, matrix, matrix);
			
			JointTransform transform = new JointTransform(new Vec3f(matrix.m30, matrix.m31, matrix.m32), Quaternion.fromMatrix(matrix),
					new Vec3f(new Vec3f(matrix.m00, matrix.m01, matrix.m02).length(),
							new Vec3f(matrix.m10, matrix.m11, matrix.m12).length(),
							new Vec3f(matrix.m20, matrix.m21, matrix.m22).length()));
			keyframeList.add(new JointKeyframe(timeStamp, transform));
		}
		
		TransformSheet sheet = new TransformSheet(keyframeList);
		
		return sheet;
	}
	
	public static void extractAnimation(ResourceLocation location, StaticAnimation data, Armature armature)
	{
		BufferedReader bufreader = null;
		
		try
		{
			bufreader = new BufferedReader(new InputStreamReader(ColladaModelLoader.getInputStream(location)));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		
		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		List<XmlNode> jointAnimations = rootNode.getChild("library_animations").getChildren("animation");
		boolean root = true;
		
		for(XmlNode jointAnimation : jointAnimations)
		{
			String jointName = jointAnimation.getAttribute("id");
			String input = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "INPUT").getAttribute("source").substring(1);
			String output = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT").getAttribute("source").substring(1);
			
			String[] timeValue = jointAnimation.getChildWithAttribute("source", "id", input).getChild("float_array").getData().split(" ");
			String[] matrixArray = jointAnimation.getChildWithAttribute("source", "id", output).getChild("float_array").getData().split(" ");
			
			String fir = jointName.substring(9);
			String sec = fir.substring(0, fir.length()-12);
			
			Joint joint = armature.findJointByName(sec);
			
			if(joint == null)
			{
				IllegalArgumentException exception = new IllegalArgumentException();
				System.err.println("Cant find joint " + sec + ". Did use wrong armature?");
				exception.printStackTrace();
				throw exception;
			}
			
			TransformSheet sheet = getTransformSheet(timeValue, matrixArray, VisibleMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			data.addSheet(sec, sheet);
			data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
			root = false;
		}
	}
	
	public static void getMixLinkAnimation(float convertTime, Pose currentPose, MixLinkAnimation player)
	{
		player.setLastPose(currentPose);
		player.setTotalTime(convertTime);
	}
}