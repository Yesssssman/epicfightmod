package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.procedural.IKSetter;
import yesman.epicfight.api.animation.types.procedural.TipPointAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.AttackResult;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.DynamicActionEntity;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class EnderDragonPatch extends MobPatch<EnderDragon> implements DynamicActionEntity {
	public static EnderDragonPatch INSTANCE_CLIENT;
	public static EnderDragonPatch INSTANCE_SERVER;
	public static final TargetingConditions NEW_TARGET_TARGETING = TargetingConditions.forCombat().ignoreLineOfSight();
	private TransformSheet dynamicActionTransform = new TransformSheet();
	private Map<String, TipPointAnimation> tipPointAnimations = Maps.newHashMap();
	private Map<LivingMotion, StaticAnimation> livingMotions = Maps.newHashMap();
	private boolean groundPhase;
	public float xRoot;
	public float xRootO;
	public float zRoot;
	public float zRootO;
	public LivingMotion prevMotion = LivingMotion.IDLE;
	
	@Override
	public void onConstructed(EnderDragon entityIn) {
		this.livingMotions.put(LivingMotion.IDLE, Animations.DRAGON_IDLE);
		this.livingMotions.put(LivingMotion.WALK, Animations.DRAGON_WALK);
		this.livingMotions.put(LivingMotion.FLY, Animations.DRAGON_FLY);
		this.livingMotions.put(LivingMotion.CHASE, Animations.DRAGON_AIRSTRIKE);
		this.livingMotions.put(LivingMotion.DEATH, Animations.DRAGON_DEATH);
		super.onConstructed(entityIn);
	}
	
	@Override
	public void onJoinWorld(EnderDragon entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		
		DragonPhaseInstance currentPhase = this.original.phaseManager.getCurrentPhase();
		EnderDragonPhase<?> startPhase = (currentPhase == null || !(currentPhase instanceof PatchedDragonPhase)) ? PatchedPhases.FLYING : this.original.phaseManager.getCurrentPhase().getPhase();
		this.original.phaseManager = new PhaseManagerPatch(this.original, this);
		this.original.phaseManager.setPhase(startPhase);
		
		entityIn.maxUpStep = 1.0F;
		
		if (entityIn.level.isClientSide()) {
			INSTANCE_CLIENT = this;
		} else {
			INSTANCE_SERVER = this;
		}
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(8.0F);
		this.original.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(10.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator animatorClient) {
		for (Map.Entry<LivingMotion, StaticAnimation> livingmotionEntry : this.livingMotions.entrySet()) {
			animatorClient.addLivingAnimation(livingmotionEntry.getKey(), livingmotionEntry.getValue());
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.inaction() && considerInaction) {
			this.currentMotion = LivingMotion.INACTION;
		} else {
			DragonPhaseInstance phase = this.original.getPhaseManager().getCurrentPhase();
			
			if (!this.groundPhase) {
				if (phase.getPhase() == PatchedPhases.AIRSTRIKE && ((DragonAirstrikePhase)phase).isActuallyAttacking()) {
					this.currentMotion = LivingMotion.CHASE;
				} else {
					this.currentMotion = LivingMotion.FLY;
				}
			} else {
				if (phase.getPhase() == PatchedPhases.GROUND_BATTLE) {
					if (this.original.getTarget() != null) {
						this.currentMotion = LivingMotion.WALK;
					} else {
						this.currentMotion = LivingMotion.IDLE;
					}
				} else {
					this.currentMotion = LivingMotion.IDLE;
				}
			}
		}
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		super.tick(event);
		
		if (this.original.getPhaseManager().getCurrentPhase().isSitting()) {
			this.original.nearestCrystal = null;
		}
	}
	
	@Override
	public void serverTick(LivingUpdateEvent event) {
		super.serverTick(event);
		this.original.hurtTime = 2;
		this.original.getSensing().tick();
		this.updateMotion(true);
		
		if (this.prevMotion != this.currentMotion && !this.animator.getEntityState().inaction()) {
			if (this.livingMotions.containsKey(this.currentMotion)) {
				this.animator.playAnimation(this.livingMotions.get(this.currentMotion), 0.0F);
			}
			
			this.prevMotion = this.currentMotion;
		}
		
		this.updateTipPoints();
		
		Entity bodyPart = this.original.getParts()[2];
		List<Entity> list = this.original.level.getEntities(this.original, bodyPart.getBoundingBox().deflate(0.7F, 0.0F, 0.7F), EntitySelector.pushableBy(this.original));
		
		if (!list.isEmpty()) {
			for (int l = 0; l < list.size(); ++l) {
				Entity entity = list.get(l);
				double d0 = entity.getX() - this.original.getX();
				double d1 = entity.getZ() - this.original.getZ();
				double d2 = Mth.absMax(d0, d1);
				
				if (d2 >= (double) 0.01F) {
					d2 = Math.sqrt(d2);
					d0 = d0 / d2;
					d1 = d1 / d2;
					double d3 = 1.0D / d2;
					
					if (d3 > 1.0D) {
						d3 = 1.0D;
					}
					
					d0 = d0 * d3 * 0.2D;
					d1 = d1 * d3 * 0.2D;
					
					if (!entity.isVehicle()) {
						entity.push(d0, 0.0D, d1);
						entity.hurtMarked = true;
					}
				}
			}
		}
	}
	
	@Override
	public void clientTick(LivingUpdateEvent event) {
		this.xRootO = this.xRoot;
		this.zRootO = this.zRoot;
		super.clientTick(event);
		this.updateTipPoints();
	}
	
	@Override
	public void setStunShield(float value) {
		super.setStunShield(value);
		
		if (value <= 0) {
			DragonPhaseInstance currentPhase = this.original.getPhaseManager().getCurrentPhase();
			
			if (currentPhase.getPhase() == PatchedPhases.CRYSTAL_LINK && ((DragonCrystalLinkPhase)currentPhase).getChargingCount() > 0) {
				this.original.playSound(EpicFightSounds.NEUTRALIZE, 5.0F, 1.0F);
				this.original.getPhaseManager().setPhase(PatchedPhases.NEUTRALIZED);
			}
		}
	}
	
	@Override
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		return super.tryHurt(damageSource, this.original.getPhaseManager().getCurrentPhase().getPhase() == PatchedPhases.CRYSTAL_LINK ? 0.0F : amount);
	}
	
	@Override
	public void rotateTo(Entity target, float limit, boolean partialSync) {
		double d0 = target.getX() - this.original.getX();
        double d1 = target.getZ() - this.original.getZ();
        float degree = 180.0F - (float)Math.toDegrees(Mth.atan2(d0, d1));
    	this.rotateTo(degree, limit, partialSync);
	}
	
	public void updateTipPoints() {
		for (Map.Entry<String, TipPointAnimation> entry : this.tipPointAnimations.entrySet()) {
			if (entry.getValue().isOnWorking()) {
				entry.getValue().tick();
			}
		}
		
		if (this.tipPointAnimations.size() > 0) {
			TipPointAnimation frontL = this.getTipPointAnimation("Leg_Front_L3");
			TipPointAnimation frontR = this.getTipPointAnimation("Leg_Front_R3");
			TipPointAnimation backL = this.getTipPointAnimation("Leg_Back_L3");
			TipPointAnimation backR = this.getTipPointAnimation("Leg_Back_R3");
			float entityPosY = (float)this.original.position().y;
			float yFrontL = (frontL != null && frontL.isTouchingGround()) ? frontL.getTargetPosition().y : entityPosY;
			float yFrontR = (frontR != null && frontR.isTouchingGround()) ? frontR.getTargetPosition().y : entityPosY;
			float yBackL = (backL != null && backL.isTouchingGround()) ? backL.getTargetPosition().y : entityPosY;
			float yBackR = (backR != null && backR.isTouchingGround()) ? backR.getTargetPosition().y : entityPosY;
			float xdiff = (yFrontL + yBackL) * 0.5F - (yFrontR + yBackR) * 0.5F;
			float zdiff = (yFrontL + yFrontR) * 0.5F - (yBackL + yBackR) * 0.5F;
			float xdistance = 4.0F;
			float zdistance = 5.7F;
			this.xRoot += Mth.clamp(((float)Math.toDegrees(Math.atan2(zdiff, zdistance)) - this.xRoot), -1.0F, 1.0F);
			this.zRoot += Mth.clamp(((float)Math.toDegrees(Math.atan2(xdiff, xdistance)) - this.zRoot), -1.0F, 1.0F);
			float averageY = (yFrontL + yFrontR + yBackL + yBackR) * 0.25F;
			
			if (!this.isLogicalClient()) {
				float dy = averageY - entityPosY;
				this.original.move(MoverType.SELF, new Vec3(0.0F, dy, 0.0F));
			}
		}
	}
	
	public void resetTipAnimations() {
		this.tipPointAnimations.clear();
	}
	
	public void setFlyingPhase() {
		this.groundPhase = false;
		this.original.horizontalCollision = false;
		this.original.verticalCollision = false;
	}
	
	public void setGroundPhase() {
		this.groundPhase = true;
	}
	
	public boolean isGroundPhase() {
		return this.groundPhase;
	}
	
	@Override
	public SoundEvent getSwingSound(InteractionHand hand) {
		return EpicFightSounds.WHOOSH_BIG;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.dragon;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return null;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, this.original.yRotO, this.original.getYRot(), partialTicks, -1.0F, 1.0F, -1.0F);
	}
	
	@Override
	public TransformSheet getRootDynamicTransform() {
		return this.dynamicActionTransform;
	}
	
	@Override
	public double getAngleTo(Entity entityIn) {
		Vec3 a = this.original.getLookAngle().scale(-1.0D);
		Vec3 b = new Vec3(entityIn.getX() - this.original.getX(), entityIn.getY() - this.original.getY(), entityIn.getZ() - this.original.getZ()).normalize();
		double cosTheta = (a.x * b.x + a.y * b.y + a.z * b.z);
		
		return Math.acos(cosTheta);
	}
	
	@Override
	public double getAngleToHorizontal(Entity entityIn) {
		Vec3 a = this.original.getLookAngle().scale(-1.0D);
		Vec3 b = new Vec3(entityIn.getX() - this.original.getX(), 0.0D, entityIn.getZ() - this.original.getZ()).normalize();
		double cos = (a.x * b.x + a.y * b.y + a.z * b.z);
		
		return Math.toDegrees(Math.acos(cos));
	}
	
	public TipPointAnimation getTipPointAnimation(String jointName) {
		return this.tipPointAnimations.get(jointName);
	}
	
	public void addTipPointAnimation(String jointName, Vec3f initpos, TransformSheet transformSheet, IKSetter ikSetter) {
		this.tipPointAnimations.put(jointName, new TipPointAnimation(transformSheet, initpos, ikSetter));
	}
	
	public Collection<TipPointAnimation> getTipPointAnimations() {
		return this.tipPointAnimations.values();
	}
}