package yesman.epicfight.skill;

import java.util.UUID;

import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import yesman.epicfight.animation.types.DodgeAnimation;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.utils.math.Formulars;

public class TechnicianSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("99e5c782-fdaf-11eb-9a03-0242ac130003");
	private static final SkillDataKey<Boolean> GAINED = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	
	public TechnicianSkill() {
		super("technician");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(GAINED);
		container.executer.getEventListener().addEventListener(EventType.ACTION_EVENT, EVENT_UUID, (event) -> {
			if (event.getAnimation() instanceof DodgeAnimation) {
				container.getDataManager().setData(GAINED, false);
			}
			return false;
		});
		
		container.executer.getEventListener().addEventListener(EventType.HIT_EVENT, EVENT_UUID, (event) -> {
			ServerPlayerData executer = event.getPlayerData();
			if (executer.getEntityState().isInvincible()) {
				DamageSource damageSource = event.getForgeEvent().getSource();
				if (damageSource instanceof EntityDamageSource && !damageSource.isExplosion() && !damageSource.isMagicDamage()
						&& !damageSource.isUnblockable() && !container.getDataManager().getDataValue(GAINED)) {
					float consumption = Formulars.getStaminarConsumePenalty(executer.getWeight(),
							executer.getSkill(SkillCategory.DODGE).containingSkill.getConsumption(), executer);
					executer.setStamina(executer.getStamina() + consumption);
					container.getDataManager().setData(GAINED, true);
					return true;
				}
			}
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ACTION_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.HIT_EVENT, EVENT_UUID);
	}
}