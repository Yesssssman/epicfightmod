package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class DealtDamageEvent<T extends PlayerPatch<?>> extends PlayerEvent<T> {
	private float attackDamage;
	private LivingEntity target;
	private ExtendedDamageSource damageSource;
	
	public DealtDamageEvent(T playerpatch, LivingEntity target, ExtendedDamageSource source, float damage) {
		super(playerpatch, false);
		this.target = target;
		this.damageSource = source;
		this.attackDamage = damage;
	}
	
	public LivingEntity getTarget() {
		return this.target;
	}
	
	public ExtendedDamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}
	
	public float getAttackDamage() {
		return this.attackDamage;
	}
}