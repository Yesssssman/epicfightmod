package maninhouse.epicfight.capabilities.entity.mob;

import java.util.Iterator;
import java.util.Set;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.MobData;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.CreeperSwellStoppableGoal;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraft.entity.ai.goal.CreeperSwellGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.util.math.MathHelper;

public class CreeperData extends MobData<CreeperEntity> {
	public CreeperData() {
		super(Faction.NATURAL);
	}

	@Override
	protected void initAI() {
        Set<PrioritizedGoal> goals = this.orgEntity.goalSelector.goals;
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		
		while (iterator.hasNext()) {
			PrioritizedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (inner instanceof CreeperSwellGoal) {
				toRemove = inner;
				break;
			}
		}
        
        if(toRemove != null)
        	orgEntity.goalSelector.removeGoal(toRemove);
        
        orgEntity.goalSelector.addGoal(2, new CreeperSwellStoppableGoal(this, this.orgEntity));
	}

	@Override
	protected void initAnimator(AnimatorClient animator) {
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.CREEPER_IDLE);
		animator.addLivingAnimation(LivingMotion.WALK, Animations.CREEPER_WALK);
		animator.addLivingAnimation(LivingMotion.DEATH, Animations.CREEPER_DEATH);
	}

	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);

		if (this.isRemote()) {
			float f = this.orgEntity.getCreeperFlashIntensity(partialTicks);
			
			float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
	        f = MathHelper.clamp(f, 0.0F, 1.0F);
	        f = f * f;
	        f = f * f;
	        float f2 = (1.0F + f * 0.4F) * f1;
	        float f3 = (1.0F + f * 0.1F) / f1;
	        
			OpenMatrix4f.scale(new Vec3f(f2, f3, f2), mat, mat);
		}
		
		return mat;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (stunType == StunType.LONG) {
			return Animations.CREEPER_HIT_LONG;
		} else {
			return Animations.CREEPER_HIT_SHORT;
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_CREEPER;
	}
}