package yesman.epicfight.network;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.client.*;
import yesman.epicfight.network.server.*;

public class ModNetworkManager {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(EpicFightMod.MODID, "network_manager"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static <MSG> void sendToServer(MSG message) {
		INSTANCE.sendToServer(message);
	}
	
	public static <MSG> void sendToAll(MSG message) {
		INSTANCE.send(PacketDistributor.ALL.noArg(), message);
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntity(MSG message, Entity entity) {
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {return entity;}), message);
	}
	
	public static <MSG> void sendToPlayer(MSG message, ServerPlayerEntity player) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return player;}), message);
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayerEntity entity) {
		sendToPlayer(message, entity);
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {return entity;}), message);
	}
	
	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(id++, CTSExecuteSkill.class, CTSExecuteSkill::toBytes, CTSExecuteSkill::fromBytes, CTSExecuteSkill::handle);
		INSTANCE.registerMessage(id++, CTSPlayAnimation.class, CTSPlayAnimation::toBytes, CTSPlayAnimation::fromBytes, CTSPlayAnimation::handle);
		INSTANCE.registerMessage(id++, CTSReqSpawnInfo.class, CTSReqSpawnInfo::toBytes, CTSReqSpawnInfo::fromBytes, CTSReqSpawnInfo::handle);
		INSTANCE.registerMessage(id++, CTSRotatePlayerYaw.class, CTSRotatePlayerYaw::toBytes, CTSRotatePlayerYaw::fromBytes, CTSRotatePlayerYaw::handle);
		INSTANCE.registerMessage(id++, CTSReqPlayerInfo.class, CTSReqPlayerInfo::toBytes, CTSReqPlayerInfo::fromBytes, CTSReqPlayerInfo::handle);
		INSTANCE.registerMessage(id++, CTSToggleMode.class, CTSToggleMode::toBytes, CTSToggleMode::fromBytes, CTSToggleMode::handle);
		INSTANCE.registerMessage(id++, CTSSetPlayerTarget.class, CTSSetPlayerTarget::toBytes, CTSSetPlayerTarget::fromBytes, CTSSetPlayerTarget::handle);
		INSTANCE.registerMessage(id++, CTSChangeSkill.class, CTSChangeSkill::toBytes, CTSChangeSkill::fromBytes, CTSChangeSkill::handle);
		
		INSTANCE.registerMessage(id++, STCChangeSkill.class, STCChangeSkill::toBytes, STCChangeSkill::fromBytes, STCChangeSkill::handle);
		INSTANCE.registerMessage(id++, STCSkillExecutionFeedback.class, STCSkillExecutionFeedback::toBytes, STCSkillExecutionFeedback::fromBytes, STCSkillExecutionFeedback::handle);
		INSTANCE.registerMessage(id++, STCMobInitialSetting.class, STCMobInitialSetting::toBytes, STCMobInitialSetting::fromBytes, STCMobInitialSetting::handle);
		INSTANCE.registerMessage(id++, STCLivingMotionChange.class, STCLivingMotionChange::toBytes, STCLivingMotionChange::fromBytes, STCLivingMotionChange::handle);
		INSTANCE.registerMessage(id++, STCSetSkillValue.class, STCSetSkillValue::toBytes, STCSetSkillValue::fromBytes, STCSetSkillValue::handle);
		INSTANCE.registerMessage(id++, STCNotifyPlayerYawChanged.class, STCNotifyPlayerYawChanged::toBytes, STCNotifyPlayerYawChanged::fromBytes, STCNotifyPlayerYawChanged::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimation.class, STCPlayAnimation::toBytes, STCPlayAnimation::fromBytes, STCPlayAnimation::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTarget.class, STCPlayAnimationTarget::toBytes, STCPlayAnimationTarget::fromBytes, STCPlayAnimationTarget::handle);
		INSTANCE.registerMessage(id++, STCPlayAnimationTP.class, STCPlayAnimationTP::toBytes, STCPlayAnimationTP::fromBytes, STCPlayAnimationTP::handle);
		INSTANCE.registerMessage(id++, STCPotion.class, STCPotion::toBytes, STCPotion::fromBytes, STCPotion::handle);
		INSTANCE.registerMessage(id++, STCResetBasicAttackCool.class, STCResetBasicAttackCool::toBytes, STCResetBasicAttackCool::fromBytes, STCResetBasicAttackCool::handle);
		INSTANCE.registerMessage(id++, STCModifySkillData.class, STCModifySkillData::toBytes, STCModifySkillData::fromBytes, STCModifySkillData::handle);
		INSTANCE.registerMessage(id++, STCGameruleChange.class, STCGameruleChange::toBytes, STCGameruleChange::fromBytes, STCGameruleChange::handle);
		INSTANCE.registerMessage(id++, STCToggleMode.class, STCToggleMode::toBytes, STCToggleMode::fromBytes, STCToggleMode::handle);
	}
}