package yesman.epicfight.skill.passive;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class SwordmasterSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("a395b692-fd97-11eb-9a03-0242ac130003");
	private static final WeaponCategories[] AVAILABLE_WEAPON_TYPES = {WeaponCategories.UCHIGATANA, WeaponCategories.LONGSWORD, WeaponCategories.SWORD, WeaponCategories.TACHI};
	
	private float speedBonus;
	
	public SwordmasterSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		this.speedBonus = parameters.getFloat("speed_bonus");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID, (event) -> {
			WeaponCategory heldWeaponCategory = event.getItemCapability().getWeaponCategory();
			
			for (WeaponCategories weaponCategory : AVAILABLE_WEAPON_TYPES) {
				if (weaponCategory == heldWeaponCategory) {
					float attackSpeed = event.getAttackSpeed();
					event.setAttackSpeed(attackSpeed * (1.0F + this.speedBonus * 0.01F));
					break;
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.MODIFY_ATTACK_SPEED_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(String.format("%.0f", this.speedBonus));
		
		StringBuilder sb = new StringBuilder();
		
        for (int i = 0; i < AVAILABLE_WEAPON_TYPES.length; i++) {
            sb.append(WeaponCategory.ENUM_MANAGER.toTranslated(AVAILABLE_WEAPON_TYPES[i]));
            
            if (i < AVAILABLE_WEAPON_TYPES.length - 1) {
            	sb.append(", ");
            }
        }
        
        list.add(sb.toString());
		
		return list;
	}
}