package yesman.epicfight.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.SpriteTexturedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
public class HierarchicalParticle extends SpriteTexturedParticle
{
	private LivingData<?> entitydata;
	private int jointKey;
	
	protected HierarchicalParticle(ClientWorld worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn)
	{
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, 0, 0, 0);
		
		Entity e = worldIn.getEntityByID((int)Double.doubleToLongBits(xSpeedIn));
		this.entitydata = (LivingData<?>) e.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		this.jointKey = (int) Double.doubleToLongBits(ySpeedIn);
		
		//this.entitydata.hierarchicalParticles.add(this);
	}
	
	@Override
	public void tick()
	{
		if(this.isExpired)
		{
			//this.entitydata.hierarchicalParticles.remove(this);
			this.entitydata = null;
		}
	}
	
	@Override
	public void renderParticle(IVertexBuilder buffer, ActiveRenderInfo entityIn, float partialTicks)
	{
		this.entitydata.getClientAnimator().setPoseToModel(partialTicks);
		Joint joint = this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().findJointById(this.jointKey);
		OpenMatrix4f jointTransform = OpenMatrix4f.mul(joint.getAnimatedTransform(), this.entitydata.getModelMatrix(partialTicks), null);
		this.posX = jointTransform.m30;
		this.posY = jointTransform.m31;
		this.posZ = jointTransform.m32;
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		
		super.renderParticle(buffer, entityIn, partialTicks);
	}
	
	@Override
	public IParticleRenderType getRenderType()
	{
		return ParticleRenderTypes.DISABLE_LIGHTMAP_PARTICLE;
	}
}