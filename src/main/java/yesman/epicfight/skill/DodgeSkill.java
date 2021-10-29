package yesman.epicfight.skill;

import io.netty.buffer.Unpooled;
import net.minecraft.client.GameSettings;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSExecuteSkill;

public class DodgeSkill extends Skill {
	public static class Builder extends Skill.Builder<DodgeSkill> {
		protected StaticAnimation[] animations;
		
		public Builder(ResourceLocation resourceLocation) {
			super(resourceLocation);
		}
		
		public Builder setCategory(SkillCategory category) {
			this.category = category;
			return this;
		}
		
		public Builder setConsumption(float consumption) {
			this.consumption = consumption;
			return this;
		}
		
		public Builder setMaxDuration(int maxDuration) {
			this.maxDuration = maxDuration;
			return this;
		}
		
		public Builder setMaxStack(int maxStack) {
			this.maxStack = maxStack;
			return this;
		}
		
		public Builder setRequiredXp(int requiredXp) {
			this.requiredXp = requiredXp;
			return this;
		}
		
		public Builder setActivateType(ActivateType activateType) {
			this.activateType = activateType;
			return this;
		}
		
		public Builder setResource(Resource resource) {
			this.resource = resource;
			return this;
		}
		
		public Builder setAnimations(StaticAnimation... animations) {
			this.animations = animations;
			return this;
		}
	}
	
	public static Builder createBuilder(ResourceLocation registryName) {
		return (new Builder(registryName)).setCategory(SkillCategory.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA).setRequiredXp(5);
	}
	
	protected final StaticAnimation[] animations;
	
	public DodgeSkill(Builder builder) {
		super(builder);
		this.animations = builder.animations;
	}
	
	@OnlyIn(Dist.CLIENT)
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
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		int forward = args.readInt();
		int backward = args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int vertic = forward + backward;
		int horizon = left + right;
		int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
		
		CTSExecuteSkill packet = new CTSExecuteSkill(this.category.getIndex());
		packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1);
		packet.getBuffer().writeFloat(degree);
		
		ModNetworkManager.sendToServer(packet);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		super.executeOnServer(executer, args);
		int i = args.readInt();
		float yaw = args.readFloat();
		executer.playAnimationSynchronize(this.animations[i], 0);
		executer.changeYaw(yaw);
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isElytraFlying() || executer.currentMotion == LivingMotion.FALL || playerState == EntityState.HIT) &&
			!executer.getOriginalEntity().isInWater() && !executer.getOriginalEntity().isOnLadder();
	}
}