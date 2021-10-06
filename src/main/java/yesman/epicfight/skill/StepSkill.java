package yesman.epicfight.skill;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSExecuteSkill;

public class StepSkill extends DodgeSkill {
	public StepSkill(float consumption, String skillName, StaticAnimation... animation) {
		super(consumption, skillName, animation);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		int forward = args.readInt();
		int backward = args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int vertic = forward + backward;
		int horizon = left + right;
		int degree = vertic == 0 ? 0 : -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
		int animation;
		
		if (vertic == 0) {
			if (horizon == 0) {
				animation = 0;
			} else {
				animation = horizon >= 0 ? 2 : 3;
			}
		} else {
			animation = vertic >= 0 ? 0 : 1;
		}
		
		CTSExecuteSkill packet = new CTSExecuteSkill(this.slot.getIndex());
		packet.getBuffer().writeInt(animation);
		packet.getBuffer().writeFloat(degree);
		
		ModNetworkManager.sendToServer(packet);
	}
}
