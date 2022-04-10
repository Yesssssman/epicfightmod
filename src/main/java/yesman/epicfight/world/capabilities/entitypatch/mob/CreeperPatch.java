package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Iterator;
import java.util.Set;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.SwellGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.CreeperSwellStoppableGoal;

public class CreeperPatch extends MobPatch<Creeper> {
	public CreeperPatch() {
		super(Faction.NATURAL);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(1.0D);
	}
	
	@Override
	protected void initAI() {
        Set<WrappedGoal> goals = this.original.goalSelector.getAvailableGoals();
		Iterator<WrappedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		
		while (iterator.hasNext()) {
			WrappedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (inner instanceof SwellGoal) {
				toRemove = inner;
				break;
			}
		}
        
        if (toRemove != null) {
        	this.original.goalSelector.removeGoal(toRemove);
        }
        
        this.original.goalSelector.addGoal(2, new CreeperSwellStoppableGoal(this, this.original));
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator animator) {
		animator.addLivingMotion(LivingMotion.IDLE, Animations.CREEPER_IDLE);
		animator.addLivingMotion(LivingMotion.WALK, Animations.CREEPER_WALK);
		animator.addLivingMotion(LivingMotion.DEATH, Animations.CREEPER_DEATH);
	}

	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);

		if (this.isLogicalClient()) {
			float f = this.original.getSwelling(partialTicks);
			float f1 = 1.0F + Mth.sin(f * 100.0F) * f * 0.01F;
	        f = Mth.clamp(f, 0.0F, 1.0F);
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
		return modelDB.creeper;
	}
}