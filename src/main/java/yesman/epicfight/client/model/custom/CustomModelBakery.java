package yesman.epicfight.client.model.custom;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.model.ModelRenderer.ModelBox;
import net.minecraft.client.renderer.model.ModelRenderer.PositionTextureVertex;
import net.minecraft.client.renderer.model.ModelRenderer.TexturedQuad;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.model.ClientModel;
import yesman.epicfight.client.model.Mesh;
import yesman.epicfight.collada.VertexData;
import yesman.epicfight.utils.math.Vec2f;
import yesman.epicfight.utils.math.Vec3f;

@OnlyIn(Dist.CLIENT)
public class CustomModelBakery {
	static int indexCount = 0;
	static final ModelPart HEAD = new SimplePart(9);
	static final ModelPart LEFT_FEET = new SimplePart(5);
	static final ModelPart RIGHT_FEET = new SimplePart(2);
	static final ModelPart LEFT_ARM = new Limb(16, 17, 19, 19.0F, false);
	static final ModelPart RIGHT_ARM = new Limb(11, 12, 14, 19.0F, false);
	static final ModelPart LEFT_LEG = new Limb(4, 5, 6, 6.0F, true);
	static final ModelPart RIGHT_LEG = new Limb(1, 2, 3, 6.0F, true);
	static final ModelPart CHEST = new Chest();
	
	public static ClientModel bakeCustomArmorModel(BipedModel<?> model, ArmorItem armorItem, EquipmentSlotType equipmentSlot) {
		List<ModelPartBind> allBoxes = Lists.<ModelPartBind>newArrayList();
		model.bipedHead.setRotationPoint(0.0F, 0.0F, 0.0F);
		resetRotation(model.bipedHead);
		model.bipedHeadwear.setRotationPoint(0.0F, 0.0F, 0.0F);
		resetRotation(model.bipedHeadwear);
		model.bipedBody.setRotationPoint(0.0F, 0.0F, 0.0F);
		resetRotation(model.bipedBody);
	    model.bipedRightArm.setRotationPoint(-5.0F, 2.0F, 0.0F);
	    resetRotation(model.bipedRightArm);
	    model.bipedLeftArm.mirror = true;
	    model.bipedLeftArm.setRotationPoint(5.0F, 2.0F, 0.0F);
	    resetRotation(model.bipedLeftArm);
	    model.bipedRightLeg.setRotationPoint(-1.9F, 12.0F, 0.0F);
	    resetRotation(model.bipedRightLeg);
	    model.bipedLeftLeg.mirror = true;
	    model.bipedLeftLeg.setRotationPoint(1.9F, 12.0F, 0.0F);
	    resetRotation(model.bipedLeftLeg);
	    
		switch (equipmentSlot) {
		case HEAD:
			allBoxes.add(new ModelPartBind(HEAD, model.bipedHead));
			allBoxes.add(new ModelPartBind(HEAD, model.bipedHeadwear));
			break;
		case CHEST:
			allBoxes.add(new ModelPartBind(CHEST, model.bipedBody));
			allBoxes.add(new ModelPartBind(RIGHT_ARM, model.bipedRightArm));
			allBoxes.add(new ModelPartBind(LEFT_ARM, model.bipedLeftArm));
			break;
		case LEGS:
			allBoxes.add(new ModelPartBind(CHEST, model.bipedBody));
			allBoxes.add(new ModelPartBind(LEFT_LEG, model.bipedLeftLeg));
			allBoxes.add(new ModelPartBind(RIGHT_LEG, model.bipedRightLeg));
			break;
		case FEET:
			allBoxes.add(new ModelPartBind(LEFT_FEET, model.bipedLeftLeg));
			allBoxes.add(new ModelPartBind(RIGHT_FEET, model.bipedRightLeg));
			break;
		default:
			return null;
		}
		ClientModel customModel = new ClientModel(bakeMeshFromCubes(allBoxes));
		return customModel;
	}
	
	private static Mesh bakeMeshFromCubes(List<ModelPartBind> cubes) {
		List<VertexData> vertices = Lists.<VertexData>newArrayList();
		List<Integer> indices = Lists.<Integer>newArrayList();
		MatrixStack matrixStack = new MatrixStack();
		indexCount = 0;
		matrixStack.rotate(Vector3f.YP.rotationDegrees(180.0F));
		matrixStack.rotate(Vector3f.XP.rotationDegrees(180.0F));
		matrixStack.translate(0, -24, 0);
		for (ModelPartBind modelPart : cubes) {
			bake(matrixStack, modelPart.partedBaker, modelPart.modelRenderer, vertices, indices);
		}
		
		return VertexData.loadVertexInformation(vertices, ArrayUtils.toPrimitive(indices.toArray(new Integer[0])), true);
	}
	
