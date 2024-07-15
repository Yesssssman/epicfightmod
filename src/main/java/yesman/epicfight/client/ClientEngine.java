package yesman.epicfight.client;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.events.engine.RenderEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

@OnlyIn(Dist.CLIENT)
public class ClientEngine {
	private static ClientEngine instance;
	
	public static ClientEngine getInstance() {
		return instance;
	}
	
	public Minecraft minecraft;
	public RenderEngine renderEngine;
	public ControllEngine controllEngine;
	private boolean vanillaModelDebuggingMode = false;
	
	public ClientEngine() {
		instance = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.controllEngine = new ControllEngine();
	}
	
	public boolean switchVanillaModelDebuggingMode() {
		this.vanillaModelDebuggingMode = !this.vanillaModelDebuggingMode;
		return this.vanillaModelDebuggingMode;
	}
	
	public boolean isVanillaModelDebuggingMode() {
		return this.vanillaModelDebuggingMode;
	}
	
	@Nullable
	public LocalPlayerPatch getPlayerPatch() {
		return EpicFightCapabilities.getEntityPatch(this.minecraft.player, LocalPlayerPatch.class);
	}
	
	public boolean isBattleMode() {
		LocalPlayerPatch localPlayerPatch = EpicFightCapabilities.getEntityPatch(this.minecraft.player, LocalPlayerPatch.class);
		
		if (localPlayerPatch == null) {
			return false;
		}
		
		return localPlayerPatch.isBattleMode();
	}
}