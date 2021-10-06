package yesman.epicfight.entity.ai.attribute;

import java.util.Map;
import java.util.UUID;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.config.CapabilityConfig;
import yesman.epicfight.config.CapabilityConfig.CustomEntityConfig;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightAttributes {
	public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, EpicFightMod.MODID);
    public static final RegistryObject<Attribute> MAX_STAMINA = ATTRIBUTES.register("staminar", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".staminar", 0.0D, 0.0D, 1024.0D).setShouldWatch(true));
    public static final RegistryObject<Attribute> STUN_ARMOR = ATTRIBUTES.register("stun_armor", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".stun_armor", 0.0D, 0.0D, 1024.0D).setShouldWatch(true));
    public static final RegistryObject<Attribute> WEIGHT = ATTRIBUTES.register("weight", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".weight", 0.0D, 0.0D, 1024.0).setShouldWatch(true));
    public static final RegistryObject<Attribute> MAX_STRIKES = ATTRIBUTES.register("max_strikes", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".max_strikes", 1.0D, 1.0D, 1024.0).setShouldWatch(true));
	public static final RegistryObject<Attribute> ARMOR_NEGATION = ATTRIBUTES.register("armor_negation", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".armor_negation", 0.0D, 0.0D, 100.0D).setShouldWatch(true));
	public static final RegistryObject<Attribute> IMPACT = ATTRIBUTES.register("impact", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".impact", 0.0D, 0.0D, 1024.0).setShouldWatch(true));
	public static final RegistryObject<Attribute> OFFHAND_ATTACK_DAMAGE = ATTRIBUTES.register("offhand_attack_damage", () -> new RangedAttribute("offhand attack damage", 1.0D, 0.0D, 2048.0D));
	public static final RegistryObject<Attribute> OFFHAND_ATTACK_SPEED = ATTRIBUTES.register("offhand_attack_speed", () -> new RangedAttribute("offhand attack speed", 4.0D, 0.0D, 1024.0D).setShouldWatch(true));
	public static final RegistryObject<Attribute> OFFHAND_MAX_STRIKES = ATTRIBUTES.register("offhand_max_strikes", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".offhand_max_strikes", 1.0D, 1.0D, 1024.0).setShouldWatch(true));
	public static final RegistryObject<Attribute> OFFHAND_ARMOR_NEGATION = ATTRIBUTES.register("offhand_armor_negation", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".offhand_armor_negation", 0.0D, 0.0D, 100.0D).setShouldWatch(true));
	public static final RegistryObject<Attribute> OFFHAND_IMPACT = ATTRIBUTES.register("offhand_impact", () -> new RangedAttribute("attribute.name." + EpicFightMod.MODID + ".offhand_impact", 0.0D, 0.0D, 1024.0).setShouldWatch(true));
	public static final UUID ARMOR_NEGATION_MODIFIER = UUID.fromString("b0a7436e-5734-11eb-ae93-0242ac130002");
	public static final UUID MAX_STRIKE_MODIFIER = UUID.fromString("b0a745b2-5734-11eb-ae93-0242ac130002");
	public static final UUID IMPACT_MODIFIER = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130002");
	public static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("1c224694-19f3-11ec-9621-0242ac130002");
	public static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("1c2249f0-19f3-11ec-9621-0242ac130002");
    
	public static void modifyAttributeMap(EntityAttributeModificationEvent event) {
		general(EntityType.CAVE_SPIDER, event);
		general(EntityType.CREEPER, event);
		general(EntityType.EVOKER, event);
		general(EntityType.IRON_GOLEM, event);
		general(EntityType.PILLAGER, event);
		general(EntityType.RAVAGER, event);
		general(EntityType.SPIDER, event);
		general(EntityType.VEX, event);
		general(EntityType.VINDICATOR, event);
		general(EntityType.WITCH, event);
		general(EntityType.HOGLIN, event);
		general(EntityType.ZOGLIN, event);
		withStunArmor(EntityType.DROWNED, event);
		withStunArmor(EntityType.ENDERMAN, event);
		withStunArmor(EntityType.HUSK, event);
		withStunArmor(EntityType.PIGLIN, event);
		withStunArmor(EntityType.field_242287_aj, event);
		withStunArmor(EntityType.SKELETON, event);
		withStunArmor(EntityType.STRAY, event);
		withStunArmor(EntityType.WITHER_SKELETON, event);
		withStunArmor(EntityType.ZOMBIE, event);
		withStunArmor(EntityType.ZOMBIE_VILLAGER, event);
		withStunArmor(EntityType.ZOMBIFIED_PIGLIN, event);
		player(EntityType.PLAYER, event);
		
		for (Map.Entry<ResourceLocation, CustomEntityConfig> config : CapabilityConfig.CUSTOM_ENTITY_MAP.entrySet()) {
			if (ForgeRegistries.ENTITIES.containsKey(config.getKey())) {
				@SuppressWarnings("unchecked")
				EntityType<? extends LivingEntity> entityType = (EntityType<? extends LivingEntity>) ForgeRegistries.ENTITIES.getValue(config.getKey());
				
				switch (config.getValue().getEntityAIType()) {
				case ZOMBIE:
					withStunArmor(entityType, event);
					break;
				case SKELETON:
					withStunArmor(entityType, event);
					break;
				case CREEPER:
					general(entityType, event);
				case SPIDER:
					general(entityType, event);
					break;
				case VINDICATOR:
					general(entityType, event);
					break;
				default:
				}
			}
		}
	}
    
    private static void general(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
		event.add(entityType, EpicFightAttributes.WEIGHT.get());
		event.add(entityType, EpicFightAttributes.ARMOR_NEGATION.get());
		event.add(entityType, EpicFightAttributes.IMPACT.get());
		event.add(entityType, EpicFightAttributes.MAX_STRIKES.get());
	}
    
    private static void withStunArmor(EntityType<? extends LivingEntity> entityType, EntityAttributeModificationEvent event) {
		general(entityType, event);
		event.add(entityType, EpicFightAttributes.STUN_ARMOR.get());
	}
    
    private static void player(EntityType<? extends PlayerEntity> entityType, EntityAttributeModificationEvent event) {
		withStunArmor(entityType, event);
		event.add(entityType, EpicFightAttributes.MAX_STAMINA.get());
		event.add(entityType, EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get());
		event.add(entityType, EpicFightAttributes.OFFHAND_ATTACK_SPEED.get());
		event.add(entityType, EpicFightAttributes.OFFHAND_MAX_STRIKES.get());
		event.add(entityType, EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get());
		event.add(entityType, EpicFightAttributes.OFFHAND_IMPACT.get());
	}
	
	public static AttributeModifier getArmorNegationModifier(double value) {
		return new AttributeModifier(EpicFightAttributes.ARMOR_NEGATION_MODIFIER, EpicFightMod.MODID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getMaxStrikesModifier(int value) {
		return new AttributeModifier(EpicFightAttributes.MAX_STRIKE_MODIFIER, EpicFightMod.MODID + ":weapon_modifier", (double) value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getImpactModifier(double value) {
		return new AttributeModifier(EpicFightAttributes.IMPACT_MODIFIER, EpicFightMod.MODID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getDamageBonusModifier(double value) {
		return new AttributeModifier(ATTACK_DAMAGE_MODIFIER, EpicFightMod.MODID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}

	public static AttributeModifier getSpeedBonusModifier(double value) {
		return new AttributeModifier(ATTACK_SPEED_MODIFIER, EpicFightMod.MODID + ":weapon_modifier", value, AttributeModifier.Operation.ADDITION);
	}
}