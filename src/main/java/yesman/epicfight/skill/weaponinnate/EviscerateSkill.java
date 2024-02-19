package yesman.epicfight.skill.weaponinnate;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.AnimationProvider.AttackAnimationProvider;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EviscerateSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("f082557a-b2f9-11eb-8529-0242ac130003");
	private AttackAnimationProvider first;
	private AttackAnimationProvider second;
	
	public EviscerateSkill(Builder<? extends Skill> builder) {
		super(builder);
		this.first = () -> (AttackAnimation)Animations.EVISCERATE_FIRST;
		this.second = () -> (AttackAnimation)Animations.EVISCERATE_SECOND;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (Animations.EVISCERATE_FIRST.equals(event.getAnimation())) {
				List<LivingEntity> hurtEntities = event.getPlayerPatch().getCurrenltyHurtEntities();
				
				if (hurtEntities.size() > 0 && hurtEntities.get(0).isAlive()) {
					event.getPlayerPatch().reserveAnimation(this.second.get());
					event.getPlayerPatch().getServerAnimator().getPlayerFor(null).reset();
					event.getPlayerPatch().getCurrenltyHurtEntities().clear();
					this.second.get().tick(event.getPlayerPatch());
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		executer.playAnimationSynchronized(this.first.get(), 0);
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "First Strike:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Second Strike:");
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		this.first.get().phases[0].addProperties(this.properties.get(0).entrySet());
		this.second.get().phases[0].addProperties(this.properties.get(1).entrySet());
		
		return this;
	}
}