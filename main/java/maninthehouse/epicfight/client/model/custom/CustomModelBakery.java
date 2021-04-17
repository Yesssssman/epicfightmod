package maninthehouse.epicfight.client.model.custom;

import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.Lists;

import maninthehouse.epicfight.client.model.ClientModel;
import maninthehouse.epicfight.client.model.Mesh;
import maninthehouse.epicfight.collada.VertexData;
import maninthehouse.epicfight.utils.math.Vec2f;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
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
	
	public static ClientModel bakeBipedCustomArmorModel(ModelBiped model, ItemArmor armorItem) {
		EntityEquipmentSlot equipmentSlot = armorItem.getEquipmentSlot();
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
			allBoxes.add(new ModelPartBind(LEFT_LEG, model.bipedLeftLeg));
			allBoxes.add(new ModelPartBind(RIGHT_LEG, model.bipedRightLeg));
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
		VisibleMatrix4f matrix4f = new VisibleMatrix4f();
		indexCount = 0;
		
		matrix4f.rotateDegree(180.0F, new Vec3f(0, 1, 0));
		matrix4f.rotateDegree(180.0F, new Vec3f(1, 0, 0));
		matrix4f.translate(0.0F, -24.0F, 0.0F);

		for (ModelPartBind modelPart : cubes) {
			bake(matrix4f, modelPart.partedBaker, modelPart.modelRenderer, vertices, indices);
		}
		
		return VertexData.loadVertexInformation(vertices, ArrayUtils.toPrimitive(indices.toArray(new Integer[0])), true);
	}
	
	private static void bake(VisibleMatrix4f matrix, ModelPart part, ModelRenderer renderer, List<VertexData> vertices, List<Integer> indices) {
		VisibleMatrix4f internalMatrix = new VisibleMatrix4f(matrix);
		internalMatrix.translate(renderer.rotationPointX, renderer.rotationPointY, renderer.rotationPointZ);
		
		if (renderer.rotateAngleZ != 0.0F) {
			internalMatrix.rotateDegree(renderer.rotateAngleZ, new Vec3f(0, 0, 1));
		}

		if (renderer.rotateAngleY != 0.0F) {
			internalMatrix.rotateDegree(renderer.rotateAngleY, new Vec3f(0, 1, 0));
		}

		if (renderer.rotateAngleX != 0.0F) {
			internalMatrix.rotateDegree(renderer.rotateAngleX, new Vec3f(1, 0, 0));
		}
		
		for (ModelBox cube : renderer.cubeList) {
			part.bakeCube(internalMatrix, cube, vertices, indices);
		}
		
		if (renderer.childModels != null) {
			for (ModelRenderer childRenderer : renderer.childModels) {
				bake(internalMatrix, part, childRenderer, vertices, indices);
			}
		}
	}
	
	@SideOnly(Side.CLIENT)
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
	
	@SideOnly(Side.CLIENT)
	abstract static class ModelPart {
		public abstract void bakeCube(VisibleMatrix4f matrixStack, ModelBox cube, List<VertexData> vertices, List<Integer> indices);
	}
	
	@SideOnly(Side.CLIENT)
	static class SimplePart extends ModelPart {
		final int jointId;
		public SimplePart (int jointId) {
			this.jointId = jointId;
		}
		
		public void bakeCube(VisibleMatrix4f matrix, ModelBox cube, List<VertexData> vertices, List<Integer> indices) {
			for (TexturedQuad quad : cube.quadList) {
				Vec3d vec3d = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[0].vector3D);
		        Vec3d vec3d1 = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[2].vector3D);
		        Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
		        Vec4f norm = new Vec4f(vec3d2);
		        VisibleMatrix4f.transform(matrix, norm, norm);
				
				for (PositionTextureVertex vertex : quad.vertexPositions) {
					Vec4f pos = new Vec4f(vertex.vector3D);
					VisibleMatrix4f.transform(matrix, pos, pos);
					vertices.add(new VertexData()
						.setPosition(new Vec3f(pos.x, pos.y, pos.z).scale(0.0625F))
						.setNormal(new Vec3f(norm.x, norm.y, norm.z))
						.setTextureCoordinate(new Vec2f(vertex.texturePositionX, vertex.texturePositionY))
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
	
	@SideOnly(Side.CLIENT)
	static class Chest extends ModelPart {
		final float cutX = 0.0F;
		final WeightPair[] cutYList = { new WeightPair(13.6666F, 0.254F, 0.746F), new WeightPair(15.8333F, 0.254F, 0.746F),
				new WeightPair(18.0F, 0.5F, 0.5F), new WeightPair(20.1666F, 0.744F, 0.256F), new WeightPair(22.3333F, 0.770F, 0.230F)};
		
		@Override
		public void bakeCube(VisibleMatrix4f matrix, ModelBox cube, List<VertexData> vertices, List<Integer> indices) {
			List<TexturedJointQuad> seperatedX = Lists.<TexturedJointQuad>newArrayList();
			List<TexturedJointQuad> seperatedXY = Lists.<TexturedJointQuad>newArrayList();
			for (TexturedQuad quad : cube.quadList) {
				PositionTextureVertex pos0 = getTranslatedVertex(quad.vertexPositions[0], matrix);
				PositionTextureVertex pos1 = getTranslatedVertex(quad.vertexPositions[1], matrix);
				PositionTextureVertex pos2 = getTranslatedVertex(quad.vertexPositions[2], matrix);
				PositionTextureVertex pos3 = getTranslatedVertex(quad.vertexPositions[3], matrix);
				Vec3f normal = getNormalFrom(quad, matrix);
				WeightPair pos0Weight = getMatchingWeightPair(pos0.vector3D.y);
				WeightPair pos1Weight = getMatchingWeightPair(pos1.vector3D.y);
				WeightPair pos2Weight = getMatchingWeightPair(pos2.vector3D.y);
				WeightPair pos3Weight = getMatchingWeightPair(pos3.vector3D.y);
				
				if (pos1.vector3D.x > this.cutX != pos2.vector3D.x > this.cutX) {
					float distance = (float)(pos2.vector3D.x - pos1.vector3D.x);
					float textureU = (float)(pos1.texturePositionX + (pos2.texturePositionX - pos1.texturePositionX) * ((this.cutX - pos1.vector3D.x) / distance));
					PositionTextureVertex pos4 = new PositionTextureVertex((float)pos0.vector3D.x, this.cutX, (float)pos0.vector3D.z, textureU, pos0.texturePositionY);
					PositionTextureVertex pos5 = new PositionTextureVertex((float)pos1.vector3D.x, this.cutX, (float)pos1.vector3D.z, textureU, pos1.texturePositionY);
					
					seperatedX.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos4, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos5, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos3, 8, 7, 0, pos3Weight.weightLower, pos3Weight.weightUpper, 0)}, normal));
					seperatedX.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos4, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos1, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos2, 8, 7, 0, pos2Weight.weightLower, pos2Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos5, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0)}, normal));
				} else {
					seperatedX.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, 8, 7, 0, pos0Weight.weightLower, pos0Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos1, 8, 7, 0, pos1Weight.weightLower, pos1Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos2, 8, 7, 0, pos2Weight.weightLower, pos2Weight.weightUpper, 0),
							new PositionTextureJointVertex(pos3, 8, 7, 0, pos3Weight.weightLower, pos3Weight.weightUpper, 0)}, normal));
				}
			}
			
			for (TexturedJointQuad quad : seperatedX) {
				boolean upsideDown = quad.animatedVertexPositions[1].vector3D.y > quad.animatedVertexPositions[2].vector3D.y;
				PositionTextureJointVertex pos0 = upsideDown ? quad.animatedVertexPositions[2] : quad.animatedVertexPositions[0];
				PositionTextureJointVertex pos1 = upsideDown ? quad.animatedVertexPositions[3] : quad.animatedVertexPositions[1];
				PositionTextureJointVertex pos2 = upsideDown ? quad.animatedVertexPositions[0] : quad.animatedVertexPositions[2];
				PositionTextureJointVertex pos3 = upsideDown ? quad.animatedVertexPositions[1] : quad.animatedVertexPositions[3];
				List<WeightPair> weightPairList = getMatchingWeightPairs(pos1.vector3D.y, pos2.vector3D.y);
				List<PositionTextureJointVertex> addedVertexList = Lists.<PositionTextureJointVertex>newArrayList();
				addedVertexList.add(pos0);
				addedVertexList.add(pos1);
				
				if (weightPairList.size() > 0) {
					for (WeightPair weightPair : weightPairList) {
						float distance = (float)(pos2.vector3D.y - pos1.vector3D.y);
						float textureV = pos1.texturePositionY + (float)((pos2.texturePositionY - pos1.texturePositionY) * ((weightPair.cutY - pos1.vector3D.y) / distance));
						PositionTextureVertex pos4 = new PositionTextureVertex((float)pos0.vector3D.x, weightPair.cutY, (float)pos0.vector3D.z, pos0.texturePositionX, textureV);
						PositionTextureVertex pos5 = new PositionTextureVertex((float)pos1.vector3D.x, weightPair.cutY, (float)pos1.vector3D.z, pos1.texturePositionX, textureV);
						
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
							new PositionTextureJointVertex(p0, 8, 7, 0, p0.weight.x, p0.weight.y, 0),
							new PositionTextureJointVertex(p1, 8, 7, 0, p1.weight.x, p1.weight.y, 0),
							new PositionTextureJointVertex(p2, 8, 7, 0, p2.weight.x, p2.weight.y, 0),
							new PositionTextureJointVertex(p3, 8, 7, 0, p3.weight.x, p3.weight.y, 0)}, quad.normal));
				}
			}
			
			for (TexturedJointQuad quad : seperatedXY) {
				Vec3f norm = quad.normal;
				for (PositionTextureJointVertex vertex : quad.animatedVertexPositions) {
					Vec4f pos = new Vec4f(vertex.vector3D);
					float weight1 = vertex.weight.x;
					float weight2 = vertex.weight.y;
					int joint1 = vertex.jointId.getX();
					int joint2 = vertex.jointId.getY();
					int count = weight1 > 0.0F && weight2 > 0.0F ? 2 : 1;
					
					if(weight1 <= 0.0F) {
						joint1 = joint2;
						weight1 = weight2;
					}
					
					vertices.add(new VertexData()
						.setPosition(new Vec3f(pos.x, pos.y, pos.z).scale(0.0625F))
						.setNormal(new Vec3f(norm.x, norm.y, norm.z))
						.setTextureCoordinate(new Vec2f(vertex.texturePositionX, vertex.texturePositionY))
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
		
		WeightPair getMatchingWeightPair(double y) {
			if (y < this.cutYList[0].cutY) {
				return new WeightPair((float)y, 0.0F, 1.0F);
			}
			
			int index = -1;
			for (int i = 0; i < this.cutYList.length; i++) {
				
			}
			
			if (index > 0) {
				WeightPair pair = cutYList[index];
				return new WeightPair((float)y, pair.weightLower, pair.weightUpper);
			}
			
			return new WeightPair((float)y, 1.0F, 0.0F);
		}
		
		List<WeightPair> getMatchingWeightPairs(double minY, double maxY) {
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
	
	@SideOnly(Side.CLIENT)
	static class Limb extends ModelPart {
		final int upperJointId;
		final int lowerJointId;
		final int middleJointId;
		final float cutY;
		final boolean frontOrBack;
		
		public Limb(int upperJointId, int lowerJointId, int middleJointId, float cutY, boolean frontOrBack) {
			this.upperJointId = upperJointId;
			this.lowerJointId = lowerJointId;
			this.middleJointId = middleJointId;
			this.cutY = cutY;
			this.frontOrBack = frontOrBack;
		}
		
		@Override
		public void bakeCube(VisibleMatrix4f matrixStack, ModelBox cube, List<VertexData> vertices, List<Integer> indices) {
			List<TexturedJointQuad> quads = Lists.<TexturedJointQuad>newArrayList();
			for (TexturedQuad quad : cube.quadList) {
				PositionTextureVertex pos0 = getTranslatedVertex(quad.vertexPositions[0], matrixStack);
				PositionTextureVertex pos1 = getTranslatedVertex(quad.vertexPositions[1], matrixStack);
				PositionTextureVertex pos2 = getTranslatedVertex(quad.vertexPositions[2], matrixStack);
				PositionTextureVertex pos3 = getTranslatedVertex(quad.vertexPositions[3], matrixStack);
				Vec3f normal = getNormalFrom(quad, matrixStack);
				if (pos1.vector3D.y > this.cutY != pos2.vector3D.y > this.cutY) {
					float distance = (float)(pos2.vector3D.y - pos1.vector3D.y);
					float textureV = pos1.texturePositionY + (pos2.texturePositionY - pos1.texturePositionY) * ((float)(this.cutY - pos1.vector3D.y) / distance);
					PositionTextureVertex pos4 = new PositionTextureVertex((float)pos0.vector3D.x, this.cutY, (float)pos0.vector3D.z, pos0.texturePositionX, textureV);
					PositionTextureVertex pos5 = new PositionTextureVertex((float)pos1.vector3D.x, this.cutY, (float)pos1.vector3D.z, pos1.texturePositionX, textureV);
					
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
							new PositionTextureJointVertex(pos5, upperId), new PositionTextureJointVertex(pos4, upperId)}, normal));
					quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos4, lowerId), new PositionTextureJointVertex(pos5, lowerId),
							new PositionTextureJointVertex(pos2, lowerId), new PositionTextureJointVertex(pos3, lowerId)}, normal));
					
					boolean hasSameZ = pos4.vector3D.z < 0.0D == pos5.vector3D.z < 0.0D;
					boolean isFront = hasSameZ && (pos4.vector3D.z < 0.0D == this.frontOrBack);
					
					if (isFront) {
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, this.middleJointId), new PositionTextureJointVertex(pos5, this.middleJointId),
								new PositionTextureJointVertex(pos5, this.upperJointId), new PositionTextureJointVertex(pos4, this.upperJointId)}, 0.001F, normal));
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, this.lowerJointId), new PositionTextureJointVertex(pos5, this.lowerJointId),
								new PositionTextureJointVertex(pos5, this.middleJointId), new PositionTextureJointVertex(pos4, this.middleJointId)}, 0.001F, normal));
					} else if (!hasSameZ) {
						boolean startFront = pos4.vector3D.z > 0.0D;
						int firstJoint = this.lowerJointId;
						int secondJoint = this.lowerJointId;
						int thirdJoint = startFront ? this.upperJointId : this.middleJointId;
						int fourthJoint = startFront ? this.middleJointId : this.upperJointId;
						int fifthJoint = this.upperJointId;
						int sixthJoint = this.upperJointId;
						
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, firstJoint), new PositionTextureJointVertex(pos5, secondJoint),
								new PositionTextureJointVertex(pos5, thirdJoint), new PositionTextureJointVertex(pos4, fourthJoint)}, 0.001F, normal));
						quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
								new PositionTextureJointVertex(pos4, fourthJoint), new PositionTextureJointVertex(pos5, thirdJoint),
								new PositionTextureJointVertex(pos5, fifthJoint), new PositionTextureJointVertex(pos4, sixthJoint)}, 0.001F, normal));
					}
				} else {
					int jointId = pos0.vector3D.y > this.cutY ? this.upperJointId : this.lowerJointId;
					quads.add(new TexturedJointQuad(new PositionTextureJointVertex[] {
							new PositionTextureJointVertex(pos0, jointId), new PositionTextureJointVertex(pos1, jointId),
							new PositionTextureJointVertex(pos2, jointId), new PositionTextureJointVertex(pos3, jointId)}, normal));
				}
			}
			
			for (TexturedJointQuad quad : quads) {
				Vec3f norm = quad.normal;
				for (PositionTextureJointVertex vertex : quad.animatedVertexPositions) {
					Vec4f pos = new Vec4f(vertex.vector3D);
					vertices.add(new VertexData()
						.setPosition(new Vec3f(pos.x, pos.y, pos.z).scale(0.0625F))
						.setNormal(new Vec3f(norm.x, norm.y, norm.z))
						.setTextureCoordinate(new Vec2f(vertex.texturePositionX, vertex.texturePositionY))
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
	
	static EnumFacing getDirectionFromVector(Vec3i directionVec) {
		for (EnumFacing direction : EnumFacing.values()) {
			Vec3i direcVec = new Vec3i(Float.compare(directionVec.getX(), -0.0F) == 0 ? 0.0F : directionVec.getX(), directionVec.getY(), directionVec.getZ());
			if (direcVec.equals(direction.getDirectionVec())) {
				return direction;
			}
		}
		
		return null;
	}
	
	static PositionTextureVertex getTranslatedVertex(PositionTextureVertex original, VisibleMatrix4f matrix) {
		Vec4f translatedPosition = new Vec4f(original.vector3D);
		VisibleMatrix4f.transform(matrix, translatedPosition, translatedPosition);
		return new PositionTextureVertex(translatedPosition.x, translatedPosition.y, translatedPosition.z, original.texturePositionX, original.texturePositionY);
	}
	
	static Vec3f getNormalFrom(TexturedQuad quad, VisibleMatrix4f matrix) {
		Vec3d vec3d = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[0].vector3D);
        Vec3d vec3d1 = quad.vertexPositions[1].vector3D.subtractReverse(quad.vertexPositions[2].vector3D);
        Vec3d vec3d2 = vec3d1.crossProduct(vec3d).normalize();
        Vec4f norm = new Vec4f(vec3d2);
        VisibleMatrix4f.transform(matrix, norm, norm);
        return new Vec3f(norm.x, norm.y, norm.z);
	}
	
	@SideOnly(Side.CLIENT)
	static class PositionTextureJointVertex extends PositionTextureVertex {
		final Vec3i jointId;
		final Vec3f weight;
		
		public PositionTextureJointVertex(PositionTextureVertex posTexVertx, int jointId) {
			this(posTexVertx, jointId, 0, 0, 1.0F, 0.0F, 0.0F);
		}
		
		public PositionTextureJointVertex(PositionTextureVertex posTexVertx, int jointId1, int jointId2, int jointId3, float weight1, float weight2, float weight3) {
			this(posTexVertx, new Vec3i(jointId1, jointId2, jointId3), new Vec3f(weight1, weight2, weight3));
		}
		
		public PositionTextureJointVertex(PositionTextureVertex posTexVertx, Vec3i ids, Vec3f weights) {
			super(posTexVertx.vector3D, posTexVertx.texturePositionX, posTexVertx.texturePositionY);
			this.jointId = ids;
			this.weight = weights;
		}
	}
	
	@SideOnly(Side.CLIENT)
	static class TexturedJointQuad {
		public final PositionTextureJointVertex[] animatedVertexPositions;
		public final Vec3f normal;
		
		public TexturedJointQuad(PositionTextureJointVertex[] positionsIn, Vec3f normal) {
			this.animatedVertexPositions = positionsIn;
			this.normal = normal;
		}
		
		public TexturedJointQuad(PositionTextureJointVertex[] positionsIn, float cor, Vec3f normal) {
			this.animatedVertexPositions = positionsIn;
			positionsIn[0] = new PositionTextureJointVertex(positionsIn[0].setTexturePosition(positionsIn[0].texturePositionX, positionsIn[0].texturePositionY + cor),
					positionsIn[0].jointId, positionsIn[0].weight);
			positionsIn[1] = new PositionTextureJointVertex(positionsIn[1].setTexturePosition(positionsIn[1].texturePositionX, positionsIn[1].texturePositionY + cor),
					positionsIn[1].jointId, positionsIn[1].weight);
			positionsIn[2] = new PositionTextureJointVertex(positionsIn[2].setTexturePosition(positionsIn[2].texturePositionX, positionsIn[2].texturePositionY - cor),
					positionsIn[2].jointId, positionsIn[2].weight);
			positionsIn[3] = new PositionTextureJointVertex(positionsIn[3].setTexturePosition(positionsIn[3].texturePositionX, positionsIn[3].texturePositionY - cor),
					positionsIn[3].jointId, positionsIn[3].weight);
			this.normal = normal;
		}
	}
}