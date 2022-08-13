package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.effect.EpicFightMobEffects;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AnimatedAttackGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.TargetChasingGoal;

public class EndermanPatch extends MobPatch<EnderMan> {
	private static final UUID SPEED_MODIFIER_RAGE_UUID = UUID.fromString("dc362d1a-8424-11ec-a8a3-0242ac120002");
	private static final AttributeModifier SPEED_MODIFIER_RAGE = new AttributeModifier(SPEED_MODIFIER_RAGE_UUID, "Rage speed bonus", 0.1D, AttributeModifier.Operation.ADDITION);
	
	private int deathTimerExt = 0;
	private boolean onRage;
	private Goal normalAttacks;
	private Goal teleportAttacks;
	private Goal rageAttacks;
	private Goal rageTargeting;
	
	public EndermanPatch() {
		super(Faction.ENDERMAN);
	}
	
	@Override
	public void onJoinWorld(EnderMan enderman, EntityJoinWorldEvent event) {
		if (enderman.level.dimension() == Level.END) {
			if (enderman.position().horizontalDistanceSqr() < 40000) {
				event.setCanceled(true);
			}
		}
		
		super.onJoinWorld(enderman, event);
	}
	
	@Override
	public void onStartTracking(ServerPlayer trackingPlayer) {
		if (this.isRaging()) {
			SPSpawnData packet = new SPSpawnData(this.original.getId());
			EpicFightNetworkManager.sendToPlayer(packet, trackingPlayer);
		}
	}
	
	@Override
	public void processSpawnData(ByteBuf buf) {
		ClientAnimator animator = this.getClientAnimator();
		animator.addLivingAnimation(LivingMotions.IDLE, Animations.ENDERMAN_RAGE_IDLE);
		animator.addLivingAnimation(LivingMotions.WALK, Animations.ENDERMAN_RAGE_WALK);
		animator.setCurrentMotionsAsDefault();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(8.0F);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.8F);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.normalAttacks = new AnimatedAttackGoal<>(this, MobCombatBehaviors.ENDERMAN.build(this));
		this.teleportAttacks = new EndermanTeleportMove(this, MobCombatBehaviors.ENDERMAN_TELEPORT.build(this));
		this.rageAttacks = new AnimatedAttackGoal<>(this, MobCombatBehaviors.ENDERMAN_RAGE.build(this));
		this.rageTargeting = new NearestAttackableTargetGoal<>(this.original, Player.class, true);
		this.original.goalSelector.addGoal(1, new TargetChasingGoal(this, this.getOriginal(), 0.75D, false));
		
