package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.BlenderAnimatedVertexBuilder;
import yesman.epicfight.api.client.model.Meshes;

@OnlyIn(Dist.CLIENT)
public class HumanoidMesh extends AnimatedMesh {
	public final AnimatedModelPart head;
	public final AnimatedModelPart torso;
	public final AnimatedModelPart leftArm;
	public final AnimatedModelPart rightArm;
	public final AnimatedModelPart leftLeg;
	public final AnimatedModelPart rightLeg;
	public final AnimatedModelPart hat;
	public final AnimatedModelPart jacket;
	public final AnimatedModelPart leftSleeve;
	public final AnimatedModelPart rightSleeve;
	public final AnimatedModelPart leftPants;
	public final AnimatedModelPart rightPants;
	
	public HumanoidMesh(Map<String, float[]> arrayMap, Map<String, List<BlenderAnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
		
		this.head = this.getOrLogException(this.parts, "head");
		this.torso = this.getOrLogException(this.parts, "torso");
		this.leftArm = this.getOrLogException(this.parts, "leftArm");
		this.rightArm = this.getOrLogException(this.parts, "rightArm");
		this.leftLeg = this.getOrLogException(this.parts, "leftLeg");
		this.rightLeg = this.getOrLogException(this.parts, "rightLeg");
		
		this.hat = this.getOrLogException(this.parts, "hat");
		this.jacket = this.getOrLogException(this.parts, "jacket");
		this.leftSleeve = this.getOrLogException(this.parts, "leftSleeve");
		this.rightSleeve = this.getOrLogException(this.parts, "rightSleeve");
		this.leftPants = this.getOrLogException(this.parts, "leftPants");
		this.rightPants = this.getOrLogException(this.parts, "rightPants");
	}
	
	public AnimatedMesh getHumanoidArmorModel(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			return Meshes.HELMET;
		case CHEST:
			return Meshes.CHESTPLATE;
		case LEGS:
			return Meshes.LEGGINS;
		case FEET:
			return Meshes.BOOTS;
		default:
			return null;
		}
	}
}