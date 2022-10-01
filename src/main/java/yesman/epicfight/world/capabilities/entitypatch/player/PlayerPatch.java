package yesman.epicfight.world.capabilities.entitypatch.player;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.Formulars;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.AttackSpeedModifyEvent;
import yesman.epicfight.world.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public abstract class PlayerPatch<T extends Player> extends LivingEntityPatch<T> {
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("e6beeac4-77d2-11eb-9439-0242ac130002");
	public static final EntityDataAccessor<Float> STAMINA = new EntityDataAccessor<Float> (253, EntityDataSerializers.FLOAT);
	
	protected float yaw;
	protected PlayerEventListener eventListeners;
	protected int tickSinceLastAction;
	protected PlayerMode playerMode = PlayerMode.MINING;
	
	public PlayerPatch() {
		this.eventListeners = new PlayerEventListener(this);
	}
	
	@Override
	public void onConstructed(T entityIn) {
		super.onConstructed(entityIn);
		entityIn.getEntityData().define(STAMINA, Float.valueOf(0.0F));
	}
	
	@Override
	public void onJoinWorld(T entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		
		CapabilitySkill skillCapability = this.getSkillCapability();
		skillCapability.skillContainers[SkillCategories.BASIC_ATTACK.universalOrdinal()].setSkill(Skills.BASIC_ATTACK);
		skillCapability.skillContainers[SkillCategories.AIR_ATTACK.universalOrdinal()].setSkill(Skills.AIR_ATTACK);
		skillCapability.skillContainers[SkillCategories.KNOCKDOWN_WAKEUP.universalOrdinal()].setSkill(Skills.KNOCKDOWN_WAKEUP);
		this.tickSinceLastAction = 0;
		this.eventListeners.addEventListener(EventType.ACTION_EVENT_SERVER, ACTION_EVENT_UUID, (playerEvent) -> {
			this.resetActionTick();
		});
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.MAX_STAMINA.get()).setBaseValue(15.0D);
		this.original.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get()).setBaseValue(0.5D);
	}
	
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.BIPED_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.RUN, Animations.BIPED_RUN);
		clientAnimator.addLivingAnimation(LivingMotions.SNEAK, Animations.BIPED_SNEAK);
		clientAnimator.addLivingAnimation(LivingMotions.SWIM, Animations.BIPED_SWIM);
		clientAnimator.addLivingAnimation(LivingMotions.FLOAT, Animations.BIPED_FLOAT);
		clientAnimator.addLivingAnimation(LivingMotions.KNEEL, Animations.BIPED_KNEEL);
		clientAnimator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotions.FLY, Animations.BIPED_FLYING);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.BIPED_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.JUMP, Animations.BIPED_JUMP);
		clientAnimator.addLivingAnimation(LivingMotions.CLIMB, Animations.BIPED_CLIMBING);
		clientAnimator.addLivingAnimation(LivingMotions.SLEEP, Animations.BIPED_SLEEPING);
		clientAnimator.addLivingAnimation(LivingMotions.DIGGING, Animations.BIPED_DIG);
		clientAnimator.addLivingAnimation(LivingMotions.AIM, Animations.BIPED_BOW_AIM);
		clientAnimator.addLivingAnimation(LivingMotions.SHOT, Animations.BIPED_BOW_SHOT);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	public void copySkillsFrom(PlayerPatch<?> old) {
		CapabilitySkill oldSkill = old.getSkillCapability();
		CapabilitySkill newSkill = this.getSkillCapability();
		int i = 0;
		
		for (SkillContainer container : newSkill.skillContainers) {
			container.setExecuter(this);
			Skill oldone = oldSkill.skillContainers[i].getSkill();
			
			if (oldone != null && oldone.getCategory().shouldSynchronized()) {
				container.setSkill(oldSkill.skillContainers[i].getSkill());
			}
			i++;
		}
		
		for (SkillCategory skillCategory : SkillCategory.ENUM_MANAGER.universalValues()) {
			if (oldSkill.hasCategory(skillCategory)) {
				for (Skill learnedSkill : oldSkill.getLearnedSkills(skillCategory)) {
					newSkill.addLearnedSkill(learnedSkill);
				}
			}
		}
	}
	
	public void changeYaw(float amount) {
		this.yaw = amount;
	}
	
	@Override
	public void serverTick(LivingUpdateEvent event) {
		super.serverTick(event);
		
		if (!this.state.inaction()) {
			this.tickSinceLastAction++;
		}
		
		float stamina = this.getStamina();
		float maxStamina = this.getMaxStamina();
		
		if (stamina < maxStamina && this.tickSinceLastAction > 30) {
			float staminaFactor = 1.0F + (float)Math.pow((stamina / (maxStamina - stamina * 0.5F)), 2);
			this.setStamina(stamina + maxStamina * 0.01F * staminaFactor);
		}
		
		if (maxStamina < stamina) {
			this.setStamina(maxStamina);
		}
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		if (this.original.getVehicle() == null) {
			for (SkillContainer container : this.getSkillCapability().skillContainers) {
				if (container != null) {
					container.update();
				}
			}
		}
		
		super.tick(event);
	}
	
	public SkillContainer getSkill(SkillCategory category) {
		return this.getSkill(category.universalOrdinal());
	}
	
	public SkillContainer getSkill(int categoryIndex) {
		return this.getSkillCapability().skillContainers[categoryIndex];
	}
	
	public CapabilitySkill getSkillCapability() {
		return this.original.getCapability(EpicFightCapabilities.CAPABILITY_SKILL).orElse(CapabilitySkill.EMPTY);
	}
	
	@Override
	public float getDamageTo(@Nullable Entity targetEntity, @Nullable ExtendedDamageSource source, InteractionHand hand) {
		return this.getModifiedDamage(targetEntity, source, super.getDamageTo(targetEntity, source, hand));
	}
	
	public float getModifiedDamage(@Nullable Entity targetEntity, @Nullable ExtendedDamageSource source, float baseDamage) {
		
		DealtDamageEvent<PlayerPatch<?>> event = new DealtDamageEvent<>(this, this.original, source, baseDamage);
		this.getEventListener().triggerEvents(EventType.DEALT_DAMAGE_EVENT_PRE, event);
		return event.getAttackDamage();
	}
	
	public float getAttackSpeed(InteractionHand hand) {
		float baseSpeed;
		
		if (hand == InteractionHand.MAIN_HAND) {
			baseSpeed = (float)this.original.getAttributeValue(Attributes.ATTACK_SPEED);
		} else {
			baseSpeed = (float) (this.isOffhandItemValid() ? this.original.getAttributeValue(Attributes.ATTACK_SPEED) : this.original.getAttributeBaseValue(Attributes.ATTACK_SPEED));
		}
		
		return this.getModifiedAttackSpeed(this.getAdvancedHoldingItemCapability(hand), baseSpeed);
	}
	
	public float getModifiedAttackSpeed(CapabilityItem itemCapability, float baseSpeed) {
		AttackSpeedModifyEvent event = new AttackSpeedModifyEvent(this, itemCapability, baseSpeed);
		this.eventListeners.triggerEvents(EventType.ATTACK_SPEED_MODIFY_EVENT, event);
		
		return Formulars.getAttackSpeedPenalty(this.getWeight(), event.getAttackSpeed(), this);
	}
	
	public PlayerEventListener getEventListener() {
		return this.eventListeners;
	}
	
	@Override
	public ExtendedDamageSource getDamageSource(StunType stunType, StaticAnimation animation, InteractionHand hand) {
		return ExtendedDamageSource.causePlayerDamage(this.original, stunType, animation, hand);
	}
	
	public float getMaxStamina() {
		AttributeInstance stun_resistance = this.original.getAttribute(EpicFightAttributes.MAX_STAMINA.get());
		return (float)(stun_resistance == null ? 0 : stun_resistance.getValue());
	}
	
	public float getStamina() {
		return this.getMaxStamina() == 0 ? 0 : this.original.getEntityData().get(STAMINA).floatValue();
	}
	
	public void setStamina(float value) {
		float f1 = Math.max(Math.min(value, this.getMaxStamina()), 0);
		this.original.getEntityData().set(STAMINA, f1);
	}
	
	public void resetActionTick() {
		this.tickSinceLastAction = 0;
	}
	
	public int getTickSinceLastAction() {
		return this.tickSinceLastAction;
	}
	
	public boolean isUnstable() {
		return this.original.isFallFlying() || this.currentLivingMotion == LivingMotions.FALL;
	}
	
	public void openSkillBook(ItemStack itemstack, InteractionHand hand) {
		;
	}
	
	public void toggleMode() {
		switch (this.playerMode) {
		case MINING:
			this.toBattleMode(true);
			break;
		case BATTLE:
			this.toMiningMode(true);
			break;
		}
	}
	
	public void toMode(PlayerMode playerMode, boolean synchronize) {
		switch (playerMode) {
		case MINING:
			this.toMiningMode(synchronize);
			break;
		case BATTLE:
			this.toBattleMode(synchronize);
			break;
		}
	}
	
	public PlayerMode getPlayerMode() {
		return this.playerMode;
	}
	
	public void toMiningMode(boolean synchronize) {
		this.playerMode = PlayerMode.MINING;
	}
	
	public void toBattleMode(boolean synchronize) {
		this.playerMode = PlayerMode.BATTLE;
	}
	
	public boolean isBattleMode() {
		return this.playerMode == PlayerMode.BATTLE;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (this.original.getVehicle() != null) {
			return Animations.BIPED_HIT_ON_MOUNT;
		} else {
			switch(stunType) {
			case LONG:
				return Animations.BIPED_HIT_LONG;
			case SHORT:
				return Animations.BIPED_HIT_SHORT;
			case HOLD:
				return Animations.BIPED_HIT_SHORT;
			case KNOCKDOWN:
				return Animations.BIPED_KNOCKDOWN;
			case FALL:
				return Animations.BIPED_LANDING;
			case NONE:
				return null;
			}
		}
		
		return null;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.biped;
	}
	
	public static enum PlayerMode {
		MINING, BATTLE
	}
}