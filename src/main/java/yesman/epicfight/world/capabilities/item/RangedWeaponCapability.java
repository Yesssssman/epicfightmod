package yesman.epicfight.world.capabilities.item;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RangedWeaponCapability extends CapabilityItem {
	protected Map<LivingMotion, StaticAnimation> rangeAnimationSet;
	
	public RangedWeaponCapability(Item item, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot) {
		this(item, reload, aiming, shot, WeaponCategories.RANGED);
	}
	
	public RangedWeaponCapability(Item item, StaticAnimation reload, StaticAnimation aiming, StaticAnimation shot, WeaponCategories weaponCategory) {
		super(item, weaponCategory);
		this.rangeAnimationSet = new HashMap<LivingMotion, StaticAnimation> ();
		
		if (reload != null) {
			this.rangeAnimationSet.put(LivingMotions.RELOAD, reload);
		}
		
		if (aiming != null) {
			this.rangeAnimationSet.put(LivingMotions.AIM, aiming);
		}
		
		if (shot != null) {
			this.rangeAnimationSet.put(LivingMotions.SHOT, shot);
		}
	}
	
	@Override
	public void setConfigFileAttribute(double armorNegation1, double impact1, int maxStrikes1, double armorNegation2, double impact2, int maxStrikes2) {
		this.addStyleAttributeSimple(Styles.RANGED, armorNegation1, impact1, maxStrikes1);
	}
	
	@Override
	public Map<LivingMotion, StaticAnimation> getLivingMotionModifier(LivingEntityPatch<?> playerdata, InteractionHand hand) {
		if (hand == InteractionHand.MAIN_HAND) {
			return this.rangeAnimationSet;
		}
		
		return super.getLivingMotionModifier(playerdata, hand);
	}

	@Override
	public boolean availableOnHorse() {
		return true;
	}
	
	@Override
	public boolean canBePlacedOffhand() {
		return false;
	}
}