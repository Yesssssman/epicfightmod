package yesman.epicfight.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Mesh.RawMesh;
import yesman.epicfight.api.client.model.Meshes;

@OnlyIn(Dist.CLIENT)
public class LaserParticle extends CustomModelParticle<RawMesh> {
	private float length;
	private float xRot;
	private float yRot;
	
	public LaserParticle(ClientLevel level, double x, double y, double z, double toX, double toY, double toZ) {
		super(level, x, y, z, 0, 0, 0, Meshes.LASER);
		this.lifetime = 5;
		
		Vec3 direction = new Vec3(toX - x, toY - y, toZ - z);
		Vec3 start = new Vec3(x, y, z);
		Vec3 destination = start.add(direction.normalize().scale(200.0D));
		BlockHitResult hitResult = level.clip(new ClipContext(start, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
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
		
		this.setBoundingBox(new AABB(x, y, z, toX, toY, toZ));
	}
	
	@Override
	public void prepareDraw(PoseStack poseStack, float partialTicks) {
		poseStack.mulPose(Vector3f.YP.rotationDegrees(this.yRot));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(this.xRot));
		float progression = (this.age + partialTicks) / (this.lifetime + 1);
		float scale = Mth.sin(progression * (float)Math.PI);
		float zScale = progression > 0.5F ? 1.0F : Mth.sin(progression * (float)Math.PI);
		poseStack.scale(scale, scale, zScale * this.length);
	}
	
	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
		super.render(vertexConsumer, camera, partialTicks);
		
		PoseStack poseStack = new PoseStack();
		this.setupPoseStack(poseStack, camera, partialTicks);
		this.prepareDraw(poseStack, partialTicks);
		poseStack.scale(1.1F, 1.1F, 1.1F);
	}
	
	@Override
	public ParticleRenderType getRenderType() {
		return EpicFightParticleRenderTypes.TRANSLUCENT_GLOWING;
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class Provider implements ParticleProvider<SimpleParticleType> {
		@Override
		public Particle createParticle(SimpleParticleType typeIn, ClientLevel level, double startX, double startY, double startZ, double endX, double endY, double endZ) {
			return new LaserParticle(level, startX, startY, startZ, endX, endY, endZ);
		}
	}
}