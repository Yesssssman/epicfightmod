package yesman.epicfight.main;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.IExtensionPoint;
import yesman.epicfight.world.item.EpicFightCreativeTabs;

/**
 * @Param skillBookCreativeTab : decides a creative tab that should display the skills belong to the mod {@link EpicFightCreativeTabs}}
 */
public record EpicFightExtensions(CreativeModeTab skillBookCreativeTab) implements IExtensionPoint<EpicFightExtensions> {
}