package yesman.epicfight.skill.dodge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class KnockdownWakeupSkill extends DodgeSkill {
	public KnockdownWakeupSkill(Builder builder) {
		super(builder);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		Input input = executer.getOriginal().input;
		float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(executer.getOriginal()), 0.0F, 1.0F);
		input.tick(false, pulse);
		
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
		int horizon = left + right;
		float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
		
		CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
		packet.getBuffer().writeInt(horizon >= 0 ? 0 : 1);
		packet.getBuffer().writeFloat(yRot);
		
		return packet;
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		float elapsedTime = executer.getAnimator().getPlayerFor(null).getElapsedTime();
		return !(executer.footsOnGround() || (playerState.hurt() && !playerState.knockDown())) && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && elapsedTime > 0.7F;
	}
}