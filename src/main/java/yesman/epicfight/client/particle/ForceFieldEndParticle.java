package yesman.epicfight.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.renderer.LightningRenderHelper;

@OnlyIn(Dist.CLIENT)
public class ForceFieldEndParticle extends Particle {
	private boolean init;
	
	protected ForceFieldEndParticle(ClientWorld level, double x, double y, double z) {
		super(level, x, y, z);
		this.lifetime = 10;
		Minecraft mc = Minecraft.getInstance();
		mc.particleEngine.add(new DustParticle.ExpansiveMetaParticle(level, x, y, z, 6.0D, 80));
	}
	
	public IParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.LIGHTNING;
	}
	
	@Override
	public void render(IVertexBuilder vertexBuilder, ActiveRenderInfo camera, float parttialTick) {
		MatrixStack poseStack = new MatrixStack();
		Vector3d vec3 = camera.getPosition();
		float f = (float) (MathHelper.lerp((double) parttialTick, this.xo, this.x) - vec3.x());
		float f1 = (float) (MathHelper.lerp((double) parttialTick, this.yo, this.y) - vec3.y());
		float f2 = (float) (MathHelper.lerp((double) parttialTick, this.zo, this.z) - vec3.z());
		poseStack.translate(f, f1, f2);
		
		if (this.age > 0) {
			float progression = ((float)this.age + parttialTick) / (float)this.lifetime;
			LightningRenderHelper.renderFlashingLight(vertexBuilder, poseStack, 255, 0, 255, 15, 1.0F, progression);
		}
		
		if (!this.init) {
			ClientEngine.instance.renderEngine.getOverlayManager().flickering("flickering", 0.05F, 1.2F);
			this.init = true;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
			return new ForceFieldEndParticle(level, x, y, z);
		}
	}
}