package maninthehouse.epicfight.effects;

import java.util.UUID;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class ModEffects {
	public static final Potion STUN_IMMUNITY = new ModEffect(false, 16758016, 0, 0, "stun_immunity").registerPotionAttributeModifier(SharedMonsterAttributes.MAX_HEALTH,
			UUID.randomUUID().toString(), 0.0D, 0).registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, UUID.randomUUID().toString(), 1.0D, 0);
	
	public static final PotionType STUN_IMMUNITY_TYPE = new PotionType("stun_immunity", new PotionEffect[] { new PotionEffect(STUN_IMMUNITY, 300) }).setRegistryName("stun_immunity");
	public static final PotionType STUN_IMMUNITY_TYPE_LONG = new PotionType("stun_immunity", new PotionEffect[] { new PotionEffect(STUN_IMMUNITY, 800) }).setRegistryName("stun_immunity_long");
	
	public static void registerModPotions() {
		ForgeRegistries.POTIONS.register(STUN_IMMUNITY);
		
		ForgeRegistries.POTION_TYPES.register(STUN_IMMUNITY_TYPE);
		ForgeRegistries.POTION_TYPES.register(STUN_IMMUNITY_TYPE_LONG);
		
		PotionHelper.addMix(PotionTypes.AWKWARD, Items.NETHER_STAR, STUN_IMMUNITY_TYPE);
		PotionHelper.addMix(STUN_IMMUNITY_TYPE, Items.REDSTONE, STUN_IMMUNITY_TYPE_LONG);
	}
}