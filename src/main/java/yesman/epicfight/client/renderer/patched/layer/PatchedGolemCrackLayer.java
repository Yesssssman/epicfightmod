package yesman.epicfight.client.renderer.patched.layer;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.layers.IronGolemCrackinessLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.client.mesh.IronGolemMesh;
import yesman.epicfight.client.renderer.EpicFightRenderTypes;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;

@OnlyIn(Dist.CLIENT)
public class PatchedGolemCrackLayer extends PatchedLayer<IronGolem, IronGolemPatch, IronGolemModel<IronGolem>, IronGolemCrackinessLayer, IronGolemMesh> {
	
	private static final Map<IronGolem.Crackiness, ResourceLocation> CRACK_MAP = ImmutableMap.of(
			IronGolem.Crackiness.LOW, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_low.png"),
			IronGolem.Crackiness.MEDIUM, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_medium.png"),
			IronGolem.Crackiness.HIGH, new ResourceLocation("textures/entity/iron_golem/iron_golem_crackiness_high.png"));
	
	public PatchedGolemCrackLayer(IronGolemMesh mesh) {
		super(mesh);
	}
	
	@Override
	public void renderLayer(IronGolemPatch entitypatch, IronGolem entityGolem, IronGolemCrackinessLayer originalRenderer, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn, OpenMatrix4f[] poses, float netYawHead, float pitchHead, float partialTicks) {
		IronGolem.Crackiness crack = entityGolem.getCrackiness();
		
		if (crack != IronGolem.Crackiness.NONE) {
			VertexConsumer ivertexbuilder = bufferIn.getBuffer(EpicFightRenderTypes.triangles(RenderType.entityCutoutNoCull(CRACK_MAP.get(crack))));
			
			this.mesh.drawModelWithPose(matrixStackIn, ivertexbuilder, packedLightIn, 1.0F, 1.0F, 1.0F, 1.0F, OverlayTexture.NO_OVERLAY, entitypatch.getArmature(), poses);
		}
	}
}