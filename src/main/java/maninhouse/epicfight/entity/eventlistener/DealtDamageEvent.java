package maninhouse.epicfight.entity.eventlistener;

import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import net.minecraft.entity.LivingEntity;

public class DealtDamageEvent<T extends PlayerData<?>> extends PlayerEvent<T> {
	private float attackDamage;
	private LivingEntity target;
	private IExtendedDamageSource damageSource;
	
	public DealtDamageEvent(T playerdata, LivingEntity target, IExtendedDamageSource source, float damage) {
		super(playerdata);
		this.target = target;
		this.damageSource = source;
		this.attackDamage = damage;
	}
	
	public LivingEntity getTarget() {
		return this.target;
	}
	
	public IExtendedDamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}
	
	public float getAttackDamage() {
		return this.attackDamage;
	}
}