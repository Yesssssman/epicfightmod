package maninthehouse.epicfight.utils.game;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextFormatting;

public interface IExtendedDamageSource {
	public static DamageSourceExtended causePlayerDamage(EntityPlayer player, StunType stunType, DamageType damageType, int id) {
        return new DamageSourceExtended("player", player, stunType, damageType, id);
    }
	
	public static DamageSourceExtended causeMobDamage(EntityLivingBase mob, StunType stunType, DamageType damageType, int id) {
        return new DamageSourceExtended("mob", mob, stunType, damageType, id);
    }
	
	public static DamageSourceExtended getFrom(IExtendedDamageSource original) {
		return new DamageSourceExtended(original.getType(), original.getOwner(), original.getStunType(), original.getExtDamageType(), original.getSkillId());
	}
	
	public void setImpact(float amount);
	public void setArmorIgnore(float amount);
	public void setStunType(StunType stunType);
	public float getImpact();
	public float getArmorIgnoreRatio();
	public int getSkillId();
	public StunType getStunType();
	public DamageType getExtDamageType();
	public Entity getOwner();
	public String getType();
	
	public static enum StunType {
		SHORT(TextFormatting.GOLD + "SHORT" + TextFormatting.DARK_GRAY + " stun"),
		LONG(TextFormatting.GOLD + "LONG" + TextFormatting.DARK_GRAY + " stun"),
		HOLD(TextFormatting.GOLD + "HOLDING");
		
		private String tooltip;
		
		StunType(String tooltip) {
			this.tooltip = tooltip;
		}
		
		@Override
		public String toString() {
			return tooltip;
		}
	}
	
	public static enum DamageType {
		PHYSICAL, MAGIC
	}
}