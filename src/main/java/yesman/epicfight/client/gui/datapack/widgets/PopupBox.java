package yesman.epicfight.client.gui.datapack.widgets;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.StringUtil;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.screen.SelectAnimationScreen;
import yesman.epicfight.client.gui.datapack.screen.SelectFromRegistryScreen;
import yesman.epicfight.gameasset.ColliderPreset;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.WeaponTypeReloadListener;

@OnlyIn(Dist.CLIENT)
public abstract class PopupBox<T> extends AbstractWidget implements DataBindingComponent<T> {
	public static final ResourceLocation POPUP_ICON = new ResourceLocation(EpicFightMod.MODID, "textures/gui/popup_icon.png");
	
	protected final Screen owner;
	protected final Font font;
	protected final Function<T, String> toDisplayString;
	
	protected T item;
	protected String itemDisplayName;
	protected Predicate<T> filter;
	protected Consumer<T> responder;
	
	public PopupBox(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, Function<T, String> displayStringMapper, Consumer<T> responder) {
		super(x1, y1, x2, y2, title);
		
		this.owner = owner;
		this.font = font;
		this.responder = responder;
		this.toDisplayString = displayStringMapper;
		
		this.x1 = x1;
		this.x2 = x2;
		this.y1 = y1;
		this.y2 = y2;
		this.horizontalSizingOption = horizontal;
		this.verticalSizingOption = vertical;
	}
	
	public Predicate<T> getFilter() {
		return this.filter == null ? (item) -> true : this.filter;
	}
	
	public PopupBox<T> applyFilter(Predicate<T> filter) {
		this.filter = filter;
		return this;
	}
	
