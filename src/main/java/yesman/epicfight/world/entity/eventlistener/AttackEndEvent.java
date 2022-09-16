package yesman.epicfight.world.entity.eventlistener;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AttackEndEvent extends PlayerEvent<ServerPlayerPatch> {
	private List<LivingEntity> attackedEntity;
	private int animationId;
	
	public AttackEndEvent(ServerPlayerPatch playerpatch, List<LivingEntity> attackedEntity, int animationId) {
		super(playerpatch, false);
		this.attackedEntity = attackedEntity;
		this.animationId = animationId;
	}
	
	public List<LivingEntity> getHitEntity() {
		return this.attackedEntity;
	}
	
	public int getAnimationId() {
		return this.animationId;
	}
}
