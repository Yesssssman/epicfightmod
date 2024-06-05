package yesman.epicfight.world.entity.eventlistener;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;

public abstract class DealtDamageEvent<T extends LivingEvent> extends PlayerEvent<ServerPlayerPatch> {
	protected final LivingEntity target;
	private final EpicFightDamageSource damageSource;
	protected final T forgeevent;
	
	public DealtDamageEvent(ServerPlayerPatch playerpatch, LivingEntity target, EpicFightDamageSource source, T forgeevent) {
		super(playerpatch, false);
		this.target = target;
		this.damageSource = source;
		this.forgeevent = forgeevent;
	}
	
	public LivingEntity getTarget() {
		return this.target;
	}
	
	public EpicFightDamageSource getDamageSource() {
		return this.damageSource;
	}
	
	public abstract float getAttackDamage();
	
	public T getForgeEvent() {
		return this.forgeevent;
	}
	
	public static class Attack extends DealtDamageEvent<LivingAttackEvent> {
		public Attack(ServerPlayerPatch playerpatch, LivingEntity target, EpicFightDamageSource source, LivingAttackEvent forgeevent) {
			super(playerpatch, target, source, forgeevent);
		}
		
		@Override
		public float getAttackDamage() {
			return this.forgeevent.getAmount();
		}
	}
	
	public static class Hurt extends DealtDamageEvent<LivingHurtEvent> {
		public Hurt(ServerPlayerPatch playerpatch, LivingEntity target, EpicFightDamageSource source, LivingHurtEvent forgeevent) {
			super(playerpatch, target, source, forgeevent);
		}
		
		public void setAttackDamage(float damage) {
			this.forgeevent.setAmount(damage);
		}
		
		@Override
		public float getAttackDamage() {
			return this.forgeevent.getAmount();
		}
	}
	
	public static class Damage extends DealtDamageEvent<LivingDamageEvent> {
		public Damage(ServerPlayerPatch playerpatch, LivingEntity target, EpicFightDamageSource source, LivingDamageEvent forgeevent) {
			super(playerpatch, target, source, forgeevent);
		}
		
		public void setAttackDamage(float damage) {
			this.forgeevent.setAmount(damage);
		}
		
		@Override
		public float getAttackDamage() {
			return this.forgeevent.getAmount();
		}
	}
}