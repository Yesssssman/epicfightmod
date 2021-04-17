package maninthehouse.epicfight.skill;

import io.netty.buffer.Unpooled;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.client.events.engine.ControllEngine;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSExecuteSkill;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.PacketBuffer;

public class DodgeSkill extends Skill {
	protected final StaticAnimation[] animations;

	public DodgeSkill(SkillSlot index, float cooldown, String skillName, StaticAnimation... animation) {
		super(index, cooldown, skillName);
		this.animations = animation;
	}

	public DodgeSkill(SkillSlot index, float cooldown, int maxStack, String skillName, StaticAnimation... animation) {
		super(index, cooldown, 0, maxStack, true, skillName);
		this.animations = animation;
	}
	
	@Override
	public PacketBuffer gatherArguments(ClientPlayerData executer, ControllEngine controllEngine) {
		GameSettings gamesetting = controllEngine.gameSettings;
		
		int forward = gamesetting.keyBindForward.isKeyDown() ? 1 : 0;
		int backward = gamesetting.keyBindBack.isKeyDown() ? -1 : 0;
		int left = gamesetting.keyBindLeft.isKeyDown() ? 1 : 0;
		int right = gamesetting.keyBindRight.isKeyDown() ? -1 : 0;
		
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		
		buf.writeInt(forward);
		buf.writeInt(backward);
		buf.writeInt(left);
		buf.writeInt(right);
		
		return buf;
	}
	
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		int forward = args.readInt();
		int backward = args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int vertic = forward + backward;
		int horizon = left + right;
		int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
		
		CTSExecuteSkill packet = new CTSExecuteSkill(this.slot.getIndex());
		packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1);
		packet.getBuffer().writeFloat(degree);
		
		ModNetworkManager.sendToServer(packet);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		super.executeOnServer(executer, args);
		int i = args.readInt();
		float yaw = args.readFloat();
		
		executer.playAnimationSynchronize(animations[i], 0);
		executer.changeYaw(yaw);
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		return super.isExecutableState(executer) && !executer.getOriginalEntity().isInWater() && !executer.getOriginalEntity().isOnLadder();
	}
}