package yesman.epicfight.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModels;

@OnlyIn(Dist.CLIENT)
public class LaserParticle extends CustomModelParticle {
	private float length;
	private float xRot;
	private float yRot;
	
	public LaserParticle(ClientWorld level, double x, double y, double z, double toX, double toY, double toZ) {
		super(level, x, y, z, 0, 0, 0, ClientModels.LOGICAL_CLIENT.laser);
		this.lifetime = 5;
		
		Vector3d direction = new Vector3d(toX - x, toY - y, toZ - z);
		Vector3d start = new Vector3d(x, y, z);
		Vector3d destination = start.add(direction.normalize().scale(200.0D));
		BlockRayTraceResult hitResult = level.clip(new RayTraceContext(start, destination, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, null));
		double xLength = hitResult.getLocation().x - x;
		double yLength = hitResult.getLocation().y - y;
		double zLength = hitResult.getLocation().z - z;
		double horizontalDistance = (float)Math.sqrt(xLength * xLength + zLength * zLength);
		this.length = (float)Math.sqrt(xLength * xLength + yLength * yLength + zLength * zLength);
		this.yRot = (float)(-Math.atan2(zLength, xLength) * (180D / Math.PI)) - 90.0F;
		this.xRot = (float)(Math.atan2(yLength, horizontalDistance) * (180D / Math.PI));
		int smokeCount = (int)this.length * 4;
		
		for (int i = 0; i < smokeCount; i++) {
			level.addParticle(ParticleTypes.SMOKE, x + xLength / smokeCount * i, y + yLength / smokeCount * i, z + zLength / smokeCount * i, 0, 0, 0);
		}
		
		this.setBoundingBox(new AxisAlignedBB(x, y, z, toX, toY, toZ));
	}
	
	@Override
	public void prepareDraw(MatrixStack poseStack, float partialTicks) {
		poseStack.mulPose(Vector3f.YP.rotationDegrees(this.yRot));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(this.xRot));
		float progression = (this.age + partialTicks) / (this.lifetime + 1);
		float scale = MathHelper.sin(progression * (float)Math.PI);
		float zScale = progression > 0.5F ? 1.0F : MathHelper.sin(progression * (float)Math.PI);
		poseStack.scale(scale, scale, zScale * this.length);
	}
	
	@Override
	public void render(IVertexBuilder vertexConsumer, ActiveRenderInfo camera, float partialTicks) {
		super.render(vertexConsumer, camera, partialTicks);
		MatrixStack poseStack = new MatrixStack();
		this.setupMatrixStack(poseStack, camera, partialTicks);
		this.prepareDraw(poseStack, partialTicks);
		poseStack.scale(1.1F, 1.1F, 1.1F);
	}
	
	@Override
	public IParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.TRANSLUCENT_GLOWING;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements IParticleFactory<BasicParticleType> {
		@Override
		public Particle createParticle(BasicParticleType typeIn, ClientWorld level, double startX, double startY, double startZ, double endX, double endY, double endZ) {
			return new LaserParticle(level, startX, startY, startZ, endX, endY, endZ);
		}
	}
}