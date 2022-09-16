package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.entity.projectile.AbstractArrowEntity;

public class ArrowPatch extends ProjectilePatch<AbstractArrowEntity> {
	@Override
	protected void setMaxStrikes(AbstractArrowEntity projectileEntity, int maxStrikes) {
		projectileEntity.setPierceLevel((byte)(maxStrikes - 1));
	}
}