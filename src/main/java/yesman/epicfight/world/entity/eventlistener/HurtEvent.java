package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public abstract class HurtEvent<T> extends PlayerEvent<ServerPlayerPatch> {
	private final T damageSource;
	private float amount;
	private boolean parried;
	private AttackResult.ResultType result;
	
	private HurtEvent(ServerPlayerPatch playerpatch, T damageSource, float amount, boolean cancelable) {
		super(playerpatch, cancelable);
		this.damageSource = damageSource;
		this.amount = amount;
		this.parried = false;
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
	
	public void setParried(boolean parried) {
		this.parried = parried;
	}
	
	public boolean isParried() {
		return this.parried;
	}
	
	public static class Pre extends HurtEvent<DamageSource> {
		public Pre(ServerPlayerPatch playerpatch, DamageSource damageSource, float amount) {
			super(playerpatch, damageSource, amount, true);
		}
	}
	
	public static class Post extends HurtEvent<EpicFightDamageSource> {
		public Post(ServerPlayerPatch playerpatch, EpicFightDamageSource damageSource, float amount) {
			super(playerpatch, damageSource, amount, false);
		}
	}
}