	@Override
	protected boolean clicked(double x, double y) {
		return this.active && this.visible && x >= (double)this.getX() && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height);
	}
	
	protected boolean clickedPopupButton(double x, double y) {
		return this.active && this.visible && x >= (double)this.getX() + this.width - 14 && y >= (double) this.getY() && x < (double) (this.getX() + this.width) && y < (double) (this.getY() + this.height);
	}
	
	@Override
	public abstract void onClick(double x, double y);
	
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
	public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		int outlineColor = this.isFocused() ? -1 : this.isActive() ? -6250336 : -12566463;
		
		guiGraphics.fill(this.getX() - 1, this.getY() - 1, this.getX() + this.width + 1, this.getY() + this.height + 1, outlineColor);
		guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, -16777216);
		
		String correctedString = StringUtil.isNullOrEmpty(this.itemDisplayName) ? "" : this.font.plainSubstrByWidth(this.itemDisplayName, this.width - 16);
		guiGraphics.drawString(this.font, correctedString, this.getX() + 4, this.getY() + this.height / 2 - this.font.lineHeight / 2 + 1, 16777215, false);
		
		RenderSystem.enableBlend();
		
		if (!this.isActive()) {
			RenderSystem.setShaderColor(0.3F, 0.3F, 0.3F, 1.0F);
		}
		
		this.renderTexture(guiGraphics, POPUP_ICON, this.getX() + this.width - this.height, this.getY(), 0, 0, 0, this.height, this.height, this.height, this.height);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
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
	public static class RegistryPopupBox<T> extends PopupBox<T> {
		protected final IForgeRegistry<T> registry;
		protected final Consumer<T> onPressRow;
		
		public RegistryPopupBox(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, IForgeRegistry<T> registry, Consumer<T> responder) {
			this(owner, font, x1, x2, y1, y2, horizontal, vertical, title, registry, (item) -> {}, responder);
		}
		
		public RegistryPopupBox(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, IForgeRegistry<T> registry, Consumer<T> onPressRow, Consumer<T> responder) {
			super(owner, font, x1, x2, y1, y2, horizontal, vertical, title, (item) -> {
				return registry.containsValue(item) ? registry.getKey(item).toString() : ParseUtil.nullParam(item);
			}, responder);
			
			this.registry = registry;
			this.onPressRow = onPressRow;
		}
		
		@Override
		public void onClick(double x, double y) {
			if (this.clickedPopupButton(x, y)) {
				this.owner.getMinecraft().setScreen(new SelectFromRegistryScreen<>(this.owner, this.registry, this.onPressRow, this::setValue, this.getFilter()));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class SoundPopupBox extends RegistryPopupBox<SoundEvent> {
		public SoundPopupBox(Screen owner, Font font, int x, int y, int width, int height, HorizontalSizing horizontal, VerticalSizing vertical, Component title, Consumer<SoundEvent> responder) {
			super(owner, font, x, y, width, height, horizontal, vertical, title, ForgeRegistries.SOUND_EVENTS, (soundevent) -> {
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(soundevent, 1.0F));
			}, responder);
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class AnimationPopupBox extends PopupBox<StaticAnimation> {
		private Supplier<Armature> armature;
		private Supplier<AnimatedMesh> mesh;
		
		public AnimationPopupBox(Screen owner, Font font, int x, int y, int width, int height, HorizontalSizing horizontal, VerticalSizing vertical, Component title, Consumer<StaticAnimation> responder) {
			super(owner, font, x, width, y, height, horizontal, vertical, title, (animation) -> ParseUtil.nullOrApply(animation, (a) -> a.getRegistryName().toString()), responder);
		}
		
		public void setModel(Supplier<Armature> armature, Supplier<AnimatedMesh> mesh) {
			this.armature = armature;
			this.mesh = mesh;
		}
		
		@Override
		public void onClick(double x, double y) {
			if (this.clickedPopupButton(x, y)) {
				this.owner.getMinecraft().setScreen(new SelectAnimationScreen(this.owner, this::setValue, this.getFilter(), this.armature.get(), this.mesh.get()));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ColliderPopupBox extends PopupBox<Collider> {
		public ColliderPopupBox(Screen owner, Font font, int x, int y, int width, int height, HorizontalSizing horizontal, VerticalSizing vertical, Component title, Consumer<Collider> responder) {
			super(owner, font, x, width, y, height, horizontal, vertical, title, (collider) -> ParseUtil.nullOrApply(collider, (c) -> ParseUtil.nullParam(ColliderPreset.getKey(c))), responder);
		}
		
		@Override
		public void onClick(double x, double y) {
			if (this.clickedPopupButton(x, y)) {
				this.owner.getMinecraft().setScreen(new SelectFromRegistryScreen<>(this.owner, ColliderPreset.entries(), "Collider", this::setValue, (c) -> {}, this.getFilter()));
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class WeaponTypePopupBox extends PopupBox<Function<Item, CapabilityItem.Builder>> {
		public WeaponTypePopupBox(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, Consumer<Function<Item, CapabilityItem.Builder>> responder) {
			super(owner, font, x1, x2, y1, y2, horizontal, vertical, title, (builder) -> ParseUtil.nullParam(WeaponTypeReloadListener.getKey(builder)), responder);
		}
		
		@Override
		public void onClick(double x, double y) {
			if (this.clickedPopupButton(x, y)) {
				this.owner.getMinecraft().setScreen(new SelectFromRegistryScreen<>(this.owner, WeaponTypeReloadListener.entries(), "Weapon Types", this::setValue, (c) -> {}, this.getFilter()));
			}
		}
	}
	
	@FunctionalInterface
	public static interface PopupBoxProvider<T, P extends PopupBox<T>> {
		public P create(Screen owner, Font font, int x1, int x2, int y1, int y2, HorizontalSizing horizontal, VerticalSizing vertical, Component title, Consumer<T> responder);
	}
	
	/*******************************************************************
	 * @DataBindingComponent variables                                 *
	 *******************************************************************/
	private int x1;
	private int x2;
	private int y1;
	private int y2;
	private final HorizontalSizing horizontalSizingOption;
	private final VerticalSizing verticalSizingOption;
	
	@Override
	public void setX1(int x1) {
		this.x1 = x1;
	}

	@Override
	public void setX2(int x2) {
		this.x2 = x2;
	}

	@Override
	public void setY1(int y1) {
		this.y1 = y1;
	}

	@Override
	public void setY2(int y2) {
		this.y2 = y2;
	}
	
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
	
	@Override
	public void setActive(boolean active) {
		this.active = active;
	}
	
	@Override
	public void setResponder(Consumer<T> responder) {
		this.responder = responder;
	}
	
	@Override
	public void setValue(@Nullable T item) {
		this.item = item;
		this.itemDisplayName = this.toDisplayString.apply(item);
		this.responder.accept(item);
		
		if (!StringUtil.isNullOrEmpty(this.itemDisplayName) && !this.itemDisplayName.equals(this.font.plainSubstrByWidth(this.itemDisplayName, this.width - 16))) {
			this.setTooltip(Tooltip.create(Component.literal(this.itemDisplayName)));
		} else {
			this.setTooltip(null);
		}
	}
	
	@Override
	public T getValue() {
		return this.item;
	}
	
	@Override
	public void reset() {
	}
	
	@Override
	public void tick() {
	}
}