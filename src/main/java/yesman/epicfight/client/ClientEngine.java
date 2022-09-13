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
	public static ClientEngine instance;
	public Minecraft minecraft;
	public RenderEngine renderEngine;
	public ControllEngine controllEngine;
	private boolean armorModelDebuggingMode;
	
	public ClientEngine() {
		instance = this;
		this.minecraft = Minecraft.getInstance();
		this.renderEngine = new RenderEngine();
		this.controllEngine = new ControllEngine();
	}
	
	public boolean switchArmorModelDebuggingMode() {
		this.armorModelDebuggingMode = !this.armorModelDebuggingMode;
		return this.armorModelDebuggingMode;
	}
	
	public boolean isArmorModelDebuggingMode() {
		return this.armorModelDebuggingMode;
	}
	
	@Nullable
	public LocalPlayerPatch getPlayerPatch() {
		return (LocalPlayerPatch) this.minecraft.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
	}
}