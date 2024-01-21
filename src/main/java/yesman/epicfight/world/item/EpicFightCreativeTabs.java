package yesman.epicfight.world.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import yesman.epicfight.main.EpicFightMod;

public class EpicFightCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EpicFightMod.MODID);

	public static final RegistryObject<CreativeModeTab> ITEMS = TABS.register("items",
			() -> CreativeModeTab.builder()
					.title(Component.translatable("itemGroup.epicfight.items"))
					.icon(() -> new ItemStack(EpicFightItems.SKILLBOOK.get()))
					.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
					.withBackgroundLocation(new ResourceLocation(EpicFightMod.MODID, "textures/gui/container/epicfight_creative_tab.png"))
					.hideTitle()
					.displayItems((params, output) -> {
						EpicFightItems.ITEMS.getEntries().forEach(it -> {
							// FIXME: bad implement, maybe based protocol better yet.
							// ignore UCHIGATANA_SHEATH
							if (it == EpicFightItems.UCHIGATANA_SHEATH) {
								return;
							}
							// expend the skill book
							if (it == EpicFightItems.SKILLBOOK) {
								if (it.get() instanceof SkillBookItem skillbookItem) {
									skillbookItem.fillItemCategory(output::accept);
								}
								return;
							}
							output.accept(it.get());
						});
					})
					.build());
}