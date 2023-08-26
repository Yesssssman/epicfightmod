package yesman.epicfight.api.animation.types;

import net.minecraft.world.damagesource.EntityDamageSource;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;

public class KnockdownAnimation extends LongHitAnimation {
	public KnockdownAnimation(float convertTime, float delayTime, String path, Armature armature) {
		super(convertTime, path, armature);

		this.stateSpectrumBlueprint
			.addState(EntityState.KNOCKDOWN, true)
			.addState(EntityState.ATTACK_RESULT, (damagesource) -> {
				if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
					if (damagesource instanceof EpicFightDamageSource) {
						return ((EpicFightDamageSource)damagesource).hasTag(SourceTags.FINISHER) ? AttackResult.ResultType.SUCCESS : AttackResult.ResultType.BLOCKED;
					} else {
						return AttackResult.ResultType.BLOCKED;
					}
				}
				
				return AttackResult.ResultType.SUCCESS;
			});
	}
}