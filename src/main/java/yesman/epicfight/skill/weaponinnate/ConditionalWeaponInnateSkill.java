package yesman.epicfight.skill.weaponinnate;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.AttackAnimationProvider;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class ConditionalWeaponInnateSkill extends WeaponInnateSkill {
	public static class Builder extends Skill.Builder<ConditionalWeaponInnateSkill> {
		protected Function<ServerPlayerPatch, Integer> selector;
		protected AttackAnimationProvider[] animations;
		
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
		
		public Builder setSelector(Function<ServerPlayerPatch, Integer> selector) {
			this.selector = selector;
			return this;
		}
		
		public Builder setAnimations(AttackAnimationProvider... animations) {
			this.animations = animations;
			return this;
		}
	}
	
	public static ConditionalWeaponInnateSkill.Builder createConditionalWeaponInnateBuilder() {
		return (new ConditionalWeaponInnateSkill.Builder()).setCategory(SkillCategories.WEAPON_INNATE).setResource(Resource.WEAPON_CHARGE);
	}
	
	protected final AttackAnimationProvider[] attackAnimations;
	protected final Function<ServerPlayerPatch, Integer> selector;
	
	public ConditionalWeaponInnateSkill(ConditionalWeaponInnateSkill.Builder builder) {
		super(builder);
		this.properties = Lists.newArrayList();
		this.attackAnimations = builder.animations;
		this.selector = builder.selector;
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strikes:");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		for (AttackAnimationProvider animationProvider : this.attackAnimations) {
			AttackAnimation anim = animationProvider.get();
			
			for (Phase phase : anim.phases) {
				phase.addProperties(this.properties.get(0).entrySet());
			}
		}
		
		return this;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		this.playSkillAnimation(executer);
		super.executeOnServer(executer, args);
	}
	
	protected int getAnimationInCondition(ServerPlayerPatch executer) {
		return this.selector.apply(executer);
	}
	
	protected void playSkillAnimation(ServerPlayerPatch executer) {
		executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)].get(), 0);
	}
}