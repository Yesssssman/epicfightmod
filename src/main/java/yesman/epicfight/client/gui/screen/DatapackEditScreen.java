package yesman.epicfight.client.gui.screen;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.ResourceLocationException;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
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
		protected PackItemList packItemList;
		protected InputComponentsList inputComponentsList;
		protected final IForgeRegistry<T> registry;
		
		@SuppressWarnings("unchecked")
		public DatapackTab(Component title, @Nullable IForgeRegistry<T> registry) {
			super(title);
			
			ScreenRectangle screenRect = DatapackEditScreen.this.getRectangle();
			
			this.registry = registry;
			this.packItemList = new PackItemList(150, screenRect.height(), screenRect.top() + 14, screenRect.height() + 7, 26);
			this.packItemList.setRenderTopAndBottom(false);
			this.packItemList.setLeftPos(8);
			
			this.inputComponentsList = new InputComponentsList(screenRect.width() - 172, screenRect.height(), screenRect.top() + 14, screenRect.height() + 7, 26);
			this.inputComponentsList.setRenderTopAndBottom(false);
			this.inputComponentsList.setLeftPos(164);
			
			GridLayout.RowHelper gridlayout$rowhelper = this.layout.rowSpacing(2).createRowHelper(2);
			
			gridlayout$rowhelper.addChild(Button.builder(Component.literal("+"), (button) -> {
				if (registry != null) {
					DatapackEditScreen.this.minecraft.setScreen(new SelectRegistryScreen<>(DatapackEditScreen.this, registry, (selItem) -> {
						PackItemList.PackItemEntry entry = this.packItemList.addEntry(selItem);
						this.packItemList.enableEditBox(entry);
					}));
				} else {
					PackItemList.PackItemEntry entry = this.packItemList.addEntry((T)new ResourceLocation(EpicFightMod.MODID + ":"));
					this.packItemList.enableEditBox(entry);
				}
			}).pos(0, 0).size(12, 12).build());
			
			gridlayout$rowhelper.addChild(Button.builder(Component.literal("x"), (button) -> {
				int removeIdx = this.packItemList.children().indexOf(this.packItemList.getSelected());
				
				if (removeIdx > 0) {
					removeIdx--;
				}
				
				this.packItemList.removeEntry(this.packItemList.getSelected());
				this.packItemList.disableEditBox();
				
				if (removeIdx > -1) {
					this.packItemList.setSelected(this.packItemList.children().get(removeIdx));
				}
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
		
		@Override
		public void tick() {
			this.packItemList.editBox.tick();
		}

		class PackItemList extends ObjectSelectionList<PackItemList.PackItemEntry> {
			private EditBox editBox;
			private PackItemEntry editingEntry;
			
			public PackItemList(int width, int height, int y0, int y1, int itemHeight) {
				super(DatapackEditScreen.this.minecraft, width, height, y0, y1, itemHeight);
				
				this.editBox = new EditBox(DatapackEditScreen.this.minecraft.font, 0, 0, 144, 20, Component.literal(""));
				this.editBox.setEditable(true);
			}
			
			public PackItemEntry addEntry(T packItem) {
				PackItemEntry newItem = new PackItemEntry(packItem);
				this.addEntry(newItem);
				
				return newItem;
			}
			
			private void enableEditBox(PackItemList.PackItemEntry entry) {
				this.setSelected(entry);
				this.editBox.setResponder(entry::setName);
				this.editBox.setValue(entry.name);
				this.editingEntry = entry;
				
				DatapackEditScreen.this.setFocused(this.editBox);
			}
			
			private void disableEditBox() {
				this.editingEntry = null;
			}
			
			@Override
			public boolean removeEntry(PackItemList.PackItemEntry packItem) {
				if (this.editingEntry == packItem) {
					this.editingEntry = null;
					this.disableEditBox();
				}
				
				return super.removeEntry(packItem);
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
				private T item;
				private String name;
				
				public PackItemEntry(T item) {
					this.item = item;
					this.name = this.getRegistryName(item);
				}
				
				@Override
				public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
					guiGraphics.drawString(DatapackEditScreen.this.minecraft.font, this.name, left + 2, top + 7, 16777215, false);
					
					if (PackItemList.this.editingEntry == this) {
						PackItemList.this.editBox.setPosition(left + 1, top + 1);
						PackItemList.this.editBox.render(guiGraphics, mouseX, mouseY, partialTicks);
					}
				}
				
				@Override
				public Component getNarration() {
					return Component.translatable("narrator.select");
				}
				
				@Override
				public boolean mouseClicked(double mouseX, double mouseY, int button) {
					if (button == 0) {
						boolean returnVal = true;
						
						if (DatapackTab.this.registry == null) {
							if (PackItemList.this.getSelected() == this) {
								if (PackItemList.this.editingEntry == null) {
									PackItemList.this.enableEditBox(this);
									returnVal = false;
								} else {
									PackItemList.this.disableEditBox();
								}
							} else {
								PackItemList.this.disableEditBox();
							}
						}
						
						PackItemList.this.setSelected(this);
						return returnVal;
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
				
				@SuppressWarnings("unchecked")
				private void setName(String name) {
					ResourceLocation rl;
					
					try {
						rl = new ResourceLocation(name);
					} catch (ResourceLocationException e) {
						int colonPos = name.indexOf(":");
						String namespace = colonPos < 0 ? "minecraft" : name.substring(0, colonPos).replaceAll("[^a-z0-9/._-]", "");
						String path = name.substring(colonPos < 0 ? 0 : colonPos, name.length()).replaceAll("[^a-z0-9/._-]", "");
						rl = new ResourceLocation(namespace, path);
					}
					
					this.name = rl.toString();
					this.item = (T) rl;
				}
			}
		}
		
		class InputComponentsList extends ContainerObjectSelectionList<InputComponentsList.InputComponentsEntry> {
			private InputComponentsList.InputComponentsEntry lastEntry;
			
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
			
			public void addComponentInThisLine(AbstractWidget inputWidget, int spacing) {
				int xPos;
				
				if (this.lastEntry.children.size() == 0) {
					xPos = this.x0 + spacing;
				} else {
					AbstractWidget lastWidget = this.lastEntry.children.get(this.lastEntry.children.size());
					xPos = lastWidget.getX() + lastWidget.getWidth() + spacing;
				}
				
				inputWidget.setX(xPos);
				
				this.lastEntry.children.add(inputWidget);
			}
			
			public void newLine() {
				this.lastEntry = new InputComponentsList.InputComponentsEntry();
				
				this.addEntry(this.lastEntry);
			}
			
			class InputComponentsEntry extends ContainerObjectSelectionList.Entry<InputComponentsList.InputComponentsEntry> {
				final List<AbstractWidget> children = Lists.newArrayList();
				
				@Override
				public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
					for (AbstractWidget widget : this.children) {
						widget.setY(top);
						widget.render(guiGraphics, mouseX, mouseY, partialTicks);
					}
				}
				
				@Override
				public List<? extends GuiEventListener> children() {
					return this.children;
				}
				
				@Override
				public List<? extends NarratableEntry> narratables() {
					return this.children;
				}
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class WeaponTypeTab extends DatapackTab<ResourceLocation> {
		public WeaponTypeTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.weapon_type"), null);
			
			this.inputComponentsList.newLine();
			this.inputComponentsList.addComponentInThisLine(Button.builder(Component.literal("a"), (button) -> {}).size(20, 20).build(), 0);
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