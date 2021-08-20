package maninhouse.epicfight.skill;

import java.util.UUID;

import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimation;
import maninhouse.epicfight.skill.SkillDataManager.SkillDataKey;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class KatanaPassive extends Skill {
	public static final SkillDataKey<Boolean> SHEATH = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final UUID EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac130002");
	
	public KatanaPassive() {
		super(SkillCategory.WEAPON_PASSIVE, 5.0F, ActivateType.ONE_SHOT, Resource.COOLDOWN, "katana_passive");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(SHEATH);
		container.executer.getEventListener().addEventListener(EventType.ACTION_EVENT, EVENT_UUID, (event) -> {
			container.getContaining().setConsumptionSynchronize(event.getPlayerData(), 0.0F);
			container.getContaining().setStackSynchronize(event.getPlayerData(), 0);
			return false;
		});
		container.executer.getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			this.onReset(container);
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ACTION_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
	}
	
	@Override
	public void onReset(SkillContainer container) {
		PlayerData<?> executer = container.executer;
		if (!executer.isRemote()) {
			ServerPlayerData playerdata = (ServerPlayerData)executer;
			container.getDataManager().setDataSync(SHEATH, false, playerdata.getOriginalEntity());
			(playerdata).setLivingMotionCurrentItem(executer.getHeldItemCapability(Hand.MAIN_HAND));
			container.getContaining().setConsumptionSynchronize(playerdata, 0);
		}
	}
	
	@Override
	public void setConsumption(SkillContainer container, float value) {
		PlayerData<?> executer = container.executer;
		if (!executer.isRemote()) {
			if (this.consumption < value) {
				ServerPlayerEntity serverPlayer = (ServerPlayerEntity) executer.getOriginalEntity();
				container.getDataManager().setDataSync(SHEATH, true, serverPlayer);
				((ServerPlayerData)container.executer).setLivingMotionCurrentItem(executer.getHeldItemCapability(Hand.MAIN_HAND));
				STCPlayAnimation msg3 = new STCPlayAnimation(Animations.BIPED_KATANA_SCRAP.getId(), serverPlayer.getEntityId(), 0.0F);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg3, serverPlayer);
			}
		}
		
		super.setConsumption(container, value);
	}
	
	@Override
	public boolean shouldDeactivateAutomatically(PlayerData<?> executer) {
		return true;
	}
	
	@Override
	public float getCooldownRegenPerSecond(PlayerData<?> player) {
		return player.getOriginalEntity().isHandActive() ? 0.0F : 1.0F;
	}
}