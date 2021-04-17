package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.entity.DataKeys;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;

public class WitherSkeletonData extends SkeletonData<EntityWitherSkeleton> {
	public WitherSkeletonData() {
		super(Faction.WITHER_ARMY);
	}
	
	@Override
	public void onEntityJoinWorld(EntityWitherSkeleton entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getDataManager().set(DataKeys.STUN_ARMOR, 4.0F);
	}
	
	@Override
	public void postInit() {
		super.resetCombatAI();
		super.postInit();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttributeMap().getAttributeInstance(ModAttributes.MAX_STUN_ARMOR).setBaseValue(4.0F);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animator) {
		animator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.WITHER_SKELETON_IDLE);
		animator.addLivingAnimation(LivingMotion.WALKING, Animations.WITHER_SKELETON_WALK);
		animator.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public boolean hurtEntity(Entity hitTarget, EnumHand handIn, IExtendedDamageSource source, float amount) {
		boolean succed = super.hurtEntity(hitTarget, handIn, source, amount);

		if (succed && hitTarget instanceof EntityLivingBase && this.orgEntity.getRNG().nextInt(10) == 0) {
			((EntityLivingBase) hitTarget).addPotionEffect(new PotionEffect(MobEffects.WITHER, 200));
		}

		return succed;
	}
	
	@Override
	public void setAIAsArmed() {
		orgEntity.tasks.addTask(0, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 2.5D, true, MobAttackPatterns.WITHER_SKELETON_PATTERN));
		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.2D, true, Animations.WITHER_SKELETON_CHASE, Animations.WITHER_SKELETON_WALK));
	}
	
	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);
		return VisibleMatrix4f.scale(new Vec3f(1.2F, 1.2F, 1.2F), mat, mat);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_SKELETON;
	}
}