package yesman.epicfight.client.gui.component;

import java.util.function.Function;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringUtil;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import yesman.epicfight.client.gui.screen.SelectFromRegistryScreen;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class PopupBox<T> extends AbstractWidget implements ResizableComponent {
	public static final ResourceLocation POPUP_ICON = new ResourceLocation(EpicFightMod.MODID, "textures/gui/popup_icon.png");
	
	protected final Screen owner;
	protected final Font font;
	protected final IForgeRegistry<T> registry;
	protected final Function<T, String> displayStringMapper;
	
	protected T item;
	protected String itemDisplayName;
	
	public PopupBox(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, IForgeRegistry<T> registry) {
		this(owner, font, x1, x2, y1, y2, horizontal, vertical, title, registry, (item) -> {
			return registry.containsValue(item) ? registry.getKey(item).toString() : item.toString();
		});
	}
	
	public PopupBox(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, IForgeRegistry<T> registry, Function<T, String> displayStringMapper) {
		super(x1, x2, y1, y2, title);
		
		this.owner = owner;
		this.font = font;
		this.registry = registry;
		this.displayStringMapper = displayStringMapper;
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
	}
	
	public void setValue(T item) {
		this.item = item;
		this.itemDisplayName = this.displayStringMapper.apply(item);
	}
	
	@Override
	protected boolean clicked(double x, double y) {
		return this.active && this.visible && x >= (double)this.getX() + this.width - 14 && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height);
	}
	
	@Override
	public void onClick(double x, double y) {
		this.owner.getMinecraft().setScreen(new SelectFromRegistryScreen<>(this.owner, this.registry, this::setValue));
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if (this.active && this.visible) {
			if (this.isValidClickButton(button)) {
				if (this.clicked(x, y)) {
					this.playDownSound(Minecraft.getInstance().getSoundManager());
					this.onClick(x, y);
					return true;
				}
			}
			
			return false;
		} else {
			return false;
		}
	}
	
	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int outlineColor = this.isFocused() ? -1 : -6250336;
		guiGraphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, outlineColor);
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
		
		String correctedString = StringUtil.isNullOrEmpty(this.itemDisplayName) ? "" : this.font.plainSubstrByWidth(this.itemDisplayName, this.width - 16);
		guiGraphics.drawString(this.font, correctedString, this.getX() + 4, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, 16777215, false);
		
		RenderSystem.enableBlend();
		this.renderTexture(guiGraphics, POPUP_ICON, this.getX() + this.width - this.height, this.getY(), 0, 0, 0, this.height, this.height, 16, 16);
		RenderSystem.disableBlend();
	}
	
	@Override
	protected MutableComponent createNarrationMessage() {
		Component component = this.getMessage();
		return Component.translatable("gui.epicfight.narrate.popbupBox", component);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementInput) {
		narrationElementInput.add(NarratedElementType.TITLE, this.createNarrationMessage());
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class SoundPopupBox extends PopupBox<SoundEvent> {
		public SoundPopupBox(Screen owner, Font font, int x, int y, int width, int height, HorizontalSizing horizontal, VerticalSizing vertical, Component title) {
			super(owner, font, x, y, width, height, horizontal, vertical, title, ForgeRegistries.SOUND_EVENTS);
		}
		
		@Override
		public void onClick(double x, double y) {
			this.owner.getMinecraft().setScreen(new SelectFromRegistryScreen<>(this.owner, this.registry, (soundevent) -> {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundevent, 1.0F));
			}, this::setValue));
		}
	}
	
	/*******************************************************************
	 * @ResizableComponent variables                                   *
	 *******************************************************************/
	private final int x1;
	private final int x2;
	private final int y1;
	private final int y2;
	private final HorizontalSizing horizontalSizingOption;
	private final VerticalSizing verticalSizingOption;
	
	@Override
	public int getX1() {
		return this.x1;
	}

	@Override
	public int getX2() {
		return this.x2;
	}

	@Override
	public int getY1() {
		return this.y1;
	}

	@Override
	public int getY2() {
		return this.y2;
	}

	@Override
	public HorizontalSizing getHorizontalSizingOption() {
		return this.horizontalSizingOption;
	}

	@Override
	public VerticalSizing getVerticalSizingOption() {
		return this.verticalSizingOption;
	}
}