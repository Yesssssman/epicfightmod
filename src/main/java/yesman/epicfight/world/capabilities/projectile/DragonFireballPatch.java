package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.DragonFireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class DragonFireballPatch extends ProjectilePatch<DragonFireballEntity> {
	@Override
	public void onJoinWorld(DragonFireballEntity projectileEntity, EntityJoinWorldEvent event) {
		super.onJoinWorld(projectileEntity, event);
		this.impact = 1.0F;
		projectileEntity.xPower *= 2.0D;
		projectileEntity.yPower *= 2.0D;
		projectileEntity.zPower *= 2.0D;
	}
	
	@Override
	protected void setMaxStrikes(DragonFireballEntity projectileEntity, int maxStrikes) {
		
	}
	
	@Override
	public boolean onProjectileImpact(ProjectileImpactEvent event) {
		if (event.getRayTraceResult() instanceof EntityRayTraceResult) {
			Entity entity = ((EntityRayTraceResult)event.getRayTraceResult()).getEntity();
			ProjectileEntity pje = (ProjectileEntity)event.getEntity();
			
			if (!entity.is(pje.getOwner())) {
				entity.hurt(DamageSource.indirectMagic(pje, pje.getOwner()), 8.0F);
			}
		}
		
		return false;
	}
}