package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.world.Difficulty;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.utils.game.IExtendedDamageSource;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class CaveSpiderData extends SpiderData<CaveSpiderEntity> {
	@Override
	protected void initAI() {
		super.initAI();
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.0D, true, AttackCombos.SPIDER));
        this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
	}
	
	@Override
	public boolean hurtEntity(Entity hitTarget, Hand handIn, IExtendedDamageSource source, float amount) {
		boolean succed = super.hurtEntity(hitTarget, handIn, source, amount);
		
		if (succed && hitTarget instanceof LivingEntity) {
			int i = 0;
            if (this.orgEntity.world.getDifficulty() == Difficulty.NORMAL) {
                i = 7;
            } else if (this.orgEntity.world.getDifficulty() == Difficulty.HARD) {
                i = 15;
            }

            if (i > 0) {
                ((LivingEntity)hitTarget).addPotionEffect(new EffectInstance(Effects.POISON, i * 20, 0));
            }
        }
		
		return succed;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);
		return OpenMatrix4f.scale(new Vec3f(0.7F, 0.7F, 0.7F), mat, mat);
	}
}