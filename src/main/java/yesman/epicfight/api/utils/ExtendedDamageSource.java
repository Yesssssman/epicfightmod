package yesman.epicfight.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import yesman.epicfight.api.animation.types.StaticAnimation;

public interface ExtendedDamageSource {
	public static EpicFightDamageSource causePlayerDamage(PlayerEntity player, StunType stunType, StaticAnimation animation, Hand hand) {
        return new EpicFightDamageSource("player", player, stunType, animation, hand);
    }
	
	public static EpicFightDamageSource causeMobDamage(LivingEntity mob, StunType stunType, StaticAnimation animation) {
        return new EpicFightDamageSource("mob", mob, stunType, animation);
    }
	
	public static EpicFightDamageSource causeDamage(String msg, LivingEntity attacker, StunType stunType, StaticAnimation animation) {
        return new EpicFightDamageSource(msg, attacker, stunType, animation);
    }
	
	public void setImpact(float amount);
	public void setArmorNegation(float amount);
	public void setStunType(StunType stunType);
	public void setFinisher(boolean flag);
	public void setInitialPosition(Vector3d initialPosition);
	public float getImpact();
	public float getArmorNegation();
	public boolean isBasicAttack();
	public boolean isFinisher();
	public int getAnimationId();
	public StunType getStunType();
	public Entity getOwner();
	public String getType();
	
	public static enum StunType {
		NONE(TextFormatting.GRAY + "NONE"),
		SHORT(TextFormatting.GREEN + "SHORT" + TextFormatting.DARK_GRAY + " stun"),
		LONG(TextFormatting.GOLD + "LONG" + TextFormatting.DARK_GRAY + " stun"),
		HOLD(TextFormatting.RED + "HOLD"),
		KNOCKDOWN(TextFormatting.RED + "KNOCKDOWN"),
		FALL(TextFormatting.GRAY + "FALL");
		
		private String tooltip;
		
		StunType(String tooltip) {
			this.tooltip = tooltip;
		}
		
		@Override
		public String toString() {
			return tooltip;
		}
	}
}