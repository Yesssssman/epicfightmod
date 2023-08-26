package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class ModifyBaseDamageEvent<T extends PlayerPatch<?>> extends PlayerEvent<T> {
	private float damage;
	
	public ModifyBaseDamageEvent(T playerpatch, float damage) {
		super(playerpatch, false);
		this.damage = damage;
	}
	
	public void setDamage(float damage) {
		this.damage = damage;
	}
	
	public float getDamage() {
		return this.damage;
	}
}