	private static void bake(MatrixStack matrixStack, ModelPart part, ModelRenderer renderer, List<VertexData> vertices, List<Integer> indices) {
		matrixStack.push();
		matrixStack.translate(renderer.rotationPointX, renderer.rotationPointY, renderer.rotationPointZ);
		if (renderer.rotateAngleZ != 0.0F) {
			matrixStack.rotate(Vector3f.ZP.rotation(renderer.rotateAngleZ));
		}

		if (renderer.rotateAngleY != 0.0F) {
			matrixStack.rotate(Vector3f.YP.rotation(renderer.rotateAngleY));
		}

		if (renderer.rotateAngleX != 0.0F) {
			matrixStack.rotate(Vector3f.XP.rotation(renderer.rotateAngleX));
		}
		
		for (ModelBox cube : renderer.cubeList) {
			part.bakeCube(matrixStack, cube, vertices, indices);
		}
		
		for (ModelRenderer childRenderer : renderer.childModels) {
			bake(matrixStack, part, childRenderer, vertices, indices);
		}
		
		matrixStack.pop();
	}
	
	@OnlyIn(Dist.CLIENT)
	static class ModelPartBind {
		ModelRenderer modelRenderer;
		ModelPart partedBaker;
		
		public ModelPartBind(ModelPart partedBaker, ModelRenderer modelRenderer) {
			this.partedBaker = partedBaker;
			this.modelRenderer = modelRenderer;
		}
	}
	
	static void resetRotation(ModelRenderer modelRenderer) {
		modelRenderer.rotateAngleX = 0.0F;
		modelRenderer.rotateAngleY = 0.0F;
		modelRenderer.rotateAngleZ = 0.0F;
	}
	
