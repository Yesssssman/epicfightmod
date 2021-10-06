package yesman.epicfight.client.renderer.layer;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.layers.IronGolemCracksLayer;
import net.minecraft.client.renderer.entity.model.IronGolemModel;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.entity.mob.IronGolemData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.client.renderer.ModRenderTypes;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class GolemCrackLayer extends AnimatedLayer<IronGolemEntity, IronGolemData, IronGolemModel<IronGolemEntity>, IronGolemCracksLayer> {
	private static final Map<IronGolemEntity.Cracks, ResourceLocation> CRACK_MAP = ImmutableMap.of(
			IronGolemEntity.Cracks.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
			IronGolemEntity.Cracks.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
			IronGolemEntity.Cracks.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
	
	@Override
	public void renderLayer(IronGolemData entitydata, IronGolemEntity entityGolem, IronGolemCracksLayer originalRenderer, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		IronGolemEntity.Cracks crack = entityGolem.func_226512_l_();
		if(crack != IronGolemEntity.Cracks.NONE) {
			IVertexBuilder ivertexbuilder = bufferIn.getBuffer(ModRenderTypes.getEntityCutoutNoCull(CRACK_MAP.get(crack)));
			entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).draw(matrixStackIn, ivertexbuilder, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, poses);
		}
	}
}