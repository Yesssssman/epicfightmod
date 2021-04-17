package maninthehouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Colliders;
import maninthehouse.epicfight.gamedata.Skills;
import maninthehouse.epicfight.gamedata.Sounds;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.skill.Skill;
import maninthehouse.epicfight.utils.game.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SwordCapability extends MaterialItemCapability {
	private static List<StaticAnimation> swordAttackMotion;
	private static List<StaticAnimation> dualSwordAttackMotion;

	public SwordCapability(Item item) {
		super(item, WeaponCategory.SWORD);
		if (swordAttackMotion == null) {
			swordAttackMotion = new ArrayList<StaticAnimation> ();
			swordAttackMotion.add(Animations.SWORD_AUTO_1);
			swordAttackMotion.add(Animations.SWORD_AUTO_2);
			swordAttackMotion.add(Animations.SWORD_AUTO_3);
			swordAttackMotion.add(Animations.SWORD_DASH);
			dualSwordAttackMotion = new ArrayList<StaticAnimation> ();
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_1);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_2);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_AUTO_3);
			dualSwordAttackMotion.add(Animations.SWORD_DUAL_DASH);
		}
	}
	
	@Override
	protected void registerAttribute() {
		int i = this.material.getHarvestLevel();
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(1)));
		this.addStyleAttibute(WieldStyle.ONE_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(ModAttributes.MAX_STRIKES, ModAttributes.getMaxStrikesModifier(1)));
		this.addStyleAttibute(WieldStyle.TWO_HAND, Pair.of(ModAttributes.IMPACT, ModAttributes.getImpactModifier(0.5D + 0.2D * i)));
	}
	
	@Override
	public Skill getSpecialAttack(PlayerData<?> playerdata) {
		if(this.getStyle(playerdata) == WieldStyle.ONE_HAND) {
			return Skills.SWEEPING_EDGE;
		} else {
			return Skills.DANCING_EDGE;
		}
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		if(this.getStyle(playerdata) == WieldStyle.ONE_HAND) {
			return swordAttackMotion;
		} else {
			return dualSwordAttackMotion;
		}
	}
	
	@Override
	public WieldStyle getStyle(LivingData<?> entitydata) {
		CapabilityItem item = entitydata.getHeldItemCapability(EnumHand.OFF_HAND);
		if(item != null && item.weaponCategory == WeaponCategory.SWORD) {
			return WieldStyle.TWO_HAND;
		} else {
			return WieldStyle.ONE_HAND;
		}
	}
	
	@Override
	public SoundEvent getHitSound() {
		return this.material == ToolMaterial.WOOD ? Sounds.BLUNT_HIT : Sounds.BLADE_HIT;
	}

	@Override
	public Collider getWeaponCollider() {
		return Colliders.sword;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public boolean canBeRenderedBoth(ItemStack item) {
		CapabilityItem cap = item.getCapability(ModCapabilities.CAPABILITY_ITEM, null);
		return super.canBeRenderedBoth(item) || (cap != null && cap.weaponCategory == WeaponCategory.SWORD);
	}
}