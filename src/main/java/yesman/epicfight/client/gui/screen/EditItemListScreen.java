package yesman.epicfight.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.client.gui.component.BasicButton;

import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class EditItemListScreen extends Screen {
	private final Screen parentScreen;
	private final EditSwitchingItemScreen.RegisteredItemList targetList;
	private final EditSwitchingItemScreen.RegisteredItemList opponentList;
	private final List<Item> registered;
	private final List<Item> opponentRegistered;
	private EditItemListScreen.ButtonList itemButtonList;
	private EditItemListScreen.ButtonList selectedItemList;
	
	protected EditItemListScreen(Screen parentScreen, EditSwitchingItemScreen.RegisteredItemList targetList, EditSwitchingItemScreen.RegisteredItemList opponentList) {
		super(Component.empty());
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
		this.addRenderableWidget(this.itemButtonList);
		this.addRenderableWidget(this.selectedItemList);
		this.addRenderableWidget(new BasicButton(this.width / 2 + 125, this.height - 26, 60, 20, CommonComponents.GUI_DONE, (button) -> {
			for (Item item : this.selectedItemList.toList()) {
				this.targetList.addEntry(item);
				this.opponentList.removeIfPresent(item);
			}
			this.onClose();
		}));
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(guiGraphics);
		this.itemButtonList.render(guiGraphics, mouseX, mouseY, partialTicks);
		this.selectedItemList.render(guiGraphics, mouseX, mouseY, partialTicks);
		super.render(guiGraphics, mouseX, mouseY, partialTicks);
		guiGraphics.drawString(this.font, Component.literal("Item List").withStyle(ChatFormatting.UNDERLINE), 28, 10, 16777215, false);
		guiGraphics.drawString(this.font, Component.literal("Seleted Items").withStyle(ChatFormatting.UNDERLINE), 28, this.height-114, 16777215, false);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	private enum Type {
		LIST, SELECTED
	}
	
	class ButtonList extends ObjectSelectionList<EditItemListScreen.ButtonList.ButtonEntry> {
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
			for (ButtonEntry entry : this.children()) {
				for (ItemButton button : entry.buttonList) {
					if (button.itemStack.getItem().equals(item)) {
						return true;
					}
				}
			}
			return false;
		}
		
		public void addItem(Item item) {
			ButtonEntry entry = this.getEntry(this.children().size() - 1);
			if (entry.buttonList.size() > this.itemsInColumn) {
				this.addEntry(new ButtonEntry());
				entry = this.getEntry(this.children().size() - 1);
			}
			
			IPressableExtended pressAction = null;
			BasicButton.OnTooltip onTooltip = null;
			
			if (ButtonList.this.type == Type.LIST) {
				pressAction = (screen, button, x, y) -> {
					if (!screen.selectedItemList.has(item)) {
						screen.selectedItemList.addItem(button.itemStack.getItem());
					}
				};
			} else if (ButtonList.this.type == Type.SELECTED) {
				pressAction = (screen, button, x, y) -> {
					screen.selectedItemList.removeAndRearrange(x, y);
				};
			}
			
			if (EditItemListScreen.this.opponentRegistered.contains(item)) {
				onTooltip = (button, guiGraphics, mouseX, mouseY) -> {
					Component displayName = ((ItemButton)button).itemStack.getHoverName();
					guiGraphics.renderTooltip(EditItemListScreen.this.font, Component.translatable("epicfight.gui.warn_already_registered",
						displayName.equals(Component.empty()) ? Component.literal((ForgeRegistries.ITEMS.getKey(((ItemButton)button).itemStack.getItem()).toString()))
							: displayName), mouseX, mouseY
					);
				};
			} else {
				onTooltip = (button, guiGraphics, mouseX, mouseY) -> {
					Component displayName = ((ItemButton)button).itemStack.getHoverName();
					guiGraphics.renderTooltip(EditItemListScreen.this.font, displayName.equals(Component.empty()) ?
						Component.literal((ForgeRegistries.ITEMS.getKey(((ItemButton)button).itemStack.getItem()).toString()) ) : displayName, mouseX, mouseY);
				};
			}
			
			entry.buttonList.add(new ItemButton(0, 0, 16, 16, pressAction, onTooltip, EditItemListScreen.this, item.getDefaultInstance()));
		}
		
		public void removeAndRearrange(int x, int y) {
			this.getEntry(y).buttonList.remove(x);
		}
		
		public List<Item> toList() {
			List<Item> result = Lists.newArrayList();
			for (ButtonEntry entry : this.children()) {
				for (ItemButton button : entry.buttonList) {
					result.add(button.itemStack.getItem());
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
			if (this.children().size() > column) {
				return this.getEntry(column);
			}
			
			return null;
		}
		
		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			this.renderBackground(guiGraphics);
			int i = this.getScrollbarPosition();
			int j = i + 6;
			
			Tesselator tessellator = Tesselator.getInstance();
			BufferBuilder bufferbuilder = tessellator.getBuilder();
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
			bufferbuilder.vertex(this.x0, this.y1, 0.0D).uv((float) this.x0 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
			bufferbuilder.vertex(this.x1, this.y1, 0.0D).uv((float) this.x1 / 32.0F, (float) (this.y1 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
			bufferbuilder.vertex(this.x1, this.y0, 0.0D).uv((float) this.x1 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
			bufferbuilder.vertex(this.x0, this.y0, 0.0D).uv((float) this.x0 / 32.0F, (float) (this.y0 + (int) this.getScrollAmount()) / 32.0F).color(32, 32, 32, 255).endVertex();
			tessellator.end();
			
			int j1 = this.getRowLeft();
			int k = this.y0 + 4 - (int) this.getScrollAmount();
			this.renderHeader(guiGraphics, j1, k);
			this.renderList(guiGraphics, mouseX, mouseY, partialTicks);
			
			RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
			RenderSystem.setShaderTexture(0, Screen.BACKGROUND_LOCATION);
		    RenderSystem.enableDepthTest();
		    RenderSystem.depthFunc(519);
		    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
		    bufferbuilder.vertex(this.x0, this.y0, -100.0D).uv(0.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.vertex(this.x0 + this.width, this.y0, -100.0D).uv((float)this.width / 32.0F, (float)this.y0 / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.vertex(this.x0 + this.width, (double)this.y0 - 16, -100.0D).uv((float)this.width / 32.0F, (float)(this.y0 - 16) / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.vertex(this.x0, (double)this.y0 - 16, -100.0D).uv(0.0F, (float)(this.y0 - 16) / 32.0F).color(64, 64, 64, 255).endVertex();
		  	bufferbuilder.vertex(this.x0, this.height, -100.0D).uv(0.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
		   	bufferbuilder.vertex(this.x0 + this.width, this.height, -100.0D).uv((float)this.width / 32.0F, (float)this.height / 32.0F).color(64, 64, 64, 255).endVertex();
		   	bufferbuilder.vertex(this.x0 + this.width, this.y1, -100.0D).uv((float)this.width / 32.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		    bufferbuilder.vertex(this.x0, this.y1, -100.0D).uv(0.0F, (float)this.y1 / 32.0F).color(64, 64, 64, 255).endVertex();
		    tessellator.end();
		    RenderSystem.depthFunc(515);
		    RenderSystem.disableDepthTest();
			RenderSystem.enableBlend();
			RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
//			RenderSystem.disableTexture();
			RenderSystem.setShader(GameRenderer::getPositionColorShader);
			bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
			bufferbuilder.vertex(this.x0, this.y0 + 4, 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.vertex(this.x1, this.y0 + 4, 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.vertex(this.x1, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(this.x0, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(this.x0, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(this.x1, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
			bufferbuilder.vertex(this.x1, this.y1 - 4, 0.0D).color(0, 0, 0, 0).endVertex();
			bufferbuilder.vertex(this.x0, this.y1 - 4, 0.0D).color(0, 0, 0, 0).endVertex();
			tessellator.end();
			
			int k1 = this.getMaxScroll();
			if (k1 > 0) {
//				RenderSystem.disableTexture();
				RenderSystem.setShader(GameRenderer::getPositionColorShader);
				int l1 = (int) ((float) ((this.y1 - this.y0) * (this.y1 - this.y0)) / (float) this.getMaxPosition());
				l1 = Mth.clamp(l1, 32, this.y1 - this.y0 - 8);
				int i2 = (int) this.getScrollAmount() * (this.y1 - this.y0 - l1) / k1 + this.y0;
				if (i2 < this.y0) {
					i2 = this.y0;
				}
				
				bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
				bufferbuilder.vertex(i, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(j, this.y1, 0.0D).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(j, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(i, this.y0, 0.0D).color(0, 0, 0, 255).endVertex();
				bufferbuilder.vertex(i, i2 + l1, 0.0D).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(j, i2 + l1, 0.0D).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(j, i2, 0.0D).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(i, i2, 0.0D).color(128, 128, 128, 255).endVertex();
				bufferbuilder.vertex(i, i2 + l1 - 1, 0.0D).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex(j - 1, i2 + l1 - 1, 0.0D).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex(j - 1, i2, 0.0D).color(192, 192, 192, 255).endVertex();
				bufferbuilder.vertex(i, i2, 0.0D).color(192, 192, 192, 255).endVertex();
				tessellator.end();
			}
			
			this.renderDecorations(guiGraphics, mouseX, mouseY);
//			RenderSystem.enableTexture();
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
		class ButtonEntry extends ObjectSelectionList.Entry<EditItemListScreen.ButtonList.ButtonEntry> {
			private final List<ItemButton> buttonList;
			
			public ButtonEntry() {
				this.buttonList = Lists.newArrayList();
			}
			
			@Override
			public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean isMouseOver, float partialTicks) {
				int x = 0;
				for (ItemButton button : buttonList) {
					button.setX(left + x);
					button.setY(top);
					button.render(guiGraphics, mouseX, mouseY, partialTicks);
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
							itembutton.playDownSound(Minecraft.getInstance().getSoundManager());
						}
					}
				}
				return false;
			}
			
			public ItemButton getButton(int index) {
				return this.buttonList.size() > index ? this.buttonList.get(index) : null;
			}

			@Override
			public Component getNarration() {
				return Component.empty();
			}
		}
	}
	
	class ItemButton extends BasicButton {
		private static final Set<Item> UNRENDERABLES = Sets.newHashSet();
		
		private final ItemStack itemStack;
		private final IPressableExtended pressedAction;
		
		public ItemButton(int x, int y, int width, int height, IPressableExtended pressedAction, OnTooltip onTooltip, EditItemListScreen screen, ItemStack itemStack) {
			super(x, y, width, height, Component.empty(), (button)->{}, onTooltip);
			this.itemStack = itemStack;
			this.pressedAction = pressedAction;
		}
		
		@Override
		public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
			if (this.isMouseOver(mouseX, mouseY)) {
				Tesselator tessellator = Tesselator.getInstance();
				//GlStateManager._disableTexture();
				BufferBuilder bufferbuilder = tessellator.getBuilder();
				bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
				bufferbuilder.vertex(this.getX(), (double) this.getY() + this.height, 0.0D).color(255, 255, 255, 255).endVertex();
				bufferbuilder.vertex((double) this.getX() + this.width, (double) this.getY() + this.height, 0.0D).color(255, 255, 255, 255).endVertex();
				bufferbuilder.vertex((double) this.getX() + this.width, this.getY(), 0.0D).color(255, 255, 255, 255).endVertex();
				bufferbuilder.vertex(this.getX(), this.getY(), 0.0D).color(255, 255, 255, 255).endVertex();
				tessellator.end();
				this.onTooltip.onTooltip(this, guiGraphics, mouseX, mouseY);
			}
			
			try {
				try {
					if (!UNRENDERABLES.contains(this.itemStack.getItem())) {
						guiGraphics.renderItem(this.itemStack, this.getX(), this.getY());
					}
				} catch (Exception e) {
					UNRENDERABLES.add(this.itemStack.getItem());
				}
			} catch (Throwable e) {
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public interface IPressableExtended {
		void onPress(EditItemListScreen parentScreen, ItemButton p_onPress_1_, int x, int y);
	}
}
