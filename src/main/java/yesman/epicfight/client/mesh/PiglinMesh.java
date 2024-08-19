package yesman.epicfight.client.mesh;

import java.util.List;
import java.util.Map;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.AnimatedVertexBuilder;
import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.Meshes;

@OnlyIn(Dist.CLIENT)
public class PiglinMesh extends HumanoidMesh {
	public PiglinMesh(Map<String, float[]> arrayMap, Map<MeshPartDefinition, List<AnimatedVertexBuilder>> parts, AnimatedMesh parent, RenderProperties properties) {
		super(arrayMap, parts, parent, properties);
	}
	
	@Override
	public AnimatedMesh getHumanoidArmorModel(EquipmentSlot slot) {
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