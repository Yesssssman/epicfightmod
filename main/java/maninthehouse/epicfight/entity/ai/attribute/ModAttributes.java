package maninthehouse.epicfight.entity.ai.attribute;

import java.util.UUID;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.RangedAttribute;

public class ModAttributes {
    public static final IAttribute MAX_STUN_ARMOR = makeNewAttribute("stun_armor", 0.0D, 0.0D, 1024.0D, true);
    public static final IAttribute WEIGHT = makeNewAttribute("weight", 0.0D, 0.0D, 1024.0D, true);
    public static final IAttribute MAX_STRIKES = makeNewAttribute("max_strikes", 1.0D, 1.0D, 1024.0D, false);
	public static final IAttribute ARMOR_NEGATION = makeNewAttribute("armor_negation", 0.0D, 0.0D, 100.0D, false);
	public static final IAttribute IMPACT = makeNewAttribute("impact", 0.0D, 0.0D, 1024.0D, false);
	public static final IAttribute OFFHAND_ATTACK_DAMAGE = makeNewAttribute("offhand attack damage", 1.0D, 0.0D, 2048.0D, false);
	public static final IAttribute OFFHAND_ATTACK_SPEED = makeNewAttribute("offhand attack damage", 4.0D, 0.0D, 1024.0D, false);
	public static final UUID IGNORE_DEFENCE_ID = UUID.fromString("b0a7436e-5734-11eb-ae93-0242ac130002");
	public static final UUID HIT_AT_ONCE_ID = UUID.fromString("b0a745b2-5734-11eb-ae93-0242ac130002");
	public static final UUID IMPACT_ID = UUID.fromString("b0a746ac-5734-11eb-ae93-0242ac130002");
    
	public static IAttribute makeNewAttribute(String name, double defaultValue, double minValue, double maxValue, boolean shouldWatch) {
		return (new RangedAttribute((IAttribute)null, name, defaultValue, minValue, maxValue)).setShouldWatch(shouldWatch);
	}
	
	public static AttributeModifier getArmorNegationModifier(double value) {
		return new AttributeModifier(ModAttributes.IGNORE_DEFENCE_ID, "Weapon modifier", value, 0);
	}
    
	public static AttributeModifier getMaxStrikesModifier(int value) {
    	return new AttributeModifier(ModAttributes.HIT_AT_ONCE_ID, "Weapon modifier", (double)value, 0);
	}
    
	public static AttributeModifier getImpactModifier(double value) {
    	return new AttributeModifier(ModAttributes.IMPACT_ID, "Weapon modifier", value, 0);
	}
}