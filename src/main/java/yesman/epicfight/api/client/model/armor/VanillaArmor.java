package yesman.epicfight.api.client.model.armor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.client.model.SingleVertex;
import yesman.epicfight.api.utils.math.QuaternionUtils;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class VanillaArmor extends ArmorModelTransformer {
	static final PartTransformer<ModelPart.Cube> HEAD = new SimpleTransformer(9);
	static final PartTransformer<ModelPart.Cube> LEFT_FEET = new SimpleTransformer(5);
	static final PartTransformer<ModelPart.Cube> RIGHT_FEET = new SimpleTransformer(2);
	static final PartTransformer<ModelPart.Cube> LEFT_ARM = new LimbPartTransformer(16, 17, 19, 19.0F, false, AABB.ofSize(new Vec3(-6.0D, 18.0D, 0), 8.0D, 14.0D, 8.0D));
	static final PartTransformer<ModelPart.Cube> RIGHT_ARM = new LimbPartTransformer(11, 12, 14, 19.0F, false, AABB.ofSize(new Vec3(6.0D, 18.0D, 0), 8.0D, 14.0D, 8.0D));
	static final PartTransformer<ModelPart.Cube> LEFT_LEG = new LimbPartTransformer(4, 5, 6, 6.0F, true, AABB.ofSize(new Vec3(-2.0D, 6.0D, 0), 8.0D, 14.0D, 8.0D));
	static final PartTransformer<ModelPart.Cube> RIGHT_LEG = new LimbPartTransformer(1, 2, 3, 6.0F, true, AABB.ofSize(new Vec3(2.0D, 6.0D, 0), 8.0D, 14.0D, 8.0D));
	static final PartTransformer<ModelPart.Cube> CHEST = new ChestPartTransformer(8, 7, 18.0F, AABB.ofSize(new Vec3(0, 18.0D, 0), 12.0D, 14.0D, 6.0D));
	
	static int indexCount = 0;
	
	static class VanillaModelPartition {
		final PartTransformer<ModelPart.Cube> partTransformer;
		final ModelPart modelPart;
		final String partName;
		
		private VanillaModelPartition(PartTransformer<ModelPart.Cube> partTransformer, ModelPart modelPart, String partName) {
			this.partTransformer = partTransformer;
			this.modelPart = modelPart;
			this.partName = partName;
		}
	}
	
	public AnimatedMesh transformModel(HumanoidModel<?> model, ArmorItem armorItem, EquipmentSlot slot, boolean debuggingMode) {
		List<VanillaModelPartition> boxes = Lists.newArrayList();
		//List<ModelPart> modelParts = getAllParts(model);
		
		model.head.setRotation(0.0F, 0.0F, 0.0F);
		model.hat.setRotation(0.0F, 0.0F, 0.0F);
		model.body.setRotation(0.0F, 0.0F, 0.0F);
	    model.rightArm.setRotation(0.0F, 0.0F, 0.0F);
	    model.leftArm.setRotation(0.0F, 0.0F, 0.0F);
	    model.rightLeg.setRotation(0.0F, 0.0F, 0.0F);
	    model.leftLeg.setRotation(0.0F, 0.0F, 0.0F);
		
	    switch (slot) {
		case HEAD:
			boxes.add(new VanillaModelPartition(HEAD, model.head, "head"));
			boxes.add(new VanillaModelPartition(HEAD, model.hat, "hat"));
			break;
		case CHEST:
			boxes.add(new VanillaModelPartition(CHEST, model.body, "body"));
			boxes.add(new VanillaModelPartition(RIGHT_ARM, model.rightArm, "rightArm"));
			boxes.add(new VanillaModelPartition(LEFT_ARM, model.leftArm, "leftArm"));
			break;
		case LEGS:
			boxes.add(new VanillaModelPartition(CHEST, model.body, "body"));
			boxes.add(new VanillaModelPartition(LEFT_LEG, model.leftLeg, "leftLeg"));
			boxes.add(new VanillaModelPartition(RIGHT_LEG, model.rightLeg, "rightLeg"));
			break;
		case FEET:
			boxes.add(new VanillaModelPartition(LEFT_FEET, model.leftLeg, "leftLeg"));
			boxes.add(new VanillaModelPartition(RIGHT_FEET, model.rightLeg, "rightLeg"));
			break;
		default:
			return null;
		}
		
		ResourceLocation rl = new ResourceLocation(ForgeRegistries.ITEMS.getKey(armorItem).getNamespace(), "armor/" + ForgeRegistries.ITEMS.getKey(armorItem).getPath());
		AnimatedMesh armorModelMesh = bakeMeshFromCubes(boxes, debuggingMode);
		Meshes.addMesh(rl, armorModelMesh);
		
		return armorModelMesh;
	}
	
	public static List<ModelPart> getAllParts(Model model) {
		Class<?> cls = model.getClass();
		List<Class<?>> superClasses = Lists.newArrayList();
		
		while (Model.class.isAssignableFrom(cls)) {
			superClasses.add(cls);
			cls = cls.getSuperclass();
		}
		
		List<ModelPart> modelParts = Lists.newArrayList();
		
		for (Class<?> modelClss : superClasses) {
			Field[] modelFields = modelClss.getDeclaredFields();
			
			for (Field field : modelFields) {
				if (field.getType().isAssignableFrom(ModelPart.class)) {
					field.setAccessible(true);
					try {
						ModelPart modelPart = (ModelPart)field.get(model);
						
						if (modelPart.visible) {
							modelParts.add(modelPart);
						}
					} catch(Exception e) {}
				}
			}
		}
		
		return modelParts;
	}
	
	private static AnimatedMesh bakeMeshFromCubes(List<VanillaModelPartition> partitions, boolean debuggingMode) {
		List<SingleVertex> vertices = Lists.newArrayList();
		Map<String, List<Integer>> indices = Maps.newHashMap();
		PoseStack poseStack = new PoseStack();
		indexCount = 0;
		poseStack.mulPose(QuaternionUtils.YP.rotationDegrees(180.0F));
		poseStack.mulPose(QuaternionUtils.XP.rotationDegrees(180.0F));
		poseStack.translate(0, -24, 0);
		
		for (VanillaModelPartition modelpartition : partitions) {
			bake(poseStack, modelpartition.partName, modelpartition, modelpartition.modelPart, vertices, indices, debuggingMode);
		}
		
		return SingleVertex.loadVertexInformation(vertices, indices);
	}
	
	private static void bake(PoseStack poseStack, String partName, VanillaModelPartition modelpartition, ModelPart part, List<SingleVertex> vertices, Map<String, List<Integer>> indices, boolean debuggingMode) {
		poseStack.pushPose();
		poseStack.translate(part.x, part.y, part.z);
		
		if (part.zRot != 0.0F) {
			poseStack.mulPose(QuaternionUtils.ZP.rotation(part.zRot));
		}
		
		if (part.yRot != 0.0F) {
			poseStack.mulPose(QuaternionUtils.YP.rotation(part.yRot));
		}
		
		if (part.xRot != 0.0F) {
			poseStack.mulPose(QuaternionUtils.XP.rotation(part.xRot));
		}
		
		for (ModelPart.Cube cube : part.cubes) {
			modelpartition.partTransformer.bakeCube(poseStack, partName, cube, vertices, indices);
		}
		
		for (Map.Entry<String, ModelPart> child : part.children.entrySet()) {
			bake(poseStack, child.getKey(), modelpartition, child.getValue(), vertices, indices, debuggingMode);
		}
		
		poseStack.popPose();
	}
	
	@OnlyIn(Dist.CLIENT)
	static class SimpleTransformer extends PartTransformer<ModelPart.Cube> {
		final int jointId;
		
		public SimpleTransformer(int jointId) {
			this.jointId = jointId;
		}
		
		public void bakeCube(PoseStack poseStack, String partName, ModelPart.Cube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			for (ModelPart.Polygon quad : cube.polygons) {
				Vector3f norm = new Vector3f(quad.normal);
				norm.mul(poseStack.last().normal());
				
				for (ModelPart.Vertex vertex : quad.vertices) {
					Vector4f pos = new Vector4f(vertex.pos, 1.0F);
					pos.mul(poseStack.last().pose());
					vertices.add(new SingleVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()).scale(0.0625F))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(this.jointId, 0, 0))
						.setEffectiveJointWeights(new Vec3f(1.0F, 0.0F, 0.0F))
						.setEffectiveJointNumber(1)
					);
				}
				
				putIndexCount(indices, partName, indexCount);
				putIndexCount(indices, partName, indexCount + 1);
				putIndexCount(indices, partName, indexCount + 3);
				putIndexCount(indices, partName, indexCount + 3);
				putIndexCount(indices, partName, indexCount + 1);
				putIndexCount(indices, partName, indexCount + 2);
				indexCount+=4;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class ChestPartTransformer extends PartTransformer<ModelPart.Cube> {
		static final float X_PLANE = 0.0F;
		static final VertexWeight[] WEIGHT_ALONG_Y = { new VertexWeight(13.6666F, 0.230F, 0.770F), new VertexWeight(15.8333F, 0.254F, 0.746F), new VertexWeight(18.0F, 0.5F, 0.5F), new VertexWeight(20.1666F, 0.744F, 0.256F), new VertexWeight(22.3333F, 0.770F, 0.230F)};
		final SimpleTransformer upperAttachmentTransformer;
		final SimpleTransformer lowerAttachmentTransformer;
		final AABB noneAttachmentArea;
		final float yClipCoord;
		
		public ChestPartTransformer(int upperJoint, int lowerJoint, float yBasis, AABB noneAttachmentArea) {
			this.noneAttachmentArea = noneAttachmentArea;
			this.upperAttachmentTransformer = new SimpleTransformer(upperJoint);
			this.lowerAttachmentTransformer = new SimpleTransformer(lowerJoint);
			this.yClipCoord = yBasis;
		}
		
		@Override
		public void bakeCube(PoseStack poseStack, String partName, ModelPart.Cube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			Vec3 centerOfCube = getCenterOfCube(poseStack, cube);
			
			if (!this.noneAttachmentArea.contains(centerOfCube)) {
				if (centerOfCube.y < this.yClipCoord) {
					this.lowerAttachmentTransformer.bakeCube(poseStack, partName, cube, vertices, indices);
				} else {
					this.upperAttachmentTransformer.bakeCube(poseStack, partName, cube, vertices, indices);
				}
				
				return;
			}
			
			
			List<AnimatedPolygon> xClipPolygons = Lists.<AnimatedPolygon>newArrayList();
			List<AnimatedPolygon> xyClipPolygons = Lists.<AnimatedPolygon>newArrayList();
			
			for (ModelPart.Polygon polygon : cube.polygons) {
				Matrix4f matrix = poseStack.last().pose();
				
				ModelPart.Vertex pos0 = getTranslatedVertex(polygon.vertices[0], matrix);
				ModelPart.Vertex pos1 = getTranslatedVertex(polygon.vertices[1], matrix);
				ModelPart.Vertex pos2 = getTranslatedVertex(polygon.vertices[2], matrix);
				ModelPart.Vertex pos3 = getTranslatedVertex(polygon.vertices[3], matrix);
				Direction direction = getDirectionFromVector(polygon.normal);
				
				VertexWeight pos0Weight = getYClipWeight(pos0.pos.y());
				VertexWeight pos1Weight = getYClipWeight(pos1.pos.y());
				VertexWeight pos2Weight = getYClipWeight(pos2.pos.y());
				VertexWeight pos3Weight = getYClipWeight(pos3.pos.y());
				
				if (pos1.pos.x() > X_PLANE != pos2.pos.x() > X_PLANE) {
					float distance = pos2.pos.x() - pos1.pos.x();
					float textureU = pos1.u + (pos2.u - pos1.u) * ((X_PLANE - pos1.pos.x()) / distance);
					ModelPart.Vertex pos4 = new ModelPart.Vertex(X_PLANE, pos0.pos.y(), pos0.pos.z(), textureU, pos0.v);
					ModelPart.Vertex pos5 = new ModelPart.Vertex(X_PLANE, pos1.pos.y(), pos1.pos.z(), textureU, pos1.v);
					
					xClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos4, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos5, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0),
						new AnimatedVertex(pos3, 8, 7, 0, pos3Weight.chestWeight, pos3Weight.torsoWeight, 0)
					}, direction));
					xClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos4, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos1, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0),
						new AnimatedVertex(pos2, 8, 7, 0, pos2Weight.chestWeight, pos2Weight.torsoWeight, 0),
						new AnimatedVertex(pos5, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0)
					}, direction));
				} else {
					xClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, 8, 7, 0, pos0Weight.chestWeight, pos0Weight.torsoWeight, 0),
						new AnimatedVertex(pos1, 8, 7, 0, pos1Weight.chestWeight, pos1Weight.torsoWeight, 0),
						new AnimatedVertex(pos2, 8, 7, 0, pos2Weight.chestWeight, pos2Weight.torsoWeight, 0),
						new AnimatedVertex(pos3, 8, 7, 0, pos3Weight.chestWeight, pos3Weight.torsoWeight, 0)
					}, direction));
				}
			}
			
			for (AnimatedPolygon polygon : xClipPolygons) {
				boolean upsideDown = polygon.animatedVertexPositions[1].pos.y() > polygon.animatedVertexPositions[2].pos.y();
				AnimatedVertex pos0 = upsideDown ? polygon.animatedVertexPositions[2] : polygon.animatedVertexPositions[0];
				AnimatedVertex pos1 = upsideDown ? polygon.animatedVertexPositions[3] : polygon.animatedVertexPositions[1];
				AnimatedVertex pos2 = upsideDown ? polygon.animatedVertexPositions[0] : polygon.animatedVertexPositions[2];
				AnimatedVertex pos3 = upsideDown ? polygon.animatedVertexPositions[1] : polygon.animatedVertexPositions[3];
				Direction direction = getDirectionFromVector(polygon.normal);
				List<VertexWeight> vertexWeights = getMiddleYClipWeights(pos1.pos.y(), pos2.pos.y());
				List<AnimatedVertex> animatedVertices = Lists.<AnimatedVertex>newArrayList();
				animatedVertices.add(pos0);
				animatedVertices.add(pos1);
				
				if (vertexWeights.size() > 0) {
					for (VertexWeight vertexWeight : vertexWeights) {
						float distance = pos2.pos.y() - pos1.pos.y();
						float textureV = pos1.v + (pos2.v - pos1.v) * ((vertexWeight.yClipCoord - pos1.pos.y()) / distance);
						Vector3f clipPos1 = getClipPoint(pos1.pos, pos2.pos, vertexWeight.yClipCoord);
						Vector3f clipPos2 = getClipPoint(pos0.pos, pos3.pos, vertexWeight.yClipCoord);
						ModelPart.Vertex pos4 = new ModelPart.Vertex(clipPos2, pos0.u, textureV);
						ModelPart.Vertex pos5 = new ModelPart.Vertex(clipPos1, pos1.u, textureV);
						animatedVertices.add(new AnimatedVertex(pos4, 8, 7, 0, vertexWeight.chestWeight, vertexWeight.torsoWeight, 0));
						animatedVertices.add(new AnimatedVertex(pos5, 8, 7, 0, vertexWeight.chestWeight, vertexWeight.torsoWeight, 0));
					}
				}
				
				animatedVertices.add(pos3);
				animatedVertices.add(pos2);
				
				for (int i = 0; i < (animatedVertices.size() - 2) / 2; i++) {
					int start = i*2;
					AnimatedVertex p0 = animatedVertices.get(start);
					AnimatedVertex p1 = animatedVertices.get(start + 1);
					AnimatedVertex p2 = animatedVertices.get(start + 3);
					AnimatedVertex p3 = animatedVertices.get(start + 2);
					xyClipPolygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(p0, 8, 7, 0, p0.weight.x, p0.weight.y, 0),
						new AnimatedVertex(p1, 8, 7, 0, p1.weight.x, p1.weight.y, 0),
						new AnimatedVertex(p2, 8, 7, 0, p2.weight.x, p2.weight.y, 0),
						new AnimatedVertex(p3, 8, 7, 0, p3.weight.x, p3.weight.y, 0)
					}, direction));
				}
			}
			
			for (AnimatedPolygon polygon : xyClipPolygons) {
				Vector3f norm = new Vector3f(polygon.normal);
				norm.mul(poseStack.last().normal());
				
				for (AnimatedVertex vertex : polygon.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.pos, 1.0F);
					float weight1 = vertex.weight.x;
					float weight2 = vertex.weight.y;
					int joint1 = vertex.jointId.getX();
					int joint2 = vertex.jointId.getY();
					int count = weight1 > 0.0F && weight2 > 0.0F ? 2 : 1;
					
					if (weight1 <= 0.0F) {
						joint1 = joint2;
						weight1 = weight2;
					}
					
					vertices.add(new SingleVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()).scale(0.0625F))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(joint1, joint2, 0))
						.setEffectiveJointWeights(new Vec3f(weight1, weight2, 0.0F))
						.setEffectiveJointNumber(count)
					);
				}
				
				putIndexCount(indices, partName, indexCount);
				putIndexCount(indices, partName, indexCount + 1);
				putIndexCount(indices, partName, indexCount + 3);
				putIndexCount(indices, partName, indexCount + 3);
				putIndexCount(indices, partName, indexCount + 1);
				putIndexCount(indices, partName, indexCount + 2);
				indexCount+=4;
			}
		}
		
		static VertexWeight getYClipWeight(float y) {
			if (y < WEIGHT_ALONG_Y[0].yClipCoord) {
				return new VertexWeight(y, 0.0F, 1.0F);
			}
			
			int index = -1;
			for (int i = 0; i < WEIGHT_ALONG_Y.length; i++) {
				
			}
			
			if (index > 0) {
				VertexWeight pair = WEIGHT_ALONG_Y[index];
				return new VertexWeight(y, pair.chestWeight, pair.torsoWeight);
			}
			
			return new VertexWeight(y, 1.0F, 0.0F);
		}
		
		static class VertexWeight {
			final float yClipCoord;
			final float chestWeight;
			final float torsoWeight;
			
			public VertexWeight(float yClipCoord, float chestWeight, float torsoWeight) {
				this.yClipCoord = yClipCoord;
				this.chestWeight = chestWeight;
				this.torsoWeight = torsoWeight;
			}
		}
		
		static List<VertexWeight> getMiddleYClipWeights(float minY, float maxY) {
			List<VertexWeight> cutYs = Lists.<VertexWeight>newArrayList();
			for (VertexWeight vertexWeight : WEIGHT_ALONG_Y) {
				if (vertexWeight.yClipCoord > minY && maxY >= vertexWeight.yClipCoord) {
					cutYs.add(vertexWeight);
				}
			}
			return cutYs;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class LimbPartTransformer extends PartTransformer<ModelPart.Cube> {
		final int upperJoint;
		final int lowerJoint;
		final int middleJoint;
		final boolean bendInFront;
		final SimpleTransformer upperAttachmentTransformer;
		final SimpleTransformer lowerAttachmentTransformer;
		final AABB noneAttachmentArea;
		final float yClipCoord;
		
		public LimbPartTransformer(int upperJoint, int lowerJoint, int middleJoint, float yClipCoord, boolean bendInFront, AABB noneAttachmentArea) {
			this.upperJoint = upperJoint;
			this.lowerJoint = lowerJoint;
			this.middleJoint = middleJoint;
			this.bendInFront = bendInFront;
			this.upperAttachmentTransformer = new SimpleTransformer(upperJoint);
			this.lowerAttachmentTransformer = new SimpleTransformer(lowerJoint);
			this.noneAttachmentArea = noneAttachmentArea;
			this.yClipCoord = yClipCoord;
		}
		
		@Override
		public void bakeCube(PoseStack poseStack, String partName,  ModelPart.Cube cube, List<SingleVertex> vertices, Map<String, List<Integer>> indices) {
			List<AnimatedPolygon> polygons = Lists.<AnimatedPolygon>newArrayList();
			
			for (ModelPart.Polygon quad : cube.polygons) {
				Matrix4f matrix = poseStack.last().pose();
				ModelPart.Vertex pos0 = getTranslatedVertex(quad.vertices[0], matrix);
				ModelPart.Vertex pos1 = getTranslatedVertex(quad.vertices[1], matrix);
				ModelPart.Vertex pos2 = getTranslatedVertex(quad.vertices[2], matrix);
				ModelPart.Vertex pos3 = getTranslatedVertex(quad.vertices[3], matrix);
				Direction direction = getDirectionFromVector(quad.normal);
				
				if (pos1.pos.y() > this.yClipCoord != pos2.pos.y() > this.yClipCoord) {
					float distance = pos2.pos.y() - pos1.pos.y();
					float textureV = pos1.v + (pos2.v - pos1.v) * ((this.yClipCoord - pos1.pos.y()) / distance);
					Vector3f clipPos1 = getClipPoint(pos1.pos, pos2.pos, this.yClipCoord);
					Vector3f clipPos2 = getClipPoint(pos0.pos, pos3.pos, this.yClipCoord);
					ModelPart.Vertex pos4 = new ModelPart.Vertex(clipPos2, pos0.u, textureV);
					ModelPart.Vertex pos5 = new ModelPart.Vertex(clipPos1, pos1.u, textureV);
					
					int upperId, lowerId;
					if (distance > 0) {
						upperId = this.lowerJoint;
						lowerId = this.upperJoint;
					} else {
						upperId = this.upperJoint;
						lowerId = this.lowerJoint;
					}
					
					polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, upperId), new AnimatedVertex(pos1, upperId),
						new AnimatedVertex(pos5, upperId), new AnimatedVertex(pos4, upperId)
					}, direction));
					polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos4, lowerId), new AnimatedVertex(pos5, lowerId),
						new AnimatedVertex(pos2, lowerId), new AnimatedVertex(pos3, lowerId)
					}, direction));
					
					boolean hasSameZ = pos4.pos.z() < 0.0F == pos5.pos.z() < 0.0F;
					boolean isFront = hasSameZ && (pos4.pos.z() < 0.0F == this.bendInFront);
					
					if (isFront) {
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, this.middleJoint), new AnimatedVertex(pos5, this.middleJoint),
							new AnimatedVertex(pos5, this.upperJoint), new AnimatedVertex(pos4, this.upperJoint)
						}, 0.001F, direction));
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, this.lowerJoint), new AnimatedVertex(pos5, this.lowerJoint),
							new AnimatedVertex(pos5, this.middleJoint), new AnimatedVertex(pos4, this.middleJoint)
						}, 0.001F, direction));
					} else if (!hasSameZ) {
						boolean startFront = pos4.pos.z() > 0;
						int firstJoint = this.lowerJoint;
						int secondJoint = this.lowerJoint;
						int thirdJoint = startFront ? this.upperJoint : this.middleJoint;
						int fourthJoint = startFront ? this.middleJoint : this.upperJoint;
						int fifthJoint = this.upperJoint;
						int sixthJoint = this.upperJoint;
						
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, firstJoint), new AnimatedVertex(pos5, secondJoint),
							new AnimatedVertex(pos5, thirdJoint), new AnimatedVertex(pos4, fourthJoint)
						}, 0.001F, direction));
						polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
							new AnimatedVertex(pos4, fourthJoint), new AnimatedVertex(pos5, thirdJoint),
							new AnimatedVertex(pos5, fifthJoint), new AnimatedVertex(pos4, sixthJoint)
						}, 0.001F, direction));
					}
				} else {
					int jointId = pos0.pos.y() > this.yClipCoord ? this.upperJoint : this.lowerJoint;
					polygons.add(new AnimatedPolygon(new AnimatedVertex[] {
						new AnimatedVertex(pos0, jointId), new AnimatedVertex(pos1, jointId),
						new AnimatedVertex(pos2, jointId), new AnimatedVertex(pos3, jointId)
					}, direction));
				}
			}
			
			for (AnimatedPolygon quad : polygons) {
				Vector3f norm = new Vector3f(quad.normal);
				norm.mul(poseStack.last().normal());
				
				for (AnimatedVertex vertex : quad.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.pos, 1.0F);
					vertices.add(new SingleVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()).scale(0.0625F))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(vertex.jointId.getX(), 0, 0))
						.setEffectiveJointWeights(new Vec3f(1.0F, 0.0F, 0.0F))
						.setEffectiveJointNumber(1)
					);
				}
				
				putIndexCount(indices, partName, indexCount);
				putIndexCount(indices, partName, indexCount + 1);
				putIndexCount(indices, partName, indexCount + 3);
				putIndexCount(indices, partName, indexCount + 3);
				putIndexCount(indices, partName, indexCount + 1);
				putIndexCount(indices, partName, indexCount + 2);
				indexCount+=4;
			}
		}
	}
	
	static Direction getDirectionFromVector(Vector3f directionVec) {
		for (Direction direction : Direction.values()) {
			Vector3f direcVec = new Vector3f(Float.compare(directionVec.x(), -0.0F) == 0 ? 0.0F : directionVec.x(), directionVec.y(), directionVec.z());
			if (direcVec.equals(direction.step())) {
				return direction;
			}
		}
		
		return null;
	}
	
	static Vec3 getCenterOfCube(PoseStack poseStack, ModelPart.Cube cube) {
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		double maxY = Double.MIN_VALUE;
		double maxZ = Double.MIN_VALUE;
		
		Matrix4f matrix = poseStack.last().pose();
		
		for (ModelPart.Polygon quad : cube.polygons) {
			for (ModelPart.Vertex v : quad.vertices) {
				Vector4f translatedPosition = new Vector4f(v.pos, 1.0F);
				translatedPosition.mul(matrix);
				
				if (minX > translatedPosition.x()) {
					minX = translatedPosition.x();
				}
				
				if (minY > translatedPosition.y()) {
					minY = translatedPosition.y();
				}
				
				if (minZ > translatedPosition.z()) {
					minZ = translatedPosition.z();
				}
				
				if (maxX < translatedPosition.x()) {
					maxX = translatedPosition.x();
				}
				
				if (maxY < translatedPosition.y()) {
					maxY = translatedPosition.y();
				}
				
				if (maxZ < translatedPosition.z()) {
					maxZ = translatedPosition.z();
				}
			}
		}
		
		return new Vec3(minX + (maxX - minX) * 0.5D, minY + (maxY - minY) * 0.5D, minZ + (maxZ - minZ) * 0.5D);
	}
	
	static Vector3f getClipPoint(Vector3f pos1, Vector3f pos2, float yClip) {
		Vector3f direct = new Vector3f(pos2);
		direct.sub(pos1);
		direct.mul((yClip - pos1.y()) / (pos2.y() - pos1.y()));
		
		Vector3f clipPoint = new Vector3f(pos1);
		clipPoint.add(direct);
		
		return clipPoint;
	}
	
	static ModelPart.Vertex getTranslatedVertex(ModelPart.Vertex original, Matrix4f matrix) {
		Vector4f translatedPosition = new Vector4f(original.pos, 1.0F);
		translatedPosition.mul(matrix);
		
		return new ModelPart.Vertex(translatedPosition.x(), translatedPosition.y(), translatedPosition.z(), original.u, original.v);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class AnimatedVertex extends ModelPart.Vertex {
		final Vec3i jointId;
		final Vec3f weight;
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, int jointId) {
			this(posTexVertx, jointId, 0, 0, 1.0F, 0.0F, 0.0F);
		}
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, int jointId1, int jointId2, int jointId3, float weight1, float weight2, float weight3) {
			this(posTexVertx, new Vec3i(jointId1, jointId2, jointId3), new Vec3f(weight1, weight2, weight3));
		}
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, Vec3i ids, Vec3f weights) {
			this(posTexVertx, posTexVertx.u, posTexVertx.v, ids, weights);
		}
		
		public AnimatedVertex(ModelPart.Vertex posTexVertx, float u, float v, Vec3i ids, Vec3f weights) {
			super(posTexVertx.pos.x(), posTexVertx.pos.y(), posTexVertx.pos.z(), u, v);
			this.jointId = ids;
			this.weight = weights;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class AnimatedPolygon {
		public final AnimatedVertex[] animatedVertexPositions;
		public final Vector3f normal;
		
		public AnimatedPolygon(AnimatedVertex[] positionsIn, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			this.normal = directionIn.step();
		}
		
		public AnimatedPolygon(AnimatedVertex[] positionsIn, float cor, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			positionsIn[0] = new AnimatedVertex(positionsIn[0], positionsIn[0].u, positionsIn[0].v + cor, positionsIn[0].jointId, positionsIn[0].weight);
			positionsIn[1] = new AnimatedVertex(positionsIn[1], positionsIn[1].u, positionsIn[1].v + cor, positionsIn[1].jointId, positionsIn[1].weight);
			positionsIn[2] = new AnimatedVertex(positionsIn[2], positionsIn[2].u, positionsIn[2].v - cor, positionsIn[2].jointId, positionsIn[2].weight);
			positionsIn[3] = new AnimatedVertex(positionsIn[3], positionsIn[3].u, positionsIn[3].v - cor, positionsIn[3].jointId, positionsIn[3].weight);
			this.normal = directionIn.step();
		}
	}
}
