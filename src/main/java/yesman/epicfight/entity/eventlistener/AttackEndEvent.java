package yesman.epicfight.entity.eventlistener;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class AttackEndEvent extends PlayerEvent<ServerPlayerData> {
	private List<LivingEntity> attackedEntity;
	private int animationId;
	
	public AttackEndEvent(ServerPlayerData playerdata, List<LivingEntity> attackedEntity, int animationId) {
		super(playerdata);
		this.attackedEntity = attackedEntity;
		this.animationId = animationId;
	}
	
	public List<LivingEntity> getAttackedEntity() {
		return this.attackedEntity;
	}
	
	public int getAnimationId() {
		return this.animationId;
	}
}
