package yesman.epicfight.api.animation.types;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageType;

public class KnockdownAnimation extends LongHitAnimation {
	public KnockdownAnimation(float convertTime, float delayTime, String path, Armature armature) {
		super(convertTime, path, armature);

		this.stateSpectrumBlueprint
			.addState(EntityState.KNOCKDOWN, true)
			.addState(EntityState.ATTACK_RESULT, (damagesource) -> {
				if (damagesource.getEntity() != null && !damagesource.is(DamageTypeTags.IS_EXPLOSION) && !damagesource.is(DamageTypes.MAGIC) && !damagesource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
					if (damagesource instanceof EpicFightDamageSource) {
						return ((EpicFightDamageSource)damagesource).is(EpicFightDamageType.FINISHER) ? AttackResult.ResultType.SUCCESS : AttackResult.ResultType.BLOCKED;
					} else {
						return AttackResult.ResultType.BLOCKED;
					}
				}
				
				return AttackResult.ResultType.SUCCESS;
			});
	}
}