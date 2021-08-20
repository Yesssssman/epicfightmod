package maninhouse.epicfight.capabilities.item;

import java.util.HashMap;
import java.util.Map;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import net.minecraft.item.Item;

public class RangedWeaponCapability extends CapabilityItem {
	protected Map<LivingMotion, StaticAnimation> rangeAnimationSet;

	public RangedWeaponCapability(Item item, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot) {
		super(item, WeaponCategory.RANGED);
		this.rangeAnimationSet = new HashMap<LivingMotion, StaticAnimation> ();
		
		if(reload != null) {
			this.rangeAnimationSet.put(LivingMotion.RELOAD, reload);
		}
		if(aiming != null) {
			this.rangeAnimationSet.put(LivingMotion.AIM, aiming);
		}
		if(shot != null) {
			this.rangeAnimationSet.put(LivingMotion.SHOT, shot);
		}
	}
	
	@Override
	public void setCustomWeapon(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributeSimple(HoldStyle.AIMING, armorNegation2, impact2, maxStrikes2);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionChanges(PlayerData<?> playerdata) {
		return this.rangeAnimationSet;
	}

	@Override
	public boolean canUseOnMount() {
		return true;
	}
	
	@Override
	public final HoldOption getHoldOption() {
		return HoldOption.TWO_HANDED;
	}
}