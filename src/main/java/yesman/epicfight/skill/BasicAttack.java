package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.entity.eventlistener.BasicAttackEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSExecuteSkill;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;

public class BasicAttack extends Skill {
	private static final SkillDataKey<Integer> COMBO_COUNTER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final UUID EVENT_UUID = UUID.fromString("a42e0198-fdbc-11eb-9a03-0242ac130003");
	
	public BasicAttack() {
		super(SkillCategory.BASIC_ATTACK, 0, ActivateType.ONE_SHOT, Resource.NONE, "basic_attack");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(COMBO_COUNTER);
		
		container.executer.getEventListener().addEventListener(EventType.ACTION_EVENT, EVENT_UUID, (event) -> {
			if (!event.getAnimation().isBasicAttackAnimation()) {
				container.getDataManager().setData(COMBO_COUNTER, 0);
			}
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ACTION_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		ModNetworkManager.sendToServer(new CTSExecuteSkill(this.slot.getIndex(), true, args));
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		PlayerEntity player = executer.getOriginalEntity();
		return !(player.isSpectator() || player.isElytraFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canBasicAttack());
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		if (executer.getEventListener().activateEvents(EventType.BASIC_ATTACK_EVENT, new BasicAttackEvent(executer))) {
			return;
		}
		CapabilityItem cap = executer.getHeldItemCapability(Hand.MAIN_HAND);
		StaticAnimation attackMotion = null;
		ServerPlayerEntity player = executer.getOriginalEntity();
		SkillDataManager dataManager = executer.getSkill(this.slot).getDataManager();
		int comboCounter = dataManager.getDataValue(COMBO_COUNTER);
		
		if (player.isPassenger()) {
			Entity entity = player.getRidingEntity();
			if ((entity instanceof IJumpingMount && ((IJumpingMount)entity).canJump()) && cap.canUseOnMount() && cap.getMountAttackMotion() != null) {
				attackMotion = cap.getMountAttackMotion().get(comboCounter);
				comboCounter++;
				comboCounter %= cap.getMountAttackMotion().size();
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
			executer.playAnimationSynchronize(attackMotion, 0);
		}
		executer.updateEntityState();
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		if (container.executer.getTickSinceLastAction() > 10 && container.getDataManager().getDataValue(COMBO_COUNTER) > 0) {
			container.getDataManager().setData(COMBO_COUNTER, 0);
		}
	}
}