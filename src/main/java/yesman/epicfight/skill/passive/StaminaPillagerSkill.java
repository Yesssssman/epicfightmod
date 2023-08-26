package yesman.epicfight.skill.passive;

import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class StaminaPillagerSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("20807880-fd30-11eb-9a03-0242ac130003");
	
	protected float regenRate;
	
	public StaminaPillagerSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		this.regenRate = parameters.getFloat("regen_rate");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (!event.getTarget().isAlive()) {
				float stamina = event.getPlayerPatch().getStamina();
				float missingStamina = event.getPlayerPatch().getMaxStamina() - stamina;
				event.getPlayerPatch().setStamina(stamina + missingStamina * this.regenRate * 0.01F);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(String.format("%.0f", this.regenRate));
		
		return list;
	}
}