package yesman.epicfight.client.gui.screen;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.components.tabs.GridLayoutTab;
import net.minecraft.client.gui.components.tabs.Tab;
import net.minecraft.client.gui.components.tabs.TabManager;
import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class DatapackEditScreen extends Screen {
	public static final Component GUI_EXPORT = Component.translatable("gui.epicfight.export");
	
	private GridLayout bottomButtons;
	private TabNavigationBar tabNavigationBar;
	//private File datapackFile;
	protected final Screen lastScreen;
	private final TabManager tabManager = new TabManager(this::addRenderableWidget, (p_267853_) -> {
		this.removeWidget(p_267853_);
	}) {
		public void setCurrentTab(Tab tab, boolean playSound) {
			if (this.getCurrentTab() instanceof DatapackTab<?> datapackTab) {
				DatapackEditScreen.this.removeWidget(datapackTab.packItemList);
				DatapackEditScreen.this.removeWidget(datapackTab.inputComponentsList);
			}
			
			super.setCurrentTab(tab, playSound);
			
			if (tab instanceof DatapackTab<?> datapackTab) {
				DatapackEditScreen.this.addRenderableWidget(datapackTab.packItemList);
				DatapackEditScreen.this.addRenderableWidget(datapackTab.inputComponentsList);
			}
		}
	};
	
	public DatapackEditScreen(Screen parentScreen) {
		super(Component.translatable("gui." + EpicFightMod.MODID + ".datapack_edit"));
		
		this.lastScreen = parentScreen;
	}
	
	@Override
	protected void init() {
		this.tabNavigationBar = TabNavigationBar.builder(this.tabManager, this.width).addTabs(new DatapackEditScreen.WeaponTypeTab(), new DatapackEditScreen.ItemCapabilityTab(), new DatapackEditScreen.MobPatchTab()).build();
		this.tabNavigationBar.selectTab(0, false);
		
	    this.addRenderableWidget(this.tabNavigationBar);
	    this.bottomButtons = (new GridLayout()).columnSpacing(10);
	    
		GridLayout.RowHelper gridlayout$rowhelper = this.bottomButtons.createRowHelper(2);
		gridlayout$rowhelper.addChild(Button.builder(GUI_EXPORT, (button) -> {
			//this.onCreate();
		}).build());
		gridlayout$rowhelper.addChild(Button.builder(CommonComponents.GUI_CANCEL, (button) -> {
			this.minecraft.setScreen(this.lastScreen);
		}).build());
		
		this.bottomButtons.visitWidgets((button) -> {
			button.setTabOrderGroup(1);
			this.addRenderableWidget(button);
		});
		
		this.repositionElements();
	}
	
	@Override
	public void repositionElements() {
		if (this.tabNavigationBar != null && this.bottomButtons != null) {
			this.tabNavigationBar.setWidth(this.width);
			this.tabNavigationBar.arrangeElements();
			this.bottomButtons.arrangeElements();
			FrameLayout.centerInRectangle(this.bottomButtons, 0, this.height - 36, this.width, 36);
			int i = this.tabNavigationBar.getRectangle().bottom();
			ScreenRectangle screenrectangle = new ScreenRectangle(0, i, this.width, this.bottomButtons.getY() - i);
			this.tabManager.setTabArea(screenrectangle);
		}
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.lastScreen);
	}
	
	@Override
	public boolean keyPressed(int keycode, int p_100876_, int p_100877_) {
		if (this.tabNavigationBar.keyPressed(keycode)) {
			return true;
		} else if (super.keyPressed(keycode, p_100876_, p_100877_)) {
			return true;
		} else if (keycode != 257 && keycode != 335) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		guiGraphics.blit(CreateWorldScreen.FOOTER_SEPERATOR, 0, Mth.roundToward(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
	
	class DatapackTab<T> extends GridLayoutTab {
		private PackItemList packItemList;
		private InputComponentsList inputComponentsList;
		private final IForgeRegistry<T> registry;
		
		@SuppressWarnings("unchecked")
		public DatapackTab(Component title, @Nullable IForgeRegistry<T> registry) {
			super(title);
			
			this.registry = registry;
			this.packItemList = new PackItemList(0, 0, 0, 0, 26);
			this.packItemList.setRenderTopAndBottom(false);
			this.inputComponentsList = new InputComponentsList(0, 0, 0, 0, 26);
			this.inputComponentsList.setRenderTopAndBottom(false);
			
			GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(2).createRowHelper(2);
			
			gridlayout$rowhelper.addChild(Button.builder(Component.literal("+"), (button) -> {
				if (registry != null) {
					DatapackEditScreen.this.minecraft.setScreen(new SelectRegistryScreen<>(DatapackEditScreen.this, registry, (selItem) -> {
						this.packItemList.addNewEntry(selItem);
					}));
				} else {
					this.packItemList.addNewEntry((T)new ResourceLocation(EpicFightMod.MODID + ":"));
				}
			}).pos(0, 0).size(12, 12).build());
			
			gridlayout$rowhelper.addChild(Button.builder(Component.literal("x"), (button) -> {
				this.packItemList.children().remove(this.packItemList.getSelected());
			}).pos(0, 0).size(12, 12).build());
		}
		
		@Override
		public void doLayout(ScreenRectangle screenRectangle) {
			this.layout.arrangeElements();
			
			this.layout.setX(132);
			this.layout.setY(screenRectangle.top());
			
			this.packItemList.setRenderTopAndBottom(false);
			this.inputComponentsList.setRenderTopAndBottom(false);
			
			this.packItemList.updateSize(150, screenRectangle.height(), screenRectangle.top() + 14, screenRectangle.height() + 7);
			this.inputComponentsList.updateSize(screenRectangle.width() - 172, screenRectangle.height(), screenRectangle.top() + 14, screenRectangle.height() + 7);
			
			this.packItemList.setLeftPos(8);
			this.inputComponentsList.setLeftPos(164);
		}
		
		class PackItemList extends ObjectSelectionList<PackItemList.PackItemEntry> {
			public PackItemList(int width, int height, int y0, int y1, int itemHeight) {
				super(DatapackEditScreen.this.minecraft, width, height, y0, y1, itemHeight);
			}
			
			public int addNewEntry(T packItem) {
				return this.addEntry(new PackItemEntry(packItem));
			}
			
			@Override
			public int getRowWidth() {
				return this.width;
			}

			@Override
			protected int getScrollbarPosition() {
				return this.x1 - 6;
			}
			
			class PackItemEntry extends ObjectSelectionList.Entry<PackItemList.PackItemEntry> {
				private final T item;
				private final String name;
				
				public PackItemEntry(T item) {
					this.item = item;
					this.name = this.getRegistryName(item);
				}
				
				@Override
				public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
					guiGraphics.drawString(DatapackEditScreen.this.minecraft.font, this.name, left + 2, top + 5, 16777215, false);
				}
				
				@Override
				public Component getNarration() {
					return Component.translatable("narrator.select");
				}
				
				@Override
				public boolean mouseClicked(double mouseX, double mouseY, int button) {
					if (button == 0) {
						PackItemList.this.setSelected(this);
						return true;
					} else {
						return false;
					}
				}
				
				private String getRegistryName(T item) {
					if (item instanceof ResourceLocation resourcelocation) {
						return resourcelocation.toString();
					} else {
						return DatapackTab.this.registry.getKey(item).toString();
					}
				}
			}
		}
		
		class InputComponentsList extends ContainerObjectSelectionList<InputComponentsList.InputComponentsEntry> {
			public InputComponentsList(int width, int height, int y0, int y1, int itemHeight) {
				super(DatapackEditScreen.this.minecraft, width, height, y0, y1, itemHeight);
			}
			
			@Override
			public int getRowWidth() {
				return this.width;
			}

			@Override
			protected int getScrollbarPosition() {
				return this.x1 - 6;
			}
			
			class InputComponentsEntry extends ContainerObjectSelectionList.Entry<InputComponentsList.InputComponentsEntry> {
				@Override
				public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
					
				}
				
				@Override
				public List<? extends GuiEventListener> children() {
					return null;
				}
				
				@Override
				public List<? extends NarratableEntry> narratables() {
					return null;
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class WeaponTypeTab extends DatapackTab<ResourceLocation> {
		public WeaponTypeTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.weapon_type"), null);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class ItemCapabilityTab extends DatapackTab<Item> {
		public ItemCapabilityTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.item_capability"), ForgeRegistries.ITEMS);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class MobPatchTab extends DatapackTab<EntityType<?>> {
		public MobPatchTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.mob_patch"), ForgeRegistries.ENTITY_TYPES);
		}
	}
}