package yesman.epicfight.api.client.model.transformer;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.SingleVertex;

@OnlyIn(Dist.CLIENT)
public abstract class HumanoidModelTransformer {
	protected abstract AnimatedMesh transformArmorModel(HumanoidModel<?> model, ResourceLocation modelName, EquipmentSlot slot);
	
	public static abstract class PartTransformer<T> {
		
		public abstract void bakeCube(PoseStack poseStack, String partName, T cube, List<SingleVertex> vertices, Map<String, IntList> indices, IndexCounter indexCounter);
		
		static void triangluatePolygon(Map<String, IntList> indices, String partName, IndexCounter indexCounter) {
			IntList list = indices.computeIfAbsent(partName, (key) -> new IntArrayList());
			
			//Optimization: do not split vertices in a cube.
			for (int i = 0; i < 3; i++) {
				list.add(indexCounter.first());
			}
			
			for (int i = 0; i < 3; i++) {
				list.add(indexCounter.second());
			}
			
			for (int i = 0; i < 3; i++) {
				list.add(indexCounter.fourth());
			}
			
			for (int i = 0; i < 3; i++) {
				list.add(indexCounter.fourth());
			}
			
			for (int i = 0; i < 3; i++) {
				list.add(indexCounter.second());
			}
			
			for (int i = 0; i < 3; i++) {
				list.add(indexCounter.third());
			}
			
			indexCounter.count();
		}
		
		public static class IndexCounter {
			private int indexCounter = 0;
			
			private int first() {
				return this.indexCounter;
			}
			
			private int second() {
				return this.indexCounter + 1;
			}
			
			private int third() {
				return this.indexCounter + 2;
			}
			
			private int fourth() {
				return this.indexCounter + 3;
			}
			
			private void count() {
				this.indexCounter += 4;
			}
		}
	}
}