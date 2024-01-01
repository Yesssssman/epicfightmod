package yesman.epicfight.client.gui.screen;

import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
		@Override
		public void setCurrentTab(Tab tab, boolean playSound) {
			if (this.getCurrentTab() instanceof DatapackTab datapackTab) {
				DatapackEditScreen.this.removeWidget(datapackTab.packItemList);
				DatapackEditScreen.this.removeWidget(datapackTab.inputComponentsList);
			}
			
			super.setCurrentTab(tab, playSound);
			
			if (tab instanceof DatapackTab datapackTab) {
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
	
	class PackItemList extends ObjectSelectionList<PackItemList.PackItemEntry> {
		public PackItemList(int width, int height, int y0, int y1, int itemHeight) {
			super(DatapackEditScreen.this.minecraft, width, height, y0, y1, itemHeight);
		}
		
		class PackItemEntry extends ObjectSelectionList.Entry<DatapackEditScreen.PackItemList.PackItemEntry> {
			private CompoundTag itemTag;
			
			public PackItemEntry() {
				this.itemTag = new CompoundTag();
				this.itemTag.putString("name", "entry example");
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				String name = this.itemTag.get("name").toString();
				guiGraphics.drawString(DatapackEditScreen.this.minecraft.font, name, left + 2, top + 5, 16777215, false);
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
			
			@Override
			public Component getNarration() {
				return Component.translatable("narrator.select", this.itemTag.get("name"));
			}
		}
		
		@Override
		public int getRowWidth() {
			return this.width;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		public void addNewList() {
			this.addEntry(new PackItemList.PackItemEntry());
		}
	}
	
	class InputComponents extends ContainerObjectSelectionList<InputComponentsEntry> {
		public InputComponents(int width, int height, int y0, int y1, int itemHeight) {
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
	}
	
	class InputComponentsEntry extends ContainerObjectSelectionList.Entry<InputComponentsEntry> {
		private AbstractWidget button;
		
		public InputComponentsEntry(AbstractWidget button) {
			this.button = button;
		}
		
		@Override
		public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
			this.button.setY(top);
			this.button.render(guiGraphics, mouseX, mouseY, partialTicks);
		}
		
		@Override
		public List<? extends GuiEventListener> children() {
			return List.of(this.button);
		}
		
		@Override
		public List<? extends NarratableEntry> narratables() {
			return List.of(this.button);
		}
	}
	
	class DatapackTab extends GridLayoutTab {
		private PackItemList packItemList;
		private InputComponents inputComponentsList;
		
		public DatapackTab(Component title) {
			super(title);
			
			GridLayout.RowHelper gridlayout$rowhelper = this.layout.columnSpacing(2).rowSpacing(2).createRowHelper(2);
			gridlayout$rowhelper.defaultCellSetting().alignHorizontallyLeft();
			
			this.packItemList = new PackItemList(150, DatapackEditScreen.this.height - 40, 40, DatapackEditScreen.this.height - 40, 26);
			this.packItemList.setLeftPos(8);
			this.packItemList.setRenderTopAndBottom(false);
			
			this.inputComponentsList = new InputComponents(DatapackEditScreen.this.width - 174, DatapackEditScreen.this.height - 40, 40, DatapackEditScreen.this.height - 40, 26);
			this.inputComponentsList.setLeftPos(166);
			this.inputComponentsList.setRenderTopAndBottom(false);
			
			Button addBtn = Button.builder(Component.literal("+"), (button) -> {
				this.packItemList.addNewList();
			}).pos(0, 0).size(12, 12).build();
			
			Button removeBtn = Button.builder(Component.literal("x"), (button) -> {
				if (this.packItemList.getSelected() != null) {
					this.packItemList.children().remove(this.packItemList.getSelected());
				}
			}).pos(0, 0).size(12, 12).build();
			
			gridlayout$rowhelper.addChild(addBtn);
			gridlayout$rowhelper.addChild(removeBtn);
		}
		
		@Override
		public void doLayout(ScreenRectangle screenRectangle) {
			this.layout.arrangeElements();
			
			this.layout.setX(132);
			this.layout.setY(screenRectangle.top() + 2);
			
			this.packItemList.updateSize(150, DatapackEditScreen.this.height - 40, 40, DatapackEditScreen.this.height - 45);
			this.packItemList.setLeftPos(8);
			
			this.inputComponentsList.updateSize(DatapackEditScreen.this.width - 174, DatapackEditScreen.this.height - 40, 40, DatapackEditScreen.this.height - 45);
			this.inputComponentsList.setLeftPos(166);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class WeaponTypeTab extends DatapackTab {
		public WeaponTypeTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.weapon_type"));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class ItemCapabilityTab extends DatapackTab {
		public ItemCapabilityTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.item_capability"));
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	class MobPatchTab extends DatapackTab {
		public MobPatchTab() {
			super(Component.translatable("gui." + EpicFightMod.MODID + ".tab.datapack.mob_patch"));
		}
	}
}