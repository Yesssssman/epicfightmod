package yesman.epicfight.api.client.model.armor;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.SingleVertex;

@OnlyIn(Dist.CLIENT)
public abstract class ArmorModelTransformer {
	protected abstract AnimatedMesh transformModel(HumanoidModel<?> model, ArmorItem armorItem, EquipmentSlot slot, boolean debuggingMode);
	
	public static abstract class PartTransformer<T> {
		void putIndexCount(Map<String, List<Integer>> indices, String partName, int value) {
			List<Integer> list = indices.computeIfAbsent(partName, (key) -> Lists.newArrayList());
			
			for (int i = 0; i < 3; i++) {
				list.add(value);
			}
		}
		
		public abstract void bakeCube(PoseStack poseStack, String partName, T cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices);
	}
}