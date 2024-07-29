package yesman.epicfight.skill.dodge;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.StaticAnimationProvider;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class DodgeSkill extends Skill {
	public static class Builder extends Skill.Builder<DodgeSkill> {
		protected StaticAnimationProvider[] animations;
		
		public Builder setCategory(SkillCategory category) {
			this.category = category;
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
		
		public Builder setCreativeTab(CreativeModeTab tab) {
			this.tab = tab;
			return this;
		}
		
		public Builder setAnimations(StaticAnimationProvider... animations) {
			this.animations = animations;
			return this;
		}
	}
	
	public static Builder createDodgeBuilder() {
		return (new Builder()).setCategory(SkillCategories.DODGE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
	}
	
	protected final StaticAnimationProvider[] animations;
	
	public DodgeSkill(Builder builder) {
		super(builder);
		
		this.animations = builder.animations;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		Input input = executer.getOriginal().input;
		float pulse = Mth.clamp(0.3F + EnchantmentHelper.getSneakingSpeedBonus(executer.getOriginal()), 0.0F, 1.0F);
		input.tick(false, pulse);
		
        int forward = input.up ? 1 : 0;
        int backward = input.down ? -1 : 0;
        int left = input.left ? 1 : 0;
        int right = input.right ? -1 : 0;
		int vertic = forward + backward;
		int horizon = left + right;
		float yRot = Minecraft.getInstance().gameRenderer.getMainCamera().getYRot();
		float degree = -(90 * horizon * (1 - Math.abs(vertic)) + 45 * vertic * horizon) + yRot;
		
		CPExecuteSkill packet = new CPExecuteSkill(executer.getSkill(this).getSlotId());
		packet.getBuffer().writeInt(vertic >= 0 ? 0 : 1);
		packet.getBuffer().writeFloat(degree);
		
		return packet;
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.consumption));
		return list;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		int i = args.readInt();
		float yRot = args.readFloat();
		
		executer.playAnimationSynchronized(this.animations[i].get(), 0);
		executer.setModelYRot(yRot, true);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.isInAir() || !playerState.canUseSkill()) && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable() && executer.getOriginal().getVehicle() == null;
	}
}