	@OnlyIn(Dist.CLIENT)
	abstract static class ModelPart {
		public abstract void bakeCube(MatrixStack matrixStack, ModelBox cube, List<VertexData> vertices, List<Integer> indices);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class SimplePart extends ModelPart {
		final int jointId;
		public SimplePart (int jointId) {
			this.jointId = jointId;
		}
		
		public void bakeCube(MatrixStack matrixStack, ModelBox cube, List<VertexData> vertices, List<Integer> indices) {
			for (TexturedQuad quad : cube.quads) {
				Vector3f norm = quad.normal.copy();
				norm.transform(matrixStack.getLast().getNormal());
				for (PositionTextureVertex vertex : quad.vertexPositions) {
					Vector4f pos = new Vector4f(vertex.position);
					pos.transform(matrixStack.getLast().getMatrix());
					vertices.add(new VertexData()
						.setPosition(new Vec3f(pos.getX(), pos.getY(), pos.getZ()).scale(0.0625F))
						.setNormal(new Vec3f(norm.getX(), norm.getY(), norm.getZ()))
						.setTextureCoordinate(new Vec2f(vertex.textureU, vertex.textureV))
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
	static class Chest extends ModelPart {
		final float cutX = 0.0F;
		final WeightPair[] cutYList = { new WeightPair(13.6666F, 0.254F, 0.746F), new WeightPair(15.8333F, 0.254F, 0.746F),
				new WeightPair(18.0F, 0.5F, 0.5F), new WeightPair(20.1666F, 0.744F, 0.256F), new WeightPair(22.3333F, 0.770F, 0.230F)};
		
		@Override
		public void bakeCube(MatrixStack matrixStack, ModelBox cube, List<VertexData> vertices, List<Integer> indices) {
			List<TexturedJointQuad> seperatedX = Lists.<TexturedJointQuad>newArrayList();
			List<TexturedJointQuad> seperatedXY = Lists.<TexturedJointQuad>newArrayList();
			for (TexturedQuad quad : cube.quads) {
				Matrix4f matrix = matrixStack.getLast().getMatrix();
				PositionTextureVertex pos0 = getTranslatedVertex(quad.vertexPositions[0], matrix);
				PositionTextureVertex pos1 = getTranslatedVertex(quad.vertexPositions[1], matrix);
				PositionTextureVertex pos2 = getTranslatedVertex(quad.vertexPositions[2], matrix);
				PositionTextureVertex pos3 = getTranslatedVertex(quad.vertexPositions[3], matrix);
				Direction direction = getDirectionFromVector(quad.normal);
				
				WeightPair pos0Weight = getMatchingWeightPair(pos0.position.getY());
				WeightPair pos1Weight = getMatchingWeightPair(pos1.position.getY());
				WeightPair pos2Weight = getMatchingWeightPair(pos2.position.getY());
				WeightPair pos3Weight = getMatchingWeightPair(pos3.position.getY());
				
				if (pos1.position.getX() > this.cutX != pos2.position.getX() > this.cutX) {
					float distance = pos2.position.getX() - pos1.position.getX();
					float textureU = pos1.textureU + (pos2.textureU - pos1.textureU) * ((this.cutX - pos1.position.getX()) / distance);
					PositionTextureVertex pos4 = new PositionTextureVertex(pos0.position.getX(), this.cutX, pos0.position.getZ(), textureU, pos0.textureV);
					PositionTextureVertex pos5 = new PositionTextureVertex(pos1.position.getX(), this.cutX, pos1.position.getZ(), textureU, pos1.textureV);
					
					seperatedX.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos4, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos5, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos3, 8, 7, 0, pos3Weight.weightLower, pos3Weight.weightUpper, 0)}, direction));
					seperatedX.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos4, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos1, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos2, 8, 7, 0, pos2Weight.weightLower, pos2Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos5, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0)}, direction));
				} else {
					seperatedX.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos1, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos2, 8, 7, 0, pos2Weight.weightLower, pos2Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos3, 8, 7, 0, pos3Weight.weightLower, pos3Weight.weightUpper, 0)}, direction));
				}
			}
			
			for (TexturedJointQuad quad : seperatedX) {
				boolean upsideDown = quad.animatedVertexPositions[1].position.getY() > quad.animatedVertexPositions[2].position.getY();
				PositionTextureJointVertex pos0 = upsideDown ? quad.animatedVertexPositions[2] : quad.animatedVertexPositions[0];
				PositionTextureJointVertex pos1 = upsideDown ? quad.animatedVertexPositions[3] : quad.animatedVertexPositions[1];
				PositionTextureJointVertex pos2 = upsideDown ? quad.animatedVertexPositions[0] : quad.animatedVertexPositions[2];
				PositionTextureJointVertex pos3 = upsideDown ? quad.animatedVertexPositions[1] : quad.animatedVertexPositions[3];
				Direction direction = getDirectionFromVector(quad.normal);
				List<WeightPair> weightPairList = getMatchingWeightPairs(pos1.position.getY(), pos2.position.getY());
				List<PositionTextureJointVertex> addedVertexList = Lists.<PositionTextureJointVertex>newArrayList();
				addedVertexList.add(pos0);
				addedVertexList.add(pos1);
				
				if (weightPairList.size() > 0) {
					for (WeightPair weightPair : weightPairList) {
						float distance = pos2.position.getY() - pos1.position.getY();
						float textureV = pos1.textureV + (pos2.textureV - pos1.textureV) * ((weightPair.cutY - pos1.position.getY()) / distance);
						PositionTextureVertex pos4 = new PositionTextureVertex(pos0.position.getX(), weightPair.cutY, pos0.position.getZ(), pos0.textureU, textureV);
						PositionTextureVertex pos5 = new PositionTextureVertex(pos1.position.getX(), weightPair.cutY, pos1.position.getZ(), pos1.textureU, textureV);
						
						addedVertexList.add(new PositionTextureJointVertex(pos4, 8, 7, 0, weightPair.weightLower, weightPair.weightUpper, 0));
						addedVertexList.add(new PositionTextureJointVertex(pos5, 8, 7, 0, weightPair.weightLower, weightPair.weightUpper, 0));
					}
				}
				
				addedVertexList.add(pos3);
				addedVertexList.add(pos2);
				
				for (int i = 0; i < (addedVertexList.size() - 2) / 2; i++) {
					int start = i*2;
					PositionTextureJointVertex p0 = addedVertexList.get(start);
					PositionTextureJointVertex p1 = addedVertexList.get(start + 1);
					PositionTextureJointVertex p2 = addedVertexList.get(start + 3);
					PositionTextureJointVertex p3 = addedVertexList.get(start + 2);
					seperatedXY.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(p0, 8, 7, 0, p0.weight.getX(), p0.weight.getY(), 0),
							new PositionTextureJointVertex(p1, 8, 7, 0, p1.weight.getX(), p1.weight.getY(), 0),
							new PositionTextureJointVertex(p2, 8, 7, 0, p2.weight.getX(), p2.weight.getY(), 0),
							new PositionTextureJointVertex(p3, 8, 7, 0, p3.weight.getX(), p3.weight.getY(), 0)}, direction));
				}
			}
			
			for (TexturedJointQuad quad : seperatedXY) {
				Vector3f norm = quad.normal.copy();
				norm.transform(matrixStack.getLast().getNormal());
				for (PositionTextureJointVertex vertex : quad.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.position);
					float weight1 = vertex.weight.getX();
					float weight2 = vertex.weight.getY();
					int joint1 = vertex.jointId.getX();
					int joint2 = vertex.jointId.getY();
					int count = weight1 > 0.0F && weight2 > 0.0F ? 2 : 1;
					
					if(weight1 <= 0.0F) {
						joint1 = joint2;
						weight1 = weight2;
					}
					
					vertices.add(new VertexData()
						.setPosition(new Vec3f(pos.getX(), pos.getY(), pos.getZ()).scale(0.0625F))
						.setNormal(new Vec3f(norm.getX(), norm.getY(), norm.getZ()))
						.setTextureCoordinate(new Vec2f(vertex.textureU, vertex.textureV))
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
	static class Limb extends ModelPart {
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
		public void bakeCube(MatrixStack matrixStack, ModelBox cube, List<VertexData> vertices, List<Integer> indices) {
			List<TexturedJointQuad> quads = Lists.<TexturedJointQuad>newArrayList();
			for (TexturedQuad quad : cube.quads) {
				Matrix4f matrix = matrixStack.getLast().getMatrix();
				PositionTextureVertex pos0 = getTranslatedVertex(quad.vertexPositions[0], matrix);
				PositionTextureVertex pos1 = getTranslatedVertex(quad.vertexPositions[1], matrix);
				PositionTextureVertex pos2 = getTranslatedVertex(quad.vertexPositions[2], matrix);
				PositionTextureVertex pos3 = getTranslatedVertex(quad.vertexPositions[3], matrix);
				Direction direction = getDirectionFromVector(quad.normal);
				if (pos1.position.getY() > this.cutY != pos2.position.getY() > this.cutY) {
					float distance = pos2.position.getY() - pos1.position.getY();
					float textureV = pos1.textureV + (pos2.textureV - pos1.textureV) * ((this.cutY - pos1.position.getY()) / distance);
					PositionTextureVertex pos4 = new PositionTextureVertex(pos0.position.getX(), this.cutY, pos0.position.getZ(), pos0.textureU, textureV);
					PositionTextureVertex pos5 = new PositionTextureVertex(pos1.position.getX(), this.cutY, pos1.position.getZ(), pos1.textureU, textureV);
					
					int upperId, lowerId;
					if (distance > 0) {
						upperId = this.lowerJointId;
						lowerId = this.upperJointId;
					} else {
						upperId = this.upperJointId;
						lowerId = this.lowerJointId;
					}
					
					quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, upperId), new PositionTextureJointVertex(pos1, upperId),
							new PositionTextureJointVertex(pos5, upperId), new PositionTextureJointVertex(pos4, upperId)}, direction));
					quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos4, lowerId), new PositionTextureJointVertex(pos5, lowerId),
							new PositionTextureJointVertex(pos2, lowerId), new PositionTextureJointVertex(pos3, lowerId)}, direction));
					
					boolean hasSameZ = pos4.position.getZ() < 0.0F == pos5.position.getZ() < 0.0F;
					boolean isFront = hasSameZ && (pos4.position.getZ() < 0.0F == this.frontOrBack);
					
					if (isFront) {
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, this.middleJointId), new PositionTextureJointVertex(pos5, this.middleJointId),
								new PositionTextureJointVertex(pos5, this.upperJointId), new PositionTextureJointVertex(pos4, this.upperJointId)}, 0.001F, direction));
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, this.lowerJointId), new PositionTextureJointVertex(pos5, this.lowerJointId),
								new PositionTextureJointVertex(pos5, this.middleJointId), new PositionTextureJointVertex(pos4, this.middleJointId)}, 0.001F, direction));
					} else if (!hasSameZ) {
						boolean startFront = pos4.position.getZ() > 0;
						int firstJoint = this.lowerJointId;
						int secondJoint = this.lowerJointId;
						int thirdJoint = startFront ? this.upperJointId : this.middleJointId;
						int fourthJoint = startFront ? this.middleJointId : this.upperJointId;
						int fifthJoint = this.upperJointId;
						int sixthJoint = this.upperJointId;
						
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, firstJoint), new PositionTextureJointVertex(pos5, secondJoint),
								new PositionTextureJointVertex(pos5, thirdJoint), new PositionTextureJointVertex(pos4, fourthJoint)}, 0.001F, direction));
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, fourthJoint), new PositionTextureJointVertex(pos5, thirdJoint),
								new PositionTextureJointVertex(pos5, fifthJoint), new PositionTextureJointVertex(pos4, sixthJoint)}, 0.001F, direction));
					}
				} else {
					int jointId = pos0.position.getY() > this.cutY ? this.upperJointId : this.lowerJointId;
					quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, jointId), new PositionTextureJointVertex(pos1, jointId),
							new PositionTextureJointVertex(pos2, jointId), new PositionTextureJointVertex(pos3, jointId)}, direction));
				}
			}
			
			for (TexturedJointQuad quad : quads) {
				Vector3f norm = quad.normal.copy();
				norm.transform(matrixStack.getLast().getNormal());
				
				for (PositionTextureJointVertex vertex : quad.animatedVertexPositions) {
					Vector4f pos = new Vector4f(vertex.position);
					vertices.add(new VertexData()
						.setPosition(new Vec3f(pos.getX(), pos.getY(), pos.getZ()).scale(0.0625F))
						.setNormal(new Vec3f(norm.getX(), norm.getY(), norm.getZ()))
						.setTextureCoordinate(new Vec2f(vertex.textureU, vertex.textureV))
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
			Vector3f direcVec = new Vector3f(Float.compare(directionVec.getX(), -0.0F) == 0 ? 0.0F : directionVec.getX(), directionVec.getY(), directionVec.getZ());
			if (direcVec.equals(direction.toVector3f())) {
				return direction;
			}
		}
		
		return null;
	}
	
	static PositionTextureVertex getTranslatedVertex(PositionTextureVertex original, Matrix4f matrix) {
		Vector4f translatedPosition = new Vector4f(original.position);
		translatedPosition.transform(matrix);
		return new PositionTextureVertex(translatedPosition.getX(), translatedPosition.getY(), translatedPosition.getZ(), original.textureU, original.textureV);
	}
	
	@OnlyIn(Dist.CLIENT)
	static class PositionTextureJointVertex extends PositionTextureVertex {
		final Vector3i jointId;
		final Vector3f weight;
		
		public PositionTextureJointVertex(PositionTextureVertex posTexVertx, int jointId) {
			this(posTexVertx, jointId, 0, 0, 1.0F, 0.0F, 0.0F);
		}
		
		public PositionTextureJointVertex(PositionTextureVertex posTexVertx, int jointId1, int jointId2, int jointId3, float weight1, float weight2, float weight3) {
			this(posTexVertx, new Vector3i(jointId1, jointId2, jointId3), new Vector3f(weight1, weight2, weight3));
		}
		
		public PositionTextureJointVertex(PositionTextureVertex posTexVertx, Vector3i ids, Vector3f weights) {
			super(posTexVertx.position.getX(), posTexVertx.position.getY(), posTexVertx.position.getZ(), posTexVertx.textureU, posTexVertx.textureV);
			this.jointId = ids;
			this.weight = weights;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	static class TexturedJointQuad {
		public final PositionTextureJointVertex[] animatedVertexPositions;
		public final Vector3f normal;
		
		public TexturedJointQuad(PositionTextureJointVertex[] positionsIn, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			this.normal = directionIn.toVector3f();
		}
		
		public TexturedJointQuad(PositionTextureJointVertex[] positionsIn, float cor, Direction directionIn) {
			this.animatedVertexPositions = positionsIn;
			positionsIn[0] = new PositionTextureJointVertex(positionsIn[0].setTextureUV(positionsIn[0].textureU, positionsIn[0].textureV + cor),
					positionsIn[0].jointId, positionsIn[0].weight);
			positionsIn[1] = new PositionTextureJointVertex(positionsIn[1].setTextureUV(positionsIn[1].textureU, positionsIn[1].textureV + cor),
					positionsIn[1].jointId, positionsIn[1].weight);
			positionsIn[2] = new PositionTextureJointVertex(positionsIn[2].setTextureUV(positionsIn[2].textureU, positionsIn[2].textureV - cor),
					positionsIn[2].jointId, positionsIn[2].weight);
			positionsIn[3] = new PositionTextureJointVertex(positionsIn[3].setTextureUV(positionsIn[3].textureU, positionsIn[3].textureV - cor),
					positionsIn[3].jointId, positionsIn[3].weight);
			this.normal = directionIn.toVector3f();
		}
	}
}