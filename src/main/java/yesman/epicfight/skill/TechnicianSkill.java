package yesman.epicfight.skill;

import java.util.UUID;

import net.minecraft.world.damagesource.DamageSource;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.math.Formulars;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class TechnicianSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("99e5c782-fdaf-11eb-9a03-0242ac130003");
	private static final SkillDataKey<Boolean> CURRENTLY_ACTIVATED = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	
	public TechnicianSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(CURRENTLY_ACTIVATED);
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (event.getAnimation() instanceof DodgeAnimation) {
				container.getDataManager().setData(CURRENTLY_ACTIVATED, false);
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			ServerPlayerPatch executer = event.getPlayerPatch();
			
			if (executer.getAnimator().getPlayerFor(null).getAnimation() instanceof DodgeAnimation) {
				DamageSource damageSource = event.getDamageSource();
				
				if (executer.getEntityState().invulnerableTo(damageSource)) {
					if (!container.getDataManager().getDataValue(CURRENTLY_ACTIVATED)) {
						float consumption = Formulars.getStaminarConsumePenalty(executer.getWeight(), executer.getSkill(SkillCategories.DODGE).containingSkill.getConsumption(), executer);
						executer.setStamina(executer.getStamina() + consumption);
						container.getDataManager().setData(CURRENTLY_ACTIVATED, true);
						event.setCanceled(true);
						event.setResult(AttackResult.ResultType.FAILED);
					}
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID);
	}
}