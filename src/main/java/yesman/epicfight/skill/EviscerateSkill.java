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

public class EviscerateSkill extends SpecialAttackSkill {
	private static final UUID EVENT_UUID = UUID.fromString("f082557a-b2f9-11eb-8529-0242ac130003");
	private StaticAnimation first;
	private StaticAnimation second;
	
	public EviscerateSkill(float consumption, String skillName) {
		super(consumption, skillName);
		this.first = Animations.EVISCERATE_FIRST;
		this.second = Animations.EVISCERATE_SECOND;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.executer.getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (event.getAnimationId() == Animations.EVISCERATE_FIRST.getId()) {
				List<LivingEntity> hitEnemies = event.getAttackedEntity();
				if (hitEnemies.size() > 0 && hitEnemies.get(0).isAlive()) {
					event.getPlayerData().reserverAnimationSynchronize(this.second);
					event.getPlayerData().getServerAnimator().getPlayerFor(null).resetPlayer();
					event.getPlayerData().currentlyAttackedEntity.clear();
					this.second.onUpdate(event.getPlayerData());
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
		executer.playAnimationSynchronize(this.first, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
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