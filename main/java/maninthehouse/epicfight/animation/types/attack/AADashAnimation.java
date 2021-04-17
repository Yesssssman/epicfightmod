package maninthehouse.epicfight.animation.types.attack;

import javax.annotation.Nullable;

import maninthehouse.epicfight.animation.types.AnimationProperty;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import net.minecraft.entity.Entity;

public class AADashAnimation extends AttackAnimation {
	public AADashAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path) {
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
		this.addProperty(AnimationProperty.DIRECTIONAL, true);
	}
	
	public AADashAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path, boolean noDirectionAttack) {
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
	}
	
	@Override
	public IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target) {
		IExtendedDamageSource extSource = super.getDamageSourceExt(entitydata, target);
		extSource.setImpact(extSource.getImpact() * 1.4F);
		
		return extSource;
	}
}