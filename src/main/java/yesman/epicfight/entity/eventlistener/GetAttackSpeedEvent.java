package yesman.epicfight.entity.eventlistener;

import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;

public class GetAttackSpeedEvent extends PlayerEvent<PlayerData<?>> {
	private CapabilityItem item;
	private float attackSpeed;
	
	public GetAttackSpeedEvent(PlayerData<?> playerdata, CapabilityItem item, float attackSpeed) {
		super(playerdata);
		this.item = item;
		this.setAttackSpeed(attackSpeed);
	}
	
	public void setAttackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}
	
	public CapabilityItem getItemCapability() {
		return this.item;
	}
	
	public float getAttackSpeed() {
		return this.attackSpeed;
	}
}