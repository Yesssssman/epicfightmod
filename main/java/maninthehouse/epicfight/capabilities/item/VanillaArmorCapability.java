package maninthehouse.epicfight.capabilities.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;

public class VanillaArmorCapability extends ArmorCapability {
	public VanillaArmorCapability(Item item) {
		super(item);
		if (item instanceof ItemArmor) {
			ItemArmor itemArmor = (ItemArmor)item;
			if(itemArmor.getArmorMaterial() instanceof ArmorMaterial) {
				switch((ArmorMaterial) itemArmor.getArmorMaterial()) {
				case LEATHER:
					this.weight = itemArmor.damageReduceAmount;
					this.stunArmor = itemArmor.damageReduceAmount * 0.25D;
					break;
				case GOLD:
					this.weight = itemArmor.damageReduceAmount * 2.0D;
					this.stunArmor = itemArmor.damageReduceAmount * 0.3D;
					break;
				case CHAIN:
					this.weight = itemArmor.damageReduceAmount * 2.5D;
					this.stunArmor = itemArmor.damageReduceAmount * 0.375D;
					break;
				case IRON:
					this.weight = itemArmor.damageReduceAmount * 3.0D;
					this.stunArmor = itemArmor.damageReduceAmount * 0.5D;
					break;
				case DIAMOND:
					this.weight = itemArmor.damageReduceAmount * 3.0D;
					this.stunArmor = itemArmor.damageReduceAmount * 0.5D;
					break;
				default:
					this.weight = 0.0D;
					this.stunArmor = 0.0D;
				}
			} else {
				this.weight = 0.0D;
				this.stunArmor = 0.0D;
			}
		}
	}
}