package maninthehouse.epicfight.capabilities.item;

import java.util.List;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Colliders;
import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.utils.game.Pair;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;

public class PickaxeCapability extends MaterialItemCapability {
	public PickaxeCapability(Item item) {
		super(item, WeaponCategory.PICKAXE);
	}
	
	@Override
	protected void registerAttribute() {
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(-0.4D + 0.1D * this.material.getHarvestLevel())));
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.ARMOR_NEGATION, ModAttributes.getArmorNegationModifier(6.0D * this.material.getHarvestLevel())));
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return AxeCapability.axeAttackMotions;
	}

	@Override
	public SoundEvent getHitSound() {
		return Sounds.BLADE_HIT;
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.tools;
	}
}