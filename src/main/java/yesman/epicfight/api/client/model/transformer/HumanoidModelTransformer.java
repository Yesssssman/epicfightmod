package yesman.epicfight.api.client.model.transformer;

import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.vertex.PoseStack;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.MeshPartDefinition;
import yesman.epicfight.api.client.model.SingleGroupVertexBuilder;
import yesman.epicfight.client.mesh.HumanoidMesh;

@OnlyIn(Dist.CLIENT)
public abstract class HumanoidModelTransformer {
	protected abstract AnimatedMesh transformArmorModel(ResourceLocation modelLocation, LivingEntity entityLiving, ItemStack itemstack, ArmorItem armorItem, EquipmentSlot slot, HumanoidModel<?> originalModel, Model forgeModel, HumanoidModel<?> entityModel, HumanoidMesh entityMesh);
	
	@OnlyIn(Dist.CLIENT)
	public static abstract class PartTransformer<T> {
		public abstract void bakeCube(PoseStack poseStack, MeshPartDefinition partDefinition, T cube, List<SingleGroupVertexBuilder> vertices, Map<MeshPartDefinition, IntList> indices, IndexCounter indexCounter);
		
		static void triangluatePolygon(Map<MeshPartDefinition, IntList> indices, MeshPartDefinition partDefinition, IndexCounter indexCounter) {
			IntList list = indices.computeIfAbsent(partDefinition, (key) -> new IntArrayList());
			
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
		
		@OnlyIn(Dist.CLIENT)
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