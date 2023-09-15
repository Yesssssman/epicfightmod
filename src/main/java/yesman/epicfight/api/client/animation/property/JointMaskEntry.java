package yesman.epicfight.api.client.animation.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;

@OnlyIn(Dist.CLIENT)
public class JointMaskEntry {
	public static final List<JointMask> BIPED_UPPER_JOINTS = new ArrayList<> (
		Arrays.asList(
			JointMask.of("Torso"), JointMask.of("Chest"),
			JointMask.of("Head"), JointMask.of("Shoulder_R"),
			JointMask.of("Arm_R"), JointMask.of("Hand_R"),
			JointMask.of("Elbow_R"), JointMask.of("Tool_R"),
			JointMask.of("Shoulder_L"), JointMask.of("Arm_L"),
			JointMask.of("Hand_L"), JointMask.of("Elbow_L"),
			JointMask.of("Tool_L")
		)
	);
	
	public static final List<JointMask> BIPED_UPPER_JOINTS_WITH_ROOT = new ArrayList<> (
		Arrays.asList(
			JointMask.of("Root", JointMask.KEEP_CHILD_LOCROT), JointMask.of("Torso"),
			JointMask.of("Chest"), JointMask.of("Head"),
			JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
			JointMask.of("Hand_R"), JointMask.of("Elbow_R"),
			JointMask.of("Tool_R"), JointMask.of("Shoulder_L"),
			JointMask.of("Arm_L"), JointMask.of("Hand_L"),
			JointMask.of("Elbow_L"), JointMask.of("Tool_L")
		)
	);
	
	public static final List<JointMask> BIPED_LOWER_JOINTS_WITH_ROOT = new ArrayList<> (
		Arrays.asList(
			JointMask.of("Root", JointMask.KEEP_CHILD_LOCROT), JointMask.of("Thigh_R"),
			JointMask.of("Leg_R"), JointMask.of("Knee_R"),
			JointMask.of("Thigh_L"), JointMask.of("Leg_L"),
			JointMask.of("Knee_L"), JointMask.of("Torso"), JointMask.of("Head")
		)
	);
	
	public static final List<JointMask> BIPED_ARMS = new ArrayList<> (
		Arrays.asList(
			JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
			JointMask.of("Hand_R"), JointMask.of("Elbow_R"),
			JointMask.of("Tool_R"), JointMask.of("Shoulder_L"),
			JointMask.of("Arm_L"), JointMask.of("Hand_L"),
			JointMask.of("Elbow_L"), JointMask.of("Tool_L")
		)
	);
	
	public static final List<JointMask> BIPED_RIGHT_ARMS = new ArrayList<> (
			Arrays.asList(
				JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
				JointMask.of("Hand_R"), JointMask.of("Elbow_R"),
				JointMask.of("Tool_R")
			)
		);
	
	public static final List<JointMask> BIPED_BODY_AND_RIGHT_ARMS = new ArrayList<> (
			Arrays.asList(
				JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
				JointMask.of("Hand_R"), JointMask.of("Elbow_R"),
				JointMask.of("Tool_R"), JointMask.of("Head"),
				JointMask.of("Chest")
			)
		);
	
	public static final List<JointMask> BIPED_BODY_AND_LEFT_ARMS = new ArrayList<> (
			Arrays.asList(
				JointMask.of("Shoulder_L"), JointMask.of("Arm_L"),
				JointMask.of("Hand_L"), JointMask.of("Elbow_L"),
				JointMask.of("Tool_L"), JointMask.of("Head"),
				JointMask.of("Chest")
			)
		);
	
	public static final List<JointMask> WINGS = new ArrayList<> (Arrays.asList(JointMask.of("Wing_L"), JointMask.of("Wing_R")));
	
	public static final List<JointMask> ALL = new ArrayList<> (
		Arrays.asList(
			JointMask.of("Root"), JointMask.of("Thigh_R"),
			JointMask.of("Leg_R"), JointMask.of("Knee_R"),
			JointMask.of("Thigh_L"), JointMask.of("Leg_L"),
			JointMask.of("Knee_L"), JointMask.of("Torso"),
			JointMask.of("Chest"), JointMask.of("Head"),
			JointMask.of("Shoulder_R"), JointMask.of("Arm_R"),
			JointMask.of("Hand_R"), JointMask.of("Elbow_R"),
			JointMask.of("Tool_R"), JointMask.of("Shoulder_L"),
			JointMask.of("Arm_L"), JointMask.of("Hand_L"),
			JointMask.of("Elbow_L"), JointMask.of("Tool_L")
		)
	);
	
	public static final JointMaskEntry BASIC_ATTACK_MASK = JointMaskEntry.builder().defaultMask(JointMaskEntry.BIPED_UPPER_JOINTS_WITH_ROOT).create();
	
	private final Map<LivingMotion, List<JointMask>> masks = Maps.newHashMap();
	private final List<JointMask> defaultMask;
	
	public JointMaskEntry(List<JointMask> defaultMask, List<Pair<LivingMotion, List<JointMask>>> masks) {
		this.defaultMask = defaultMask;
		
		for (Pair<LivingMotion, List<JointMask>> mask : masks) {
			this.masks.put(mask.getLeft(), mask.getRight());
		}
	}
	
	public List<JointMask> getMask(LivingMotion livingmotion) {
		return this.masks.getOrDefault(livingmotion, this.defaultMask);
	}
	
	public boolean isMasked(LivingMotion livingmotion, String jointName) {
		List<JointMask> masks = this.masks.getOrDefault(livingmotion, this.defaultMask);
		
		for (JointMask mask : masks) {
			if (mask.equals(JointMask.of(jointName))) {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean isValid() {
		return this.defaultMask != null;
	}
	
	public static JointMaskEntry.Builder builder() {
		return new JointMaskEntry.Builder();
	}
	
	public static class Builder {
		private List<Pair<LivingMotion, List<JointMask>>> masks = Lists.newArrayList();
		private List<JointMask> defaultMask = null;
		
		public JointMaskEntry.Builder mask(LivingMotion motion, List<JointMask> masks) {
			this.masks.add(Pair.of(motion, masks));
			return this;
		}
		
		public JointMaskEntry.Builder defaultMask(List<JointMask> masks) {
			this.defaultMask = masks;
			return this;
		}
		
		public JointMaskEntry create() {
			return new JointMaskEntry(this.defaultMask, this.masks);
		}
	}
}