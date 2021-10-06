package yesman.epicfight.skill;

import java.util.UUID;

import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;

public class StaminaPillagerSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("20807880-fd30-11eb-9a03-0242ac130003");
	
	public StaminaPillagerSkill() {
		super("stamina_pillager");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.executer.getEventListener().addEventListener(EventType.DEALT_DAMAGE_POST_EVENT, EVENT_UUID, (event) -> {
			if (!event.getTarget().isAlive()) {
				float stamina = event.getPlayerData().getStamina();
				float missingStamina = event.getPlayerData().getMaxStamina() - stamina;
				event.getPlayerData().setStamina(stamina + missingStamina * 0.3F);
			}
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.DEALT_DAMAGE_POST_EVENT, EVENT_UUID);
	}
}