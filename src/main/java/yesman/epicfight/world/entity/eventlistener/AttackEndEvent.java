package yesman.epicfight.world.entity.eventlistener;

import java.util.List;

import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AttackEndEvent extends PlayerEvent<ServerPlayerPatch> {
	private List<LivingEntity> attackedEntity;
	private AttackAnimation animation;
	
	public AttackEndEvent(ServerPlayerPatch playerpatch, List<LivingEntity> attackedEntity, AttackAnimation animation) {
		super(playerpatch, false);
		this.attackedEntity = attackedEntity;
		this.animation = animation;
	}
	
	public List<LivingEntity> getHitEntity() {
		return this.attackedEntity;
	}
	
	public AttackAnimation getAnimation() {
		return this.animation;
	}
}
