package yesman.epicfight.skill.passive;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.Sets;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EmergencyEscapeSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("4074c6de-0268-11ee-be56-0242ac120002");
	
	public static Builder createEmergencyEscapeBuilder() {
		return (new Builder()).setCategory(SkillCategories.PASSIVE).setResource(Resource.COOLDOWN);
	}
	
	public static class Builder extends Skill.Builder<EmergencyEscapeSkill> {
		protected final Set<WeaponCategory> availableWeapons = Sets.newHashSet();
		
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
		
		public Builder addAvailableWeaponCategory(WeaponCategory... wc) {
			this.availableWeapons.addAll(Arrays.asList(wc));
			return this;
		}
	}
	
	private final Set<WeaponCategory> availableWeapons;
	
	public EmergencyEscapeSkill(Builder builder) {
		super(builder);
		
		this.availableWeapons = builder.availableWeapons;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		PlayerEventListener listener = container.getExecuter().getEventListener();
		
		listener.addEventListener(EventType.SKILL_EXECUTE_EVENT, EVENT_UUID, (event) -> {
			if (event.getSkillContainer().getSkill().getCategory() == SkillCategories.DODGE && !event.isStateExecutable()
					&& this.availableWeapons.contains(container.getExecuter().getHoldingItemCapability(InteractionHand.MAIN_HAND).getWeaponCategory())) {
				
				EntityState state = container.getExecuter().getEntityState();
				DynamicAnimation animation = container.getExecuter().getAnimator().getPlayerFor(null).getAnimation().getRealAnimation();
				
				if (!state.hurt() && !state.knockDown() && animation instanceof AttackAnimation) {
					event.setStateExecutable(true);
				}
			}
		});
		
		listener.addEventListener(EventType.SKILL_CONSUME_EVENT, EVENT_UUID, (event) -> {
			if (event.getSkill().getCategory() == SkillCategories.DODGE) {
				if (!container.getExecuter().getOriginal().isCreative() && event.getSkill().getConsumption() > container.getExecuter().getStamina() && container.getStack() > 0) {
					if (event.shouldConsume()) {
						this.setStackSynchronize((ServerPlayerPatch)container.getExecuter(), container.getStack() - 1);
					}
					
					event.setResourceType(Skill.Resource.NONE);
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.SKILL_EXECUTE_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.SKILL_CONSUME_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getStack() == 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(String.format("%.1f", this.consumption));
		String availableWeapons = "";
		
		for (WeaponCategory category : this.availableWeapons) {
			availableWeapons += WeaponCategory.ENUM_MANAGER.toTranslated(category) + ", ";
		}
		
		list.add(availableWeapons);
		
		return list;
	}
}