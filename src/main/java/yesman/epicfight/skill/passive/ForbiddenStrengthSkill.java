package yesman.epicfight.skill.passive;

import java.util.UUID;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
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
			if (event.getResourceType() == Skill.Resource.STAMINA && event.getSkill() != this) {
				if (!container.getExecuter().hasStamina(event.getAmount()) && !container.getExecuter().getOriginal().isCreative()) {
					event.setResourceType(Skill.Resource.HEALTH);
					
					float healthConsumeAmount = event.getAmount() - container.getExecuter().getStamina();
					event.setAmount(healthConsumeAmount);
					
					if (!container.getExecuter().isLogicalClient() && event.getResourceType().predicate.canExecute(this, container.getExecuter(), healthConsumeAmount)) {
						container.getExecuter().setStamina(0.0F);
						
						Player player = container.getExecuter().getOriginal();
						player.level().playSound(null, player.getX(), player.getY(), player.getZ(), EpicFightSounds.FORBIDDEN_STRENGTH.get(), player.getSoundSource(), 1.0F, 1.0F);
						((ServerLevel)player.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, player.getX(), player.getY(0.5D), player.getZ(), (int)healthConsumeAmount, 0.1D, 0.0D, 0.1D, 0.2D);
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
	
	@Override
	public boolean getCustomConsumptionTooltips(SkillBookScreen.AttributeIconList consumptionList) {
		consumptionList.add(Component.translatable("attribute.name.epicfight.health.consume.tooltip"), Component.translatable("skill.epicfight.forbidden_strength.consume.tooltip"), SkillBookScreen.HEALTH_TEXTURE_INFO);
		return true;
	}
}