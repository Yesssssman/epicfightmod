package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SkillConsumeEvent extends PlayerEvent<PlayerPatch<?>> {
	private final Skill skill;
	private Skill.Resource resource;
	private float amount;
	private final boolean consume;
	
	public SkillConsumeEvent(PlayerPatch<?> playerpatch, Skill skill, Skill.Resource resource, boolean consume) {
		this(playerpatch, skill, resource, skill.getDefaultConsumeptionAmount(playerpatch), consume);
	}
	
	public SkillConsumeEvent(PlayerPatch<?> playerpatch, Skill skill, Skill.Resource resource, float amount, boolean consume) {
		super(playerpatch, true);
		this.skill = skill;
		this.resource = resource;
		this.amount = amount;
		this.consume = consume;
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
	
	public boolean shouldConsume() {
		return this.consume;
	}
	
	public void setResourceType(Skill.Resource resource) {
		this.resource = resource;
	}
	
	public void setAmount(float amount) {
		this.amount = amount;
	}
}