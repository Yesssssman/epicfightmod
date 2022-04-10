package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

public class DragonFireballPatch extends ProjectilePatch<DragonFireball> {
	@Override
	public void onJoinWorld(DragonFireball projectileEntity, EntityJoinWorldEvent event) {
		super.onJoinWorld(projectileEntity, event);
		this.impact = 1.0F;
		projectileEntity.xPower *= 2.0D;
		projectileEntity.yPower *= 2.0D;
		projectileEntity.zPower *= 2.0D;
	}
	
	@Override
	protected void setMaxStrikes(DragonFireball projectileEntity, int maxStrikes) {
		
	}
	
	@Override
	public boolean onProjectileImpact(ProjectileImpactEvent event) {
		if (event.getRayTraceResult() instanceof EntityHitResult) {
			Entity entity = ((EntityHitResult)event.getRayTraceResult()).getEntity();
			
			if (!entity.is(event.getProjectile().getOwner())) {
				entity.hurt(DamageSource.indirectMagic(event.getProjectile(), event.getProjectile().getOwner()), 8.0F);
			}
		}
		
		return false;
	}
}