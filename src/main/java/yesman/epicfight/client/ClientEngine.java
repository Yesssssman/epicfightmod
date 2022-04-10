package yesman.epicfight.client;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPToggleMode;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

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
				this.minecraft.options.setCameraType(CameraType.FIRST_PERSON);
			}
			
			this.getPlayerPatch().setBattleMode(false);
			EpicFightNetworkManager.sendToServer(new CPToggleMode(false));
		}
	}
	
	public void switchToBattleMode() {
		if (this.playerActingMode != PlayerActingMode.BATTLE) {
			this.playerActingMode = PlayerActingMode.BATTLE;
			this.renderEngine.guiSkillBar.slideUp();
			
			if (EpicFightMod.CLIENT_INGAME_CONFIG.cameraAutoSwitch.getValue()) {
				this.minecraft.options.setCameraType(CameraType.THIRD_PERSON_BACK);
			}
			
			this.getPlayerPatch().setBattleMode(true);
			EpicFightNetworkManager.sendToServer(new CPToggleMode(true));
		}
	}
	
	public PlayerActingMode getPlayerActingMode() {
		return this.playerActingMode;
	}
	
	public boolean isBattleMode() {
		return this.playerActingMode == PlayerActingMode.BATTLE;
	}
	
	public LocalPlayerPatch getPlayerPatch() {
		return (LocalPlayerPatch) minecraft.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
	}
	
	public static enum PlayerActingMode {
		MINING, BATTLE
	}
}