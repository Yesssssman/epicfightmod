package yesman.epicfight.skill;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class SimpleSpecialAttackSkill extends SpecialAttackSkill {
	public static class Builder extends Skill.Builder<SimpleSpecialAttackSkill> {
		protected StaticAnimation attackAnimation;
		
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
		
		public Builder setAnimations(StaticAnimation attackAnimation) {
			this.attackAnimation = attackAnimation;
			return this;
		}
	}
	
	public static Builder createBuilder(ResourceLocation resourceLocation) {
		return (new Builder(resourceLocation)).setCategory(SkillCategories.WEAPON_SPECIAL_ATTACK).setResource(Resource.SPECIAL_GAUAGE);
	}
	
	protected final StaticAnimation attackAnimation;
	
	public SimpleSpecialAttackSkill(Builder builder) {
		super(builder);
		this.attackAnimation = builder.attackAnimation;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, PacketBuffer args) {
		executer.playAnimationSynchronized(this.attackAnimation, 0);
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strike:");
		
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		AttackAnimation anim = ((AttackAnimation)this.attackAnimation);
		for(Phase phase : anim.phases) {
			phase.addProperties(this.properties.get(0).entrySet());
		}
		
		return this;
	}
}