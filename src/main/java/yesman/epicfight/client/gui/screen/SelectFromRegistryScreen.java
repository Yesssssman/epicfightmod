package yesman.epicfight.client.gui.screen;

import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
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
	private final Consumer<T> selectCallback;
	private final Consumer<T> onSelect;
	
	public SelectFromRegistryScreen(Screen parentScreen, IForgeRegistry<T> registry, Consumer<T> selectCallback) {
		this(parentScreen, registry, (select) -> {}, selectCallback);
	}
	
	public SelectFromRegistryScreen(Screen parentScreen, IForgeRegistry<T> registry, Consumer<T> onSelect, Consumer<T> selectCallback) {
		super(Component.translatable("gui.epicfight.select", ParseUtil.makeFirstLetterToUpper(registry.getRegistryName().getPath())));
		
		this.registryList = new RegistryList(parentScreen.getMinecraft(), this.width, this.height, 36, this.height - 16, 21, registry);
		this.parentScreen = parentScreen;
		this.selectCallback = selectCallback;
		this.onSelect = onSelect;
	}
	
	@Override
	protected void init() {
		this.registryList.updateSize(this.width, this.height, 36, this.height - 32);
		
		EditBox editBox = new EditBox(this.minecraft.font, this.width / 2, 12, this.width / 2 - 12, 16, Component.literal(EpicFightMod.MODID + ":"));
		editBox.setResponder(this.registryList::applyFilter);
		
		this.addRenderableWidget(this.registryList);
		this.addRenderableWidget(editBox);
		
		this.addRenderableWidget(Button.builder(CommonComponents.GUI_OK, (button) -> {
			this.selectCallback.accept(this.registryList.getSelected().item);
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
		private final IForgeRegistry<T> registry;
		
		public RegistryList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight, IForgeRegistry<T> registry) {
			super(minecraft, width, height, y0, y1, itemHeight);
			
			this.registry = registry;
			
			for (Map.Entry<ResourceKey<T>, T> entry : registry.getEntries()) {
				this.addEntry(new RegistryEntry(entry.getValue(), entry.getKey().location().toString()));
			}
		}
		
		@Override
		public void setSelected(@Nullable RegistryEntry selEntry) {
			SelectFromRegistryScreen.this.onSelect.accept(selEntry.item);
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
			
			this.registry.getEntries().stream().filter((entry) -> StringUtil.isNullOrEmpty(keyward) ? true : entry.getKey().toString().contains(keyward))
											.map((entry) -> new RegistryEntry(entry.getValue(), entry.getKey().location().toString())) .forEach(this::addEntry);
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
						SelectFromRegistryScreen.this.selectCallback.accept(this.item);
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