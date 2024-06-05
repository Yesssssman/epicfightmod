package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

/**
 * Canceling this event will make skill failed to predicate resource check
 * See also {@link Skill#resourcePredicate(PlayerPatch)}
 */
public class SkillConsumeEvent extends PlayerEvent<PlayerPatch<?>> {
	private final Skill skill;
	private float amount;
	private Skill.Resource resource;
	
	public SkillConsumeEvent(PlayerPatch<?> playerpatch, Skill skill, Skill.Resource resource) {
		this(playerpatch, skill, resource, skill.getDefaultConsumptionAmount(playerpatch));
	}
	
	public SkillConsumeEvent(PlayerPatch<?> playerpatch, Skill skill, Skill.Resource resource, float amount) {
		super(playerpatch, true);
		
		this.skill = skill;
		this.resource = resource;
		this.amount = amount;
	}
	
	public Skill getSkill() {
		return this.skill;
	}
	
	public Skill.Resource getResourceType() {
		return this.resource;
	}
	
	public float getAmount() {
		return this.amount;
	}
	
	public void setResourceType(Skill.Resource resource) {
		this.resource = resource;
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
}