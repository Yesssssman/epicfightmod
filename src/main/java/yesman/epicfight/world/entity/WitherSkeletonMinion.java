package yesman.epicfight.world.entity;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.monster.WitherSkeletonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class WitherSkeletonMinion extends WitherSkeletonEntity {
	private WitherEntity summoner;
	
	public WitherSkeletonMinion(EntityType<? extends WitherSkeletonMinion> p_34166_, World p_34167_) {
		super(p_34166_, p_34167_);
	}
	
	public WitherSkeletonMinion(World level, WitherEntity summoner, double x, double y, double z) {
		super(EpicFightEntities.WITHER_SKELETON_MINION.get(), level);
		this.setPos(x, y, z);
		this.summoner = summoner;
		
		if (this.summoner != null && this.summoner.isAlive()) {
			this.setTarget((LivingEntity)this.summoner.level.getEntity(this.summoner.getAlternativeTarget(0)));
		}
	}
	
	@Override
	public boolean canBeAffected(EffectInstance p_70687_1_) {
		return p_70687_1_.getEffect() == Effects.WITHER ? false : super.canBeAffected(p_70687_1_);
	}
	
	@Override
	protected void registerGoals() {
		super.registerGoals();
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, LivingEntity.class, 10, true, false, (livingentity) -> (livingentity.getMobType() != CreatureAttribute.UNDEAD && livingentity.attackable())));
	}
	
	@Override
	public boolean hurt(DamageSource source, float amount) {
		if (this.summoner != null && source.getEntity() == this.summoner) {
			return false;
		}
		
		return super.hurt(source, amount);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (this.level.isClientSide()) {
			this.level.addParticle(ParticleTypes.SMOKE, this.getX() + this.random.nextGaussian() * (double) 0.3F, this.getEyeY() + this.random.nextGaussian() * (double) 0.3F, this.getZ() + this.random.nextGaussian() * (double) 0.3F, 0.0D, 0.0D, 0.0D);
		} else {
			if (this.tickCount < 2) {
				this.setDeltaMovement(0.0D, 0.0D, 0.0D);
			}
			
			if (this.tickCount > 200 && this.tickCount % 30 == 0) {
				this.hurt(DamageSource.WITHER, 1.0F);
			}
			
			if (this.summoner != null && !this.summoner.isAlive()) {
				this.setHealth(0.0F);
			}
		}
	}
}