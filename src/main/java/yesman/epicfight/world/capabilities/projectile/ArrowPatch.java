package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.world.entity.projectile.AbstractArrow;

public class ArrowPatch extends ProjectilePatch<AbstractArrow> {
	@Override
	protected void setMaxStrikes(AbstractArrow projectileEntity, int maxStrikes) {
		projectileEntity.setPierceLevel((byte)(maxStrikes - 1));
	}
}