package yesman.epicfight.animation.types;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.fml.RegistryObject;
import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.animation.types.EntityState.Translation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.entity.MobData;
import yesman.epicfight.capabilities.entity.mob.BipedMobData;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.collada.AnimationDataExtractor;
import yesman.epicfight.entity.eventlistener.AttackEndEvent;
import yesman.epicfight.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.game.AttackResult;
import yesman.epicfight.utils.game.AttackResult.Priority;
import yesman.epicfight.utils.game.IExtendedDamageSource;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.ExtraDamageCalculator;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec3f;

public class AttackAnimation extends ActionAnimation {
	public final Phase[] phases;
	
	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, true, affectY, path, model, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean breakMovement, boolean affectY, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, breakMovement, affectY, path, model, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public AttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, Hand hand, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, true, affectY, path, model, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public AttackAnimation(float convertTime, boolean affectY, String path, Model model, Phase... phases) {
		this(convertTime, true, affectY, path, model, phases);
	}
	
	public AttackAnimation(float convertTime, boolean breakMovement, boolean affectY, String path, Model model, Phase... phases) {
		super(convertTime, breakMovement, affectY, path, model);
		this.phases = phases;
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		if (!entitydata.isRemote()) {
			AnimationPlayer player = entitydata.getAnimator().getPlayerFor(this);
			float elapsedTime = player.getElapsedTime();
			float prevElapsedTime = player.getPrevElapsedTime();
			EntityState state = this.getState(elapsedTime);
			EntityState prevState = this.getState(prevElapsedTime);
			Phase phase = this.getPhaseByTime(elapsedTime);
			
			if (state == EntityState.FREE_CAMERA) {
				if (entitydata instanceof MobData) {
					((MobEntity)entitydata.getOriginalEntity()).getNavigator().clearPath();
					LivingEntity target = entitydata.getAttackTarget();
					if (target != null) {
						entitydata.rotateTo(target, 30.0F, false);
					}
				}
			} else if (prevState.shouldDetectCollision() || state.shouldDetectCollision() || (prevState.getLevel() < 2 && state.getLevel() > 2)) {
				if (!prevState.shouldDetectCollision()) {
					entitydata.playSound(this.getSwingSound(entitydata, phase), 0.0F, 0.0F);
					entitydata.currentlyAttackedEntity.clear();
				}
				
				Collider collider = this.getCollider(entitydata, elapsedTime);
				LivingEntity entity = entitydata.getOriginalEntity();
				entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature().initializeTransform();
				
				float prevPoseTime = prevState.shouldDetectCollision() ? prevElapsedTime : phase.preDelay;
				float poseTime = state.shouldDetectCollision() ? elapsedTime : phase.contact;
				List<Entity> list = collider.updateAndFilterCollideEntity(entitydata, this, prevPoseTime, poseTime, phase.jointIndexer, this.getPlaySpeed(entitydata));
				
				if (list.size() > 0) {
					AttackResult attackResult = new AttackResult(entitydata, list, phase.getProperty(AttackPhaseProperty.TARGET_PRIORITY).orElse(Priority.DISTANCE));
					boolean flag1 = true;
					int maxStrikes = this.getMaxStrikes(entitydata, phase);
					while (entitydata.currentlyAttackedEntity.size() < maxStrikes && attackResult.next()) {
						Entity e = attackResult.getEntity();
						LivingEntity trueEntity = this.getTrueEntity(e);
						if (!entitydata.currentlyAttackedEntity.contains(trueEntity) && !entitydata.canAttack(e)) {
							if (e instanceof LivingEntity || e instanceof PartEntity) {
								if (entity.world.rayTraceBlocks(new RayTraceContext(new Vector3d(e.getPosX(), e.getPosY() + (double)e.getEyeHeight(), e.getPosZ()),
										new Vector3d(entity.getPosX(), entity.getPosY() + entity.getHeight() * 0.5F, entity.getPosZ()), 
										RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, entity)).getType() == RayTraceResult.Type.MISS) {
									IExtendedDamageSource source = this.getDamageSourceExt(entitydata, e, phase);
									float damage = this.getDamageTo(entitydata, trueEntity, phase, source);
									if (entitydata.hurtEntity(e, phase.hand, source, damage)) {
										if (entitydata instanceof ServerPlayerData) {
											ServerPlayerData playerdata = ((ServerPlayerData)entitydata);
											playerdata.getEventListener().activateEvents(EventType.DEALT_DAMAGE_POST_EVENT, new DealtDamageEvent<>(playerdata, trueEntity, source, damage));
										}
										e.hurtResistantTime = 0;
										e.world.playSound(null, e.getPosX(), e.getPosY(), e.getPosZ(), this.getHitSound(entitydata, phase), e.getSoundCategory(), 1.0F, 1.0F);
										this.spawnHitParticle(((ServerWorld)e.world), entitydata, e, phase);
										if (flag1 && entitydata instanceof PlayerData) {
											entity.getHeldItem(phase.hand).hitEntity(trueEntity, (PlayerEntity)entity);
											flag1 = false;
										}
									}
									entitydata.currentlyAttackedEntity.add(trueEntity);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		super.onFinish(entitydata, isEnd);
		
		if (entitydata instanceof ServerPlayerData && isEnd) {
			ServerPlayerData playerdata = ((ServerPlayerData)entitydata);
			playerdata.getEventListener().activateEvents(EventType.ATTACK_ANIMATION_END_EVENT, new AttackEndEvent(playerdata, entitydata.currentlyAttackedEntity, this.getId()));
		}
		
		entitydata.currentlyAttackedEntity.clear();
		
		if (entitydata instanceof BipedMobData && entitydata.isRemote()) {
			MobEntity entity = (MobEntity) entitydata.getOriginalEntity();
			if (entity.getAttackTarget() != null && !entity.getAttackTarget().isAlive()) {
				entity.setAttackTarget((LivingEntity) null);
			}
		}
	}
	
	@Override
	public EntityState getState(float time) {
		Phase phase = this.getPhaseByTime(time);
		EntityState state;
		
		if (phase.antic >= time)
			state = EntityState.FREE_CAMERA;
		else if (phase.antic < time && phase.preDelay > time)
			state = EntityState.PRE_DELAY;
		else if (phase.preDelay <= time && phase.contact >= time)
			state = EntityState.CONTACT;
		else if (phase.recovery > time)
			state = EntityState.POST_DELAY;
		else
			state = EntityState.CANCELABLE_POST_DELAY;
		
		if (!this.getProperty(AttackAnimationProperty.LOCK_ROTATION).orElse(false)) {
			state = EntityState.translation(state, Translation.TO_ROTATABLE);
		}
		
		return state;
	}
	
	@Override
	protected Vec3f getCoordVector(LivingData<?> entitydata, DynamicAnimation dynamicAnimation) {
		Vec3f vec3 = super.getCoordVector(entitydata, dynamicAnimation);
		if (!this.getProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE).isPresent()) {
			EntityState state = this.getState(entitydata.getAnimator().getPlayerFor(this).getElapsedTime());
			if (state.getLevel() < 3) {
				LivingEntity orgEntity = entitydata.getOriginalEntity();
				LivingEntity target = entitydata.getAttackTarget();
				float multiplier = (orgEntity instanceof PlayerEntity) ? 2.0F : 1.0F;
				if (target != null) {
					float distance = Math.max(Math.min(orgEntity.getDistance(target) - (orgEntity.getWidth() + target.getWidth()) * 0.8F, multiplier), 0.0F);
					vec3.x *= distance;
					vec3.z *= distance;
				}
			}
		}
		return vec3;
	}
	
	public Collider getCollider(LivingData<?> entitydata, float elapsedTime) {
		Phase phase = this.getPhaseByTime(elapsedTime);
		return phase.collider != null ? phase.collider : entitydata.getColliderMatching(phase.hand);
	}
	
	public LivingEntity getTrueEntity(Entity entity) {
		if (entity instanceof LivingEntity) {
			return (LivingEntity)entity;
		} else if (entity instanceof PartEntity) {
			Entity parentEntity = ((PartEntity<?>)entity).getParent();
			if (parentEntity instanceof LivingEntity) {
				return (LivingEntity)parentEntity;
			}
		}
		return null;
	}
	
	protected int getMaxStrikes(LivingData<?> entitydata, Phase phase) {
		return phase.getProperty(AttackPhaseProperty.MAX_STRIKES).map((valueCorrector) -> valueCorrector.get(entitydata.getHitEnemies(phase.hand)))
				.orElse(Float.valueOf(entitydata.getHitEnemies(phase.hand))).intValue();
	}
	
	protected float getDamageTo(LivingData<?> entitydata, LivingEntity target, Phase phase, IExtendedDamageSource source) {
		float totalDamage = phase.getProperty(AttackPhaseProperty.DAMAGE).map((valueCorrector) -> 
			valueCorrector.get(entitydata.getDamageToEntity(target, source, phase.hand))).orElse(entitydata.getDamageToEntity(target, source, phase.hand));
		ExtraDamageCalculator extraCalculator = phase.getProperty(AttackPhaseProperty.EXTRA_DAMAGE).orElse(null);
		
		if (extraCalculator != null) {
			totalDamage += extraCalculator.get(entitydata.getOriginalEntity(), target);
		}
		
		return totalDamage;
	}
	
	protected SoundEvent getSwingSound(LivingData<?> entitydata, Phase phase) {
		return phase.getProperty(AttackPhaseProperty.SWING_SOUND).orElse(entitydata.getSwingSound(phase.hand));
	}
	
	protected SoundEvent getHitSound(LivingData<?> entitydata, Phase phase) {
		return phase.getProperty(AttackPhaseProperty.HIT_SOUND).orElse(entitydata.getWeaponHitSound(phase.hand));
	}
	
	protected IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target, Phase phase) {
		StunType stunType = phase.getProperty(AttackPhaseProperty.STUN_TYPE).orElse(StunType.SHORT);
		IExtendedDamageSource extDmgSource = entitydata.getDamageSource(stunType, this, phase.hand);
		
		phase.getProperty(AttackPhaseProperty.ARMOR_NEGATION).ifPresent((opt) -> {
			extDmgSource.setArmorNegation(opt.get(extDmgSource.getArmorNegation()));
		});
		phase.getProperty(AttackPhaseProperty.IMPACT).ifPresent((opt) -> {
			extDmgSource.setImpact(opt.get(extDmgSource.getImpact()));
		});
		
		return extDmgSource;
	}
	
	protected void spawnHitParticle(ServerWorld world, LivingData<?> attacker, Entity hit, Phase phase) {
		Optional<RegistryObject<HitParticleType>> particleOptional = phase.getProperty(AttackPhaseProperty.PARTICLE);
		HitParticleType particle = particleOptional.isPresent() ? particleOptional.get().get() : attacker.getWeaponHitParticle(phase.hand);
		particle.spawnParticleWithArgument(world, null, null, hit, attacker.getOriginalEntity());
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		Pose pose = super.getPoseByTime(entitydata, time);
		this.getProperty(AttackAnimationProperty.DIRECTIONAL).ifPresent((flag) -> {
			float pitch = entitydata.getAttackDirectionPitch();
			JointTransform chest = pose.getTransformByName("Chest");
			chest.push(JointTransform.DYNAMIC_TRANSFORM, OpenMatrix4f::mulOnOrigin, JointTransform.of(new Quaternion(new Vector3f(1, 0, 0), -pitch, true)));
			if (entitydata instanceof PlayerData) {
				JointTransform head = pose.getTransformByName("Head");
				Quaternion q = new Quaternion(new Vector3f(1, 0, 0), pitch, true);
				q.multiply(head.getRotation());
				MathUtils.setQuaternion(head.getRotation(), q.getX(), q.getY(), q.getZ(), q.getW());
			}
		});
		return pose;
	}
	
	@Override
	public float getPlaySpeed(LivingData<?> entitydata) {
		if (entitydata instanceof PlayerData<?>) {
			float speedFactor = this.getProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR).orElse(1.0F);
			Optional<Float> property = this.getProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED);
			float correctedSpeed = property.map((value) -> ((PlayerData<?>)entitydata).getAttackSpeed() / value).orElse(this.totalTime * ((PlayerData<?>)entitydata).getAttackSpeed());
			return 1.0F + (correctedSpeed - 1.0F) * speedFactor;
		} else {
			return 1.0F;
		}
	}
	
	public <V> AttackAnimation addProperty(AttackAnimationProperty<V> propertyType, V value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	public <V> AttackAnimation addProperty(AttackPhaseProperty<V> propertyType, V value) {
		return this.addProperty(propertyType, value, 0);
	}
	
	public <V> AttackAnimation addProperty(AttackPhaseProperty<V> propertyType, V value, int index) {
		this.phases[index].addProperty(propertyType, value);
		return this;
	}
	
	public int getIndexer(float elapsedTime) {
		return this.getPhaseByTime(elapsedTime).jointIndexer;
	}
	
	public Phase getPhaseByTime(float elapsedTime) {
		Phase currentPhase = null;
		for(Phase phase : this.phases) {
			currentPhase = phase;
			if(phase.recovery > elapsedTime) {
				break;
			}
		}
		return currentPhase;
	}
	
	@Deprecated
	public void changeCollider(Collider newCollider, int index) {
		this.phases[index].collider = newCollider;
	}
	
	public static class Phase {
		protected final Map<AttackPhaseProperty<?>, Object> properties = new HashMap<AttackPhaseProperty<?>, Object> ();;
		protected final float antic;
		protected final float preDelay;
		protected final float contact;
		protected final float recovery;
		protected final int jointIndexer;
		protected final Hand hand;
		protected Collider collider;
		
		public Phase(float antic, float preDelay, float contact, float recovery, String indexer, Collider collider) {
			this(antic, preDelay, contact, recovery, Hand.MAIN_HAND, indexer, collider);
		}
		
		public Phase(float antic, float preDelay, float contact, float recovery, Hand hand, String indexer, Collider collider) {
			this.antic = antic;
			this.preDelay = preDelay;
			this.contact = contact;
			this.recovery = recovery;
			this.collider = collider;
			this.hand = hand;
			
			int coded = 0;
			if (indexer.length() == 0) {
				this.jointIndexer = -1;
			} else {
				for (int i = 0; i < indexer.length(); i++) {
					int value = indexer.charAt(i) - '0';
					coded = coded | value;
					coded = coded << 5;
				}
				this.jointIndexer = coded;
			}
		}
		
		public <V> Phase addProperty(AttackPhaseProperty<V> propertyType, V value) {
			this.properties.put(propertyType, value);
			return this;
		}
		
		public void addProperties(Set<Map.Entry<AttackPhaseProperty<?>, Object>> set) {
			for(Map.Entry<AttackPhaseProperty<?>, Object> entry : set) {
				this.properties.put(entry.getKey(), entry.getValue());
			}
		}
		
		@SuppressWarnings("unchecked")
		protected <V> Optional<V> getProperty(AttackPhaseProperty<V> propertyType) {
			return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
		}
		
		public int getJointIndexer() {
			return this.jointIndexer;
		}
	}
	
	@Override
	protected void load(IResourceManager resourceManager) {
		AnimationDataExtractor.loadAttackAnimation(resourceManager, this);
	}
}