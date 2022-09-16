package yesman.epicfight.client.renderer.entity;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.entity.WitherGhostClone;

@OnlyIn(Dist.CLIENT)
public class WitherGhostRenderer extends NoopLivingEntityRenderer<WitherGhostClone> {
	public WitherGhostRenderer(EntityRendererManager entityRenderManager) {
		super(entityRenderManager, 1.0F);
	}
	
	@Override
	protected int getBlockLightLevel(WitherGhostClone witherBoss, BlockPos blockpos) {
		return 15;
	}
}