package yesman.epicfight.skill;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public interface ChargeableSkill {
	/**
	 * When player starts to charge skills
	 * @param PlayerPatch caster
	 */
	public void startCharging(PlayerPatch<?> caster);
	
	/**
	 * When player takes another actio while charging
	 * @param PlayerPatch caster
	 */
	public void resetCharging(PlayerPatch<?> caster);
	
	/**
	 * Max charging ticks players can persists
	 * @return
	 */
	public int getAllowedMaxChargingTicks();
	
	/**
	 * An limitation value for charging that returns at {@link PlayerPatch#getSkillChargingTime}
	 * @return
	 */
	public int getMaxChargingTicks();
	
	/**
	 * A required minimal charging tick to execute the skill
	 * @return
	 */
	public int getMinChargingTicks();
	
	/**
	 * Called each tick during charging skill
	 */
	default void chargingTick(PlayerPatch<?> caster) {
		caster.setChargingAmount(caster.getChargingAmount() + 1);
	}
	
	/**
	 * Get how many ticks the player charged
	 * Default: (current tick - charging begin tick)
	 * @param accumulatedTicks
	 */
	default int getChargingAmount(PlayerPatch<?> caster) {
		return caster.getChargingAmount();
	}
	
	/**
	 * Called when player finished charging and executes skill
	 * @param ServerPlayerPatch caster
	 * @param chargingTicks
	 * @param onMaxTick
	 */
	public void castSkill(ServerPlayerPatch caster, SkillContainer skillContainer, int chargingTicks, SPSkillExecutionFeedback feedbackPacket, boolean onMaxTick);
	
	@OnlyIn(Dist.CLIENT)
	public void gatherChargingArguemtns(LocalPlayerPatch caster, ControllEngine controllEngine, FriendlyByteBuf buffer);
	
	@OnlyIn(Dist.CLIENT)
	public KeyMapping getKeyMapping();
	
	default Skill asSkill() {
		return (Skill)this;
	}
}