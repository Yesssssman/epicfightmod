package yesman.epicfight.skill.passive;

import java.util.UUID;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class TechnicianSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("99e5c782-fdaf-11eb-9a03-0242ac130003");
	
	public TechnicianSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID, (event) -> {
			float consumption = container.getExecuter().getModifiedStaminaConsume(container.getExecuter().getSkill(SkillSlots.DODGE).getSkill().getConsumption());
			container.getExecuter().setStamina(container.getExecuter().getStamina() + consumption);
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.DODGE_SUCCESS_EVENT, EVENT_UUID);
	}
}