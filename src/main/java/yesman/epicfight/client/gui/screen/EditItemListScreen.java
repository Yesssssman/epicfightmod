package yesman.epicfight.client.gui.screen;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.Button.ITooltip;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

public class EditItemListScreen extends Screen {
	private final Screen parentScreen;
	private final EditSwitchingItemScreen.RegisteredItemList targetList;
	private final EditSwitchingItemScreen.RegisteredItemList opponentList;
	private final List<Item> registered;
	private final List<Item> opponentRegistered;
	private EditItemListScreen.ButtonList itemButtonList;
	private EditItemListScreen.ButtonList selectedItemList;
	
	protected EditItemListScreen(Screen parentScreen, EditSwitchingItemScreen.RegisteredItemList targetList, EditSwitchingItemScreen.RegisteredItemList opponentList) {
		super(StringTextComponent.EMPTY);
		this.parentScreen = parentScreen;
		this.targetList = targetList;
		this.opponentList = opponentList;
		this.registered = targetList.toList();
		this.opponentRegistered = opponentList.toList();
	}
	
	@Override
	protected void init() {
		List<Item> itemList = Lists.newArrayList(ForgeRegistries.ITEMS.getValues());
		List<Item> selectedItemList = this.selectedItemList == null ? Lists.newArrayList() : this.selectedItemList.toList();
		this.itemButtonList = new EditItemListScreen.ButtonList(this.minecraft, this.width - 50, this.height, 24, this.height - 120, itemList, Type.LIST);
		this.selectedItemList = new EditItemListScreen.ButtonList(this.minecraft, this.width - 50, this.height, this.height - 100, this.height - 30, selectedItemList, Type.SELECTED);
		this.itemButtonList.setLeftPos(25);
		this.selectedItemList.setLeftPos(25);
		this.children.add(this.itemButtonList);
		this.children.add(this.selectedItemList);
		this.addButton(new Button(this.width / 2 + 125, this.height - 26, 60, 20, DialogTexts.GUI_DONE, (button) -> {
			for (Item item : this.selectedItemList.toList()) {
				this.targetList.addEntry(item);
				this.opponentList.removeIfPresent(item);
			}
			this.closeScreen();
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(0);
		this.itemButtonList.render(matrixStack, mouseX, mouseY, partialTicks);
		this.selectedItemList.render(matrixStack, mouseX, mouseY, partialTicks);
		drawString(matrixStack, this.font, new StringTextComponent("Item List").mergeStyle(TextFormatting.UNDERLINE), 28, 10, 16777215);
		drawString(matrixStack, this.font, new StringTextComponent("Seleted Items").mergeStyle(TextFormatting.UNDERLINE), 28, this.height-114, 16777215);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void closeScreen() {
		this.minecraft.displayGuiScreen(this.parentScreen);
	}
	
	private static enum Type {
		LIST, SELECTED
	}
	
	class ButtonList extends ExtendedList<EditItemListScreen.ButtonList.ButtonEntry> {
		private final Type type;
		private final int itemsInColumn;
		
		public ButtonList(Minecraft mcIn, int width, int height, int top, int bottom, List<Item> items, Type type) {
			super(mcIn, width, height, top, bottom, 18);
			this.itemsInColumn = width / 17;
			this.type = type;
			this.addEntry(new ButtonEntry());
			
			for (Item item : items) {
				if (this.type != Type.LIST || !EditItemListScreen.this.registered.contains(item)) {
					this.addItem(item);
				}
			}
		}
		
		public boolean has(Item item) {
			for (ButtonEntry entry : this.getEventListeners()) {
				for (ItemButton button : entry.buttonList) {
					if (button.item.equals(item)) {
						return true;
					}
				}
			}
			return false;
		}
		
		public void addItem(Item item) {
			ButtonEntry entry = this.getEntry(this.getEventListeners().size() - 1);
			if (entry.buttonList.size() > this.itemsInColumn) {
				this.addEntry(new ButtonEntry());
				entry = this.getEntry(this.getEventListeners().size() - 1);
			}
			
			IPressableExtended pressAction = null;
			ITooltip onTooltip = null;
			
			if (ButtonList.this.type == Type.LIST) {
				pressAction = (screen, button, x, y) -> {
					if (!screen.selectedItemList.has(item)) {
						screen.selectedItemList.addItem(button.item);
					}
				};
			} else if (ButtonList.this.type == Type.SELECTED) {
				pressAction = (screen, button, x, y) -> {
					screen.selectedItemList.removeAndRearrange(x, y);
				};
			}
			
			if (EditItemListScreen.this.opponentRegistered.contains(item)) {
				onTooltip = (button, matrixStack, mouseX, mouseY) -> {
					ITextComponent displayName = ((ItemButton)button).item.getDisplayName(ItemStack.EMPTY);
					EditItemListScreen.this.renderTooltip(matrixStack, new TranslationTextComponent("epicfight.gui.warn_already_registered",
						displayName.equals(StringTextComponent.EMPTY) ? new StringTextComponent(((ItemButton)button).item.getRegistryName().toString())
							: displayName), mouseX, mouseY
					);
				};
			} else {
				onTooltip = (button, matrixStack, mouseX, mouseY) -> {
					ITextComponent displayName = ((ItemButton)button).item.getDisplayName(ItemStack.EMPTY);
					EditItemListScreen.this.renderTooltip(matrixStack, displayName.equals(StringTextComponent.EMPTY) ?
						new StringTextComponent(((ItemButton)button).item.getRegistryName().toString()) : displayName, mouseX, mouseY);
				};
			}
			
			entry.buttonList.add(new ItemButton(0, 0, 16, 16, pressAction, onTooltip, EditItemListScreen.this, item));
		}
		
		public void removeAndRearrange(int x, int y) {
			this.getEntry(y).buttonList.remove(x);
		}
		
		public List<Item> toList() {
			List<Item> result = Lists.newArrayList();
			for (ButtonEntry entry : this.getEventListeners()) {
				for (ItemButton button : entry.buttonList) {
					result.add(button.item);
				}
			}
			return result;
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			ButtonEntry listener = this.getEntry(mouseX, mouseY);
			if (listener != null) {
				return listener.mouseClicked(mouseX, mouseY, button);
			} else {
				return false;
			}
		}
		
		public ButtonEntry getEntry(double mouseX, double mouseY) {
			if (mouseX < this.x0 + 2 || mouseX > this.x1 - 8 || mouseY < this.y0 + 2 || mouseY > this.y1 - 2) {
				return null;
			}
			int column = (int)((this.getScrollAmount() + mouseY - this.y0 - 4) / this.itemHeight);
			if (this.getEventListeners().size() > column) {
				return this.getEntry(column);
			}
			
			return null;
		}
		
		@SuppressWarnings("deprecation")
		@Override
		public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			this.renderBackground(matrixStack);
			int i = this.getScrollbarPosition();
			int j = i + 6;
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuffer();
			if (true) {
				this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
				RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferbuilder.pos((double) this.x0, (double) this.y1, 0.0D).tex((float) this.x0 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
				bufferbuilder.pos((double) this.x1, (double) this.y1, 0.0D).tex((float) this.x1 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
				bufferbuilder.pos((double) this.x1, (double) this.y0, 0.0D).tex((float) this.x1 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
				bufferbuilder.pos((double) this.x0, (double) this.y0, 0.0D).tex((float) this.x0 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
				tessellator.draw();
			}
			
			int j1 = this.getRowLeft();
			int k = this.y0 + 4 - (int) this.getScrollAmount();
			if (true) {
				this.renderHeader(matrixStack, j1, k, tessellator);
			}

			this.renderList(matrixStack, j1, k, mouseX, mouseY, partialTicks);
			
			this.minecraft.getTextureManager().bindTexture(AbstractGui.BACKGROUND_LOCATION);
		    RenderSystem.enableDepthTest();
		    RenderSystem.depthFunc(519);
		    bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		    bufferbuilder.pos((double)this.x0, (double)this.y0, -100.0D).tex(0.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.pos((double)(this.x0 + this.width), (double)this.y0, -100.0D).tex((float)this.width / 32.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.pos((double)(this.x0 + this.width), (double)this.y0 - 16, -100.0D).tex((float)this.width / 32.0F, (float)(this.y0 - 16) / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.pos((double)this.x0, (double)this.y0 - 16, -100.0D).tex(0.0F, (float)(this.y0 - 16) / 32.0F).color(64, 64, 64, 255).endVertex();
		  	bufferbuilder.pos((double)this.x0, (double)this.height, -100.0D).tex(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
		   	bufferbuilder.pos((double)(this.x0 + this.width), (double)this.height, -100.0D).tex((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
		   	bufferbuilder.pos((double)(this.x0 + this.width), (double)this.y1, -100.0D).tex((float)this.width / 32.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.pos((double)this.x0, (double)this.y1, -100.0D).tex(0.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		    tessellator.draw();
		    RenderSystem.depthFunc(515);
		    RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO,GlStateManager.DestFactor.ONE);
			RenderSystem.disableAlphaTest();
			RenderSystem.shadeModel(7425);
			RenderSystem.disableTexture();
			bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
			bufferbuilder.pos((double) this.x0, (double) (this.y0 + 4), 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 0).endVertex();
			bufferbuilder.pos((double) this.x1, (double) (this.y0 + 4), 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 0).endVertex();
			bufferbuilder.pos((double) this.x1, (double) this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x0, (double) this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x0, (double) this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x1, (double) this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
			bufferbuilder.pos((double) this.x1, (double) (this.y1 - 4), 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			bufferbuilder.pos((double) this.x0, (double) (this.y1 - 4), 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			tessellator.draw();
			
			int k1 = this.getMaxScroll();
			if (k1 > 0) {
				RenderSystem.disableTexture();
				int l1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
				l1 = MathHelper.clamp(l1, 32, this.y1 - this.y0 - 8);
				int i2 = (int) this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
				if (i2 < this.y0) {
					i2 = this.y0;
				}
				
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
				bufferbuilder.pos((double) i, (double) this.y1, 0.0D).tex(0.0F, 1.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.pos((double) j, (double) this.y1, 0.0D).tex(1.0F, 1.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.pos((double) j, (double) this.y0, 0.0D).tex(1.0F, 0.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.pos((double) i, (double) this.y0, 0.0D).tex(0.0F, 0.0F).color(0, 0, 0, 255).endVertex();
				bufferbuilder.pos((double) i, (double) (i2 + l1), 0.0D).tex(0.0F, 1.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.pos((double) j, (double) (i2 + l1), 0.0D).tex(1.0F, 1.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.pos((double) j, (double) i2, 0.0D).tex(1.0F, 0.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.pos((double) i, (double) i2, 0.0D).tex(0.0F, 0.0F).color(128, 128, 128, 255).endVertex();
				bufferbuilder.pos((double) i, (double) (i2 + l1 - 1), 0.0D).tex(0.0F, 1.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.pos((double) (j - 1), (double) (i2 + l1 - 1), 0.0D).tex(1.0F, 1.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.pos((double) (j - 1), (double) i2, 0.0D).tex(1.0F, 0.0F).color(192, 192, 192, 255).endVertex();
				bufferbuilder.pos((double) i, (double) i2, 0.0D).tex(0.0F, 0.0F).color(192, 192, 192, 255).endVertex();
				tessellator.draw();
			}
			
			this.renderDecorations(matrixStack, mouseX, mouseY);
			RenderSystem.enableTexture();
			RenderSystem.shadeModel(7424);
			RenderSystem.enableAlphaTest();
			RenderSystem.disableBlend();
		}
		
		@Override
		public int getRowLeft() {
			return this.x0 + 2;
		}
		
		@Override
		protected int getScrollbarPosition() {
			return this.x1 - 6;
		}
		
		@OnlyIn(Dist.CLIENT)
		class ButtonEntry extends ExtendedList.AbstractListEntry<EditItemListScreen.ButtonList.ButtonEntry> {
			private final List<ItemButton> buttonList;
			
			public ButtonEntry() {
				this.buttonList = Lists.newArrayList();
			}
			
			@Override
			public void render(MatrixStack matrixStack, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				int x = 0;
				for (ItemButton button : buttonList) {
					button.x = left + x;
					button.y = top;
					button.render(matrixStack, mouseX, mouseY, partialTicks);
					x+=16;
				}
			}
			
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				if (button == 0) {
					int row = (int)((mouseX - ButtonList.this.x0 - 2) / 16);
					int column = (int)((ButtonList.this.getScrollAmount() + mouseY - ButtonList.this.y0 - 4) / ButtonList.this.itemHeight);
					ItemButton itembutton = this.getButton(row);
					if (itembutton != null) {
						itembutton = itembutton.isMouseOver(mouseX, mouseY) ? itembutton : null;
						if (itembutton != null) {
							itembutton.pressedAction.onPress(EditItemListScreen.this, itembutton, row, column);
							itembutton.playDownSound(Minecraft.getInstance().getSoundHandler());
						}
					}
				}
				return false;
			}
			
			public ItemButton getButton(int index) {
				return this.buttonList.size() > index ? this.buttonList.get(index) : null;
			}
		}
	}
	
	class ItemButton extends Button {
		private final Item item;
		private final IPressableExtended pressedAction;
		
		public ItemButton(int x, int y, int width, int height, IPressableExtended pressedAction, ITooltip onTooltip, EditItemListScreen screen, Item item) {
			super(x, y, width, height, StringTextComponent.EMPTY, (button)->{}, onTooltip);
			this.item = item;
			this.pressedAction = pressedAction;
		}
		
		@Override
		public void renderWidget(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
			if (this.isMouseOver(mouseX, mouseY)) {
				Tessellator tessellator = Tessellator.getInstance();
				GlStateManager.disableTexture();
				BufferBuilder bufferbuilder = tessellator.getBuffer();
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
				bufferbuilder.pos((double) this.x, (double) this.y + this.height, 0.0D).color(255, 255, 255, 255).endVertex();
				bufferbuilder.pos((double) this.x + this.width, (double) this.y + this.height, 0.0D).color(255, 255, 255, 255).endVertex();
				bufferbuilder.pos((double) this.x + this.width, (double) this.y, 0.0D).color(255, 255, 255, 255).endVertex();
				bufferbuilder.pos((double) this.x, (double) this.y, 0.0D).color(255, 255, 255, 255).endVertex();
				tessellator.draw();
				this.onTooltip.onTooltip(this, matrixStack, mouseX, mouseY);
			}
			EditItemListScreen.this.itemRenderer.renderItemIntoGUI(new ItemStack(this.item), this.x, this.y);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public interface IPressableExtended {
		void onPress(EditItemListScreen parentScreen, ItemButton p_onPress_1_, int x, int y);
	}
}
