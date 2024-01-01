package yesman.epicfight.client.gui.screen;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.registries.IForgeRegistry;

public class SelectRegistryScreen<T> extends Screen {
	private final DatapackEditScreen lastScreen;
	private final IForgeRegistry<T> registry;
	private final Consumer<T> parentCallback;
	
	public SelectRegistryScreen(DatapackEditScreen lastScreen, IForgeRegistry<T> registry, Consumer<T> parentCallback) {
		super(Component.literal(registry.getRegistryName().getPath()));
		
		this.lastScreen = lastScreen;
		this.registry = registry;
		this.parentCallback = parentCallback;
	}
	
	@Override
	protected void init() {
		this.lastScreen.width = this.width;
		this.lastScreen.height = this.height;
		
		this.lastScreen.repositionElements();
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.lastScreen.render(guiGraphics, 0, 0, partialTick);
		guiGraphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
		MinecraftForge.EVENT_BUS.post(new ScreenEvent.BackgroundRendered(this, guiGraphics));
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.lastScreen);
	}
	
	class RegistryList extends ObjectSelectionList<RegistryList.RegistryEntry> {
		public RegistryList(int width, int height, int y0, int y1, int itemHeight) {
			super(SelectRegistryScreen.this.minecraft, width, height, y0, y1, itemHeight);
		}
		
		@Override
		public int getRowWidth() {
			return this.width;
		}

		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		class RegistryEntry extends ObjectSelectionList.Entry<RegistryList.RegistryEntry> {
			private final T item;
			private final String name;
			
			public RegistryEntry(T item) {
				this.item = item;
				this.name = SelectRegistryScreen.this.registry.getKey(this.item).toString();
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				guiGraphics.drawString(RegistryList.this.minecraft.font, this.name, left + 30, top + 5, 16777215, false);
			}
			
			@Override
			public Component getNarration() {
				return Component.translatable("narrator.select");
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					if (RegistryList.this.getSelected() == this) {
						SelectRegistryScreen.this.parentCallback.accept(this.item);
						SelectRegistryScreen.this.minecraft.setScreen(SelectRegistryScreen.this.lastScreen);
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