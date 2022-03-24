package yesman.epicfight.api.client.model;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.Vec2f;
import yesman.epicfight.api.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class CustomModelBakery {
	static int indexCount = 0;
	static final ModelPartition HEAD = new SimplePart(9);
	static final ModelPartition LEFT_FEET = new SimplePart(5);
	static final ModelPartition RIGHT_FEET = new SimplePart(2);
	static final ModelPartition LEFT_ARM = new Limb(16, 17, 19, 19.0F, false);
	static final ModelPartition RIGHT_ARM = new Limb(11, 12, 14, 19.0F, false);
	static final ModelPartition LEFT_LEG = new Limb(4, 5, 6, 6.0F, true);
	static final ModelPartition RIGHT_LEG = new Limb(1, 2, 3, 6.0F, true);
	static final ModelPartition CHEST = new Chest();
	
	public static ClientModel bakeBipedCustomArmorModel(HumanoidModel<?> model, ArmorItem armorItem, EquipmentSlot equipmentSlot) {
		List<ModelPartBind> allBoxes = Lists.<ModelPartBind>newArrayList();
		
		model.head.setPos(0.0F, 0.0F, 0.0F);
		resetRotation(model.head);
		model.hat.setPos(0.0F, 0.0F, 0.0F);
		resetRotation(model.hat);
		model.body.setPos(0.0F, 0.0F, 0.0F);
		resetRotation(model.body);
	    model.rightArm.setPos(-5.0F, 2.0F, 0.0F);
	    resetRotation(model.rightArm);
	    //model.leftArm.mirror = true;
	    model.leftArm.setPos(5.0F, 2.0F, 0.0F);
	    resetRotation(model.leftArm);
	    model.rightLeg.setPos(-1.9F, 12.0F, 0.0F);
	    resetRotation(model.rightLeg);
	    //model.leftLeg.mirror = true;
	    model.leftLeg.setPos(1.9F, 12.0F, 0.0F);
	    resetRotation(model.leftLeg);
		
		switch (equipmentSlot) {
		case HEAD:
			allBoxes.add(new ModelPartBind(HEAD, model.head));
			allBoxes.add(new ModelPartBind(HEAD, model.hat));
			break;
		case CHEST:
			allBoxes.add(new ModelPartBind(CHEST, model.body));
			allBoxes.add(new ModelPartBind(RIGHT_ARM, model.rightArm));
			allBoxes.add(new ModelPartBind(LEFT_ARM, model.leftArm));
			break;
		case LEGS:
			allBoxes.add(new ModelPartBind(CHEST, model.body));
			allBoxes.add(new ModelPartBind(LEFT_LEG, model.leftLeg));
			allBoxes.add(new ModelPartBind(RIGHT_LEG, model.rightLeg));
			break;
		case FEET:
			allBoxes.add(new ModelPartBind(LEFT_FEET, model.leftLeg));
			allBoxes.add(new ModelPartBind(RIGHT_FEET, model.rightLeg));
			break;
		default:
			return null;
		}
		ClientModel customModel = new ClientModel(bakeMeshFromCubes(allBoxes));
		return customModel;
	}
	
	private static Mesh bakeMeshFromCubes(List<ModelPartBind> cubes) {
		List<CustomArmorVertex> vertices = Lists.<CustomArmorVertex>newArrayList();
		List<Integer> indices = Lists.<Integer>newArrayList();
		PoseStack matrixStack = new PoseStack();
		indexCount = 0;
		matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
		matrixStack.mulPose(Vector3f.XP.rotationDegrees(180.0F));
		matrixStack.translate(0, -24, 0);
		for (ModelPartBind modelPart : cubes) {
			bake(matrixStack, modelPart.partedBaker, modelPart.modelRenderer, vertices, indices);
		}
		
		return CustomArmorVertex.loadVertexInformation(vertices, ArrayUtils.toPrimitive(indices.toArray(new Integer[0])), true);
	}
	
	private static void bake(PoseStack matrixStack, ModelPartition part, ModelPart renderer, List<CustomArmorVertex> vertices, List<Integer> indices) {
		matrixStack.pushPose();
		matrixStack.translate(renderer.x, renderer.y, renderer.z);
		if (renderer.zRot != 0.0F) {
			matrixStack.mulPose(Vector3f.ZP.rotation(renderer.zRot));
		}

		if (renderer.yRot != 0.0F) {
			matrixStack.mulPose(Vector3f.YP.rotation(renderer.yRot));
		}

		if (renderer.xRot != 0.0F) {
			matrixStack.mulPose(Vector3f.XP.rotation(renderer.xRot));
		}
		
		for (ModelPart.Cube cube : renderer.cubes) {
			part.bakeCube(matrixStack, cube, vertices, indices);
		}
		
		for (ModelPart childRenderer : renderer.children.values()) {
			bake(matrixStack, part, childRenderer, vertices, indices);
		}
		
		matrixStack.popPose();
	}
	
	@OnlyIn(Dist.CLIENT)
	static class ModelPartBind {
		ModelPartition partedBaker;
		ModelPart modelRenderer;
		
		private ModelPartBind(ModelPartition partedBaker, ModelPart modelRenderer) {
			this.partedBaker = partedBaker;
			this.modelRenderer = modelRenderer;
		}
	}
	
	static void resetRotation(ModelPart modelRenderer) {
		modelRenderer.setRotation(0.0F, 0.0F, 0.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	abstract static class ModelPartition {
		public abstract void bakeCube(PoseStack matrixStack, ModelPart.Cube cube, List<CustomArmorVertex> vertices, List<Integer> indices);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class SimplePart extends ModelPartition {
		final int jointId;
		public SimplePart (int jointId) {
			this.jointId = jointId;
		}
		
		public void bakeCube(PoseStack matrixStack, ModelPart.Cube cube, List<CustomArmorVertex> vertices, List<Integer> indices) {
			for (ModelPart.Polygon quad : cube.polygons) {
				Vector3f norm = quad.normal.copy();
				norm.transform(matrixStack.last().normal());
				for (ModelPart.Vertex vertex : quad.vertices) {
					Vector4f pos = new Vector4f(vertex.pos);
					pos.transform(matrixStack.last().pose());
					vertices.add(new CustomArmorVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()).scale(0.0625F))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(jointId, 0, 0))
						.setEffectiveJointWeights(new Vec3f(1.0F, 0.0F, 0.0F))
						.setEffectiveJointNumber(1)
					);
				}
				
				indices.add(indexCount);
				indices.add(indexCount+1);
				indices.add(indexCount+3);
				indices.add(indexCount+3);
				indices.add(indexCount+1);
				indices.add(indexCount+2);
				indexCount+=4;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class Chest extends ModelPartition {
		final float cutX = 0.0F;
		final WeightPair[] cutYList = { new WeightPair(13.6666F, 0.254F, 0.746F), new WeightPair(15.8333F, 0.254F, 0.746F),
				new WeightPair(18.0F, 0.5F, 0.5F), new WeightPair(20.1666F, 0.744F, 0.256F), new WeightPair(22.3333F, 0.770F, 0.230F)};
		
		@Override
		public void bakeCube(PoseStack matrixStack, ModelPart.Cube cube, List<CustomArmorVertex> vertices, List<Integer> indices) {
			List<AnimatableCube> seperatedX = Lists.<AnimatableCube>newArrayList();
			List<AnimatableCube> seperatedXY = Lists.<AnimatableCube>newArrayList();
			for (ModelPart.Polygon quad : cube.polygons) {
				Matrix4f matrix = matrixStack.last().pose();
				ModelPart.Vertex pos0 = getTranslatedVertex(quad.vertices[0], matrix);
				ModelPart.Vertex pos1 = getTranslatedVertex(quad.vertices[1], matrix);
				ModelPart.Vertex pos2 = getTranslatedVertex(quad.vertices[2], matrix);
				ModelPart.Vertex pos3 = getTranslatedVertex(quad.vertices[3], matrix);
				Direction direction = getDirectionFromVector(quad.normal);
				
				WeightPair pos0Weight = getMatchingWeightPair(pos0.pos.y());
				WeightPair pos1Weight = getMatchingWeightPair(pos1.pos.y());
				WeightPair pos2Weight = getMatchingWeightPair(pos2.pos.y());
				WeightPair pos3Weight = getMatchingWeightPair(pos3.pos.y());
				
				if (pos1.pos.x() > this.cutX != pos2.pos.x() > this.cutX) {
					float distance = pos2.pos.x() - pos1.pos.x();
					float textureU = pos1.u + (pos2.u - pos1.u) * ((this.cutX - pos1.pos.x()) / distance);
					ModelPart.Vertex pos4 = new ModelPart.Vertex(pos0.pos.x(), this.cutX, pos0.pos.z(), textureU, pos0.v);
					ModelPart.Vertex pos5 = new ModelPart.Vertex(pos1.pos.x(), this.cutX, pos1.pos.z(), textureU, pos1.v);
					
					seperatedX.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(pos0, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new AnimatableVertex(pos4, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new AnimatableVertex(pos5, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new AnimatableVertex(pos3, 8, 7, 0, pos3Weight.weightLower, pos3Weight.weightUpper, 0)}, direction));
					seperatedX.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(pos4, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new AnimatableVertex(pos1, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new AnimatableVertex(pos2, 8, 7, 0, pos2Weight.weightLower, pos2Weight.weightUpper, 0),
							new AnimatableVertex(pos5, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0)}, direction));
				} else {
					seperatedX.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(pos0, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new AnimatableVertex(pos1, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new AnimatableVertex(pos2, 8, 7, 0, pos2Weight.weightLower, pos2Weight.weightUpper, 0),
							new AnimatableVertex(pos3, 8, 7, 0, pos3Weight.weightLower, pos3Weight.weightUpper, 0)}, direction));
				}
			}
			
			for (AnimatableCube quad : seperatedX) {
				boolean upsideDown = quad.animatedVertexPositions[1].pos.y() > quad.animatedVertexPositions[2].pos.y();
				AnimatableVertex pos0 = upsideDown ? quad.animatedVertexPositions[2] : quad.animatedVertexPositions[0];
				AnimatableVertex pos1 = upsideDown ? quad.animatedVertexPositions[3] : quad.animatedVertexPositions[1];
				AnimatableVertex pos2 = upsideDown ? quad.animatedVertexPositions[0] : quad.animatedVertexPositions[2];
				AnimatableVertex pos3 = upsideDown ? quad.animatedVertexPositions[1] : quad.animatedVertexPositions[3];
				Direction direction = getDirectionFromVector(quad.normal);
				List<WeightPair> weightPairList = getMatchingWeightPairs(pos1.pos.y(), pos2.pos.y());
				List<AnimatableVertex> addedVertexList = Lists.<AnimatableVertex>newArrayList();
				addedVertexList.add(pos0);
				addedVertexList.add(pos1);
				
				if (weightPairList.size() > 0) {
					for (WeightPair weightPair : weightPairList) {
						float distance = pos2.pos.y() - pos1.pos.y();
						float textureV = pos1.v + (pos2.v - pos1.v) * ((weightPair.cutY - pos1.pos.y()) / distance);
						ModelPart.Vertex pos4 = new ModelPart.Vertex(pos0.pos.x(), weightPair.cutY, pos0.pos.z(), pos0.u, textureV);
						ModelPart.Vertex pos5 = new ModelPart.Vertex(pos1.pos.x(), weightPair.cutY, pos1.pos.z(), pos1.u, textureV);
						
						addedVertexList.add(new AnimatableVertex(pos4, 8, 7, 0, weightPair.weightLower, weightPair.weightUpper, 0));
						addedVertexList.add(new AnimatableVertex(pos5, 8, 7, 0, weightPair.weightLower, weightPair.weightUpper, 0));
					}
				}
				
				addedVertexList.add(pos3);
				addedVertexList.add(pos2);
				
				for (int i = 0; i < (addedVertexList.size() - 2) / 2; i++) {
					int start = i*2;
					AnimatableVertex p0 = addedVertexList.get(start);
					AnimatableVertex p1 = addedVertexList.get(start + 1);
					AnimatableVertex p2 = addedVertexList.get(start + 3);
					AnimatableVertex p3 = addedVertexList.get(start + 2);
					seperatedXY.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(p0, 8, 7, 0, p0.weight.x, p0.weight.y, 0),
							new AnimatableVertex(p1, 8, 7, 0, p1.weight.x, p1.weight.y, 0),
							new AnimatableVertex(p2, 8, 7, 0, p2.weight.x, p2.weight.y, 0),
							new AnimatableVertex(p3, 8, 7, 0, p3.weight.x, p3.weight.y, 0)}, direction));
				}
			}
			
			for (AnimatableCube quad : seperatedXY) {
				Vector3f norm = quad.normal.copy();
				norm.transform(matrixStack.last().normal());
				for (AnimatableVertex vertex : quad.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.pos);
					float weight1 = vertex.weight.x;
					float weight2 = vertex.weight.y;
					int joint1 = vertex.jointId.getX();
					int joint2 = vertex.jointId.getY();
					int count = weight1 > 0.0F && weight2 > 0.0F ? 2 : 1;
					
					if(weight1 <= 0.0F) {
						joint1 = joint2;
						weight1 = weight2;
					}
					
					vertices.add(new CustomArmorVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()).scale(0.0625F))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(joint1, joint2, 0))
						.setEffectiveJointWeights(new Vec3f(weight1, weight2, 0.0F))
						.setEffectiveJointNumber(count)
					);
				}
				
				indices.add(indexCount);
				indices.add(indexCount+1);
				indices.add(indexCount+3);
				indices.add(indexCount+3);
				indices.add(indexCount+1);
				indices.add(indexCount+2);
				indexCount+=4;
			}
		}
		
		WeightPair getMatchingWeightPair(float y) {
			if (y < this.cutYList[0].cutY) {
				return new WeightPair(y, 0.0F, 1.0F);
			}
			
			int index = -1;
			for (int i = 0; i < this.cutYList.length; i++) {
				
			}
			
			if (index > 0) {
				WeightPair pair = cutYList[index];
				return new WeightPair(y, pair.weightLower, pair.weightUpper);
			}
			
			return new WeightPair(y, 1.0F, 0.0F);
		}
		
		List<WeightPair> getMatchingWeightPairs(float minY, float maxY) {
			List<WeightPair> cutYs = Lists.<WeightPair>newArrayList();
			for (WeightPair pair : this.cutYList) {
				if(pair.cutY > minY && maxY >= pair.cutY) {
					cutYs.add(pair);
				}
			}
			return cutYs;
		}
		
		static class WeightPair {
			final float cutY;
			final float weightLower;
			final float weightUpper;
			
			public WeightPair(float cutY, float weightLower, float weightUpper) {
				this.cutY = cutY;
				this.weightLower = weightLower;
				this.weightUpper = weightUpper;
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class Limb extends ModelPartition {
		final int upperJointId;
		final int lowerJointId;
		final int middleJointId;
		final float cutY;
		final boolean frontOrBack;
		
		public Limb (int upperJointId, int lowerJointId, int middleJointId, float cutY, boolean frontOrBack) {
			this.upperJointId = upperJointId;
			this.lowerJointId = lowerJointId;
			this.middleJointId = middleJointId;
			this.cutY = cutY;
			this.frontOrBack = frontOrBack;
		}
		
		@Override
		public void bakeCube(PoseStack matrixStack, ModelPart.Cube cube, List<CustomArmorVertex> vertices, List<Integer> indices) {
			List<AnimatableCube> polygons = Lists.<AnimatableCube>newArrayList();
			for (ModelPart.Polygon quad : cube.polygons) {
				Matrix4f matrix = matrixStack.last().pose();
				ModelPart.Vertex pos0 = getTranslatedVertex(quad.vertices[0], matrix);
				ModelPart.Vertex pos1 = getTranslatedVertex(quad.vertices[1], matrix);
				ModelPart.Vertex pos2 = getTranslatedVertex(quad.vertices[2], matrix);
				ModelPart.Vertex pos3 = getTranslatedVertex(quad.vertices[3], matrix);
				Direction direction = getDirectionFromVector(quad.normal);
				if (pos1.pos.y() > this.cutY != pos2.pos.y() > this.cutY) {
					float distance = pos2.pos.y() - pos1.pos.y();
					float textureV = pos1.v + (pos2.v - pos1.v) * ((this.cutY - pos1.pos.y()) / distance);
					ModelPart.Vertex pos4 = new ModelPart.Vertex(pos0.pos.x(), this.cutY, pos0.pos.z(), pos0.u, textureV);
					ModelPart.Vertex pos5 = new ModelPart.Vertex(pos1.pos.x(), this.cutY, pos1.pos.z(), pos1.u, textureV);
					
					int upperId, lowerId;
					if (distance > 0) {
						upperId = this.lowerJointId;
						lowerId = this.upperJointId;
					} else {
						upperId = this.upperJointId;
						lowerId = this.lowerJointId;
					}
					
					polygons.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(pos0, upperId), new AnimatableVertex(pos1, upperId),
							new AnimatableVertex(pos5, upperId), new AnimatableVertex(pos4, upperId)}, direction));
					polygons.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(pos4, lowerId), new AnimatableVertex(pos5, lowerId),
							new AnimatableVertex(pos2, lowerId), new AnimatableVertex(pos3, lowerId)}, direction));
					
					boolean hasSameZ = pos4.pos.z() < 0.0F == pos5.pos.z() < 0.0F;
					boolean isFront = hasSameZ && (pos4.pos.z() < 0.0F == this.frontOrBack);
					
					if (isFront) {
						polygons.add(new AnimatableCube(new AnimatableVertex[] {
								new AnimatableVertex(pos4, this.middleJointId), new AnimatableVertex(pos5, this.middleJointId),
								new AnimatableVertex(pos5, this.upperJointId), new AnimatableVertex(pos4, this.upperJointId)}, 0.001F, direction));
						polygons.add(new AnimatableCube(new AnimatableVertex[] {
								new AnimatableVertex(pos4, this.lowerJointId), new AnimatableVertex(pos5, this.lowerJointId),
								new AnimatableVertex(pos5, this.middleJointId), new AnimatableVertex(pos4, this.middleJointId)}, 0.001F, direction));
					} else if (!hasSameZ) {
						boolean startFront = pos4.pos.z() > 0;
						int firstJoint = this.lowerJointId;
						int secondJoint = this.lowerJointId;
						int thirdJoint = startFront ? this.upperJointId : this.middleJointId;
						int fourthJoint = startFront ? this.middleJointId : this.upperJointId;
						int fifthJoint = this.upperJointId;
						int sixthJoint = this.upperJointId;
						
						polygons.add(new AnimatableCube(new AnimatableVertex[] {
								new AnimatableVertex(pos4, firstJoint), new AnimatableVertex(pos5, secondJoint),
								new AnimatableVertex(pos5, thirdJoint), new AnimatableVertex(pos4, fourthJoint)}, 0.001F, direction));
						polygons.add(new AnimatableCube(new AnimatableVertex[] {
								new AnimatableVertex(pos4, fourthJoint), new AnimatableVertex(pos5, thirdJoint),
								new AnimatableVertex(pos5, fifthJoint), new AnimatableVertex(pos4, sixthJoint)}, 0.001F, direction));
					}
				} else {
					int jointId = pos0.pos.y() > this.cutY ? this.upperJointId : this.lowerJointId;
					polygons.add(new AnimatableCube(new AnimatableVertex[] {
							new AnimatableVertex(pos0, jointId), new AnimatableVertex(pos1, jointId),
							new AnimatableVertex(pos2, jointId), new AnimatableVertex(pos3, jointId)}, direction));
				}
			}
			
			for (AnimatableCube quad : polygons) {
				Vector3f norm = quad.normal.copy();
				norm.transform(matrixStack.last().normal());
				
				for (AnimatableVertex vertex : quad.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.pos);
					vertices.add(new CustomArmorVertex()
						.setPosition(new Vec3f(pos.x(), pos.y(), pos.z()).scale(0.0625F))
						.setNormal(new Vec3f(norm.x(), norm.y(), norm.z()))
						.setTextureCoordinate(new Vec2f(vertex.u, vertex.v))
						.setEffectiveJointIDs(new Vec3f(vertex.jointId.getX(), 0, 0))
						.setEffectiveJointWeights(new Vec3f(1.0F, 0.0F, 0.0F))
						.setEffectiveJointNumber(1)
					);
				}
				
				indices.add(indexCount);
				indices.add(indexCount+1);
				indices.add(indexCount+3);
				indices.add(indexCount+3);
				indices.add(indexCount+1);
				indices.add(indexCount+2);
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
	
	static ModelPart.Vertex getTranslatedVertex(ModelPart.Vertex original, Matrix4f matrix) {
		Vector4f translatedPosition = new Vector4f(original.pos);
		translatedPosition.transform(matrix);
		
		return new ModelPart.Vertex(translatedPosition.x(), translatedPosition.y(), translatedPosition.z(), original.u, original.v);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class AnimatableVertex extends ModelPart.Vertex {
		final Vec3i jointId;
		final Vec3f weight;
		
		public AnimatableVertex(ModelPart.Vertex posTexVertx, int jointId) {
			this(posTexVertx, jointId, 0, 0, 1.0F, 0.0F, 0.0F);
		}
		
		public AnimatableVertex(ModelPart.Vertex posTexVertx, int jointId1, int jointId2, int jointId3, float weight1, float weight2, float weight3) {
			this(posTexVertx, new Vec3i(jointId1, jointId2, jointId3), new Vec3f(weight1, weight2, weight3));
		}
		
		public AnimatableVertex(ModelPart.Vertex posTexVertx, Vec3i ids, Vec3f weights) {
			this(posTexVertx, posTexVertx.u, posTexVertx.v, ids, weights);
		}
		
		public AnimatableVertex(ModelPart.Vertex posTexVertx, float u, float v, Vec3i ids, Vec3f weights) {
			super(posTexVertx.pos.x(), posTexVertx.pos.y(), posTexVertx.pos.z(), u, v);
			this.jointId = ids;
			this.weight = weights;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class AnimatableCube {
		public final AnimatableVertex[] animatedVertexPositions;
		public final Vector3f normal;
		
		public AnimatableCube(AnimatableVertex[] positionsIn, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			this.normal = directionIn.step();
		}
		
		public AnimatableCube(AnimatableVertex[] positionsIn, float cor, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			positionsIn[0] = new AnimatableVertex(positionsIn[0], positionsIn[0].u, positionsIn[0].v + cor, positionsIn[0].jointId, positionsIn[0].weight);
			positionsIn[1] = new AnimatableVertex(positionsIn[1], positionsIn[1].u, positionsIn[1].v + cor, positionsIn[1].jointId, positionsIn[1].weight);
			positionsIn[2] = new AnimatableVertex(positionsIn[2], positionsIn[2].u, positionsIn[2].v - cor, positionsIn[2].jointId, positionsIn[2].weight);
			positionsIn[3] = new AnimatableVertex(positionsIn[3], positionsIn[3].u, positionsIn[3].v - cor, positionsIn[3].jointId, positionsIn[3].weight);
			this.normal = directionIn.step();
		}
	}
}