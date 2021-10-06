package yesman.epicfight.collada;

import java.io.BufferedReader;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Keyframe;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.TransformSheet;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.animation.types.LayerOffAnimation;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.animation.types.AttackAnimation.Phase;
import yesman.epicfight.collada.xml.XmlNode;
import yesman.epicfight.collada.xml.XmlParser;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class AnimationDataExtractor {
	private static final OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0,0));
	
	private static TransformSheet getTransformSheet(String[] times, String[] trasnformMatrix, OpenMatrix4f invLocalTransform, boolean correct) {
		List<Keyframe> keyframeList = new ArrayList<Keyframe> ();
		
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
			
			JointTransform transform = new JointTransform(new Vec3f(matrix.m30, matrix.m31, matrix.m32), OpenMatrix4f.toQuaternion(matrix),
					new Vec3f(new Vec3f(matrix.m00, matrix.m01, matrix.m02).length(),
							new Vec3f(matrix.m10, matrix.m11, matrix.m12).length(),
							new Vec3f(matrix.m20, matrix.m21, matrix.m22).length()));
			keyframeList.add(new Keyframe(timeStamp, transform));
		}
		
		TransformSheet sheet = new TransformSheet(keyframeList);
		return sheet;
	}
	
	public static void loadStaticAnimation(IResourceManager resourceManager, StaticAnimation data) {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			ResourceLocation location = data.getLocation();
			BufferedReader bufreader = ColladaModelLoader.getBufferedReaderUnsafe(resourceManager, new ResourceLocation(location.getNamespace(), location.getPath() + ".dae"));
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
				Joint joint = data.getModel().getArmature().findJointByName(sec);
				
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
	
	public static void loadActionAnimation(IResourceManager resourceManager, StaticAnimation data) {
		ResourceLocation location = data.getLocation();
		BufferedReader bufreader = ColladaModelLoader.getBufferedReaderUnsafe(resourceManager, new ResourceLocation(location.getNamespace(), location.getPath() + ".dae"));
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
			
			Joint joint = data.getModel().getArmature().findJointByName(sec);
			
			if (joint == null) {
				System.err.println("Cannot find joint name " + sec + ". Did you use wrong armature?");
				throw new IllegalArgumentException();
			}
			
			TransformSheet sheet = getTransformSheet(timeValue, matrixArray, OpenMatrix4f.invert(joint.getLocalTrasnform(), null), root);
			data.addSheet(sec, sheet);
			data.setTotalTime(Float.parseFloat(timeValue[timeValue.length - 1]));
			root = false;
			
			if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
				break;
			}
		}
	}
	
	public static void loadAttackAnimation(IResourceManager resourceManager, StaticAnimation data) {
		List<String> allowedJointList = Lists.<String>newArrayList();
		
		for (Phase phase : ((AttackAnimation)data).phases) {
			Joint joint = data.getModel().getArmature().getJointHierarcy();
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
		
		ResourceLocation location = data.getLocation();
		BufferedReader bufreader = ColladaModelLoader.getBufferedReaderUnsafe(resourceManager, new ResourceLocation(location.getNamespace(), location.getPath() + ".dae"));
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
			
			if (FMLEnvironment.dist == Dist.DEDICATED_SERVER && !allowedJointList.contains(sec)) {
				continue;
			}
			
			Joint joint = data.getModel().getArmature().findJointByName(sec);
			
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