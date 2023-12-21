package yesman.epicfight.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.component.EpicFightOptionList;
import yesman.epicfight.client.gui.component.RewindableButton;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.config.OptionHandler;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class EpicFightControlOptionScreen extends EpicFightOptionSubScreen {
	private EpicFightOptionList optionsList;
	
	public EpicFightControlOptionScreen(Screen parentScreen, EpicFightOptions config) {
		super(parentScreen, config, Component.translatable("gui." + EpicFightMod.MODID + ".control_options"));
		
	}
	
	@Override
	protected void init() {
		super.init();
		
		this.optionsList = new EpicFightOptionList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
		
		OptionHandler<Integer> longPressCounter = this.config.longPressCount;
		OptionHandler<Boolean> cameraAutoSwitch = this.config.cameraAutoSwitch;
		OptionHandler<Boolean> autoPreparation = this.config.autoPreparation;
		OptionHandler<Boolean> noMiningInCombat = this.config.noMiningInCombat;
		
		int buttonHeight = -32;
		
		Button longPressCounterButton = new RewindableButton(this.width / 2 - 165, this.height / 4 + buttonHeight, 160, 20,
			Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))),
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() + 1);
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			},
			(button) -> {
				longPressCounter.setValue(longPressCounter.getValue() - 1);
				button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(longPressCounter.getValue()))));
			}
		);
		
		longPressCounterButton.setTooltip(Tooltip.create(Component.translatable("gui."+EpicFightMod.MODID+".long_press_counter.tooltip")));
		
		Button cameraAutoSwitchButton = Button.builder(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")), (button) -> {
			cameraAutoSwitch.setValue(!cameraAutoSwitch.getValue());
			button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch." + (cameraAutoSwitch.getValue() ? "on" : "off")));
		}).pos(this.width / 2 + 5, this.height / 4 + buttonHeight).size(160, 20).tooltip(Tooltip.create(Component.translatable("gui."+EpicFightMod.MODID+".camera_auto_switch.tooltip"))).build();
		
		this.optionsList.addSmall(longPressCounterButton, cameraAutoSwitchButton);
		
		buttonHeight += 24;
		
		Button autoPreparationButton = Button.builder(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")), (button) -> {
			autoPreparation.setValue(!autoPreparation.getValue());
			button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation." + (autoPreparation.getValue() ? "on" : "off")));
		}).pos(this.width / 2 - 165, this.height / 4 + buttonHeight).size(160, 20).tooltip(Tooltip.create(Component.translatable("gui."+EpicFightMod.MODID+".auto_preparation.tooltip"))).build();
		
		Button autoSwitchingItems = Button.builder(Component.translatable("gui."+EpicFightMod.MODID+".auto_switching_items"), (button) -> {
			this.minecraft.setScreen(new EditSwitchingItemScreen(this));
		}).pos(this.width / 2 + 5, this.height / 4 + buttonHeight).size(160, 20).tooltip(Tooltip.create(Component.translatable("gui."+EpicFightMod.MODID+".auto_switching_items.tooltip"))).build();
		
		this.optionsList.addSmall(autoPreparationButton, autoSwitchingItems);
		
		buttonHeight += 24;
		
		Button noMiningInCombatButton = Button.builder(Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat." + (noMiningInCombat.getValue() ? "on" : "off")), (button) -> {
			noMiningInCombat.setValue(!noMiningInCombat.getValue());
			button.setMessage(Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat." + (noMiningInCombat.getValue() ? "on" : "off")));
		}).pos(this.width / 2 - 165, this.height / 4 + buttonHeight).size(160, 20).tooltip(Tooltip.create(Component.translatable("gui."+EpicFightMod.MODID+".no_mining_in_combat.tooltip"))) .build();
		
		this.optionsList.addSmall(noMiningInCombatButton, null);
		this.addWidget(this.optionsList);
	}
	
	@Override
	public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
		this.basicListRender(guiGraphics, this.optionsList, mouseX, mouseY, partialTicks);
	}
}