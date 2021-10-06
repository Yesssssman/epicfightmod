package yesman.epicfight.client.gui.screen;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class EditSwitchingItemScreen extends Screen {
	private EditSwitchingItemScreen.RegisteredItemList battleAutoSwitchItems;
	private EditSwitchingItemScreen.RegisteredItemList miningAutoSwitchItems;
	protected final Screen parentScreen;
	
	public EditSwitchingItemScreen(Screen parentScreen) {
		super(new TranslationTextComponent(EpicFightMod.MODID + ".gui.configuration.autoswitching"));
		this.parentScreen = parentScreen;
	}
	
	@Override
	protected void init() {
		if (this.battleAutoSwitchItems == null) {
			this.battleAutoSwitchItems = new EditSwitchingItemScreen.RegisteredItemList(this.minecraft, 200, this.height,
				new TranslationTextComponent(EpicFightMod.MODID+".gui.to_battle_mode"), EpicFightMod.CLIENT_INGAME_CONFIG.battleAutoSwitchItems);
		} else {
			this.battleAutoSwitchItems.resize(200, this.height);
		}
		
		if (this.miningAutoSwitchItems == null) {
			this.miningAutoSwitchItems = new EditSwitchingItemScreen.RegisteredItemList(this.minecraft, 200, this.height,
				new TranslationTextComponent(EpicFightMod.MODID+".gui.to_mining_mode"), EpicFightMod.CLIENT_INGAME_CONFIG.miningAutoSwitchItems);
		} else {
			this.miningAutoSwitchItems.resize(200, this.height);
		}
		
		this.battleAutoSwitchItems.setLeftPos(this.width / 2 - 204);
		this.miningAutoSwitchItems.setLeftPos(this.width / 2 + 4);
		this.children.add(this.battleAutoSwitchItems);
		this.children.add(this.miningAutoSwitchItems);
		
		this.addButton(new Button(this.width / 2 - 80, this.height - 28, 160, 20, DialogTexts.GUI_DONE, (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.battleAutoSwitchItems.clear();
			EpicFightMod.CLIENT_INGAME_CONFIG.miningAutoSwitchItems.clear();
			this.battleAutoSwitchItems.toList().forEach((item) -> {
				EpicFightMod.CLIENT_INGAME_CONFIG.battleAutoSwitchItems.add(item);
			});
			this.miningAutoSwitchItems.toList().forEach((item) -> {
				EpicFightMod.CLIENT_INGAME_CONFIG.miningAutoSwitchItems.add(item);
			});
			EpicFightMod.CLIENT_INGAME_CONFIG.save();
			this.closeScreen();
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(0);
		this.battleAutoSwitchItems.render(matrixStack, mouseX, mouseY, partialTicks);
		this.miningAutoSwitchItems.render(matrixStack, mouseX, mouseY, partialTicks);
		drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 16, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void closeScreen() {
		this.minecraft.displayGuiScreen(this.parentScreen);
	}
	
	@OnlyIn(Dist.CLIENT)
	class RegisteredItemList extends ExtendedList<EditSwitchingItemScreen.RegisteredItemList.ItemEntry> {
		private final ITextComponent title;
		
		public RegisteredItemList(Minecraft mcIn, int width, int height, ITextComponent title, List<Item> saved) {
			super(mcIn, width, height, 32, height - 50, 22);
			this.title = title;
			this.setRenderHeader(true, (int)(9.0F * 1.5F));
			
			if (this.getSelected() != null) {
				this.centerScrollOn(this.getSelected());
			}
			
			for (Item item : saved) {
				this.addEntry(new ItemEntry(item));
			}
			
			this.addEntry(new ButtonInEntry());
		}
		
		public void resize(int width, int height) {
			this.width = width;
			this.height = height;
			this.y0 = 32;
			this.y1 = height - 50;
			this.x0 = 0;
			this.x1 = width;
		}
		
		@Override
		protected void renderHeader(MatrixStack matrixStack, int x, int y, Tessellator tessellator) {
			ITextComponent itextcomponent = (new StringTextComponent("")).appendSibling(this.title).mergeStyle(TextFormatting.UNDERLINE, TextFormatting.BOLD);
			this.minecraft.fontRenderer.drawText(matrixStack, itextcomponent, (float) (x + this.width / 2 - this.minecraft.fontRenderer.getStringPropertyWidth(itextcomponent) / 2), (float) Math.min(this.y0 + 3, y), 16777215);
		}
		
		@Override
		public int getRowWidth() {
			return this.width;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		protected void addEntry(Item item) {
			int size = this.getEventListeners().size();
			this.getEventListeners().add(size-1, new ItemEntry(item));
		}
		
		protected void removeIfPresent(Item item) {
			this.getEventListeners().remove(new ItemEntry(item));
		}
		
		protected List<Item> toList() {
			List<Item> list = Lists.newArrayList();
			for (ItemEntry entry : this.getEventListeners()) {
				if (entry.item != null) {
					list.add(entry.item);
				}
			}
			return list;
		}
		
		@OnlyIn(Dist.CLIENT)
		class ItemEntry extends ExtendedList.AbstractListEntry<EditSwitchingItemScreen.RegisteredItemList.ItemEntry> {
			private Item item;
			
			public ItemEntry(Item item) {
				this.item = item;
			}
			
			@Override
			public void render(MatrixStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				EditSwitchingItemScreen.this.itemRenderer.renderItemIntoGUI(new ItemStack(this.item), left+4, top+1);
				ITextComponent itextcomponent = this.item.getDisplayName(ItemStack.EMPTY);
				RegisteredItemList.this.minecraft.fontRenderer.drawText(matrixStack, itextcomponent, (float) (left + 30), (float) top+5, 16777215);
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					if (RegisteredItemList.this.getSelected() != null && RegisteredItemList.this.getSelected().equals(this)) {
						RegisteredItemList.this.removeEntry(this);
						return false;
					}
					RegisteredItemList.this.setSelected(this);
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public boolean equals(Object obj) {
				if (obj instanceof ItemEntry && !(this instanceof ButtonInEntry)) {
					return this.item.equals(((ItemEntry)obj).item);
				} else {
					return super.equals(obj);
				}
			}
		}
		
		@OnlyIn(Dist.CLIENT)
		class ButtonInEntry extends ItemEntry {
			private Button button;
			
			public ButtonInEntry() {
				super(null);
				this.button = new Button(0, 0, 20, 20, new StringTextComponent("+"), (button) -> {
					EditSwitchingItemScreen.RegisteredItemList thisList = EditSwitchingItemScreen.RegisteredItemList.this == EditSwitchingItemScreen.this.battleAutoSwitchItems ?
							EditSwitchingItemScreen.this.battleAutoSwitchItems : EditSwitchingItemScreen.this.miningAutoSwitchItems;
					EditSwitchingItemScreen.RegisteredItemList opponentList = EditSwitchingItemScreen.RegisteredItemList.this == EditSwitchingItemScreen.this.battleAutoSwitchItems ?
							EditSwitchingItemScreen.this.miningAutoSwitchItems : EditSwitchingItemScreen.this.battleAutoSwitchItems;
					RegisteredItemList.this.minecraft.displayGuiScreen(
							new EditItemListScreen(EditSwitchingItemScreen.this, thisList, opponentList));
				}, Button.EMPTY_TOOLTIP);
			}
			
			@Override
			public void render(MatrixStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				this.button.x = left+87;
				this.button.y = top-2;
				this.button.render(matrixStack, mouseX, mouseY, partialTicks);
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					if (this.button.isMouseOver(mouseX, mouseY)) {
						this.button.playDownSound(Minecraft.getInstance().getSoundHandler());
						this.button.onPress();
					}
				}
				return false;
			}
		}
	}
}