package yesman.epicfight.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.widget.ColorSlider;
import yesman.epicfight.client.gui.widget.RewindableButton;
import yesman.epicfight.config.Option;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class IngameConfigurationScreen extends Screen {
	protected final Screen parentScreen;
	
	public IngameConfigurationScreen(Minecraft mc, Screen screen) {
		super(new StringTextComponent(EpicFightMod.MODID + ".gui.configuration"));
		this.parentScreen = screen;
	}
	
	@Override
	protected void init() {
		Option<Boolean> showHealthIndicator = EpicFightMod.CLIENT_INGAME_CONFIG.showHealthIndicator;
		Option<Boolean> showTargetIndicator = EpicFightMod.CLIENT_INGAME_CONFIG.showTargetIndicator;
		Option<Boolean> filterAnimation = EpicFightMod.CLIENT_INGAME_CONFIG.filterAnimation;
		Option<Integer> longPressCounter = EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount;
		Option<Boolean> enableAimHelper = EpicFightMod.CLIENT_INGAME_CONFIG.enableAimHelperPointer;
		Option<Double> aimHelperColor = EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperColor;
		Option<Boolean> cameraAutoSwitch = EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch;
		Option<Boolean> autoPreparation = EpicFightMod.CLIENT_INGAME_CONFIG.autoPreparation;
		Option<Boolean> offGore = EpicFightMod.CLIENT_INGAME_CONFIG.offGoreEffect;
		
		Button longPressCounterButton = this.addButton(new RewindableButton(this.width / 2 - 165, this.height / 4 - 32, 160, 20,
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
		
		Button filterAnimationButton = this.addButton(new Button(this.width / 2 + 5, this.height / 4 - 32, 160, 20,
			new TranslationTextComponent("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")), (button) -> {
				filterAnimation.setValue(!filterAnimation.getValue());
				button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.filter_animation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showHealthIndicatorButton = this.addButton(new Button(this.width / 2 - 165, this.height / 4 - 8, 160, 20,
			new TranslationTextComponent("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")), (button) -> {
				showHealthIndicator.setValue(!showHealthIndicator.getValue());
				button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.health_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showTargetIndicatorButton = this.addButton(new Button(this.width / 2 + 5, this.height / 4 - 8, 160, 20,
				new TranslationTextComponent("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")), (button) -> {
					showTargetIndicator.setValue(!showTargetIndicator.getValue());
					button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.target_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button cameraAutoSwitchButton = this.addButton(new Button(this.width / 2 - 165, this.height / 4 + 16, 160, 20,
				new TranslationTextComponent("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")), (button) -> {
					cameraAutoSwitch.setValue(!cameraAutoSwitch.getValue());
					button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.camera_auto_switch.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button enableAimHelperButton = this.addButton(new Button(this.width / 2 + 5, this.height / 4 + 16, 160, 20,
				new TranslationTextComponent("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")), (button) -> {
					enableAimHelper.setValue(!enableAimHelper.getValue());
					button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.aim_helper.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button autoPreparationButton = this.addButton(new Button(this.width / 2 - 165, this.height / 4 + 40, 160, 20,
				new TranslationTextComponent("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")), (button) -> {
					autoPreparation.setValue(!autoPreparation.getValue());
					button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.auto_preparation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button offGoreButton = this.addButton(new Button(this.width / 2 + 5, this.height / 4 + 40, 160, 20,
				new TranslationTextComponent("gui."+EpicFightMod.MODID+".off_gore_effect." + (offGore.getValue() ? "on" : "off")), (button) -> {
					offGore.setValue(!offGore.getValue());
					button.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".off_gore_effect." + (offGore.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.off_gore_effect.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		this.addButton(new Button(this.width / 2 - 165, this.height / 4 + 64, 160, 20, new TranslationTextComponent("gui."+EpicFightMod.MODID+".auto_switching_items"),  (button) -> {
			this.minecraft.displayGuiScreen(new EditSwitchingItemScreen(this));
		}, (button, matrixStack, mouseX, mouseY) -> {
	        this.renderTooltip(matrixStack, this.minecraft.fontRenderer.trimStringToWidth(new TranslationTextComponent("gui.epicfight.auto_switching_items.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		}));
		
		this.addButton(new ColorSlider(this.width / 2 - 150, this.height / 4 + 108, 300, 20, new TranslationTextComponent("gui.epicfight.aim_helper_color"), aimHelperColor.getValue(), EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperColor));
		
		this.addButton(new Button(this.width / 2 + 90, this.height / 4 + 150, 48, 20, DialogTexts.GUI_DONE, (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.save();
			this.closeScreen();
		}));
		
		this.addButton(new Button(this.width / 2 + 140, this.height / 4 + 150, 48, 20, new TranslationTextComponent("controls.reset"), (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.resetSettings();
			filterAnimationButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			longPressCounterButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.DECIMALFORMAT.format(longPressCounter.getValue()))));
			showHealthIndicatorButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			showTargetIndicatorButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
			enableAimHelperButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")));
			cameraAutoSwitchButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
			autoPreparationButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
			offGoreButton.setMessage(new TranslationTextComponent("gui."+EpicFightMod.MODID+".off_gore_effect." + (offGore.getValue() ? "on" : "off")));
		}));
	}
	
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(0);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void closeScreen() {
		this.minecraft.displayGuiScreen(this.parentScreen);
	}
}