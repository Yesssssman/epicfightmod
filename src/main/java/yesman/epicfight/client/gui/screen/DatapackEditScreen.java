package yesman.epicfight.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import org.apache.commons.compress.utils.Lists;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
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
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.component.ComboBox;
import yesman.epicfight.client.gui.component.Grid;
import yesman.epicfight.client.gui.component.PopupBox;
import yesman.epicfight.client.gui.component.PopupBox.SoundPopupBox;
import yesman.epicfight.client.gui.component.ResizableButton;
import yesman.epicfight.client.gui.component.ResizableComponent;
import yesman.epicfight.client.gui.component.ResizableComponent.HorizontalSizing;
import yesman.epicfight.client.gui.component.ResizableEditBox;
import yesman.epicfight.client.gui.component.Static;
import yesman.epicfight.client.gui.screen.DatapackEditScreen.DatapackTab.InputComponentsList.InputComponentsEntry;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

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
		@Override
		public void setCurrentTab(Tab tab, boolean playSound) {
			if (this.getCurrentTab() instanceof DatapackTab<?> datapackTab) {
				DatapackEditScreen.this.removeWidget(datapackTab.grid);
				DatapackEditScreen.this.removeWidget(datapackTab.inputComponentsList);
			}
			
			super.setCurrentTab(tab, playSound);
			
			if (tab instanceof DatapackTab<?> datapackTab) {
				DatapackEditScreen.this.addRenderableWidget(datapackTab.grid);
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
	public void setFocused(@Nullable GuiEventListener target) {
		if (this.getFocused() == target) {
			return;
		}
		
		super.setFocused(target);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderBackground(guiGraphics);
		guiGraphics.blit(CreateWorldScreen.FOOTER_SEPERATOR, 0, Mth.roundToward(this.height - 36 - 2, 2), 0.0F, 0.0F, this.width, 2, 32, 2);
		super.render(guiGraphics, mouseX, mouseY, partialTick);
	}
	
	@OnlyIn(Dist.CLIENT)
	class DatapackTab<T> extends GridLayoutTab {
		protected Grid grid;
		protected InputComponentsList inputComponentsList;
		protected final IForgeRegistry<T> registry;
		
		public DatapackTab(Component title, @Nullable IForgeRegistry<T> registry) {
			super(title);
			
			ScreenRectangle screenRect = DatapackEditScreen.this.getRectangle();
			
			this.grid = Grid.builder(DatapackEditScreen.this)
							.xy1(8, screenRect.top() + 14)
							.xy2(150, screenRect.height() - screenRect.top() - 7)
							.rowHeight(26)
							.rowEditable(true)
							.transparentBackground(true)
							.addEditboxColumn("pack_item", EpicFightMod.MODID + ":", true, 150)
							.onAddPress((grid, button) -> {
								if (registry != null) {
									DatapackEditScreen.this.minecraft.setScreen(new SelectFromRegistryScreen<>(DatapackEditScreen.this, registry, (selItem) -> {
										int rowposition = grid.addRow();
										grid.setGridFocus(rowposition, "pack_item");
									}));
								} else {
									int rowposition = grid.addRowWithDefaultValues("pack_item", EpicFightMod.MODID + ":");
									grid.setGridFocus(rowposition, "pack_item");
								}
								
								DatapackEditScreen.this.setFocused(grid);
							})
							.onRemovePress((grid, button) -> {
								int rowposition = grid.removeRow();
								
								if (rowposition >= 0) {
									grid.setSelected(rowposition);
								}
							})
							.buttonHorizontalSizing(HorizontalSizing.LEFT_WIDTH)
							.build();
			
			this.registry = registry;
			this.inputComponentsList = new InputComponentsList(screenRect.width() - 172, screenRect.height(), screenRect.top() + 14, screenRect.height() + 7, 30);
			this.inputComponentsList.setRenderTopAndBottom(false);
			this.inputComponentsList.setLeftPos(164);
		}
		
		@Override
		public void doLayout(ScreenRectangle screenRectangle) {
			this.layout.arrangeElements();
			this.layout.setY(screenRectangle.top());
			
			this.grid.updateSize(150, screenRectangle.height(), screenRectangle.top() + 14, screenRectangle.height() + 7);
			this.inputComponentsList.updateSize(screenRectangle.width() - 172, screenRectangle.height(), screenRectangle.top() + 14, screenRectangle.height() + 7);
			
			this.grid.setLeftPos(8);
			this.inputComponentsList.setLeftPos(164);
		}
		
		@Override
		public void tick() {
			this.grid.tick();
		}
		
		@OnlyIn(Dist.CLIENT)
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
			
			public int nextStart(int spacing) {
				int xPos;
				
				if (this.lastEntry.children.size() == 0) {
					xPos = this.x0;
				} else {
					ResizableComponent lastWidget = this.lastEntry.children.get(this.lastEntry.children.size() - 1);
					xPos = lastWidget.getX() + lastWidget.getWidth();
				}
				
				return xPos + spacing;
			}
			
			public void addComponentCurrentRow(ResizableComponent inputWidget) {
				this.lastEntry.children.add(inputWidget);
			}
			
			public void newRow() {
				this.lastEntry = new InputComponentsList.InputComponentsEntry();
				
				this.addEntry(this.lastEntry);
			}
			
			@Override
			public boolean mouseClicked(double x, double y, int button) {
				if (!this.isMouseOver(x, y)) {
					return false;
				}
				
				for (int i = 0; i < this.children().size(); i++) {
					InputComponentsEntry entry = this.children().get(i);
					int j1 = this.getRowTop(i);
					int k1 = this.getRowBottom(i);
					
					if (k1 >= this.y0 && j1 <= this.y1) {
						if (entry.getChildAt(x, y).filter((component) -> {
								if (component.mouseClicked(x, y, button)) {
									DatapackEditScreen.this.setFocused(component);
									return true;
								}
								return false;
							}).isPresent()) {
							return false;
						}
					}
				}
				
				return false;
			}
			
			@Override
			public boolean mouseScrolled(double x, double y, double amount) {
				for (int i = 0; i < this.children().size(); i++) {
					InputComponentsEntry entry = this.children().get(i);
					int j1 = this.getRowTop(i);
					int k1 = this.getRowBottom(i);
					
					if (k1 >= this.y0 && j1 <= this.y1) {
						if (entry.getChildAt(x, y).filter((component) -> component.mouseScrolled(x, y, amount)).isPresent()) {
							return true;
						}
					}
				}
				
				return super.mouseScrolled(x, y, amount);
			}
			
			@OnlyIn(Dist.CLIENT)
			class InputComponentsEntry extends ContainerObjectSelectionList.Entry<InputComponentsList.InputComponentsEntry> {
				final List<ResizableComponent> children = Lists.newArrayList();
				
				@Override
				public Optional<GuiEventListener> getChildAt(double x, double y) {
					for (GuiEventListener guieventlistener : this.children()) {
						if (guieventlistener.isMouseOver(x, y)) {
							return Optional.of(guieventlistener);
						}
					}
					
					return Optional.empty();
				}

				@Override
				public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
					for (ResizableComponent widget : this.children) {
						widget.relocateY(top + InputComponentsList.this.itemHeight / 2 - widget.getHeight() / 2);
						
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
			
			Screen parentScreen = DatapackEditScreen.this;
			Font font = DatapackEditScreen.this.font;
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Category")));
			this.inputComponentsList.addComponentCurrentRow(new ComboBox<>(parentScreen, parentScreen.getMinecraft().font, this.inputComponentsList.nextStart(5), 124, 100, 15, HorizontalSizing.LEFT_WIDTH, null, 8,
																			Component.translatable("datapack_edit.weapon_type.category"),
																			new ArrayList<>(WeaponCategory.ENUM_MANAGER.universalValues()), (e) -> ParseUtil.makeFirstLetterToUpper(e.toString())));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Hit Particle")));
			this.inputComponentsList.addComponentCurrentRow(new PopupBox<>(parentScreen, font, this.inputComponentsList.nextStart(5), 30, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.weapon_type.hit_particle"), ForgeRegistries.PARTICLE_TYPES));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Hit Sound")));
			this.inputComponentsList.addComponentCurrentRow(new SoundPopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 30, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.weapon_type.hit_sound")));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Swing Sound")));
			this.inputComponentsList.addComponentCurrentRow(new SoundPopupBox(parentScreen, font, this.inputComponentsList.nextStart(5), 30, 130, 15, HorizontalSizing.LEFT_RIGHT, null,
																			Component.translatable("datapack_edit.weapon_type.swing_sound")));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Styles")));
			this.inputComponentsList.addComponentCurrentRow(ResizableButton.builder(Component.literal("..."), (button) -> {}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Offhand Validator")));
			this.inputComponentsList.addComponentCurrentRow(ResizableButton.builder(Component.literal("..."), (button) -> {}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Collider")));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 0, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Count")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 40, 15, Component.translatable("datapack_edit.weapon_type.number_of_colliders"), HorizontalSizing.LEFT_WIDTH, null));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 0, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Center")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 0, 8, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 30, 15, Component.translatable("datapack_edit.weapon_type.center.x"), HorizontalSizing.LEFT_WIDTH, null));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 0, 8, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 30, 15, Component.translatable("datapack_edit.weapon_type.center.y"), HorizontalSizing.LEFT_WIDTH, null));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 0, 8, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 30, 15, Component.translatable("datapack_edit.weapon_type.center.z"), HorizontalSizing.LEFT_WIDTH, null));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(20), 0, 40, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Size")));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(5), 0, 8, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("X: ")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 30, 15, Component.translatable("datapack_edit.weapon_type.size.x"), HorizontalSizing.LEFT_WIDTH, null));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 0, 8, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Y: ")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 30, 15, Component.translatable("datapack_edit.weapon_type.size.y"), HorizontalSizing.LEFT_WIDTH, null));
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(8), 0, 8, 15, HorizontalSizing.LEFT_WIDTH, null, Component.literal("Z: ")));
			this.inputComponentsList.addComponentCurrentRow(new ResizableEditBox(font, this.inputComponentsList.nextStart(5), 0, 30, 15, Component.translatable("datapack_edit.weapon_type.size.z"), HorizontalSizing.LEFT_WIDTH, null));
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Combos")));
			this.inputComponentsList.addComponentCurrentRow(ResizableButton.builder(Component.literal("..."), (button) -> {}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Innate Skill")));
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			
			this.inputComponentsList.addComponentCurrentRow(Grid.builder(DatapackEditScreen.this)
					.xy1(this.inputComponentsList.nextStart(5), 0)
					.xy2(20, 90)
					.horizontalSizing(HorizontalSizing.LEFT_RIGHT)
					.rowHeight(26)
					.rowEditable(true)
					.transparentBackground(false)
					.addEditboxColumn("pack_item", EpicFightMod.MODID + ":", true, 150)
					.onAddPress((grid, button) -> {
						int rowposition = grid.addRowWithDefaultValues("pack_item", EpicFightMod.MODID + ":");
						grid.setGridFocus(rowposition, "pack_item");
						DatapackEditScreen.this.setFocused(grid);
					})
					.onRemovePress((grid, button) -> {
						int rowposition = grid.removeRow();
						
						if (rowposition >= 0) {
							grid.setSelected(rowposition);
						}
					})
					.buttonHorizontalSizing(HorizontalSizing.WIDTH_RIGHT)
					.build());
			
			this.inputComponentsList.newRow();
			this.inputComponentsList.newRow();
			this.inputComponentsList.addComponentCurrentRow(new Static(font, this.inputComponentsList.nextStart(4), 0, 100, 15, HorizontalSizing.LEFT_WIDTH, null, Component.translatable("Living Animations")));
			this.inputComponentsList.addComponentCurrentRow(ResizableButton.builder(Component.literal("..."), (button) -> {}).bounds(this.inputComponentsList.nextStart(4), 0, 15, 15).build());
		}
		
		@SuppressWarnings("rawtypes")
		@Override
		public void doLayout(ScreenRectangle screenRectangle) {
			super.doLayout(screenRectangle);
			
			for (InputComponentsEntry entry : this.inputComponentsList.children()) {
				for (Object widget : entry.children()) {
					if (widget instanceof ResizableComponent resizableComponent) {
						resizableComponent.resize(screenRectangle);
					}
				}
			}
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