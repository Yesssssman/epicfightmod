package maninthehouse.epicfight.animation.types.attack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Nullable;

import maninthehouse.epicfight.animation.JointTransform;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.animation.Quaternion;
import maninthehouse.epicfight.animation.types.ActionAnimation;
import maninthehouse.epicfight.animation.types.AnimationProperty;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.utils.game.AttackResult;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.DamageType;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.MultiPartEntityPart;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;

public class AttackAnimation extends ActionAnimation {
	protected final Map<AnimationProperty<?>, Object> properties;
	protected final Phase[] phases;
	
	public AttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, @Nullable Collider collider,
			String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, index, collider));
	}
	
	public AttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY, EnumHand hand, @Nullable Collider collider,
			String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public AttackAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases) {
		super(id, convertTime, true, affectY, path);
		this.properties = new HashMap<AnimationProperty<?>, Object> ();
		this.phases = phases;
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		
		if(!entitydata.isRemote()) {
			float elapsedTime = entitydata.getAnimator().getPlayer().getElapsedTime();
			float prevElapsedTime = entitydata.getAnimator().getPlayer().getPrevElapsedTime();
			LivingData.EntityState state = this.getState(elapsedTime);
			LivingData.EntityState prevState = this.getState(prevElapsedTime);
			Phase phase = this.getCurrentPhase(elapsedTime);
			
			if(state == LivingData.EntityState.FREE_CAMERA) {
				if(entitydata instanceof MobData) {
					((EntityCreature) entitydata.getOriginalEntity()).getNavigator().clearPath();
					EntityLivingBase target = entitydata.getAttackTarget();
					if(target != null) {
						entitydata.rotateTo(target, 60.0F, false);
					}
				}
			} else if (state.shouldDetectCollision() || (prevState.getLevel() < 2 && state.getLevel() > 2)) {
				
				if(!prevState.shouldDetectCollision()) {
					entitydata.playSound(this.getSwingSound(entitydata, phase.hand), 0.0F, 0.0F);
					entitydata.currentlyAttackedEntity.clear();
				}
				
				Collider collider = this.getCollider(entitydata, elapsedTime);
				EntityLivingBase entity = entitydata.getOriginalEntity();
				entitydata.getEntityModel(Models.LOGICAL_SERVER).getArmature().initializeTransform();
				VisibleMatrix4f jointTransform = entitydata.getServerAnimator().getColliderTransformMatrix(phase.jointIndexer);
				collider.transform(VisibleMatrix4f.mul(entitydata.getModelMatrix(1.0F), jointTransform, null));
				List<Entity> list = entity.world.getEntitiesWithinAABBExcludingEntity(entity, collider.getHitboxAABB());
				collider.extractHitEntities(list);
				
				if (list.size() > 0) {
					AttackResult attackResult = new AttackResult(entity, list);
					boolean flag1 = true;
					while (entitydata.currentlyAttackedEntity.size() < getHitEnemies(entitydata)) {
						Entity e = attackResult.getEntity();
						Entity trueEntity = this.getTrueEntity(e);
						if (!entitydata.currentlyAttackedEntity.contains(trueEntity) && !entitydata.isTeam(e)) {
							if (e instanceof EntityLivingBase || e instanceof MultiPartEntityPart) {
								if(e.world.rayTraceBlocks(new Vec3d(e.posX, e.posY + (double)e.getEyeHeight(), e.posZ),
										new Vec3d(entity.posX, entity.posY + (double)entity.getEyeHeight(), entity.posZ),
										false, true, false) == null) {
									IExtendedDamageSource source = this.getDamageSourceExt(entitydata, e);
									if(entitydata.hurtEntity(e, phase.hand, source, this.getDamageAmount(entitydata, e, phase.hand))) {
										entity.setLastAttackedEntity(e);
										e.hurtResistantTime = 0;
										e.world.playSound(null, e.posX, e.posY, e.posZ, this.getHitSound(entitydata, phase.hand), e.getSoundCategory(), 1.0F, 1.0F);
										if(flag1 && entitydata instanceof PlayerData && trueEntity instanceof EntityLivingBase) {
											entitydata.getOriginalEntity().getHeldItem(phase.hand).hitEntity((EntityLivingBase)trueEntity, ((PlayerData<?>)entitydata).getOriginalEntity());
										}
										flag1 = false;
									}
									entitydata.currentlyAttackedEntity.add(trueEntity);
								}
							}
						}
						
						if(!attackResult.next()) {
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		super.onFinish(entitydata, isEnd);
		entitydata.currentlyAttackedEntity.clear();
		
		if(entitydata instanceof BipedMobData && entitydata.isRemote()) {
			EntityCreature entity = (EntityCreature) entitydata.getOriginalEntity();
			if(entity.getAttackTarget() !=null && !entity.getAttackTarget().isEntityAlive()) {
				entity.setAttackTarget((EntityLivingBase)null);
			}
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time) {
		Phase phase = this.getCurrentPhase(time);
		boolean lockCameraRotation = this.getProperty(AnimationProperty.LOCK_ROTATION).orElse(false);
		
		if(phase.antic >= time)
			return LivingData.EntityState.FREE_CAMERA;
		else if(phase.antic < time && phase.preDelay > time)
			return LivingData.EntityState.FREE_CAMERA;
		else if(phase.preDelay <= time && phase.contact >= time)
			return lockCameraRotation ? LivingData.EntityState.CONTACT : LivingData.EntityState.ROTATABLE_CONTACT;
		else if(phase.recovery > time)
			return lockCameraRotation ? LivingData.EntityState.POST_DELAY : LivingData.EntityState.ROTATABLE_POST_DELAY;
		else
			return LivingData.EntityState.FREE_INPUT;
	}
	
	public Collider getCollider(LivingData<?> entitydata, float elapsedTime) {
		Phase phase = this.getCurrentPhase(elapsedTime);
		return phase.collider != null ? phase.collider : entitydata.getColliderMatching(phase.hand);
	}
	
	public Entity getTrueEntity(Entity entity) {
		if (entity instanceof MultiPartEntityPart) {
			IEntityMultiPart parent = ((MultiPartEntityPart)entity).parent;
			if (parent instanceof Entity) {
				return (Entity)parent;
			} else {
				return null;
			}
		}
		
		return entity;
	}
	
	protected int getHitEnemies(LivingData<?> entitydata) {
		return this.getProperty(AnimationProperty.HIT_AT_ONCE).orElse(entitydata.getHitEnemies());
	}
	
	protected float getDamageAmount(LivingData<?> entitydata, Entity target, EnumHand hand) {
		float multiplier = this.getProperty(AnimationProperty.DAMAGE_MULTIPLIER).orElse(1.0F);
		float adder = this.getProperty(AnimationProperty.DAMAGE_ADDER).orElse(0.0F);
		return entitydata.getDamageToEntity(target, hand) * multiplier + adder;
	}
	
	protected SoundEvent getSwingSound(LivingData<?> entitydata, EnumHand hand) {
		return this.getProperty(AnimationProperty.SWING_SOUND).orElse(entitydata.getSwingSound(hand));
	}
	
	protected SoundEvent getHitSound(LivingData<?> entitydata, EnumHand hand) {
		return this.getProperty(AnimationProperty.HIT_SOUND).orElse(entitydata.getWeaponHitSound(hand));
	}
	
	protected IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target) {
		DamageType dmgType = this.getProperty(AnimationProperty.DAMAGE_TYPE).orElse(DamageType.PHYSICAL);
		StunType stunType = this.getProperty(AnimationProperty.STUN_TYPE).orElse(StunType.SHORT);
		IExtendedDamageSource extDmgSource = entitydata.getDamageSource(stunType, dmgType, this.getId());
		
		this.getProperty(AnimationProperty.ARMOR_NEGATION).ifPresent((opt) -> {
			extDmgSource.setArmorIgnore(opt);
		});
		this.getProperty(AnimationProperty.IMPACT).ifPresent((opt) -> {
			extDmgSource.setImpact(opt);
		});
		
		return extDmgSource;
	}
	
	public <T> AttackAnimation addProperty(AnimationProperty<T> propertyType, T value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	public void addProperties(Set<Map.Entry<AnimationProperty<?>, Object>> set) {
		for(Map.Entry<AnimationProperty<?>, Object> entry : set) {
			this.properties.put(entry.getKey(), entry.getValue());
		}
	}
	
	protected <T> Optional<T> getProperty(AnimationProperty<T> propertyType) {
		return (Optional<T>) Optional.ofNullable(this.properties.get(propertyType));
	}
	
	public int getIndexer(float elapsedTime) {
		return this.getCurrentPhase(elapsedTime).jointIndexer;
	}
	
	public Phase getCurrentPhase(float elapsedTime) {
		Phase currentPhase = null;
		for(Phase phase : this.phases) {
			currentPhase = phase;
			if(phase.recovery > elapsedTime) {
				break;
			}
		}
		return currentPhase;
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		Pose pose = super.getPoseByTime(entitydata, time);
		
		this.getProperty(AnimationProperty.DIRECTIONAL).ifPresent((b)->{
			float pitch = entitydata.getAttackDirectionPitch();
			JointTransform chest = pose.getTransformByName("Chest");
			chest.setCustomRotation(Quaternion.rotate((float)Math.toRadians(pitch), new Vec3f(1,0,0), null));
			
			if (entitydata instanceof PlayerData) {
				JointTransform head = pose.getTransformByName("Head");
				head.setRotation(Quaternion.rotate((float)-Math.toRadians(pitch), new Vec3f(1,0,0), head.getRotation()));
			}
		});
		
		return pose;
	}
	
	@Deprecated
	public void changeCollider(Collider newCollider, int index) {
		this.phases[index].collider = newCollider;
	}
	
	public static class Phase {
		protected final float antic;
		protected final float preDelay;
		protected final float contact;
		protected final float recovery;
		protected final int jointIndexer;
		protected final EnumHand hand;
		protected Collider collider;
		
		public Phase(float antic, float preDelay, float contact, float recovery, String indexer, Collider collider) {
			this(antic, preDelay, contact, recovery, EnumHand.MAIN_HAND, indexer, collider);
		}
		
		public Phase(float antic, float preDelay, float contact, float recovery, EnumHand hand, String indexer, Collider collider) {
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
	}
}