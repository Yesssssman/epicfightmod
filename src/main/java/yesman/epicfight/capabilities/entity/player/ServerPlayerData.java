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
import net.minecraft.item.Item;
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
import yesman.epicfight.network.server.STCChangeSkill;
import yesman.epicfight.network.server.STCLivingMotionChange;
import yesman.epicfight.network.server.STCNotifyPlayerYawChanged;
import yesman.epicfight.network.server.STCPlayAnimation;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.utils.game.IExtendedDamageSource;

public class ServerPlayerData extends PlayerData<ServerPlayerEntity> {
	private LivingEntity attackTarget;
	private Map<LivingMotion, StaticAnimation> overridenLivingMotions = Maps.<LivingMotion, StaticAnimation>newHashMap();
	
	@Override
	public void onEntityJoinWorld(ServerPlayerEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		CapabilitySkill skillCapability = this.getSkillCapability();
		for (SkillContainer skill : skillCapability.skills) {
			if (skill.getContaining() != null && skill.getContaining().getCategory().shouldSyncronized()) {
				ModNetworkManager.sendToPlayer(new STCChangeSkill(skill.getContaining().getCategory().getIndex(), skill.getContaining().getSkillName(),
						STCChangeSkill.State.ENABLE), this.orgEntity);
			}
		}
	}
	
	@Override
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {
		if (source.isBasicAttack()) {
			SkillContainer container = this.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK);
			if (!container.isFull() && container.hasSkill(this.getHeldItemCapability(Hand.MAIN_HAND).getSpecialAttack(this))) {
				float value = container.getResource() + amount;
				if (value > 0.0F) {
					this.getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).getContaining().setConsumptionSynchronize(this, value);
				}
			}
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		;
	}
	
	public void updateHeldItem(CapabilityItem toChange, ItemStack stack, Hand hand) {
		CapabilityItem mainHandCap = (hand == Hand.MAIN_HAND) ? toChange : this.getHeldItemCapability(Hand.MAIN_HAND);
		mainHandCap.onHeld(this);
		if (hand == Hand.OFF_HAND) {
			this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get()).removeModifier(Item.ATTACK_DAMAGE_MODIFIER);
			this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get()).removeModifier(Item.ATTACK_SPEED_MODIFIER);
			this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get()).removeModifier(EpicFightAttributes.ARMOR_NEGATION_MODIFIER);
			this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get()).removeModifier(EpicFightAttributes.IMPACT_MODIFIER);
			this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get()).removeModifier(EpicFightAttributes.MAX_STRIKE_MODIFIER);
			stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())::applyNonPersistentModifier);
			stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_SPEED).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())::applyNonPersistentModifier);
			toChange.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.ARMOR_NEGATION.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get())::applyNonPersistentModifier);
			toChange.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.IMPACT.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_IMPACT.get())::applyNonPersistentModifier);
			toChange.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(EpicFightAttributes.MAX_STRIKES.get()).forEach(this.orgEntity.getAttribute(EpicFightAttributes.OFFHAND_MAX_STRIKES.get())::applyNonPersistentModifier);
		}
		
		this.setLivingMotionCurrentItem(toChange);
	}
	
	@Override
	public void updateArmor(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		
	}
	
	public void setLivingMotionCurrentItem(CapabilityItem capability) {
		this.resetOverridenLivingMotions();
		Map<LivingMotion, StaticAnimation> motionChanger = capability.getLivingMotionModifier(this);
		List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
		List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
		for (Map.Entry<LivingMotion, StaticAnimation> entry : motionChanger.entrySet()) {
			this.addOverridenLivingMotion(entry.getKey(), entry.getValue());
			motions.add(entry.getKey());
			animations.add(entry.getValue());
		}
		LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
		StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getEntityId(), motionChanger.size());
		msg.setMotions(motionarr);
		msg.setAnimations(animationarr);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, this.orgEntity);
	}
	
	private void addOverridenLivingMotion(LivingMotion motion, StaticAnimation animation) {
		if(animation != null) {
			this.overridenLivingMotions.put(motion, animation);
		}
	}
	
	private void resetOverridenLivingMotions() {
		this.overridenLivingMotions.clear();
	}
	
	public void modifyLivingMotion(STCLivingMotionChange packet) {
		this.resetOverridenLivingMotions();
		LivingMotion[] motions = packet.getMotions();
		StaticAnimation[] animations = packet.getAnimations();
		
		for (int i = 0; i < motions.length; i++) {
			this.addOverridenLivingMotion(motions[i], animations[i]);
		}
		
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(packet, this.orgEntity);
	}
	
	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingMotionEntrySet() {
		return this.overridenLivingMotions.entrySet();
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