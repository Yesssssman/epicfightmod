package yesman.epicfight.capabilities.item;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.Item;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.gamedata.Animations;

public class ShieldCapability extends CapabilityItem {
	protected StaticAnimation blockingMotion;
	
	public ShieldCapability(Item item) {
		super(item, WeaponCategory.SHIELD);
		this.blockingMotion = Animations.BIPED_BLOCK;
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(PlayerData<?> playerdata) {
		return ImmutableMap.of(LivingMotion.BLOCK_SHIELD, this.blockingMotion);
	}
}