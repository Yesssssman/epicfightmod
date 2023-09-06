package yesman.epicfight.skill.passive;

import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ForbiddenStrengthSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("0cfd60ba-b900-11ed-afa1-0242ac120002");
	
	public ForbiddenStrengthSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getExecuter().getEventListener().addEventListener(EventType.SKILL_CONSUME_EVENT, EVENT_UUID, (event) -> {
			if (event.getResourceType() == Skill.Resource.STAMINA) {
				float staminaConsume = event.getAmount();
				
				if (!container.getExecuter().hasStamina(staminaConsume) && !container.getExecuter().getOriginal().isCreative()) {
					event.setResourceType(Skill.Resource.HEALTH);
					event.setAmount(staminaConsume);
					
					if (event.shouldConsume()) {
						Player player = container.getExecuter().getOriginal();
						player.level.playSound(null, player.getX(), player.getY(), player.getZ(), EpicFightSounds.FORBIDDEN_STRENGTH, player.getSoundSource(), 1.0F, 1.0F);
						((ServerLevel)player.level).sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY(0.5D), player.getZ(), (int)staminaConsume, 0.1D, 0.0D, 0.1D, 0.2D);
					}
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.SKILL_CONSUME_EVENT, EVENT_UUID);
	}
}