package yesman.epicfight.skill;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.game.AttackResult;
import yesman.epicfight.api.utils.game.ExtendedDamageSource;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.HurtEventPre;

public class EnergizingGuardSkill extends GuardSkill {
	private static final List<WeaponCategory> AVAILABLE_WEAPON_TYPES = Lists.<WeaponCategory>newLinkedList();
	
	static {
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.GREATSWORD);
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.LONGSWORD);
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.SPEAR);
		AVAILABLE_WEAPON_TYPES.add(WeaponCategory.TACHI);
	}
	
	public EnergizingGuardSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void guard(SkillContainer container, CapabilityItem itemCapapbility, HurtEventPre event, float knockback, float impact, boolean reinforced) {
		boolean reinforce = AVAILABLE_WEAPON_TYPES.contains(itemCapapbility.getWeaponCategory());
		
		if (event.getDamageSource().isExplosion()) {
			impact = event.getAmount();
		}
		
		super.guard(container, itemCapapbility, event, knockback, impact, reinforce);
	}
	
	@Override
	public void dealEvent(PlayerPatch<?> playerpatch, HurtEventPre event) {
		boolean isSpecialSource = isSpecialDamageSource(event.getDamageSource());
		event.setAmount(isSpecialSource ? event.getAmount() * 0.2F : 0.0F);
		event.setResult(isSpecialSource ? AttackResult.ResultType.SUCCESS : AttackResult.ResultType.BLOCKED);
		
		if (event.getDamageSource() instanceof ExtendedDamageSource) {
			((ExtendedDamageSource)event.getDamageSource()).setStunType(ExtendedDamageSource.StunType.NONE);
		}
		
		event.setCanceled(true);
		
		Entity directEntity = event.getDamageSource().getDirectEntity();
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)directEntity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (entitypatch != null) {
			entitypatch.onAttackBlocked(event, playerpatch);
		}
	}
	
	@Override
	protected boolean isBlockableSource(DamageSource damageSource, boolean specialSourceBlockCondition) {
		return (!damageSource.isBypassArmor() || damageSource.msgId.equals("indirectMagic")) && (specialSourceBlockCondition || super.isBlockableSource(damageSource, false)) && !damageSource.isBypassInvul();
	}
	
	@Override
	public float getPenaltyStamina(CapabilityItem itemCap) {
		return AVAILABLE_WEAPON_TYPES.contains(itemCap.getWeaponCategory()) ? 0.2F : 0.6F;
	}
	
	private static boolean isSpecialDamageSource(DamageSource damageSource) {
		return (damageSource.isExplosion() || damageSource.isMagic() || damageSource.isFire() || damageSource.isProjectile()) && !(damageSource.isBypassArmor() && !damageSource.msgId.equals("indirectMagic"));
	}
	
	@Override
	public Skill getPriorSkill() {
		return Skills.GUARD;
	}
	
	@Override
	protected boolean isAdvancedGuard() {
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