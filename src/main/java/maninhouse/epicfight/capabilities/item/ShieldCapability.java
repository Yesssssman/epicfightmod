package maninhouse.epicfight.capabilities.item;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.item.Item;

public class ShieldCapability extends CapabilityItem {
	protected StaticAnimation blockingMotion;
	
	public ShieldCapability(Item item) {
		super(item, WeaponCategory.SHIELD);
		this.blockingMotion = Animations.BIPED_BLOCK;
	}
	
	@Override
	public void setCustomWeapon(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributeSimple(HoldStyle.ONE_HAND, armorNegation2, impact2, maxStrikes2);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> playerdata) {
		return ImmutableMap.of(LivingMotion.BLOCK_SHIELD, this.blockingMotion);
	}
}