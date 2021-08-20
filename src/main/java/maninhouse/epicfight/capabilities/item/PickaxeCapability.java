package maninhouse.epicfight.capabilities.item;

import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class PickaxeCapability extends ToolCapability {
	public PickaxeCapability(Item item) {
		super(item, WeaponCategory.PICKAXE);
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(-0.4D + 0.1D * this.itemTier.getHarvestLevel())));
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.ARMOR_NEGATION, ModAttributes.getArmorNegationModifier(6.0D * this.itemTier.getHarvestLevel())));
	}
	
	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLADE_HIT;
	}
	
	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLADE.get();
	}
}