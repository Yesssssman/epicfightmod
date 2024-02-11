package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.BasicAttackEvent;
import yesman.epicfight.world.entity.eventlistener.ComboCounterHandleEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.world.entity.eventlistener.SkillConsumeEvent;

public class BasicAttack extends Skill {
	private static final UUID EVENT_UUID = UUID.fromString("a42e0198-fdbc-11eb-9a03-0242ac130003");
	
	public static Skill.Builder<BasicAttack> createBasicAttackBuilder() {
		return (new Builder<BasicAttack>()).setCategory(SkillCategories.BASIC_ATTACK).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.NONE);
	}
	
	public static void setComboCounterWithEvent(ComboCounterHandleEvent.Causal reason, ServerPlayerPatch playerpatch, SkillContainer container, StaticAnimation causalAnimation, int value) {
		int prevValue = container.getDataManager().getDataValue(SkillDataKeys.COMBO_COUNTER.get());
		ComboCounterHandleEvent comboResetEvent = new ComboCounterHandleEvent(reason, playerpatch, causalAnimation, prevValue, value);
		container.getExecuter().getEventListener().triggerEvents(EventType.COMBO_COUNTER_HANDLE_EVENT, comboResetEvent);
		container.getDataManager().setData(SkillDataKeys.COMBO_COUNTER.get(), comboResetEvent.getNextValue());
	}
	
	public BasicAttack(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (!event.getAnimation().isBasicAttackAnimation() && event.getAnimation().getProperty(ActionAnimationProperty.RESET_PLAYER_COMBO_COUNTER).orElse(true)) {
				CapabilityItem item = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
				
				if (item.shouldCancelCombo(event.getPlayerPatch())) {
					setComboCounterWithEvent(ComboCounterHandleEvent.Causal.ACTION_ANIMATION_RESET, event.getPlayerPatch(), container, event.getAnimation(), 0);
				}
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		Player player = executer.getOriginal();
		
		return !(player.isSpectator() || executer.isUnstable() || !playerState.canBasicAttack());
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		SkillConsumeEvent event = new SkillConsumeEvent(executer, this, this.resource, true);
		executer.getEventListener().triggerEvents(EventType.SKILL_CONSUME_EVENT, event);
		
		if (!event.isCanceled()) {
			event.getResourceType().consumer.consume(this, executer, event.getAmount());
		}
		
		if (executer.getEventListener().triggerEvents(EventType.BASIC_ATTACK_EVENT, new BasicAttackEvent(executer))) {
			return;
		}
		
		CapabilityItem cap = executer.getHoldingItemCapability(InteractionHand.MAIN_HAND);
		StaticAnimation attackMotion = null;
		ServerPlayer player = executer.getOriginal();
		SkillContainer skillContainer = executer.getSkill(this);
		SkillDataManager dataManager = skillContainer.getDataManager();
		int comboCounter = dataManager.getDataValue(SkillDataKeys.COMBO_COUNTER.get());
		
		if (player.isPassenger()) {
			Entity entity = player.getVehicle();
			
			if ((entity instanceof PlayerRideableJumping ridable && ridable.canJump()) && cap.availableOnHorse() && cap.getMountAttackMotion() != null) {
				comboCounter %= cap.getMountAttackMotion().size();
				attackMotion = cap.getMountAttackMotion().get(comboCounter);
				comboCounter++;
			}
		} else {
			List<StaticAnimation> combo = cap.getAutoAttckMotion(executer);
			int comboSize = combo.size();
			boolean dashAttack = player.isSprinting();
			
			if (dashAttack) {
				comboCounter = comboSize - 2;
			} else {
				comboCounter %= comboSize - 2;
			}
			
			attackMotion = combo.get(comboCounter);
			comboCounter = dashAttack ? 0 : comboCounter + 1;
		}
		
		setComboCounterWithEvent(ComboCounterHandleEvent.Causal.ACTION_ANIMATION_RESET, executer, skillContainer, attackMotion, comboCounter);
		
		if (attackMotion != null) {
			executer.playAnimationSynchronized(attackMotion, 0);
		}
		
		executer.updateEntityState();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		if (!container.getExecuter().isLogicalClient() && container.getExecuter().getTickSinceLastAction() > 16 && container.getDataManager().getDataValue(SkillDataKeys.COMBO_COUNTER.get()) > 0) {
			setComboCounterWithEvent(ComboCounterHandleEvent.Causal.TIME_EXPIRED_RESET, (ServerPlayerPatch)container.getExecuter(), container, null, 0);
		}
	}
}