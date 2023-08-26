package yesman.epicfight.client.renderer.blockentity;

import java.util.BitSet;
import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.world.level.block.entity.FractureBlockEntity;

@OnlyIn(Dist.CLIENT)
public class FractureBlockRenderer implements BlockEntityRenderer<FractureBlockEntity> {
	static final Direction[] DIRECTIONS = Direction.values();
	private ModelBlockRenderer modelBlockRenderer;
	
	public FractureBlockRenderer(BlockEntityRendererProvider.Context context) {
		this.modelBlockRenderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();
	}
	
	@Override
	public boolean shouldRender(FractureBlockEntity p_173568_, Vec3 p_173569_) {
		return Vec3.atCenterOf(p_173568_.getBlockPos()).closerThan(p_173569_, (double)this.getViewDistance());
	}
	
	public void renderWithoutFaceLighting(BlockAndTintGetter level, BakedModel model, BlockState blockState, BlockPos blockPos, PoseStack poseStack, VertexConsumer vertexConsumer, Random random, long seed, int p_111100_, IModelData modelData) {
		BitSet bitset = new BitSet(3);
		int lightColor = LevelRenderer.getLightColor(level, blockState, blockPos.above());
		
		for (Direction direction : DIRECTIONS) {
			random.setSeed(seed);
			List<BakedQuad> list = model.getQuads(blockState, direction, random, modelData);
			
			if (!list.isEmpty()) {
				this.modelBlockRenderer.renderModelFaceFlat(level, blockState, blockPos, lightColor, p_111100_, false, poseStack, vertexConsumer, list, bitset);
			}
		}
		
		random.setSeed(seed);
		List<BakedQuad> list1 = model.getQuads(blockState, (Direction)null, random, modelData);
		
		if (!list1.isEmpty()) {
			this.modelBlockRenderer.renderModelFaceFlat(level, blockState, blockPos, -1, p_111100_, true, poseStack, vertexConsumer, list1, bitset);
		}
	}
	
	@Override
	public void render(FractureBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource multiBufferSource, int lightColor, int overlayColor) {
		Minecraft mc = Minecraft.getInstance();
		float turnBackTime = 5.0F;
		float lerpAmount = Mth.clamp(partialTicks * (1.0F / turnBackTime) + (turnBackTime - (blockEntity.getMaxLifeTime() - blockEntity.getLifeTime())) * (1.0F / turnBackTime), 0.0F, 1.0F);
		Vector3f translate = blockEntity.getMaxLifeTime() > blockEntity.getLifeTime() + turnBackTime ? blockEntity.getTranslate() : MathUtils.lerpMojangVector(blockEntity.getTranslate(), Vector3f.ZERO, lerpAmount);
		Quaternion rotate = blockEntity.getMaxLifeTime() > blockEntity.getLifeTime() + turnBackTime ? blockEntity.getRotation() : MathUtils.lerpQuaternion(blockEntity.getRotation(), Quaternion.ONE, lerpAmount);
		
		double BOUNCE_MAX_HEIGHT = blockEntity.getBouncing();
		double TIME = Math.max(BOUNCE_MAX_HEIGHT * 8.0D, 8.0D);
		double EXTENDER = 1 / Math.pow(TIME * 0.5, 2);
		double MOVE_GRAPH = Math.sqrt(BOUNCE_MAX_HEIGHT / EXTENDER);
		double bouncingAnimation = Math.max(-EXTENDER * Math.pow(blockEntity.getLifeTime() + partialTicks - MOVE_GRAPH, 2.0D) + BOUNCE_MAX_HEIGHT, 0.0D);
		
		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.mulPose(rotate);
		poseStack.translate(translate.x(), translate.y() + bouncingAnimation, translate.z());
		poseStack.translate(-0.5D, -0.5D, -0.5D);
		
		this.renderWithoutFaceLighting(blockEntity.getLevel()
								     , mc.getBlockRenderer().getBlockModel(blockEntity.getOriginalBlockState())
								     , blockEntity.getOriginalBlockState()
								     , blockEntity.getBlockPos()
								     , poseStack
								     , multiBufferSource.getBuffer(RenderType.cutout())
								     , blockEntity.getLevel().getRandom()
								     , blockEntity.getBlockState().getSeed(blockEntity.getBlockPos())
								     , OverlayTexture.NO_OVERLAY
								     , EmptyModelData.INSTANCE
								     );
		
		poseStack.popPose();
	}
}