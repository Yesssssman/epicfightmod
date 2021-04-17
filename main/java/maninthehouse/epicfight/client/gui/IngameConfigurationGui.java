package maninthehouse.epicfight.client.gui;

import java.io.IOException;
import java.util.Set;

import maninthehouse.epicfight.config.ConfigurationIngame;
import maninthehouse.epicfight.main.EpicFightMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.client.IModGuiFactory;

public class IngameConfigurationGui extends GuiScreen implements IModGuiFactory {
	private GuiScreen parentScreen;
	OptionButton<Boolean> healthIndicatorButton;
	OptionButton<Boolean> filterAnimationButton;
	OptionButton<Integer> longPressButton;
	
	@Override
	public void initGui() {
		this.longPressButton = this.addButton(new OptionButton(0, this.width / 2 - 100, this.height / 4 - 24, 200, 20,
				(btn) -> {
					ConfigurationIngame.longPressCount%=10;
					ConfigurationIngame.longPressCount++;
				},
				() -> new TextComponentTranslation("gui."+EpicFightMod.MODID+".long_press_counter", (ItemStack.DECIMALFORMAT.format(ConfigurationIngame.longPressCount))).getFormattedText()
		));
		
		this.filterAnimationButton = this.addButton(new OptionButton(1, this.width / 2 - 100, this.height / 4, 200, 20,
				(btn) -> {
					ConfigurationIngame.filterAnimation = !ConfigurationIngame.filterAnimation;
				},
				() -> new TextComponentTranslation("gui."+EpicFightMod.MODID+".filter_animation." + (ConfigurationIngame.filterAnimation ? "on" : "off")).getFormattedText()
		));
		
		this.healthIndicatorButton = this.addButton(new OptionButton(2, this.width / 2 - 100, this.height / 4 + 24, 200, 20,
				(btn) -> {
					ConfigurationIngame.showHealthIndicator = !ConfigurationIngame.showHealthIndicator;
				}, 
				() -> new TextComponentTranslation("gui."+EpicFightMod.MODID+".health_indicator." + (ConfigurationIngame.showHealthIndicator ? "on" : "off")).getFormattedText()
		));
		
		this.addButton(new OptionButton(3, this.width / 2 - 100, this.height / 4 + 150, 96, 20,
				(btn) -> {
					Minecraft.getMinecraft().displayGuiScreen(this.parentScreen);
				}, () -> new TextComponentTranslation("gui.done").getFormattedText()
		));
		
		this.addButton(new OptionButton(4, this.width / 2 + 4, this.height / 4 + 150, 96, 20,
				(btn) -> {
					this.resetAll();
				}, () -> new TextComponentTranslation("controls.reset").getFormattedText()
		));
	}
	
	private void resetAll() {
		ConfigurationIngame.resetSettings();
		this.healthIndicatorButton.refreshText();
		this.filterAnimationButton.refreshText();
		this.longPressButton.refreshText();
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button instanceof OptionButton) {
			((OptionButton)button).onClicked();
		}
    }
	
	@Override
	public void onGuiClosed() {
		//EpicFightMod.INGAME_CONFIG.save();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	public void initialize(Minecraft minecraftInstance) {
		
	}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		IngameConfigurationGui configGui = new IngameConfigurationGui();
		configGui.parentScreen = parentScreen;
		return configGui;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}
}