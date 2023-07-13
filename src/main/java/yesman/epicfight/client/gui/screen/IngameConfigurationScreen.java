package yesman.epicfight.client.gui.screen;

import java.io.File;
import java.io.IOException;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.CustomModelBakery;
import yesman.epicfight.client.gui.widget.ColorSlider;
import yesman.epicfight.client.gui.widget.RewindableButton;
import yesman.epicfight.config.Option;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class IngameConfigurationScreen extends Screen {
	protected final Screen parentScreen;
	
	public IngameConfigurationScreen(Minecraft mc, Screen screen) {
		super(Component.literal(EpicFightMod.MODID + ".gui.configuration"));
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
		Option<Boolean> offBlood = EpicFightMod.CLIENT_INGAME_CONFIG.offBloodEffects;
		
		Button longPressCounterButton = this.addRenderableWidget(new RewindableButton(this.width / 2 - 165, this.height / 4 - 32, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))),
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() + 1);
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			},
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() - 1);
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.long_press_counter.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button filterAnimationButton = this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 4 - 32, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")), (button) -> {
				filterAnimation.setValue(!filterAnimation.getValue());
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.filter_animation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showHealthIndicatorButton = this.addRenderableWidget(new Button(this.width / 2 - 165, this.height / 4 - 8, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")), (button) -> {
				showHealthIndicator.setValue(!showHealthIndicator.getValue());
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			}, (button, matrixStack, mouseX, mouseY) -> {
		        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.health_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
			}
		));
		
		Button showTargetIndicatorButton = this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 4 - 8, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")), (button) -> {
					showTargetIndicator.setValue(!showTargetIndicator.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.target_indicator.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button cameraAutoSwitchButton = this.addRenderableWidget(new Button(this.width / 2 - 165, this.height / 4 + 16, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")), (button) -> {
					cameraAutoSwitch.setValue(!cameraAutoSwitch.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.camera_auto_switch.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button enableAimHelperButton = this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 4 + 16, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")), (button) -> {
					enableAimHelper.setValue(!enableAimHelper.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.aim_helper.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button autoPreparationButton = this.addRenderableWidget(new Button(this.width / 2 - 165, this.height / 4 + 40, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")), (button) -> {
					autoPreparation.setValue(!autoPreparation.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.auto_preparation.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		Button offGoreButton = this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 4 + 40, 160, 20,
				Component.translatable("gui."+EpicFightMod.MODID+".off_blood_effects." + (offBlood.getValue() ? "on" : "off")), (button) -> {
					offBlood.setValue(!offBlood.getValue());
					button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".off_blood_effects." + (offBlood.getValue() ? "on" : "off")));
				}, (button, matrixStack, mouseX, mouseY) -> {
			        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.off_blood_effects.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
				}
			));
		
		this.addRenderableWidget(new Button(this.width / 2 - 165, this.height / 4 + 64, 160, 20, Component.translatable("gui."+EpicFightMod.MODID+".auto_switching_items"),  (button) -> {
			this.minecraft.setScreen(new EditSwitchingItemScreen(this));
		}, (button, matrixStack, mouseX, mouseY) -> {
	        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.auto_switching_items.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		}));
		
		this.addRenderableWidget(new Button(this.width / 2 + 5, this.height / 4 + 64, 160, 20, Component.translatable("gui."+EpicFightMod.MODID+".export_custom_armor"),  (button) -> {
			File resourcePackDirectory = Minecraft.getInstance().getResourcePackDirectory();
			try {
				CustomModelBakery.exportModels(resourcePackDirectory);
				Util.getPlatform().openFile(resourcePackDirectory);
			} catch (IOException e) {
				EpicFightMod.LOGGER.info("Failed to export custom armor models");
				e.printStackTrace();
			}
		}, (button, matrixStack, mouseX, mouseY) -> {
	        this.renderTooltip(matrixStack, this.minecraft.font.split(Component.translatable("gui.epicfight.export_custom_armor.tooltip"), Math.max(this.width / 2 - 43, 170)), mouseX, mouseY);
		}));
		
		this.addRenderableWidget(new ColorSlider(this.width / 2 - 150, this.height / 4 + 108, 300, 20, Component.translatable("gui.epicfight.aim_helper_color"), aimHelperColor.getValue(), EpicFightMod.CLIENT_INGAME_CONFIG.aimHelperColor));
			
		this.addRenderableWidget(new Button(this.width / 2 + 90, this.height / 4 + 150, 48, 20, CommonComponents.GUI_DONE, (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.save();
			this.onClose();
		}));
		
		this.addRenderableWidget(new Button(this.width / 2 + 140, this.height / 4 + 150, 48, 20, Component.translatable("controls.reset"), (button) -> {
			EpicFightMod.CLIENT_INGAME_CONFIG.resetSettings();
			filterAnimationButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".filter_animation." + (filterAnimation.getValue() ? "on" : "off")));
			longPressCounterButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			showHealthIndicatorButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".health_indicator." + (showHealthIndicator.getValue() ? "on" : "off")));
			showTargetIndicatorButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".target_indicator." + (showTargetIndicator.getValue() ? "on" : "off")));
			enableAimHelperButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".aim_helper." + (enableAimHelper.getValue() ? "on" : "off")));
			cameraAutoSwitchButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
			autoPreparationButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
			offGoreButton.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".off_blood_effect." + (offBlood.getValue() ? "on" : "off")));
		}));
	}
	
	@Override
	public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderDirtBackground(0);
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
}