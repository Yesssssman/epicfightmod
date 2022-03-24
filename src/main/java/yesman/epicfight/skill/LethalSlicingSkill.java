package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class LethalSlicingSkill extends SpecialAttackSkill {
	private static final UUID EVENT_UUID = UUID.fromString("bfa79c04-97a5-11eb-a8b3-0242ac130003");
	private AttackAnimation elbow;
	private AttackAnimation swing;
	private AttackAnimation doubleSwing;
	
	public LethalSlicingSkill(Builder<? extends Skill> builder) {
		super(builder);
		this.elbow = (AttackAnimation)Animations.LETHAL_SLICING;
		this.swing = (AttackAnimation)Animations.LETHAL_SLICING_ONCE;
		this.doubleSwing = (AttackAnimation)Animations.LETHAL_SLICING_TWICE;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.executer.getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (event.getAnimationId() == Animations.LETHAL_SLICING.getId()) {
				List<LivingEntity> hitEnemies = event.getHitEntity();
				if (hitEnemies.size() <= 1) {
					event.getPlayerPatch().reserveAnimation(this.swing);
				} else if (hitEnemies.size() > 1) {
					event.getPlayerPatch().reserveAnimation(this.doubleSwing);
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		executer.playAnimationSynchronized(this.elbow, 0.0F);
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Elbow:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Each Strike:");
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		this.elbow.phases[0].addProperties(this.properties.get(0).entrySet());
		this.swing.phases[0].addProperties(this.properties.get(1).entrySet());
		this.doubleSwing.phases[0].addProperties(this.properties.get(1).entrySet());
		this.doubleSwing.phases[1].addProperties(this.properties.get(1).entrySet());
		return this;
	}
}