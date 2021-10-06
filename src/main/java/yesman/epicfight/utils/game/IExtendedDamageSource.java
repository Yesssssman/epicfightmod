package yesman.epicfight.utils.game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.text.TextFormatting;
import yesman.epicfight.animation.types.AttackAnimation;

public interface IExtendedDamageSource {
	public static DamageSourceExtended causePlayerDamage(PlayerEntity player, StunType stunType, AttackAnimation animation, Hand hand) {
        return new DamageSourceExtended("player", player, stunType, animation, hand);
    }
	
	public static DamageSourceExtended causeMobDamage(LivingEntity mob, StunType stunType, AttackAnimation animation) {
        return new DamageSourceExtended("mob", mob, stunType, animation);
    }
	
	public void setImpact(float amount);
	public void setArmorNegation(float amount);
	public void setStunType(StunType stunType);
	public float getImpact();
	public float getArmorNegation();
	public boolean isBasicAttack();
	public int getSkillId();
	public StunType getStunType();
	public Entity getOwner();
	public String getType();
	
	public static enum StunType {
		SHORT(TextFormatting.GREEN + "SHORT" + TextFormatting.DARK_GRAY + " stun"),
		LONG(TextFormatting.GOLD + "LONG" + TextFormatting.DARK_GRAY + " stun"),
		HOLD(TextFormatting.RED + "HOLDING");
		
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