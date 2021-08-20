package maninhouse.epicfight.capabilities.entity.player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.capabilities.skill.CapabilitySkill;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.entity.eventlistener.HitEvent;
import maninhouse.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCChangeSkill;
import maninhouse.epicfight.network.server.STCLivingMotionChange;
import maninhouse.epicfight.network.server.STCNotifyPlayerYawChanged;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import maninhouse.epicfight.skill.SkillCategory;
import maninhouse.epicfight.skill.SkillContainer;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class ServerPlayerData extends PlayerData<ServerPlayerEntity> {
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
	private LivingEntity attackTarget;
	private Map<LivingMotion, StaticAnimation> overridenLivingMotions = Maps.<LivingMotion, StaticAnimation>newHashMap();
	
	@Override
	public void onEntityJoinWorld(ServerPlayerEntity entityIn) {
		super.onEntityJoinWorld(entityIn);
		CapabilitySkill skillCapability = this.getSkillCapability();
		for (SkillContainer skill : skillCapability.skills) {
			if (skill.getContaining() != null && skill.getContaining().getCategory().shouldSyncronized()) {
				ModNetworkManager.sendToPlayer(new STCChangeSkill(skill.getContaining().getCategory().getIndex(), skill.getContaining().getSkillName()), this.orgEntity);
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
	public void updateMotion() {
		;
	}
	
	public void onHeldItemChange(CapabilityItem toChange, ItemStack stack, Hand hand) {
		CapabilityItem mainHandCap = (hand == Hand.MAIN_HAND) ? toChange : this.getHeldItemCapability(Hand.MAIN_HAND);
		mainHandCap.onHeld(this);
		
		if (hand == Hand.OFF_HAND) {
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_DAMAGE.get()).removeModifier(ATTACK_DAMAGE_MODIFIER);
			
			for (AttributeModifier attributeModifier : stack.getAttributeModifiers(EquipmentSlotType.MAINHAND).get(Attributes.ATTACK_DAMAGE)) {
				this.orgEntity.getAttribute(ModAttributes.OFFHAND_ATTACK_DAMAGE.get()).applyNonPersistentModifier(attributeModifier);
			}
			
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_ARMOR_NEGATION.get()).removeModifier(ModAttributes.ARMOR_NEGATION_MODIFIER);
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_IMPACT.get()).removeModifier(ModAttributes.IMPACT_MODIFIER);
			this.orgEntity.getAttribute(ModAttributes.OFFHAND_MAX_STRIKES.get()).removeModifier(ModAttributes.MAX_STRIKE_MODIFIER);
			
			for (AttributeModifier attributeModifier : toChange.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(ModAttributes.ARMOR_NEGATION.get())) {
				this.orgEntity.getAttribute(ModAttributes.OFFHAND_ARMOR_NEGATION.get()).applyNonPersistentModifier(attributeModifier);
			}
			for (AttributeModifier attributeModifier : toChange.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(ModAttributes.IMPACT.get())) {
				this.orgEntity.getAttribute(ModAttributes.OFFHAND_IMPACT.get()).applyNonPersistentModifier(attributeModifier);
			}
			for (AttributeModifier attributeModifier : toChange.getAttributeModifiers(EquipmentSlotType.MAINHAND, this).get(ModAttributes.MAX_STRIKES.get())) {
				this.orgEntity.getAttribute(ModAttributes.OFFHAND_MAX_STRIKES.get()).applyNonPersistentModifier(attributeModifier);
			}
		}
	}
	
	@Override
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		
	}
	
	public void setLivingMotionCurrentItem(CapabilityItem capability) {
		this.resetOverridenLivingMotions();
		Map<LivingMotion, StaticAnimation> motionChanger = capability.getLivingMotionChanges(this);
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
	public void playAnimationSynchronize(int id, float modifyTime) {
		super.playAnimationSynchronize(id, modifyTime);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}
	
	@Override
	public void reserverAnimationSynchronize(StaticAnimation animation) {
		super.reserverAnimationSynchronize(animation);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(animation.getId(), this.orgEntity.getEntityId(), 0.0F), this.orgEntity);
	}
	
	@Override
	public void changeYaw(float amount) {
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(new STCNotifyPlayerYawChanged(this.orgEntity.getEntityId(), this.yaw), this.orgEntity);
	}
	
	@Override
	public boolean attackEntityFrom(LivingAttackEvent event) {
		HitEvent hitEvent = new HitEvent(this, event);
		if (this.getEventListener().activateEvents(EventType.HIT_EVENT, hitEvent)) {
			return false;
		} else {
			return super.attackEntityFrom(event);
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