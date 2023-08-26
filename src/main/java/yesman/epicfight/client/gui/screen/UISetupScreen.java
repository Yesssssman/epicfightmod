package yesman.epicfight.client.gui.screen;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.widget.UIComponent;
import yesman.epicfight.client.gui.widget.UIComponent.PassiveUIComponent;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
public class UISetupScreen extends Screen {
	private final ConfigurationIngame config;
	protected final Screen parentScreen;
	private UIComponent draggingButton;
	
	protected UISetupScreen(Screen parentScreen) {
		super(new TextComponent(EpicFightMod.MODID + ".gui.configuration.ui_setup"));
		
		this.parentScreen = parentScreen;
		this.config = EpicFightMod.CLIENT_INGAME_CONFIG;
	}
	
	@Override
	public void init() {
		int weaponInnateX = this.config.weaponInnateXBase.getValue().positionGetter.apply(this.width, this.config.weaponInnateX.getValue());
		int weaponInnateY = this.config.weaponInnateYBase.getValue().positionGetter.apply(this.height, this.config.weaponInnateY.getValue());
		
		//Weapon innate icon
		this.addRenderableWidget(new UIComponent(weaponInnateX, weaponInnateY, this.config.weaponInnateX, this.config.weaponInnateY, this.config.weaponInnateXBase, this.config.weaponInnateYBase
			, 32, 32, 0, 0, 1, 1, 1, 1, 0, 163, 184, this, new ResourceLocation(EpicFightMod.MODID, "textures/gui/skills/sweeping_edge.png")
		));
		
		int staminaX = this.config.staminaBarXBase.getValue().positionGetter.apply(this.width, this.config.staminaBarX.getValue());
		int staminaY = this.config.staminaBarYBase.getValue().positionGetter.apply(this.height, this.config.staminaBarY.getValue());
		
		//Stamina bar
		this.addRenderableWidget(new UIComponent(staminaX, staminaY, this.config.staminaBarX, this.config.staminaBarY, this.config.staminaBarXBase, this.config.staminaBarYBase
			, 118, 4, 2, 38, 237, 9, 256, 256, 255, 128, 64, this, new ResourceLocation(EpicFightMod.MODID, "textures/gui/battle_icons.png")
		));
		
		int chargingBarX = this.config.chargingBarXBase.getValue().positionGetter.apply(this.width, this.config.chargingBarX.getValue());
		int chargingBarY = this.config.chargingBarYBase.getValue().positionGetter.apply(this.height, this.config.chargingBarY.getValue());
		
		//Charging bar
		this.addRenderableWidget(new UIComponent(chargingBarX, chargingBarY, this.config.chargingBarX, this.config.chargingBarY, this.config.chargingBarXBase, this.config.chargingBarYBase
			, 238, 13, 1, 71, 237, 13, 256, 256, 255, 255, 255, this, new ResourceLocation(EpicFightMod.MODID, "textures/gui/battle_icons.png")
		));
		
		int passivesX = this.config.passivesXBase.getValue().positionGetter.apply(this.width, this.config.passivesX.getValue());
		int passivesY = this.config.passivesYBase.getValue().positionGetter.apply(this.height, this.config.passivesY.getValue());
		
		//Passive skill icons
		this.addRenderableWidget(new PassiveUIComponent(passivesX, passivesY, this.config.passivesX, this.config.passivesY, this.config.passivesXBase, this.config.passivesYBase, this.config.passivesAlignDirection
			, 24, 24, 0, 0, 1, 1, 1, 1, 255, 255, 255, this, new ResourceLocation(EpicFightMod.MODID, "textures/gui/skills/guard.png"), new ResourceLocation(EpicFightMod.MODID, "textures/gui/skills/berserker.png")
		));
	}
	
	@Override
	public boolean mouseClicked(double x, double y, int pressType) {
		for (GuiEventListener guieventlistener : this.children()) {
			if (guieventlistener instanceof UIComponent uiComponent) {
				if (uiComponent.popupScreen.isOpen() && uiComponent.popupScreen.mouseClicked(x, y, pressType)) {
					this.setFocused(guieventlistener);
					
					if (pressType == 0) {
						this.setDragging(true);
					}

					return true;
				}
			}
			
			if (guieventlistener.mouseClicked(x, y, pressType)) {
				this.setFocused(guieventlistener);
				
				if (pressType == 0) {
					this.setDragging(true);
				}

				return true;
			}
		}

		return false;
	}
	
	@Override
	public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(poseStack, mouseY);
		super.render(poseStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose() {
		this.minecraft.setScreen(this.parentScreen);
	}
	
	public void beginToDrag(UIComponent button) {
		this.draggingButton = button;
	}
	
	public void endDragging() {
		this.draggingButton = null;
	}
	
	public boolean isDraggingComponent(UIComponent button) {
		return this.draggingButton == button;
	}
}