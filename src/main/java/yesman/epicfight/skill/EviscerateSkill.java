package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EviscerateSkill extends SpecialAttackSkill {
	private static final UUID EVENT_UUID = UUID.fromString("f082557a-b2f9-11eb-8529-0242ac130003");
	private StaticAnimation first;
	private StaticAnimation second;
	
	public EviscerateSkill(Builder<? extends Skill> builder) {
		super(builder);
		this.first = Animations.EVISCERATE_FIRST;
		this.second = Animations.EVISCERATE_SECOND;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (event.getAnimationId() == Animations.EVISCERATE_FIRST.getId()) {
				List<LivingEntity> hitEnemies = event.getHitEntity();
				
				if (hitEnemies.size() > 0 && hitEnemies.get(0).isAlive()) {
					event.getPlayerPatch().reserveAnimation(this.second);
					event.getPlayerPatch().getServerAnimator().getPlayerFor(null).reset();
					event.getPlayerPatch().currentlyAttackedEntity.clear();
					this.second.tick(event.getPlayerPatch());
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
		executer.playAnimationSynchronized(this.first, 0);
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
	public SpecialAttackSkill registerPropertiesToAnimation() {
		AttackAnimation _first = ((AttackAnimation)this.first);
		AttackAnimation _second = ((AttackAnimation)this.second);
		_first.phases[0].addProperties(this.properties.get(0).entrySet());
		_second.phases[0].addProperties(this.properties.get(1).entrySet());
		
		return this;
	}
}