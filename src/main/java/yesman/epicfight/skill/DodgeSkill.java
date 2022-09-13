package yesman.epicfight.skill;

import io.netty.buffer.Unpooled;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

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
		return (new Builder(registryName)).setCategory(SkillCategories.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA).setRequiredXp(5);
	}
	
	protected final StaticAnimation[] animations;
	
	public DodgeSkill(Builder builder) {
		super(builder);
		this.animations = builder.animations;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public FriendlyByteBuf gatherArguments(LocalPlayerPatch executer, ControllEngine controllEngine) {
		Input input = executer.getOriginal().input;
		input.tick(false);
		
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
		
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
		buf.writeInt(forward);
		buf.writeInt(backward);
		buf.writeInt(left);
		buf.writeInt(right);
		
		return buf;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		int forward = args.readInt();
		int backward = args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int vertic = forward + backward;
		int horizon = left + right;
		int degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon);
		CPExecuteSkill packet = new CPExecuteSkill(this.category.universalOrdinal());
		packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1);
		packet.getBuffer().writeFloat(degree);
		
		return packet;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		int i = args.readInt();
		float yaw = args.readFloat();
		executer.playAnimationSynchronized(this.animations[i], 0);
		executer.changeYaw(yaw);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.isUnstable() || !playerState.canUseSkill()) && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable();
	}
}