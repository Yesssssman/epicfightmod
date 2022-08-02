package yesman.epicfight.world.capabilities.entitypatch.player;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPAddSkill;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPChangePlayerMode;
import yesman.epicfight.network.server.SPChangePlayerYaw;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.eventlistener.HurtEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class ServerPlayerPatch extends PlayerPatch<ServerPlayer> {
	private LivingEntity attackTarget;
	private boolean updatedMotionCurrentTick;
	
	@Override
	public void onJoinWorld(ServerPlayer entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		CapabilitySkill skillCapability = this.getSkillCapability();
		
		for (SkillContainer skill : skillCapability.skillContainers) {
			if (skill.getSkill() != null && skill.getSkill().getCategory().shouldSynchronized()) {
				EpicFightNetworkManager.sendToPlayer(new SPChangeSkill(skill.getSkill().getCategory().universalOrdinal(), skill.getSkill().toString(), SPChangeSkill.State.ENABLE), this.original);
			}
		}
		
		List<String> learnedSkill = Lists.newArrayList();
		
		for (SkillCategory category : SkillCategory.ENUM_MANAGER.universalValues()) {
			if (skillCapability.hasCategory(category)) {
				learnedSkill.addAll(Lists.newArrayList(skillCapability.getLearnedSkills(category).stream().map((skill) -> skill.toString()).iterator()));
			}
		}
		
		EpicFightNetworkManager.sendToPlayer(new SPAddSkill(learnedSkill.toArray(new String[0])), this.original);
		EpicFightNetworkManager.sendToPlayer(new SPChangePlayerMode(this.getOriginal().getId(), this.playerMode), this.original);
	}
	
	@Override
	public void onStartTracking(ServerPlayer trackingPlayer) {
		SPChangeLivingMotion msg = new SPChangeLivingMotion(this.getOriginal().getId());
		msg.putEntries(this.getAnimator().getLivingAnimationEntrySet());
		EpicFightNetworkManager.sendToPlayer(msg, trackingPlayer);
		EpicFightNetworkManager.sendToPlayer(new SPChangePlayerMode(this.getOriginal().getId(), this.playerMode), trackingPlayer);
	}
	
	@Override
	public void gatherDamageDealt(ExtendedDamageSource source, float amount) {
		if (source.isBasicAttack()) {
			SkillContainer container = this.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK);
			
			if (!container.isFull() && container.hasSkill(this.getHoldingItemCapability(InteractionHand.MAIN_HAND).getSpecialAttack(this))) {
				float value = container.getResource() + amount;
				
				if (value > 0.0F) {
					this.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK).getSkill().setConsumptionSynchronize(this, value);
				}
			}
		}
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		super.tick(event);
		this.updatedMotionCurrentTick = false;
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
	}
	
	@Override
	public void updateHeldItem(CapabilityItem fromCap, CapabilityItem toCap, ItemStack from, ItemStack to, InteractionHand hand) {
		CapabilityItem mainHandCap = (hand == InteractionHand.MAIN_HAND) ? toCap : this.getHoldingItemCapability(InteractionHand.MAIN_HAND);
		mainHandCap.changeWeaponSpecialSkill(this);
		
		if (hand == InteractionHand.OFF_HAND) {
			if (!from.isEmpty()) {
				from.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::removeModifier);
				from.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_SPEED).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::removeModifier);
			}
			if (!fromCap.isEmpty()) {
				fromCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(EpicFightAttributes.ARMOR_NEGATION.get()).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get())::removeModifier);
				fromCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(EpicFightAttributes.IMPACT.get()).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get())::removeModifier);
				fromCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(EpicFightAttributes.MAX_STRIKES.get()).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get())::removeModifier);
				fromCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(Attributes.ATTACK_DAMAGE).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::removeModifier);
				fromCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(Attributes.ATTACK_SPEED).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::removeModifier);
			}
			
			if (!to.isEmpty()) {
				to.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::addTransientModifier);
				to.getAttributeModifiers(EquipmentSlot.MAINHAND).get(Attributes.ATTACK_SPEED).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::addTransientModifier);
			}
			if (!toCap.isEmpty()) {
				toCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(EpicFightAttributes.ARMOR_NEGATION.get()).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get())::addTransientModifier);
				toCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(EpicFightAttributes.IMPACT.get()).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get())::addTransientModifier);
				toCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(EpicFightAttributes.MAX_STRIKES.get()).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get())::addTransientModifier);
				toCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(Attributes.ATTACK_DAMAGE).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::addTransientModifier);
				toCap.getAttributeModifiers(EquipmentSlot.MAINHAND, this).get(Attributes.ATTACK_SPEED).forEach(this.original.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::addTransientModifier);
			}
		}
		
		this.modifyLivingMotionByCurrentItem();
	}
	
	public void modifyLivingMotionByCurrentItem() {
		if (this.updatedMotionCurrentTick) {
			return;
		}
		
		this.getAnimator().resetMotions();
		CapabilityItem mainhandCap = this.getHoldingItemCapability(InteractionHand.MAIN_HAND);
		CapabilityItem offhandCap = this.getAdvancedHoldingItemCapability(InteractionHand.OFF_HAND);
		Map<LivingMotion, StaticAnimation> motionModifier = Maps.newHashMap();
		
		offhandCap.getLivingMotionModifier(this, InteractionHand.OFF_HAND).forEach(motionModifier::put);
		mainhandCap.getLivingMotionModifier(this, InteractionHand.MAIN_HAND).forEach(motionModifier::put);
		
		for (Map.Entry<LivingMotion, StaticAnimation> entry : motionModifier.entrySet()) {
			this.getAnimator().addLivingAnimation(entry.getKey(), entry.getValue());
		}
		
		SPChangeLivingMotion msg = new SPChangeLivingMotion(this.original.getId());
		msg.putEntries(this.getAnimator().getLivingAnimationEntrySet());
		
		EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, this.original);
		this.updatedMotionCurrentTick = true;
	}
	
	@Override
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier, AnimationPacketProvider packetProvider) {
		super.playAnimationSynchronized(animation, convertTimeModifier, packetProvider);
		EpicFightNetworkManager.sendToPlayer(packetProvider.get(animation, convertTimeModifier, this), this.original);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation animation) {
		super.reserveAnimation(animation);
		EpicFightNetworkManager.sendToPlayer(new SPPlayAnimation(animation, this.original.getId(), 0.0F), this.original);
	}
	
	@Override
	public void changeYaw(float amount) {
		super.changeYaw(amount);
		EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new SPChangePlayerYaw(this.original.getId(), this.yaw), this.original);
	}
	
	@Override
	public AttackResult tryHurt(DamageSource damageSource, float amount) {
		HurtEvent.Pre hurtEvent = new HurtEvent.Pre(this, damageSource, amount);
		
		if (this.getEventListener().triggerEvents(EventType.HURT_EVENT_PRE, hurtEvent)) {
			return new AttackResult(hurtEvent.getResult(), hurtEvent.getAmount());
		} else {
			return super.tryHurt(damageSource, amount);
		}
	}
	
	@Override
	public void toMiningMode(boolean synchronize) {
		super.toMiningMode(synchronize);
		
		if (synchronize) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new SPChangePlayerMode(this.original.getId(), PlayerMode.MINING), this.original);
		}
	}
	
	@Override
	public void toBattleMode(boolean synchronize) {
		super.toBattleMode(synchronize);
		
		if (synchronize) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new SPChangePlayerMode(this.original.getId(), PlayerMode.BATTLE), this.original);
		}
	}
	
	@Override
	public boolean isTeammate(Entity entityIn) {
		if (entityIn instanceof Player && !this.getOriginal().server.isPvpAllowed()) {
			return true;
		}
		
		return super.isTeammate(entityIn);
	}
	
	public void setAttackTarget(LivingEntity entity) {
		this.attackTarget = entity;
	}
	
	@Override
	public LivingEntity getTarget() {
		return this.attackTarget;
	}
}