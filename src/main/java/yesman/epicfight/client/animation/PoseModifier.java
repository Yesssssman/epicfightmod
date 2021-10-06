package yesman.epicfight.client.animation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;

@OnlyIn(Dist.CLIENT)
public class PoseModifier {
	public static final List<PoseModifyingEntry> BIPED_UPPER_JOINTS = new ArrayList<> (
		Arrays.asList(PoseModifyingEntry.of("Torso", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Chest", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Head", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Shoulder_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Arm_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Hand_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Elbow_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Tool_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Shoulder_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Arm_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Hand_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Elbow_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Tool_L", PoseModifyingEntry.NONE)
		)
	);
	
	public static final List<PoseModifyingEntry> BIPED_UPPER_JOINTS_ROOT = new ArrayList<> (
		Arrays.asList(PoseModifyingEntry.of("Root", PoseModifyingEntry.POSE_ROOT_MIX), PoseModifyingEntry.of("Torso", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Chest", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Head", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Shoulder_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Arm_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Hand_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Elbow_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Tool_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Shoulder_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Arm_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Hand_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Elbow_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Tool_L", PoseModifyingEntry.NONE)
		)
	);
	
	public static final List<PoseModifyingEntry> BIPED_LOWER_JOINTS_ROOT = new ArrayList<> (
		Arrays.asList(PoseModifyingEntry.of("Root", PoseModifyingEntry.POSE_ROOT_MIX), PoseModifyingEntry.of("Thigh_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Leg_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Knee_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Thigh_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Leg_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Knee_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Torso", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Head", PoseModifyingEntry.NONE)
		)
	);
	
	public static final List<PoseModifyingEntry> BIPED_ARMS = new ArrayList<> (
		Arrays.asList(PoseModifyingEntry.of("Shoulder_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Arm_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Hand_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Elbow_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Tool_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Shoulder_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Arm_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Hand_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Elbow_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Tool_L", PoseModifyingEntry.NONE)
		)
	);
	
	public static final List<PoseModifyingEntry> NONE = new ArrayList<> (
		Arrays.asList(PoseModifyingEntry.of("Root", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Thigh_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Leg_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Knee_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Thigh_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Leg_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Knee_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Torso", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Chest", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Head", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Shoulder_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Arm_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Hand_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Elbow_R", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Tool_R", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Shoulder_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Arm_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Hand_L", PoseModifyingEntry.NONE),
			PoseModifyingEntry.of("Elbow_L", PoseModifyingEntry.NONE), PoseModifyingEntry.of("Tool_L", PoseModifyingEntry.NONE)
		)
	);
	
	private final Map<LivingMotion, List<PoseModifyingEntry>> bindDataMap = Maps.newHashMap();
	private final List<PoseModifyingEntry> defaultModifyingFunction;
	
	public PoseModifier(List<PoseModifyingEntry> NONEData, List<Pair<LivingMotion, List<PoseModifyingEntry>>> bindDatas) {
		this.defaultModifyingFunction = NONEData;
		for (Pair<LivingMotion, List<PoseModifyingEntry>> bindData : bindDatas) {
			this.bindDataMap.put(bindData.getLeft(), bindData.getRight());
		}
	}
	
	public List<PoseModifyingEntry> getBindData(LivingMotion livingmotion) {
		return this.bindDataMap.getOrDefault(livingmotion, this.defaultModifyingFunction);
	}
	
	public boolean isMasked(LivingMotion livingmotion, String jointName) {
		List<PoseModifyingEntry> bindDatas = this.bindDataMap.getOrDefault(livingmotion, this.defaultModifyingFunction);
		for (PoseModifyingEntry bindData : bindDatas) {
			if (bindData.equals(PoseModifyingEntry.compareUtil(jointName))) {
				return false;
			}
		}
		return true;
	}
	
	public boolean isValid() {
		return this.defaultModifyingFunction != null;
	}
	
	public static PoseModifier.Builder builder() {
		return new PoseModifier.Builder();
	}
	
	public static class Builder {
		private List<Pair<LivingMotion, List<PoseModifyingEntry>>> modifyingByCondition = Lists.newArrayList();
		private List<PoseModifyingEntry> defaultEntry = null;
		
		public PoseModifier.Builder addEntry(LivingMotion motion, List<PoseModifyingEntry> poseModifyingEntry) {
			this.modifyingByCondition.add(Pair.of(motion, poseModifyingEntry));
			return this;
		}
		
		public PoseModifier.Builder setDefaultData(List<PoseModifyingEntry> poseModifyingEntry) {
			this.defaultEntry = poseModifyingEntry;
			return this;
		}
		
		public PoseModifier create() {
			return new PoseModifier(this.defaultEntry, this.modifyingByCondition);
		}
	}
}