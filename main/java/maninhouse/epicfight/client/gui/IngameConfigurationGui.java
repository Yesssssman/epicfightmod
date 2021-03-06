package maninhouse.epicfight.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;

import maninhouse.epicfight.client.gui.button.RewindableButton;
import maninhouse.epicfight.config.Option;
import maninhouse.epicfight.main.EpicFightMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IngameConfigurationGui extends Screen {
	protected final Screen parentScreen;
	
	public IngameConfigurationGui(Minecraft mc, Screen screen) {
		super(new StringTextComponent(EpicFightMod.MODID + ".gui.configuration"));
		this.parentScreen = screen;
	}
	
	@Override
	protected void init() {
		Option<Boolean> showHealthIndicator = EpicFightMod.INGAME_CONFIG.showHealthIndicator;
		Option<Boolean> filterAnimation = EpicFightMod.INGAME_CONFIG.filterAnimation;
		Option<Integer> longPressCounter = EpicFightMod.INGAME_CONFIG.longPressCount;
		
		Button longPressCounterButton = this.addButton(new RewindableButton(this.width / 2 - 100, this.height / 4 - 24, 200, 20,
			new TranslationTextComponent("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.DECIMALFORMAT.format(longPressCounter.getValue()))),
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() + 1);
				button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.DECIMALFORMAT.format(longPressCounter.getValue()))));
			},
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() - 1);
				button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.DECIMALFORMAT.format(longPressCounter.getValue()))));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.long_press_counter.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button filterAnimationButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4, 200, 20,
			new TranslationTextComponent("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")), (button) -> {
				filterAnimation.setValue(!filterAnimation.getValue());
				button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.filter_animation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showHealthIndicatorButton = this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 24, 200, 20,
			new TranslationTextComponent("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")), (button) -> {
				showHealthIndicator.setValue(!showHealthIndicator.getValue());
				button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.health_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 96, 20, new TranslationTextComponent("gui.done"), (button) -> {
			this.closeScreen();
		}));
		
		this.addButton(new Button(this.width / 2 + 4, this.height / 4 + 150, 96, 20, new TranslationTextComponent("controls.reset"), (button) -> {
			EpicFightMod.INGAME_CONFIG.resetSettings();
			filterAnimationButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			longPressCounterButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.DECIMALFORMAT.format(longPressCounter.getValue()))));
			showHealthIndicatorButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(0);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void closeScreen() {
		EpicFightMod.INGAME_CONFIG.save();
		this.minecraft.displayGuiScreen(this.parentScreen);
	}
}