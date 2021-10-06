package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCResetBasicAttackCool;

public class LethalSlicingSkill extends SpecialAttackSkill {
	private static final UUID EVENT_UUID = UUID.fromString("bfa79c04-97a5-11eb-a8b3-0242ac130003");
	private StaticAnimation elbow;
	private StaticAnimation swing;
	private StaticAnimation doubleSwing;
	
	public LethalSlicingSkill(float consumption, String skillName) {
		super(consumption, skillName);
		this.elbow = Animations.LETHAL_SLICING;
		this.swing = Animations.LETHAL_SLICING_ONCE;
		this.doubleSwing = Animations.LETHAL_SLICING_TWICE;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.executer.getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event)->{
			if (event.getAnimationId() == Animations.LETHAL_SLICING.getId()) {
				List<LivingEntity> hitEnemies = event.getAttackedEntity();
				if (hitEnemies.size() <= 1) {
					event.getPlayerData().reserverAnimationSynchronize(this.swing);
				} else if (hitEnemies.size() > 1) {
					event.getPlayerData().reserverAnimationSynchronize(this.doubleSwing);
				}
			}
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(this.elbow, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Elbow:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Each Strike:");
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		AttackAnimation _elbow = ((AttackAnimation)this.elbow);
		AttackAnimation _swing = ((AttackAnimation)this.swing);
		AttackAnimation _doubleSwing = ((AttackAnimation)this.doubleSwing);
		_elbow.phases[0].addProperties(this.properties.get(0).entrySet());
		_swing.phases[0].addProperties(this.properties.get(1).entrySet());
		_doubleSwing.phases[0].addProperties(this.properties.get(1).entrySet());
		_doubleSwing.phases[1].addProperties(this.properties.get(1).entrySet());
		
		return this;
	}
}