		if (this.isRaging()) {
			this.original.targetSelector.addGoal(3, this.rageTargeting);
			this.original.goalSelector.addGoal(1, this.rageAttacks);
		} else {
			this.original.goalSelector.addGoal(1, this.normalAttacks);
			this.original.goalSelector.addGoal(0, this.teleportAttacks);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.ENDERMAN_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.ENDERMAN_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.ENDERMAN_IDLE);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonMobUpdateMotion(considerInaction);
	}
	
	@Override
	public void serverTick(LivingUpdateEvent event) {
		super.serverTick(event);
		
		if (this.isRaging() && !this.onRage && this.original.tickCount > 5) {
			this.toRaging();
		} else if (this.onRage && !this.isRaging()) {
			this.toNormal();
		}
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		if (this.original.getHealth() <= 0.0F) {
			this.original.setXRot(0);
			
			if (this.original.deathTime > 1 && this.deathTimerExt < 20) {
				this.deathTimerExt++;
				this.original.deathTime--;
			}
		}
		
		super.tick(event);
	}
	
	@Override
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		if (!this.original.level.isClientSide()) {
			if (damageSource instanceof EntityDamageSource && !this.isRaging()) {
				ExtendedDamageSource extDamageSource = null;
				
				if (damageSource instanceof ExtendedDamageSource) {
					extDamageSource = ((ExtendedDamageSource)damageSource);
				}
				
				if (extDamageSource == null || extDamageSource.getStunType() != StunType.HOLD) {
					int percentage = this.getServerAnimator().getPlayerFor(null).getAnimation() instanceof AttackAnimation ? 10 : 3;
					if (this.original.getRandom().nextInt(percentage) == 0) {
						for (int i = 0; i < 9; i++) {
							if (this.original.teleport()) {
								if (damageSource.getEntity() instanceof LivingEntity) {
									this.original.setLastHurtByMob((LivingEntity) damageSource.getEntity());
								}
								
								if (this.state.inaction()) {
									this.playAnimationSynchronized(Animations.ENDERMAN_TP_EMERGENCE, 0.0F);
								}
								
								return new AttackResult(AttackResult.ResultType.FAILED, amount);
							}
						}
					}
				}
			}
		}
		
		return super.tryHurt(damageSource, amount);
	}
	
	public boolean isRaging() {
		return this.original.getHealth() / this.original.getMaxHealth() < 0.33F;
	}
	
	protected void toRaging() {
		this.onRage = true;
		this.playAnimationSynchronized(Animations.ENDERMAN_CONVERT_RAGE, 0);
		
		if (!this.original.isNoAi()) {
			this.original.goalSelector.removeGoal(this.normalAttacks);
			this.original.goalSelector.removeGoal(this.teleportAttacks);
			this.original.goalSelector.addGoal(1, this.rageAttacks);
			this.original.targetSelector.addGoal(3, this.rageTargeting);
			this.original.getEntityData().set(EnderMan.DATA_CREEPY, Boolean.valueOf(true));
			this.original.addEffect(new MobEffectInstance(EpicFightMobEffects.STUN_IMMUNITY.get(), 120000));
			this.original.getAttribute(Attributes.MOVEMENT_SPEED).addTransientModifier(SPEED_MODIFIER_RAGE);
			
			SPChangeLivingMotion msg = new SPChangeLivingMotion(this.original.getId(), true)
					.putPair(LivingMotions.IDLE, Animations.ENDERMAN_RAGE_IDLE)
					.putPair(LivingMotions.WALK, Animations.ENDERMAN_RAGE_WALK);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.original);
		}
	}
	
	protected void toNormal() {
		this.onRage = false;
		
		if (!this.original.isNoAi()) {
			this.original.goalSelector.addGoal(1, this.normalAttacks);
			this.original.goalSelector.addGoal(0, this.teleportAttacks);
			this.original.goalSelector.removeGoal(this.rageAttacks);
			this.original.targetSelector.removeGoal(this.rageTargeting);
			
			if (this.original.getTarget() == null) {
				this.original.getEntityData().set(EnderMan.DATA_CREEPY, Boolean.valueOf(false));
			}
			
			this.original.removeEffect(EpicFightMobEffects.STUN_IMMUNITY.get());
			this.original.getAttribute(Attributes.MOVEMENT_SPEED).removeModifier(SPEED_MODIFIER_RAGE);
			
			SPChangeLivingMotion msg = new SPChangeLivingMotion(this.original.getId(), true)
					.putPair(LivingMotions.IDLE, Animations.ENDERMAN_IDLE)
					.putPair(LivingMotions.WALK, Animations.ENDERMAN_WALK);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(msg, this.original);
		}
	}
	
	@Override
	public void aboutToDeath() {
		this.original.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		
		if (this.isLogicalClient()) {
			for (int i = 0; i < 100; i++) {
				Random rand = original.getRandom();
				Vec3f vec = new Vec3f(rand.nextInt(), rand.nextInt(), rand.nextInt());
				vec.normalise().scale(0.5F);
				Minecraft minecraft = Minecraft.getInstance();
				minecraft.particleEngine.createParticle(EpicFightParticles.ENDERMAN_DEATH_EMIT.get(), this.original.getX(), this.original.getY() + this.original.getDimensions(Pose.STANDING).height / 2, this.original.getZ(), vec.x, vec.y, vec.z);
			}
		}
		
		super.aboutToDeath();
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (stunType == StunType.LONG) {
			return Animations.ENDERMAN_HIT_LONG;
		} else {
			return Animations.ENDERMAN_HIT_SHORT;
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.enderman;
	}
	
	static class EndermanTeleportMove extends CombatBehaviorGoal<EndermanPatch> {
		private int waitingCounter;
		private int delayCounter;
		private CombatBehaviors.Behavior<EndermanPatch> move;
		
		public EndermanTeleportMove(EndermanPatch mobpatch, CombatBehaviors<EndermanPatch> mobAttacks) {
			super(mobpatch, mobAttacks);
			this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}
		
		@Override
		public boolean canUse() {
			this.combatBehaviors.tick();
			
			if (super.canUse()) {
				this.move = this.combatBehaviors.selectRandomBehaviorSeries();
				return this.move != null;
			} else {
				return false;
			}
		}
		
		@Override
		public boolean canContinueToUse() {
			boolean waitExpired = this.waitingCounter <= 100;
			
			if (!waitExpired) {
				this.waitingCounter = 500;
			}
			
	    	return this.isValidTarget(this.mob.getTarget()) && !this.mobpatch.getEntityState().hurt() && !this.mobpatch.getEntityState().inaction() && waitExpired;
	    }
		
		@Override
		public void start() {
			this.delayCounter = 20 + this.mob.getRandom().nextInt(5);
			this.waitingCounter = 0;
		}
		
		@Override
		public void stop() {
			this.move = null;
		}
		
		@Override
		public void tick() {
			LivingEntity target = this.mob.getTarget();
	        this.mob.getLookControl().setLookAt(target, 30.0F, 30.0F);
	        
			if (this.delayCounter-- < 0 && !this.mobpatch.getEntityState().inaction()) {
				Vec3f vec = new Vec3f((float)(this.mob.getX() - target.getX()), 0, (float)(this.mob.getZ() - target.getZ()));
	        	vec.normalise().scale(1.414F);
	        	boolean flag = this.mob.randomTeleport(target.getX() + vec.x, target.getY(), target.getZ() + vec.z, true);
	        	
				if (flag) {
					this.mobpatch.rotateTo(target, 360.0F, true);
					this.move.execute(this.mobpatch);
		        	this.mob.level.playSound((Player)null, this.mob.xo, this.mob.yo, this.mob.zo, SoundEvents.ENDERMAN_TELEPORT, this.mob.getSoundSource(), 1.0F, 1.0F);
		        	this.mob.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
		        	this.waitingCounter = 0;
				} else {
	            	this.waitingCounter++;
				}
	        }
	    }
	}
}