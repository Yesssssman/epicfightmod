package yesman.epicfight.skill;

import java.util.UUID;

import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class StaminaPillagerSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("20807880-fd30-11eb-9a03-0242ac130003");
	
	public StaminaPillagerSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (!event.getTarget().isAlive()) {
				float stamina = event.getPlayerPatch().getStamina();
				float missingStamina = event.getPlayerPatch().getMaxStamina() - stamina;
				event.getPlayerPatch().setStamina(stamina + missingStamina * 0.3F);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
}