package yesman.epicfight.world.entity.ai.attribute;

import java.util.function.Consumer;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;

public class EpicFightAttributeSupplier extends AttributeModifierMap {
	private final AttributeModifierMap epicfightInstances;
	
	public EpicFightAttributeSupplier(AttributeModifierMap copy) {
		super(copy.instances);
		this.epicfightInstances = AttributeModifierMap.builder()
				.add(Attributes.ATTACK_DAMAGE)
				.add(EpicFightAttributes.WEIGHT.get())
				.add(EpicFightAttributes.IMPACT.get())
				.add(EpicFightAttributes.ARMOR_NEGATION.get())
				.add(EpicFightAttributes.MAX_STRIKES.get())
				.add(EpicFightAttributes.STUN_ARMOR.get())
				.add(EpicFightAttributes.OFFHAND_ARMOR_NEGATION.get())
				.add(EpicFightAttributes.OFFHAND_IMPACT.get())
				.add(EpicFightAttributes.OFFHAND_MAX_STRIKES.get())
				.add(EpicFightAttributes.OFFHAND_ATTACK_DAMAGE.get())
				.add(EpicFightAttributes.OFFHAND_ATTACK_SPEED.get())
			.build();
	}
	
	@Override
	public ModifiableAttributeInstance createInstance(Consumer<ModifiableAttributeInstance> onDirty, Attribute attribute) {
		ModifiableAttributeInstance instance = super.createInstance(onDirty, attribute);
		
		if (instance == null) {
			instance = this.epicfightInstances.createInstance(onDirty, attribute);
		}
		
		return instance;
	}
}