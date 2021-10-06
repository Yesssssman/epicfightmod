package yesman.epicfight.capabilities.entity.player;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.DataKeys;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.skill.CapabilitySkill;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.entity.eventlistener.DealtDamageEvent;
import yesman.epicfight.entity.eventlistener.GetAttackSpeedEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.model.Model;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.utils.game.IExtendedDamageSource;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.Formulars;

public abstract class PlayerData<T extends PlayerEntity> extends LivingData<T> {
	private static final UUID ACTION_EVENT_UUID = UUID.fromString("e6beeac4-77d2-11eb-9439-0242ac130002");
	protected float yaw;
	protected PlayerEventListener eventListeners;
	protected int tickSinceLastAction;
	protected boolean isBattleMode;
	
	public PlayerData() {
		this.eventListeners = new PlayerEventListener(this);
	}
	
	@Override
	public void onEntityConstructed(T entityIn) {
		super.onEntityConstructed(entityIn);
		this.orgEntity.getDataManager().register(DataKeys.STAMINA, Float.valueOf(0.0F));
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		CapabilitySkill skillCapability = this.getSkillCapability();
		skillCapability.skills[SkillCategory.BASIC_ATTACK.getIndex()].setSkill(Skills.BASIC_ATTACK);
		skillCapability.skills[SkillCategory.AIR_ATTACK.getIndex()].setSkill(Skills.AIR_ATTACK);
		this.tickSinceLastAction = 40;
		this.eventListeners.addEventListener(EventType.ACTION_EVENT, ACTION_EVENT_UUID, (event) -> {
			this.resetActionTick();
			return false;
		});
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(EpicFightAttributes.MAX_STAMINA.get()).setBaseValue(15.0D);
		this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get()).setBaseValue(0.5D);
	}
	
	@Override
	public void initAnimator(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_WALK);
		animatorClient.addLivingAnimation(LivingMotion.RUN, Animations.BIPED_RUN);
		animatorClient.addLivingAnimation(LivingMotion.SNEAK, Animations.BIPED_SNEAK);
		animatorClient.addLivingAnimation(LivingMotion.SWIM, Animations.BIPED_SWIM);
		animatorClient.addLivingAnimation(LivingMotion.FLOAT, Animations.BIPED_FLOAT);
		animatorClient.addLivingAnimation(LivingMotion.KNEEL, Animations.BIPED_KNEEL);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.FLY, Animations.BIPED_FLYING);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.JUMP, Animations.BIPED_JUMP);
		animatorClient.addLivingAnimation(LivingMotion.CLIMB, Animations.BIPED_CLIMBING);
		animatorClient.addLivingAnimation(LivingMotion.SLEEP, Animations.BIPED_SLEEPING);
		animatorClient.addOverwritingLivingMotion(LivingMotion.DIGGING, Animations.BIPED_DIG);
		animatorClient.addDefaultLivingMotion(LivingMotion.DIGGING, Animations.BIPED_DIG);
		animatorClient.addDefaultLivingMotion(LivingMotion.AIM, Animations.BIPED_BOW_AIM);
		animatorClient.addDefaultLivingMotion(LivingMotion.SHOT, Animations.BIPED_BOW_SHOT);
	}
	
	public void initFromOldOne(PlayerData<?> old) {
		CapabilitySkill oldSkill = old.getSkillCapability();
		CapabilitySkill newSkill = this.getSkillCapability();
		int i = 0;
		
		for (SkillContainer container : newSkill.skills) {
			container.setExecuter(this);
			if (oldSkill.skills[i].getContaining() != null) {
				container.setSkill(oldSkill.skills[i].getContaining());
			}
			i++;
		}
		this.setStamina(old.getStamina());
	}
	
	public void changeYaw(float amount) {
		this.yaw = amount;
	}
	
	public void setBattleMode(boolean isBattleMode) {
		this.isBattleMode = isBattleMode;
	}
	
	public boolean isBattleMode() {
		return this.isBattleMode;
	}
	
	@Override
	public void updateOnServer() {
		super.updateOnServer();
		
		if (!this.state.isInaction()) {
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
	public void update() {
		if (this.orgEntity.getRidingEntity() == null) {
			for (SkillContainer container : this.getSkillCapability().skills) {
				if (container != null) {
					container.update();
				}
			}
		}
		super.update();
	}
	
	public SkillContainer getSkill(SkillCategory category) {
		return this.getSkill(category.getIndex());
	}
	
	public SkillContainer getSkill(int categoryIndex) {
		return this.getSkillCapability().skills[categoryIndex];
	}
	
	public CapabilitySkill getSkillCapability() {
		return this.orgEntity.getCapability(ModCapabilities.CAPABILITY_SKILL).orElse(CapabilitySkill.EMPTY);
	}
	
	@Override
	public float getDamageToEntity(@Nullable Entity targetEntity, @Nullable IExtendedDamageSource source, Hand hand) {
		return this.getDamageToEntity(targetEntity, source, super.getDamageToEntity(targetEntity, source, hand));
	}
	
	public float getDamageToEntity(@Nullable Entity targetEntity, @Nullable IExtendedDamageSource source, float baseDamage) {
		DealtDamageEvent<PlayerData<?>> event = new DealtDamageEvent<>(this, this.orgEntity, source, baseDamage);
		this.getEventListener().activateEvents(EventType.DEALT_DAMAGE_PRE_EVENT, event);
		return event.getAttackDamage();
	}
	
	public float getAttackSpeed() {
		return this.getAttackSpeed(this.getHeldItemCapability(Hand.MAIN_HAND), (float)this.orgEntity.getAttributeValue(Attributes.ATTACK_SPEED));
	}
	
	public float getAttackSpeed(CapabilityItem itemCapability, float baseSpeed) {
		GetAttackSpeedEvent event = new GetAttackSpeedEvent(this, itemCapability, baseSpeed);
		this.eventListeners.activateEvents(EventType.ATTACK_SPEED_GET_EVENT, event);
		return Formulars.getAttackSpeedPenalty(this.getWeight(), event.getAttackSpeed(), this);
	}
	
	public PlayerEventListener getEventListener() {
		return this.eventListeners;
	}
	
	@Override
	public IExtendedDamageSource getDamageSource(StunType stunType, AttackAnimation animation, Hand hand) {
		return IExtendedDamageSource.causePlayerDamage(this.orgEntity, stunType, animation, hand);
	}
	
	public float getMaxStamina() {
		ModifiableAttributeInstance stun_resistance = this.orgEntity.getAttribute(EpicFightAttributes.MAX_STAMINA.get());
		return (float)(stun_resistance == null ? 0 : stun_resistance.getValue());
	}
	
	public float getStamina() {
		return this.getMaxStamina() == 0 ? 0 : this.orgEntity.getDataManager().get(DataKeys.STAMINA).floatValue();
	}
	
	public void setStamina(float value) {
		float f1 = Math.max(Math.min(value, this.getMaxStamina()), 0);
		this.orgEntity.getDataManager().set(DataKeys.STAMINA, f1);
	}
	
	public void resetActionTick() {
		this.tickSinceLastAction = 0;
	}
	
	public int getTickSinceLastAction() {
		return this.tickSinceLastAction;
	}
	
	public void openSkillBook(ItemStack itemstack) {
		;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (this.orgEntity.getRidingEntity() != null) {
			return Animations.BIPED_HIT_ON_MOUNT;
		} else {
			switch(stunType) {
			case LONG:
				return Animations.BIPED_HIT_LONG;
			case SHORT:
				return Animations.BIPED_HIT_SHORT;
			case HOLD:
				return Animations.BIPED_HIT_SHORT;
			default:
				return null;
			}
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.biped;
	}
}