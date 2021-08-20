package maninhouse.epicfight.collada;

import java.io.BufferedReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.animation.JointKeyframe;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.Quaternion;
import maninhouse.epicfight.animation.TransformSheet;
import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.animation.types.AttackAnimation.Phase;
import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.LayerOffAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.collada.xml.XmlNode;
import maninhouse.epicfight.collada.xml.XmlParser;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class AnimationDataExtractor {
	private static final OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0,0));
	
	private static TransformSheet getTransformSheet(String[] times, String[] trasnformMatrix, OpenMatrix4f invLocalTransform, boolean correct) {
		List<JointKeyframe> keyframeList = new ArrayList<JointKeyframe> ();
		
		for (int i = 0; i < times.length; i++) {
			float timeStamp = Float.parseFloat(times[i]);
			
			if (timeStamp < 0) {
				continue;
			}
			
			float[] matrixValue = new float[16];
			for (int j = 0; j < 16; j++) {
				matrixValue[j] = Float.parseFloat(trasnformMatrix[i*16 + j]);
			}
			
			FloatBuffer buffer = FloatBuffer.allocate(16);
			buffer.put(matrixValue);
			buffer.flip();
			OpenMatrix4f matrix = new OpenMatrix4f();
			matrix.load(buffer);
			matrix.transpose();
			
			if (correct) {
				OpenMatrix4f.mul(CORRECTION, matrix, matrix);
			}
			
			OpenMatrix4f.mul(invLocalTransform, matrix, matrix);
			
			JointTransform transform = new JointTransform(new Vec3f(matrix.m30, matrix.m31, matrix.m32), Quaternion.fromMatrix(matrix),
					new Vec3f(new Vec3f(matrix.m00, matrix.m01, matrix.m02).length(),
							new Vec3f(matrix.m10, matrix.m11, matrix.m12).length(),
							new Vec3f(matrix.m20, matrix.m21, matrix.m22).length()));
			keyframeList.add(new JointKeyframe(timeStamp, transform));
		}
		
		TransformSheet sheet = new TransformSheet(keyframeList);
		return sheet;
	}
	
	public static void extractStaticAnimation(ResourceLocation location, StaticAnimation data, Armature armature, Dist dist) {
		if (dist == Dist.CLIENT) {
			BufferedReader bufreader = ColladaModelLoader.getBufferedReaderUnsafe(location);
			XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
			List<XmlNode> jointAnimations = rootNode.getChild("library_animations").getChildren("animation");
			boolean root = true;
			
			for (XmlNode jointAnimation : jointAnimations) {
				String jointName = jointAnimation.getAttribute("id");
				String input = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "INPUT").getAttribute("source").substring(1);
				String output = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT").getAttribute("source").substring(1);
				String[] timeValue = jointAnimation.getChildWithAttribute("source", "id", input).getChild("float_array").getData().split(" ");
				String[] matrixArray = jointAnimation.getChildWithAttribute("source", "id", output).getChild("float_array").getData().split(" ");
				String fir = jointName.substring(9);
				String sec = fir.substring(0, fir.length() - 12);
				Joint joint = armature.findJointByName(sec);
				
				if (joint == null) {
					System.err.println("Cannot find joint name " + sec + ". Did you use wrong armature?");
					throw new IllegalArgumentException();
				}
				
				TransformSheet sheet = getTransformSheet(timeValue, matrixArray, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);
				data.addSheet(sec, sheet);
				data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
				root = false;
			}
		}
	}
	
	public static void extractActionAnimation(ResourceLocation location, StaticAnimation data, Armature armature,  Dist dist) {
		BufferedReader bufreader = ColladaModelLoader.getBufferedReaderUnsafe(location);
		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		List<XmlNode> jointAnimations = rootNode.getChild("library_animations").getChildren("animation");
		boolean root = true;
		
		for (XmlNode jointAnimation : jointAnimations) {
			String jointName = jointAnimation.getAttribute("id");
			String input = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "INPUT").getAttribute("source").substring(1);
			String output = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT").getAttribute("source").substring(1);
			
			String[] timeValue = jointAnimation.getChildWithAttribute("source", "id", input).getChild("float_array").getData().split(" ");
			String[] matrixArray = jointAnimation.getChildWithAttribute("source", "id", output).getChild("float_array").getData().split(" ");
			
			String fir = jointName.substring(9);
			String sec = fir.substring(0, fir.length() - 12);
			
			Joint joint = armature.findJointByName(sec);
			
			if (joint == null) {
				System.err.println("Cannot find joint name " + sec + ". Did you use wrong armature?");
				throw new IllegalArgumentException();
			}
			
			TransformSheet sheet = getTransformSheet(timeValue, matrixArray, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			data.addSheet(sec, sheet);
			data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
			root = false;
			
			if (dist == Dist.DEDICATED_SERVER) {
				break;
			}
		}
	}
	
	public static void extractAttackAnimation(ResourceLocation location, AttackAnimation data, Armature armature, Dist dist) {
		List<String> allowedJointList = Lists.<String>newArrayList();
		
		for (Phase phase : data.phases) {
			Joint joint = armature.getJointHierarcy();
			int indexer = phase.getJointIndexer();
			
			while (joint != null) {
				allowedJointList.add(joint.getName());
				if (indexer == -1) {
					break;
				}
				indexer = indexer >> 5;
				
				if (indexer == 0) {
					joint = null;
				} else {
					joint = joint.getSubJoints().get((indexer & 31) - 1);
				}
			}
		}
		
		BufferedReader bufreader = ColladaModelLoader.getBufferedReaderUnsafe(location);
		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		List<XmlNode> jointAnimations = rootNode.getChild("library_animations").getChildren("animation");
		boolean root = true;
		
		for (XmlNode jointAnimation : jointAnimations) {
			String jointName = jointAnimation.getAttribute("id");
			String input = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "INPUT").getAttribute("source").substring(1);
			String output = jointAnimation.getChild("sampler").getChildWithAttribute("input", "semantic", "OUTPUT").getAttribute("source").substring(1);
			
			String[] timeValue = jointAnimation.getChildWithAttribute("source", "id", input).getChild("float_array").getData().split(" ");
			String[] matrixArray = jointAnimation.getChildWithAttribute("source", "id", output).getChild("float_array").getData().split(" ");
			
			String fir = jointName.substring(9);
			String sec = fir.substring(0, fir.length() - 12);
			
			if (dist == Dist.DEDICATED_SERVER && !allowedJointList.contains(sec)) {
				continue;
			}
			
			Joint joint = armature.findJointByName(sec);
			
			if (joint == null) {
				System.err.println("Cannot find joint name " + sec + ". Did you use wrong armature?");
				throw new IllegalArgumentException();
			}
			
			TransformSheet sheet = getTransformSheet(timeValue, matrixArray, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			data.addSheet(sec, sheet);
			data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
			root = false;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void extractLayerOffAnimation(DynamicAnimation currentAnimation, Pose currentPose, LayerOffAnimation animation) {
		animation.setLastAnimation(currentAnimation.getRealAnimation());
		animation.setLastPose(currentPose);
		animation.setTotalTime(0.16F);
	}
}