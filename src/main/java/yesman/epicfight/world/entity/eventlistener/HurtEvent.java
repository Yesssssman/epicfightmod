package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public abstract class HurtEvent<T> extends PlayerEvent<ServerPlayerPatch> {
	private final T damageSource;
	private float amount;
	private AttackResult.ResultType result;
	
	public HurtEvent(ServerPlayerPatch playerpatch, T damageSource, float amount) {
		super(playerpatch, true);
		this.damageSource = damageSource;
		this.amount = amount;
		this.result = AttackResult.ResultType.SUCCESS;
	}
	
	public T getDamageSource() {
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
	
	public static class Pre extends HurtEvent<DamageSource> {
		public Pre(ServerPlayerPatch playerpatch, DamageSource damageSource, float amount) {
			super(playerpatch, damageSource, amount);
		}
	}
	
	public static class Post extends HurtEvent<ExtendedDamageSource> {
		public Post(ServerPlayerPatch playerpatch, ExtendedDamageSource damageSource, float amount) {
			super(playerpatch, damageSource, amount);
		}
	}
}