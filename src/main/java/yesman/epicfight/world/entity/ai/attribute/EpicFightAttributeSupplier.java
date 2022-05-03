package yesman.epicfight.world.entity.ai.attribute;

import java.util.function.Consumer;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class EpicFightAttributeSupplier extends AttributeSupplier {
	private final AttributeSupplier epicfightInstances;
	
	public EpicFightAttributeSupplier(AttributeSupplier copy) {
		super(copy.instances);
		this.epicfightInstances = AttributeSupplier.builder()
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
	public AttributeInstance createInstance(Consumer<AttributeInstance> onDirty, Attribute attribute) {
		AttributeInstance instance = super.createInstance(onDirty, attribute);
		
		if (instance == null) {
			instance = this.epicfightInstances.createInstance(onDirty, attribute);
		}
		
		return instance;
	}
}