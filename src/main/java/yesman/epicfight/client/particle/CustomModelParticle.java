package yesman.epicfight.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.ClientModel;

@OnlyIn(Dist.CLIENT)
public abstract class CustomModelParticle extends Particle {
	protected final ClientModel particleMesh;
	protected float pitch;
	protected float pitchO;
	protected float yaw;
	protected float yawO;
	protected float scale = 1.0F;
	protected float scaleO = 1.0F;
	
	public CustomModelParticle(ClientWorld level, double x, double y, double z, double xd, double yd, double zd, ClientModel particleMesh) {
		super(level, x, y, z, xd, yd, zd);
		this.particleMesh = particleMesh;
	}
	
	@Override
	public void render(IVertexBuilder vertexConsumer, ActiveRenderInfo camera, float partialTicks) {
		MatrixStack poseStack = new MatrixStack();
		this.setupMatrixStack(poseStack, camera, partialTicks);
		this.prepareDraw(poseStack, partialTicks);
		
		this.particleMesh.drawRawModel(poseStack, vertexConsumer, 16777216, this.rCol, this.gCol, this.bCol, this.alpha, OverlayTexture.NO_OVERLAY);
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
	
	public void prepareDraw(MatrixStack poseStack, float partialTicks) {}
	
	protected void setupMatrixStack(MatrixStack poseStack, ActiveRenderInfo camera, float partialTicks) {
		Quaternion rotation = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
		float roll = MathHelper.lerp(partialTicks, this.oRoll, this.roll);
		float pitch = MathHelper.lerp(partialTicks, this.pitchO, this.pitch);
		float yaw = MathHelper.lerp(partialTicks, this.yawO, this.yaw);
		rotation.mul(Vector3f.YP.rotation(yaw));
		rotation.mul(Vector3f.XP.rotation(pitch));
		rotation.mul(Vector3f.ZP.rotation(roll));
		
		Vector3d vec3 = camera.getPosition();
		float x = (float)(MathHelper.lerp((double)partialTicks, this.xo, this.x) - vec3.x());
		float y = (float)(MathHelper.lerp((double)partialTicks, this.yo, this.y) - vec3.y());
		float z = (float)(MathHelper.lerp((double)partialTicks, this.zo, this.z) - vec3.z());
		float scale = (float)MathHelper.lerp((double)partialTicks, this.scaleO, this.scale);
		
		poseStack.translate(x, y, z);
		poseStack.mulPose(rotation);
		poseStack.scale(scale, scale, scale);
	}
}