package maninhouse.epicfight.capabilities.item;

import com.mojang.datafixers.util.Pair;

import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.particle.HitParticleType;
import maninhouse.epicfight.particle.Particles;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class ShovelCapability extends ToolCapability {
	public ShovelCapability(Item item) {
		super(item, WeaponCategory.SHOVEL);
	}
	
	@Override
	protected void registerAttribute() {
		double impact = this.itemTier.getHarvestLevel() * 0.4D + 0.8D;
		this.addStyleAttibute(HoldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(impact)));
	}
	
	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLUNT_HIT;
	}

	@Override
	public HitParticleType getHitParticle() {
		return Particles.HIT_BLUNT.get();
	}
}