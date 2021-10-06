package yesman.epicfight.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.PointOfView;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSToggleMode;

@OnlyIn(Dist.CLIENT)
public class ClientEngine {
	public static ClientEngine instance;
	public Minecraft minecraft;
	public RenderEngine renderEngine;
	public ControllEngine inputController;
	private PlayerActingMode playerActingMode = PlayerActingMode.MINING;
	
	public ClientEngine() {
		instance = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.inputController = new ControllEngine();
	}
	
	public void toggleActingMode() {
		if (this.playerActingMode == PlayerActingMode.MINING) {
			this.switchToBattleMode();
		} else {
			this.switchToMiningMode();
		}
	}
	
	public void switchToMiningMode() {
		if (this.playerActingMode != PlayerActingMode.MINING) {
			this.playerActingMode = PlayerActingMode.MINING;
			this.renderEngine.guiSkillBar.slideDown();
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.FIRST_PERSON);
			}
			ModNetworkManager.sendToServer(new CTSToggleMode(false));
		}
	}
	
	public void switchToBattleMode() {
		if (this.playerActingMode != PlayerActingMode.BATTLE) {
			this.playerActingMode = PlayerActingMode.BATTLE;
			this.renderEngine.guiSkillBar.slideUp();
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				Minecraft.getInstance().gameSettings.setPointOfView(PointOfView.THIRD_PERSON_BACK);
			}
			ModNetworkManager.sendToServer(new CTSToggleMode(true));
		}
	}
	
	public PlayerActingMode getPlayerActingMode() {
		return this.playerActingMode;
	}
	
	public boolean isBattleMode() {
		return this.playerActingMode == PlayerActingMode.BATTLE;
	}
	
	public ClientPlayerData getPlayerData() {
		return (ClientPlayerData) Minecraft.getInstance().player.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
	}
	
	public static enum PlayerActingMode {
		MINING, BATTLE
	}
}