package yesman.epicfight.client.particle;

import java.util.List;
import java.util.Random;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

@OnlyIn(Dist.CLIENT)
public abstract class AnimatedMeshParticle extends Particle {
	protected IBakedModel mesh;
	protected float rotationX;
	protected float rotationY;
	protected float rotationZ;
	
	protected float prevScale;
	protected float scale;
	
	protected AnimatedMeshParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, IBakedModel mesh) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.maxAge = 60;
		this.mesh = mesh;
		this.prevScale = 1.0F;
		this.scale = 1.0F;
	}
	
	@Override
	public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
		List<BakedQuad> quads = mesh.getQuads(null, null, new Random(), EmptyModelData.INSTANCE);
		Vector3d vector3d = renderInfo.getProjectedView();
	    float f5 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosX, this.posX) - vector3d.getX());
	    float f6 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosY, this.posY) - vector3d.getY());
	    float f7 = (float)(MathHelper.lerp((double)partialTicks, this.prevPosZ, this.posZ) - vector3d.getZ());
	    float sin = (float) Math.sin(-rotationY * Math.PI / 180.0);
	    float cos = (float) Math.cos(-rotationY * Math.PI / 180.0);;
	    
	    int ii = 0;
	    int i = this.getBrightnessForRender(partialTicks);
	    int j = i >> 16 & '\uffff';
	    int k = i & '\uffff';
	    
	    for(int jj = quads.size(); ii < jj; ++ii) {
        	BakedQuad bakedquad = quads.get(ii);
       	 	int[] vertexData = bakedquad.getVertexData();
       	 	
	        for(int a = 0; a < 4; a++) {
		        float x = Float.intBitsToFloat(vertexData[0+7*a]);
		        float y = Float.intBitsToFloat(vertexData[1+7*a]);
		        float z = Float.intBitsToFloat(vertexData[2+7*a]);
		        float u = Float.intBitsToFloat(vertexData[3+7*a]);
		        float v = Float.intBitsToFloat(vertexData[4+7*a]);
		        float scale = this.prevScale + (this.scale - this.prevScale) * partialTicks;
		        
		        buffer.pos(f5 + (x*cos + z*sin) * scale, f6 + y * scale, f7 + (-x*sin + z*cos) * scale).tex(u, v).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).lightmap(j, k).endVertex();
	        }
        }
	}
}