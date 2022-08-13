package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.BasicAttackEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class BasicAttack extends Skill {
	private static final SkillDataKey<Integer> COMBO_COUNTER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final UUID EVENT_UUID = UUID.fromString("a42e0198-fdbc-11eb-9a03-0242ac130003");
	
	public static Skill.Builder<BasicAttack> createBuilder() {
		return (new Builder<BasicAttack>(new ResourceLocation(EpicFightMod.MODID, "basic_attack"))).setCategory(SkillCategories.BASIC_ATTACK).setConsumption(0.0F).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.NONE);
	}
	
	public BasicAttack(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(COMBO_COUNTER);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ACTION_EVENT_SERVER, EVENT_UUID, (event) -> {
			if (!event.getAnimation().isBasicAttackAnimation()) {
				container.getDataManager().setData(COMBO_COUNTER, 0);
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
		
		if (executer.getEventListener().triggerEvents(EventType.BASIC_ATTACK_EVENT, new BasicAttackEvent(executer))) {
			return;
		}
		
		CapabilityItem cap = executer.getHoldingItemCapability(InteractionHand.MAIN_HAND);
		StaticAnimation attackMotion = null;
		ServerPlayer player = executer.getOriginal();
		SkillDataManager dataManager = executer.getSkill(this.category).getDataManager();
		int comboCounter = dataManager.getDataValue(COMBO_COUNTER);
		
		if (player.isPassenger()) {
			Entity entity = player.getVehicle();
			
			if ((entity instanceof PlayerRideableJumping && ((PlayerRideableJumping)entity).canJump()) && cap.availableOnHorse() && cap.getMountAttackMotion() != null) {
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
		
		dataManager.setData(COMBO_COUNTER, comboCounter);
		
		if (attackMotion != null) {
			executer.playAnimationSynchronized(attackMotion, 0);
		}
		
		executer.updateEntityState();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		if (container.getExecuter().getTickSinceLastAction() > 10 && container.getDataManager().getDataValue(COMBO_COUNTER) > 0) {
			container.getDataManager().setData(COMBO_COUNTER, 0);
		}
	}
}