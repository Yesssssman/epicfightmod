package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;

public class SwordmasterSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("a395b692-fd97-11eb-9a03-0242ac130003");
	private static final WeaponCategory[] AVAILABLE_WEAPON_TYPES = {WeaponCategory.KATANA, WeaponCategory.LONGSWORD, WeaponCategory.SWORD, WeaponCategory.TACHI};
	
	public SwordmasterSkill() {
		super("swordmaster");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.executer.getEventListener().addEventListener(EventType.ATTACK_SPEED_GET_EVENT, EVENT_UUID, (event) -> {
			WeaponCategory heldWeaponCategory = event.getItemCapability().getWeaponCategory();
			
			for (WeaponCategory weaponCategory : AVAILABLE_WEAPON_TYPES) {
				if (weaponCategory == heldWeaponCategory) {
					float attackSpeed = event.getAttackSpeed();
					event.setAttackSpeed(attackSpeed * 1.3F);
					break;
				}
			}
			
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ATTACK_SPEED_GET_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.<Object>newArrayList();
		list.add(String.format("%s, %s, %s, %s", (Object[])AVAILABLE_WEAPON_TYPES).toLowerCase());
		return list;
	}
}