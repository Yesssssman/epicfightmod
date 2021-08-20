package maninhouse.epicfight.capabilities.item;

import net.minecraft.item.ArmorItem;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;

public class VanillaArmorCapability extends ArmorCapability {
	public VanillaArmorCapability(Item item) {
		super(item);
		
		if (item instanceof ArmorItem) {
			ArmorItem armorItem = ((ArmorItem)item);
			if(armorItem.getArmorMaterial() instanceof ArmorMaterial) {
				switch((ArmorMaterial) armorItem.getArmorMaterial()) {
				case LEATHER:
					this.weight = armorItem.getDamageReduceAmount();
					this.stunArmor = armorItem.getDamageReduceAmount() * 0.25D;
					break;
				case GOLD:
					this.weight = armorItem.getDamageReduceAmount() * 2.0D;
					this.stunArmor = armorItem.getDamageReduceAmount() * 0.3D;
					break;
				case CHAIN:
					this.weight = armorItem.getDamageReduceAmount() * 2.5D;
					this.stunArmor = armorItem.getDamageReduceAmount() * 0.375D;
					break;
				case IRON:
					this.weight = armorItem.getDamageReduceAmount() * 3.0D;
					this.stunArmor = armorItem.getDamageReduceAmount() * 0.5D;
					break;
				case DIAMOND:
					this.weight = armorItem.getDamageReduceAmount() * 3.0D;
					this.stunArmor = armorItem.getDamageReduceAmount() * 0.5D;
					break;
				case NETHERITE:
					this.weight = armorItem.getDamageReduceAmount() * 3.2D;
					this.stunArmor = armorItem.getDamageReduceAmount() * 0.75D;
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