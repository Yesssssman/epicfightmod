package maninthehouse.epicfight.skill;

import java.util.UUID;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.entity.event.PlayerEvent;
import maninthehouse.epicfight.entity.event.EntityEventListener.Event;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCLivingMotionChange;
import maninthehouse.epicfight.network.server.STCModifySkillVariable;
import maninthehouse.epicfight.network.server.STCPlayAnimation;
import maninthehouse.epicfight.network.server.STCModifySkillVariable.VariableType;
import net.minecraft.entity.player.EntityPlayerMP;

public class KatanaPassive extends Skill {
	private static final String NBT_KEY = "sheath";
	private static final UUID EVENT_UUID = UUID.fromString("a416c93a-42cb-11eb-b378-0242ac130002");

	public KatanaPassive() {
		super(SkillSlot.WEAPON_GIMMICK, 5.0F, "katana_passive");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getVariableNBT().setBoolean(NBT_KEY, false);
		container.executer.getEventListener().addEventListener(Event.ON_ACTION_SERVER_EVENT, PlayerEvent.makeEvent(EVENT_UUID, (player)->{
			container.executer.getSkill(SkillSlot.WEAPON_GIMMICK).getContaining().setCooldownSynchronize((ServerPlayerData)player, 0);
			return false;
		}));
	}
	
	@Override
	public void onDeleted(SkillContainer container) {
		container.executer.getEventListener().removeListener(Event.ON_ACTION_SERVER_EVENT, EVENT_UUID);
	}
	
	@Override
	public void onReset(SkillContainer container) {
		PlayerData<?> executer = container.executer;

		if (!executer.isRemote()) {
			EntityPlayerMP executePlayer = (EntityPlayerMP) executer.getOriginalEntity();
			container.getVariableNBT().setBoolean(NBT_KEY, false);
			
			STCLivingMotionChange msg = new STCLivingMotionChange(executePlayer.getEntityId(), 3);
			msg.setMotions(LivingMotion.IDLE, LivingMotion.WALKING, LivingMotion.RUNNING);
			msg.setAnimations(Animations.BIPED_IDLE_UNSHEATHING, Animations.BIPED_WALK_UNSHEATHING, Animations.BIPED_RUN_UNSHEATHING);
			((ServerPlayerData)executer).modifiLivingMotionToAll(msg);
			
			STCModifySkillVariable msg2 = new STCModifySkillVariable(VariableType.BOOLEAN, SkillSlot.WEAPON_GIMMICK, NBT_KEY, false);
			ModNetworkManager.sendToPlayer(msg2, executePlayer);
		}
	}
	
	@Override
	public void setCooldown(SkillContainer container, float value) {
		PlayerData<?> executer = container.executer;

		if (!executer.isRemote()) {
			if (this.cooldown < value) {
				EntityPlayerMP executePlayer = (EntityPlayerMP) executer.getOriginalEntity();
				container.getVariableNBT().setBoolean(NBT_KEY, true);
				
				STCLivingMotionChange msg = new STCLivingMotionChange(executePlayer.getEntityId(), 6);
				msg.setMotions(LivingMotion.IDLE, LivingMotion.WALKING, LivingMotion.RUNNING, LivingMotion.JUMPING, LivingMotion.KNEELING, LivingMotion.SNEAKING);
				msg.setAnimations(Animations.BIPED_IDLE_SHEATHING, Animations.BIPED_WALK_SHEATHING, Animations.BIPED_RUN_SHEATHING,
						Animations.BIPED_JUMP_SHEATHING, Animations.BIPED_KNEEL_SHEATHING, Animations.BIPED_SNEAK_SHEATHING);
				((ServerPlayerData)executer).modifiLivingMotionToAll(msg);
				
				STCModifySkillVariable msg2 = new STCModifySkillVariable(VariableType.BOOLEAN, SkillSlot.WEAPON_GIMMICK, NBT_KEY, true);
				ModNetworkManager.sendToPlayer(msg2, executePlayer);
				
				STCPlayAnimation msg3 = new STCPlayAnimation(Animations.BIPED_KATANA_SCRAP.getId(), executePlayer.getEntityId(), 0.0F, true);
				ModNetworkManager.sendToAllPlayerTrackingThisEntityWithSelf(msg3, executePlayer);
			}
		}
		
		super.setCooldown(container, value);
	}
	
	@Override
	public float getRegenTimePerTick(PlayerData<?> player) {
		return 1.0F;
	}
}