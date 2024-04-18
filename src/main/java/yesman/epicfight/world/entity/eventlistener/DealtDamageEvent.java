package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public class DealtDamageEvent extends PlayerEvent<ServerPlayerPatch> {
	private float attackDamage;
	private final LivingEntity target;
	private final EpicFightDamageSource damageSource;
	
	public DealtDamageEvent(ServerPlayerPatch playerpatch, LivingEntity target, EpicFightDamageSource source, float damage) {
		super(playerpatch, false);
		this.target = target;
		this.damageSource = source;
		this.attackDamage = damage;
	}
	
	public LivingEntity getTarget() {
		return this.target;
	}
	
	public EpicFightDamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public void setAttackDamage(float damage) {
		this.attackDamage = damage;
	}
	
	public float getAttackDamage() {
		return this.attackDamage;
	}
	
	public static class Pre extends DealtDamageEvent {
		private final LivingHurtEvent forgeevent;
		
		public Pre(ServerPlayerPatch playerpatch, LivingEntity target, EpicFightDamageSource source, float damage, LivingHurtEvent forgeevent) {
			super(playerpatch, target, source, damage);
			
			this.forgeevent = forgeevent;
		}
		
		public LivingHurtEvent getForgeEvent() {
			return this.forgeevent;
		}
	}
}