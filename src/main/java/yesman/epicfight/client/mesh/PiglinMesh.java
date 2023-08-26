package yesman.epicfight.client.mesh;

import java.util.Map;

import net.minecraft.world.entity.EquipmentSlot;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.ModelPart;
import yesman.epicfight.api.client.model.VertexIndicator.AnimatedVertexIndicator;

public class PiglinMesh extends HumanoidMesh {
	public PiglinMesh(Map<String, float[]> arrayMap, AnimatedMesh parent, RenderProperties properties, Map<String, ModelPart<AnimatedVertexIndicator>> parts) {
		super(arrayMap, parent, properties, parts);
	}
	
	public AnimatedMesh getArmorModel(EquipmentSlot slot) {
		switch (slot) {
		case HEAD:
			return Meshes.HELMET_PIGLIN;
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