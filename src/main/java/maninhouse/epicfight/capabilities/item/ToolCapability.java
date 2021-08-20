package maninhouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.physics.Collider;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;
import net.minecraft.item.UseAction;

public abstract class ToolCapability extends CapabilityItem {
	protected IItemTier itemTier;
	protected static List<StaticAnimation> toolAttackMotion;
	protected static List<StaticAnimation> mountAttackMotion;
	
	static {
		toolAttackMotion = new ArrayList<StaticAnimation> ();
		toolAttackMotion.add(Animations.AXE_AUTO1);
		toolAttackMotion.add(Animations.AXE_AUTO2);
		toolAttackMotion.add(Animations.AXE_DASH);
		toolAttackMotion.add(Animations.AXE_AIRSLASH);
		mountAttackMotion = new ArrayList<StaticAnimation> ();
		mountAttackMotion.add(Animations.SWORD_MOUNT_ATTACK);
	}
	
	public ToolCapability(Item item, WeaponCategory category) {
		super(item, category);
		this.itemTier = ((TieredItem)item).getTier();
		if (EpicFightMod.isPhysicalClient()) {
			loadClientThings();
		}
		registerAttribute();
	}
	
	@Override
	public List<StaticAnimation> getAutoAttckMotion(PlayerData<?> playerdata) {
		return toolAttackMotion;
	}
	
	@Override
	public Collider getWeaponCollider() {
		return Colliders.tools;
	}
	
	@Override
	public List<StaticAnimation> getMountAttackMotion() {
		return mountAttackMotion;
	}
	
	@Override
	public UseAction getUseAction(PlayerData<?> player) {
		return UseAction.BLOCK;
	}
	
	@Override
	public boolean canUsedOffhandAlone() {
		return false;
	}
}