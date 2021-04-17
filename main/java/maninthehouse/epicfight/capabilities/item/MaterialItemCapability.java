package maninthehouse.epicfight.capabilities.item;

import java.util.ArrayList;
import java.util.List;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;

public abstract class MaterialItemCapability extends CapabilityItem {
	protected ToolMaterial material;

	protected static List<StaticAnimation> toolAttackMotion;
	protected static List<StaticAnimation> mountAttackMotion;

	static {
		toolAttackMotion = new ArrayList<StaticAnimation> ();
		toolAttackMotion.add(Animations.TOOL_AUTO_1);
		toolAttackMotion.add(Animations.TOOL_AUTO_2);
		toolAttackMotion.add(Animations.TOOL_DASH);
		mountAttackMotion = new ArrayList<StaticAnimation> ();
		mountAttackMotion.add(Animations.SWORD_MOUNT_ATTACK);
	}
	
	public MaterialItemCapability(Item item, WeaponCategory category) {
		super(item, category);
		
		if (item instanceof ItemTool) {
			this.material = ((ItemTool)item).toolMaterial;
		} else if (item instanceof ItemSword) {
			this.material = ((ItemSword)item).material;
		} else if (item instanceof ItemHoe) {
			this.material = ((ItemHoe)item).toolMaterial;
		}
		
		if (EpicFightMod.isPhysicalClient()) {
			loadClientThings();
		}
		registerAttribute();
	}
	
	@Override
	public List<StaticAnimation> getMountAttackMotion() {
		return mountAttackMotion;
	}
}