package maninthehouse.epicfight.capabilities.entity;

import maninthehouse.epicfight.utils.game.IndirectDamageSourceExtended;
import net.minecraft.entity.Entity;

public interface IRangedAttackMobCapability {
	public abstract IndirectDamageSourceExtended getRangedDamageSource(Entity damageCarrier);
}