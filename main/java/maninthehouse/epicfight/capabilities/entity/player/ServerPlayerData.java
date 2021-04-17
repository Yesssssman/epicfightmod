package maninthehouse.epicfight.capabilities.entity.player;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCLivingMotionChange;
import maninthehouse.epicfight.network.server.STCNotifyPlayerYawChanged;
import maninthehouse.epicfight.network.server.STCPlayAnimation;
import maninthehouse.epicfight.network.server.STCSetSkillValue;
import maninthehouse.epicfight.network.server.STCSetSkillValue.Target;
import maninthehouse.epicfight.skill.SkillContainer;
import maninthehouse.epicfight.skill.SkillSlot;
import maninthehouse.epicfight.utils.game.Formulars;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ServerPlayerData extends PlayerData<EntityPlayerMP> {
	private Map<LivingMotion, StaticAnimation> livingMotionMap = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private Map<LivingMotion, StaticAnimation> defaultLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
	private List<LivingMotion> modifiedLivingMotions = Lists.<LivingMotion>newArrayList();
	
	public static final UUID WEIGHT_PENALTY_MODIFIIER = UUID.fromString("414fed9e-e5e3-11ea-adc1-0242ac120002");
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	
	@Override
	public void gatherDamageDealt(IExtendedDamageSource source, float amount) {
		if (source.getSkillId() > Animations.BASIC_ATTACK_MIN && source.getSkillId() < Animations.BASIC_ATTACK_MAX) {
			SkillContainer container = this.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
			CapabilityItem itemCap = this.getHeldItemCapability(EnumHand.MAIN_HAND);

			if (itemCap != null && container.hasSkill(itemCap.getSpecialAttack(this))) {
				float value = container.getRemainCooldown() + amount;
				
				if (value > 0.0F) {
					this.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK).setCooldown(value);
					ModNetworkManager.sendToPlayer(new STCSetSkillValue(Target.COOLDOWN, SkillSlot.WEAPON_SPECIAL_ATTACK.getIndex(), value, false), orgEntity);
				}
			}
		}
	}
	
	@Override
	public void onEntityJoinWorld(EntityPlayerMP entityIn) {
		super.onEntityJoinWorld(entityIn);
		livingMotionMap.put(LivingMotion.IDLE, Animations.BIPED_IDLE);
		livingMotionMap.put(LivingMotion.WALKING, Animations.BIPED_WALK);
		livingMotionMap.put(LivingMotion.RUNNING, Animations.BIPED_RUN);
		livingMotionMap.put(LivingMotion.SNEAKING, Animations.BIPED_SNEAK);
		livingMotionMap.put(LivingMotion.SWIMMING, Animations.BIPED_SWIM);
		livingMotionMap.put(LivingMotion.FLOATING, Animations.BIPED_FLOAT);
		livingMotionMap.put(LivingMotion.KNEELING, Animations.BIPED_KNEEL);
		livingMotionMap.put(LivingMotion.FALL, Animations.BIPED_FALL);
		livingMotionMap.put(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		livingMotionMap.put(LivingMotion.FLYING, Animations.BIPED_FLYING);
		livingMotionMap.put(LivingMotion.DEATH, Animations.BIPED_DEATH);
		
		for (Map.Entry<LivingMotion, StaticAnimation> entry : livingMotionMap.entrySet()) {
			defaultLivingAnimations.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void updateMotion() {
		;
	}
	
	public void onHeldItemChange(CapabilityItem toChange, ItemStack stack, EnumHand hand) {
		CapabilityItem mainHandCap = (hand == EnumHand.MAIN_HAND) ? toChange : this.getHeldItemCapability(EnumHand.MAIN_HAND);
		if(mainHandCap != null) {
			mainHandCap.onHeld(this);
		} else {
			this.getSkill(SkillSlot.WEAPON_GIMMICK).setSkill(null);
		}
		
		if (hand == EnumHand.MAIN_HAND) {
			this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).removeModifier(WEIGHT_PENALTY_MODIFIIER);
			float weaponSpeed = (float) this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).getBaseValue();
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
				weaponSpeed += attributeModifier.getAmount();
			}
			
			this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED).applyModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight panelty modifier",
					Formulars.getAttackSpeedPenalty(this.getWeight(), weaponSpeed, this), 0));
		} else {
			this.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED).removeModifier(WEIGHT_PENALTY_MODIFIIER);
			float weaponSpeed = (float) this.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED).getBaseValue();
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
				weaponSpeed += attributeModifier.getAmount();
			}
			
			this.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED).applyModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight panelty modifier",
					Formulars.getAttackSpeedPenalty(this.getWeight(), weaponSpeed, this), 0));
			this.getAttribute(ModAttributes.OFFHAND_ATTACK_DAMAGE).removeModifier(ATTACK_DAMAGE_MODIFIER);
			
			for(AttributeModifier attributeModifier : stack.getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName())) {
				this.getAttribute(ModAttributes.OFFHAND_ATTACK_DAMAGE).applyModifier(attributeModifier);
			}
		}
		
		this.modifiLivingMotions(mainHandCap);
	}
	
	@Override
	public void onArmorSlotChanged(CapabilityItem fromCap, CapabilityItem toCap, EntityEquipmentSlot slotType) {
		IAttributeInstance mainhandAttackSpeed = this.getAttribute(SharedMonsterAttributes.ATTACK_SPEED);
		IAttributeInstance offhandAttackSpeed = this.getAttribute(ModAttributes.OFFHAND_ATTACK_SPEED);
		
		mainhandAttackSpeed.removeModifier(WEIGHT_PENALTY_MODIFIIER);
		float mainWeaponSpeed = (float) mainhandAttackSpeed.getBaseValue();
		for(AttributeModifier attributeModifier : this.getOriginalEntity().getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
			mainWeaponSpeed += (float)attributeModifier.getAmount();
		}
		mainhandAttackSpeed.applyModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight panelty modifier", 
				Formulars.getAttackSpeedPenalty(this.getWeight(), mainWeaponSpeed, this), 0));
		
		offhandAttackSpeed.removeModifier(WEIGHT_PENALTY_MODIFIIER);
		float offWeaponSpeed = (float) offhandAttackSpeed.getBaseValue();
		for(AttributeModifier attributeModifier : this.getOriginalEntity().getHeldItemOffhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_SPEED.getName())) {
			offWeaponSpeed += (float)attributeModifier.getAmount();
		}
		offhandAttackSpeed.applyModifier(new AttributeModifier(WEIGHT_PENALTY_MODIFIIER, "weight panelty modifier",
				Formulars.getAttackSpeedPenalty(this.getWeight(), offWeaponSpeed, this), 0));
	}
	
	public void modifiLivingMotions(CapabilityItem mainhand) {
		this.resetModifiedLivingMotions();
		
		if (mainhand != null) {
			Map<LivingMotion, StaticAnimation> motionChanger = mainhand.getLivingMotionChanges(this);
			if (motionChanger != null) {
				List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
				List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();

				for (Map.Entry<LivingMotion, StaticAnimation> entry : motionChanger.entrySet()) {
					this.addModifiedLivingMotion(entry.getKey(), entry.getValue());
					motions.add(entry.getKey());
					animations.add(entry.getValue());
				}
				
				LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
				StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
				STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getEntityId(), motionChanger.size());
				msg.setMotions(motionarr);
				msg.setAnimations(animationarr);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
				return;
			}
		}
		
		STCLivingMotionChange msg = new STCLivingMotionChange(this.orgEntity.getEntityId(), 0);
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg, orgEntity);
	}
	
	private void addModifiedLivingMotion(LivingMotion motion, StaticAnimation animation) {
		if(animation != null) {
			if (!this.modifiedLivingMotions.contains(motion)) {
				this.modifiedLivingMotions.add(motion);
			}
			
			this.livingMotionMap.put(motion, animation);
		}
	}
	
	private void resetModifiedLivingMotions() {
		for(LivingMotion livingMotion : modifiedLivingMotions) {
			this.livingMotionMap.put(livingMotion, defaultLivingAnimations.get(livingMotion));
		}
		
		modifiedLivingMotions.clear();
	}

	public void modifiLivingMotionToAll(STCLivingMotionChange packet) {
		LivingMotion[] motions = packet.getMotions();
		StaticAnimation[] animations = packet.getAnimations();
		
		for(int i = 0; i < motions.length; i++) {
			this.addModifiedLivingMotion(motions[i], animations[i]);
		}
		
		ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(packet, this.orgEntity);
	}
	
	public Set<Map.Entry<LivingMotion, StaticAnimation>> getLivingMotionEntrySet() {
		return this.livingMotionMap.entrySet();
	}

	@Override
	public void playAnimationSynchronize(int id, float modifyTime) {
		super.playAnimationSynchronize(id, modifyTime);
		ModNetworkManager.sendToPlayer(new STCPlayAnimation(id, this.orgEntity.getEntityId(), modifyTime), this.orgEntity);
	}
	
	@Override
	public void changeYaw(float amount) {
		super.changeYaw(amount);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(this.orgEntity.getEntityId(), yaw), this.orgEntity);
		ModNetworkManager.sendToPlayer(new STCNotifyPlayerYawChanged(this.orgEntity.getEntityId(), yaw), this.orgEntity);
	}

	@Override
	public EntityPlayerMP getOriginalEntity() {
		return orgEntity;
	}

	@Override
	public void aboutToDeath() {
		;
	}
}