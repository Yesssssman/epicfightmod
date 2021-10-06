package yesman.epicfight.skill;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.util.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.entity.eventlistener.HitEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Skills;

public class EnergizingGuardSkill extends GuardSkill {
	private static final List<WeaponCategory> AVAILABLE_WEAPON_TYPES = Lists.<WeaponCategory>newLinkedList();
	
	static {
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.GREATSWORD);
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.LONGSWORD);
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.SPEAR);
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.TACHI);
	}
	
	public EnergizingGuardSkill() {
		super("energizing_guard");
	}
	
	@Override
	public boolean guard(SkillContainer container, CapabilityItem itemCapapbility, HitEvent event, float knockback, float impact, boolean reinforced) {
		boolean reinforce = AVAILABLE_WEAPON_TYPES.contains(itemCapapbility.getWeaponCategory());
		if (event.getForgeEvent().getSource().isExplosion()) {
			impact = event.getForgeEvent().getAmount();
		}
		
		boolean guard = super.guard(container, itemCapapbility, event, knockback, impact, reinforce);
		if (guard && isSpecialDamageSource(event.getForgeEvent().getSource())) {
			event.getPlayerData().getOriginalEntity().attackEntityFrom(DamageSource.GENERIC, event.getForgeEvent().getAmount() * 0.2F);
		}
		
		return guard;
	}
	
	@Override
	protected boolean isBlockableSource(DamageSource damageSource, boolean specialSourceBlockCondition) {
		return (!damageSource.isUnblockable() || damageSource.damageType.equals("indirectMagic")) &&
				(specialSourceBlockCondition || super.isBlockableSource(damageSource, false));
	}
	
	@Override
	public float getPenaltyStamina(CapabilityItem itemCap) {
		return AVAILABLE_WEAPON_TYPES.contains(itemCap.getWeaponCategory()) ? 0.2F : 0.6F;
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		container.executer.getEventListener().removeListener(EventType.TAKE_DAMAGE_EVENT, EVENT_UUID);
	}
	
	private static boolean isSpecialDamageSource(DamageSource damageSource) {
		return (damageSource.isExplosion() || damageSource.isMagicDamage() || damageSource.isFireDamage() || damageSource.isProjectile()) && !
				(damageSource.isUnblockable() && !damageSource.damageType.equals("indirectMagic"));
	}
	
	@Override
	public Skill getPriorSkill() {
		return Skills.GUARD;
	}
	
	@Override
	protected boolean isHighTierGuard() {
		return true;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.<Object>newArrayList();
		list.add(String.format("%s, %s, %s, %s", WeaponCategory.GREATSWORD, WeaponCategory.LONGSWORD, WeaponCategory.SPEAR, WeaponCategory.TACHI).toLowerCase());
		return list;
	}
}