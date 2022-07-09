package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class SwordmasterSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("a395b692-fd97-11eb-9a03-0242ac130003");
	private static final WeaponCategories[] AVAILABLE_WEAPON_TYPES = {WeaponCategories.KATANA, WeaponCategories.LONGSWORD, WeaponCategories.SWORD, WeaponCategories.TACHI};
	
	public SwordmasterSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_SPEED_MODIFY_EVENT, EVENT_UUID, (event) -> {
			WeaponCategories heldWeaponCategory = event.getItemCapability().getWeaponCategory();
			
			for (WeaponCategories weaponCategory : AVAILABLE_WEAPON_TYPES) {
				if (weaponCategory == heldWeaponCategory) {
					float attackSpeed = event.getAttackSpeed();
					event.setAttackSpeed(attackSpeed * 1.3F);
					break;
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_SPEED_MODIFY_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.<Object>newArrayList();
		list.add(String.format("%s, %s, %s, %s", (Object[])AVAILABLE_WEAPON_TYPES).toLowerCase());
		return list;
	}
}