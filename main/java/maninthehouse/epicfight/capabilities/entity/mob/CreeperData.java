package maninthehouse.epicfight.capabilities.entity.mob;

import java.util.Iterator;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAICreeperSwellStoppable;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAICreeperSwell;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.MathHelper;

public class CreeperData extends MobData<EntityCreeper> {
	public CreeperData() {
		super(Faction.NATURAL);
	}

	@Override
	protected void initAI() {
		Iterator<EntityAITasks.EntityAITaskEntry> iterator = orgEntity.tasks.taskEntries.iterator();
		while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityAI = entityaitasks$entityaitaskentry.action;
            
			if (entityAI instanceof EntityAICreeperSwell) {
            	iterator.remove();
            }
        }
        
        orgEntity.tasks.addTask(2, new EntityAICreeperSwellStoppable(this, this.orgEntity));
	}

	@Override
	protected void initAnimator(AnimatorClient animator) {
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.CREEPER_IDLE);
		animator.addLivingAnimation(LivingMotion.WALKING, Animations.CREEPER_WALK);
		animator.addLivingAnimation(LivingMotion.DEATH, Animations.CREEPER_DEATH);
		animator.setCurrentLivingMotionsToDefault();
	}

	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);

		if (this.isRemote()) {
			float f = this.orgEntity.getCreeperFlashIntensity(partialTicks);
			
			float f1 = 1.0F + MathHelper.sin(f * 100.0F) * f * 0.01F;
	        f = MathHelper.clamp(f, 0.0F, 1.0F);
	        f = f * f;
	        f = f * f;
	        float f2 = (1.0F + f * 0.4F) * f1;
	        float f3 = (1.0F + f * 0.1F) / f1;
	        
			VisibleMatrix4f.scale(new Vec3f(f2, f3, f2), mat, mat);
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