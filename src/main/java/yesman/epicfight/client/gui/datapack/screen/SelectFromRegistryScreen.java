package yesman.epicfight.client.gui.datapack.screen;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Maps;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.IForgeRegistry;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class SelectFromRegistryScreen<T> extends Screen {
	private final RegistryList registryList;
	private final Screen parentScreen;
	private final Consumer<T> onPressRow;
	private final BiConsumer<String, T> onAccept;
	
	public SelectFromRegistryScreen(Screen parentScreen, IForgeRegistry<T> registry, Consumer<T> onPressRow) {
		this(parentScreen, registry, onPressRow, (item) -> true);
	}
	
	public SelectFromRegistryScreen(Screen parentScreen, IForgeRegistry<T> registry, Consumer<T> onPressRow, Predicate<T> filter) {
		this(parentScreen, registry, (name, select) -> {}, onPressRow, filter);
	}
	
	public SelectFromRegistryScreen(Screen parentScreen, IForgeRegistry<T> registry, BiConsumer<String, T> onAccept, Consumer<T> selectCallback, Predicate<T> filter) {
		super(Component.translatable("gui.epicfight.select", ParseUtil.snakeToSpacedCamel(registry.getRegistryName().getPath())));
		
		final Map<ResourceLocation, T> filteredItems = Maps.newHashMap();
		registry.getValues().stream().filter(filter).forEach((value) -> filteredItems.put(registry.getKey(value), value));
		
		this.registryList = new RegistryList(parentScreen.getMinecraft(), this.width, this.height, 36, this.height - 16, 21, filteredItems);
		this.parentScreen = parentScreen;
		this.onPressRow = selectCallback;
		this.onAccept = onAccept;
	}
	
	public SelectFromRegistryScreen(Screen parentScreen, Set<Map.Entry<ResourceLocation, T>> entries, String title, BiConsumer<String, T> onAccept, Consumer<T> selectCallback, Predicate<T> filter) {
		super(Component.translatable("gui.epicfight.select", ParseUtil.snakeToSpacedCamel(title)));
		
		Map<ResourceLocation, T> filteredItems = entries.stream().filter((entry) -> filter.test(entry.getValue())).reduce(Maps.newHashMap(), (map, element) -> {
			map.put(element.getKey(), element.getValue());
			return map;
		}, (map1, map2) -> {
			map1.putAll(map2);
			return map1;
		});
		
		this.registryList = new RegistryList(parentScreen.getMinecraft(), this.width, this.height, 36, this.height - 16, 21, filteredItems);
		this.parentScreen = parentScreen;
		this.onPressRow = selectCallback;
		this.onAccept = onAccept;
	}
	
	@Override
	protected void init() {
		this.registryList.updateSize(this.width, this.height, 36, this.height - 32);
		
		EditBox editBox = new EditBox(this.minecraft.font, this.width / 2, 12, this.width / 2 - 12, 16, Component.literal(EpicFightMod.MODID + ":"));
		editBox.setResponder(this.registryList::applyFilter);
		
		this.addRenderableWidget(this.registryList);
		this.addRenderableWidget(editBox);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			this.onPressRow.accept(this.registryList.getSelected() == null ? null : this.registryList.getSelected().item);
			this.minecraft.setScreen(this.parentScreen);
		}).pos(this.width / 2 - 162, this.height - 28).size(160, 21).build());
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(this.parentScreen);
		}).pos(this.width / 2 + 2, this.height - 28).size(160, 21).build());
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderDirtBackground(guiGraphics);
		guiGraphics.drawString(this.font, this.title, 20, 16, 16777215);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	@OnlyIn(Dist.CLIENT)
	class RegistryList extends ObjectSelectionList<RegistryList.RegistryEntry> {
		private final Map<ResourceLocation, T> registry;
		
		public RegistryList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight, Map<ResourceLocation, T> registry) {
			super(minecraft, width, height, y0, y1, itemHeight);
			
			this.registry = registry;
			
			registry.entrySet().stream().sorted((entry1, entry2) -> entry1.getKey().toString().compareTo(entry2.getKey().toString())).forEach((entry) -> this.addEntry(new RegistryEntry(entry.getValue(), entry.getKey().toString())));
		}
		
		@Override
		public void setSelected(@Nullable RegistryEntry selEntry) {
			SelectFromRegistryScreen.this.onAccept.accept(selEntry.name, selEntry.item);
			super.setSelected(selEntry);
		}

		@Override
		public int getRowWidth() {
			return this.width;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		public void applyFilter(String keyward) {
			this.setScrollAmount(0.0D);
			this.children().clear();
			
			this.registry.entrySet().stream().sorted((entry1, entry2) -> entry1.getKey().toString().compareTo(entry2.getKey().toString())).filter((entry) -> StringUtil.isNullOrEmpty(keyward) ? true : entry.getKey().toString().contains(keyward))
												.map((entry) -> new RegistryEntry(entry.getValue(), entry.getKey().toString())).forEach(this::addEntry);
		}
		
		@OnlyIn(Dist.CLIENT)
		class RegistryEntry extends ObjectSelectionList.Entry<RegistryList.RegistryEntry> {
			private final T item;
			private final String name;
			
			public RegistryEntry(T item, String name) {
				this.item = item;
				this.name = name;
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				guiGraphics.drawString(SelectFromRegistryScreen.this.minecraft.font, this.name, left + 25, top + 5, 16777215, false);
			}
			
			@Override
			public Component getNarration() {
				return Component.translatable("narrator.select");
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					if (RegistryList.this.getSelected() == this) {
						SelectFromRegistryScreen.this.onPressRow.accept(this.item);
						SelectFromRegistryScreen.this.minecraft.setScreen(SelectFromRegistryScreen.this.parentScreen);
						return true;
					}
					
					RegistryList.this.setSelected(this);
					
					return true;
				} else {
					return false;
				}
			}
		}
	}
}