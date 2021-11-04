package yesman.epicfight.capabilities.entity.player;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.skill.CapabilitySkill;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.entity.eventlistener.HitEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCAddLearnedSkill;
import yesman.epicfight.network.server.STCChangeSkill;
import yesman.epicfight.network.server.STCLivingMotionChange;
import yesman.epicfight.network.server.STCNotifyPlayerYawChanged;
import yesman.epicfight.network.server.STCPlayAnimation;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.utils.game.IExtendedDamageSource;

public class ServerPlayerData extends PlayerData<ServerPlayerEntity> {
	private LivingEntity attackTarget;
	private Map<LivingMotion, StaticAnimation> mainhandOverwritingLivingMotions = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private Map<LivingMotion, StaticAnimation> offhandOverwritingLivingMotions = Maps.<LivingMotion, StaticAnimation>newHashMap();
	
	@Override
	public void onEntityJoinWorld(ServerPlayerEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		
		CapabilitySkill skillCapability = this.getSkillCapability();
		for (SkillContainer skill : skillCapability.skillContainers) {
			if (skill.getSkill() != null && skill.getSkill().getCategory().shouldSyncronized()) {
				ModNetworkManager.sendToPlayer(new STCChangeSkill(skill.getSkill().getCategory().getIndex(), skill.getSkill().getName(), STCChangeSkill.State.ENABLE), this.orgEntity);
			}
		}
		
		List<String> learnedSkill = Lists.newArrayList();
		for (SkillCategory category : SkillCategory.values()) {
			if (skillCapability.hasCategory(category)) {
				learnedSkill.addAll(Lists.newArrayList(skillCapability.getLearnedSkills(category).stream().map((skill)->skill.getName()).iterator()));
			}
		}
		ModNetworkManager.sendToPlayer(new STCAddLearnedSkill(learnedSkill.toArray(new String[0])), this.orgEntity);
	}
	
	@Override
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {
		if (source.isBasicAttack()) {
			SkillContainer container = this.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK);
			if (!container.isFull() && container.hasSkill(this.getHeldItemCapability(Hand.MAIN_HAND).getSpecialAttack(this))) {
				float value = container.getResource() + amount;
				if (value > 0.0F) {
					this.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).getSkill().setConsumptionSynchronize(this, value);
				}
			}
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		;
	}
	
	public void updateHeldItem(CapabilityItem fromCap, CapabilityItem toCap, ItemStack from, ItemStack to, Hand hand) {
		CapabilityItem mainHandCap = (hand == Hand.MAIN_HAND) ? toCap : this.getHeldItemCapability(Hand.MAIN_HAND);
		mainHandCap.changeWeaponSpecialSkill(this);
		if (hand == Hand.OFF_HAND) {
			if (!from.isEmpty()) {
				from.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::removeModifier);
				from.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::removeModifier);
			}
			if (!fromCap.isEmpty()) {
				fromCap.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.ARMOR_NEGATION.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get())::removeModifier);
				fromCap.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.IMPACT.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get())::removeModifier);
				fromCap.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.MAX_STRIKES.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get())::removeModifier);
			}
			
			if (!to.isEmpty()) {
				to.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::applyNonPersistentModifier);
				to.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::applyNonPersistentModifier);
			}
			if (!toCap.isEmpty()) {
				toCap.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.ARMOR_NEGATION.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get())::applyNonPersistentModifier);
				toCap.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.IMPACT.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get())::applyNonPersistentModifier);
				toCap.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.MAX_STRIKES.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get())::applyNonPersistentModifier);
			}
		}
		this.setLivingMotionCurrentItem(toCap, hand);
	}
	
	@Override
	public void updateArmor(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		
	}
	
	public void setLivingMotionCurrentItem(CapabilityItem capabilityItem, Hand hand) {
		this.resetOverwritingLivingMotions(hand);
		Map<LivingMotion, StaticAnimation> motionChanger = capabilityItem.getLivingMotionModifier(this, hand);
		List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
		List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
		
		for (Map.Entry<LivingMotion, StaticAnimation> entry : motionChanger.entrySet()) {
			this.addOverwritingLivingMotion(entry.getKey(), entry.getValue(), hand);
		}
		
		for (Map.Entry<LivingMotion, StaticAnimation> finalEntry : this.getOverwritingLivingMotionEntrySet()) {
			motions.add(finalEntry.getKey());
			animations.add(finalEntry.getValue());
		}
		
		LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
		StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getEntityId(), motions.size());
		msg.setMotions(motionarr);
		msg.setAnimations(animationarr);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, this.orgEntity);
	}
	
	private void addOverwritingLivingMotion(LivingMotion motion, StaticAnimation animation, Hand hand) {
		Map<LivingMotion, StaticAnimation> overwritingMotion = hand == Hand.MAIN_HAND ? this.mainhandOverwritingLivingMotions : this.offhandOverwritingLivingMotions;
		if (animation != null) {
			overwritingMotion.put(motion, animation);
		}
	}
	
	private void resetOverwritingLivingMotions(Hand hand) {
		Map<LivingMotion, StaticAnimation> overwritingMotion = hand == Hand.MAIN_HAND ? this.mainhandOverwritingLivingMotions : this.offhandOverwritingLivingMotions;
		overwritingMotion.clear();
	}
	
	public Set<Map.Entry<LivingMotion, StaticAnimation>> getOverwritingLivingMotionEntrySet() {
		Map<LivingMotion, StaticAnimation> map = Maps.newHashMap();
		map.putAll(this.mainhandOverwritingLivingMotions);
		for (Map.Entry<LivingMotion, StaticAnimation> entry : this.offhandOverwritingLivingMotions.entrySet()) {
			map.computeIfAbsent(entry.getKey(), (key) -> entry.getValue());
		}
		return map.entrySet();
	}
	
	@Override
	public void playAnimationSynchronize(int namespaceId, int id, float modifyTime) {
		super.playAnimationSynchronize(namespaceId, id, modifyTime);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(namespaceId, id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}
	
	@Override
	public void reserverAnimationSynchronize(StaticAnimation animation) {
		super.reserverAnimationSynchronize(animation);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(animation.getNamespaceId(), animation.getId(), this.orgEntity.getEntityId(), 0.0F), this.orgEntity);
	}
	
	@Override
	public void changeYaw(float amount) {
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCNotifyPlayerYawChanged(this.orgEntity.getEntityId(), this.yaw), this.orgEntity);
	}
	
	@Override
	public boolean hurtBy(LivingAttackEvent event) {
		HitEvent hitEvent = new HitEvent(this, event);
		if (this.getEventListener().activateEvents(EventType.HIT_EVENT, hitEvent)) {
			return false;
		} else {
			return super.hurtBy(event);
		}
	}
	
	@Override
	public ServerPlayerEntity getOriginalEntity() {
		return this.orgEntity;
	}
	
	public void setAttackTarget(LivingEntity entity) {
		this.attackTarget = entity;
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		return this.attackTarget;
	}
}