package yesman.epicfight.api.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;

public interface ExtendedDamageSource {
	public static EpicFightDamageSource causePlayerDamage(Player player, StunType stunType, StaticAnimation animation, InteractionHand hand) {
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
	public void setInitialPosition(Vec3 initialPosition);
	public float getImpact();
	public float getArmorNegation();
	public boolean isBasicAttack();
	public boolean isFinisher();
	public int getAnimationId();
	public StunType getStunType();
	public Entity getOwner();
	public String getType();
	
	public static enum StunType {
		NONE(ChatFormatting.GRAY + "NONE"),
		SHORT(ChatFormatting.GREEN + "SHORT" + ChatFormatting.DARK_GRAY + " stun"),
		LONG(ChatFormatting.GOLD + "LONG" + ChatFormatting.DARK_GRAY + " stun"),
		HOLD(ChatFormatting.RED + "HOLD"),
		KNOCKDOWN(ChatFormatting.RED + "KNOCKDOWN"),
		FALL(ChatFormatting.GRAY + "FALL");
		
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