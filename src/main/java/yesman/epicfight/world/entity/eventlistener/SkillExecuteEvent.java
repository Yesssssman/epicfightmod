package yesman.epicfight.world.entity.eventlistener;

import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SkillExecuteEvent extends PlayerEvent<PlayerPatch<?>> {
	private final SkillContainer skillContainer;
	private boolean resourceAvailable;
	private boolean skillExecutable;
	private boolean stateExecutable;
	
	public SkillExecuteEvent(PlayerPatch<?> playerpatch, SkillContainer skillContainer) {
		super(playerpatch, true);
		
		this.skillContainer = skillContainer;
	}
	
	public SkillContainer getSkillContainer() {
		return this.skillContainer;
	}
	
	public boolean isResourceAvailable() {
		return this.resourceAvailable;
	}

	public boolean isSkillExecutable() {
		return this.skillExecutable;
	}

	public boolean isStateExecutable() {
		return this.stateExecutable;
	}

	public void setResourcePredicate(boolean resourcePredicate) {
		this.resourceAvailable = resourcePredicate;
	}
	
	public void setSkillExecutable(boolean skillExecutable) {
		this.skillExecutable = skillExecutable;
	}
	
	public void setStateExecutable(boolean stateExecutable) {
		this.stateExecutable = stateExecutable;
	}
	
	public boolean isExecutable() {
		return (this.resourceAvailable || this.getPlayerPatch().getOriginal().isCreative()) && this.skillExecutable && this.stateExecutable;
	}
	
	public boolean shouldReserverKey() {
		return !this.isExecutable() && !this.isCanceled();
	}
}