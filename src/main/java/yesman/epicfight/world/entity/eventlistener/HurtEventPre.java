package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.utils.game.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class HurtEventPre extends PlayerEvent<ServerPlayerPatch> {
	private final DamageSource damageSource;
	private float amount;
	private AttackResult.ResultType result;
	
	public HurtEventPre(ServerPlayerPatch playerpatch, DamageSource damageSource, float amount) {
		super(playerpatch, true);
		this.damageSource = damageSource;
		this.amount = amount;
		this.result = AttackResult.ResultType.SUCCESS;
	}
	
	public DamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public float getAmount() {
		return this.amount;
	}
	
	public AttackResult.ResultType getResult() {
		return this.result;
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	public void setResult(AttackResult.ResultType result) {
		this.result = result;
	}
}