package yesman.epicfight.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.Mesh;

@OnlyIn(Dist.CLIENT)
public abstract class CustomModelParticle<M extends Mesh<?>> extends Particle {
	protected final M particleMesh;
	protected float pitch;
	protected float pitchO;
	protected float yaw;
	protected float yawO;
	protected float scale = 1.0F;
	protected float scaleO = 1.0F;
	
	public CustomModelParticle(ClientLevel level, double x, double y, double z, double xd, double yd, double zd, M particleMesh) {
		super(level, x, y, z, xd, yd, zd);
		this.particleMesh = particleMesh;
	}
	
	@Override
	public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
		PoseStack poseStack = new PoseStack();
		this.setupPoseStack(poseStack, camera, partialTicks);
		this.prepareDraw(poseStack, partialTicks);
		this.particleMesh.drawRawModelNormal(poseStack, vertexConsumer, this.getLightColor(partialTicks), this.rCol, this.gCol, this.bCol, this.alpha, OverlayTexture.NO_OVERLAY);
	}
	
	@Override
	public void tick() {
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.pitchO = this.pitch;
			this.yawO = this.yaw;
			this.oRoll = this.roll;
			this.scaleO = this.scale;
		}
	}
	
	public void prepareDraw(PoseStack poseStack, float partialTicks) {}
	
	protected void setupPoseStack(PoseStack poseStack, Camera camera, float partialTicks) {
		Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		float roll = Mth.lerp(partialTicks, this.oRoll, this.roll);
		float pitch = Mth.lerp(partialTicks, this.pitchO, this.pitch);
		float yaw = Mth.lerp(partialTicks, this.yawO, this.yaw);
		rotation.mul(Vector3f.YP.rotationDegrees(yaw));
		rotation.mul(Vector3f.XP.rotationDegrees(pitch));
		rotation.mul(Vector3f.ZP.rotationDegrees(roll));
		
		Vec3 vec3 = camera.getPosition();
		float x = (float)(Mth.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
		float y = (float)(Mth.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
		float z = (float)(Mth.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
		float scale = (float)Mth.lerp((double)partialTicks, this.scaleO, this.scale);
		
		poseStack.translate(x, y, z);
		poseStack.mulPose(rotation);
		poseStack.scale(scale, scale, scale);
	}
}