package yesman.epicfight.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkRegistry;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import net.minecraftforge.fmllegacy.network.simple.SimpleChannel;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.network.client.CPPlayAnimation;
import yesman.epicfight.network.client.CPReqPlayerInfo;
import yesman.epicfight.network.client.CPReqSpawnInfo;
import yesman.epicfight.network.client.CPRotatePlayerYaw;
import yesman.epicfight.network.client.CPSetPlayerTarget;
import yesman.epicfight.network.client.CPToggleMode;
import yesman.epicfight.network.server.SPAddSkill;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPChangePlayerYaw;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.network.server.SPMobInitialize;
import yesman.epicfight.network.server.SPModifySkillData;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.network.server.SPPlayAnimationAndSetTarget;
import yesman.epicfight.network.server.SPPlayAnimationAndSyncTransform;
import yesman.epicfight.network.server.SPPlayAnimationInstant;
import yesman.epicfight.network.server.SPPotion;
import yesman.epicfight.network.server.SPSetAttackTarget;
import yesman.epicfight.network.server.SPSetSkillValue;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.network.server.SPTogglePlayerMode;

public class EpicFightNetworkManager {
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
	
	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
		INSTANCE.send(PacketDistributor.PLAYER.with(() -> {return player;}), message);
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayer entity) {
		sendToPlayer(message, entity);
		INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> {return entity;}), message);
	}
	
	public static void registerPackets() {
		int id = 0;
		INSTANCE.registerMessage(id++, CPExecuteSkill.class, CPExecuteSkill::toBytes, CPExecuteSkill::fromBytes, CPExecuteSkill::handle);
		INSTANCE.registerMessage(id++, CPPlayAnimation.class, CPPlayAnimation::toBytes, CPPlayAnimation::fromBytes, CPPlayAnimation::handle);
		INSTANCE.registerMessage(id++, CPReqSpawnInfo.class, CPReqSpawnInfo::toBytes, CPReqSpawnInfo::fromBytes, CPReqSpawnInfo::handle);
		INSTANCE.registerMessage(id++, CPRotatePlayerYaw.class, CPRotatePlayerYaw::toBytes, CPRotatePlayerYaw::fromBytes, CPRotatePlayerYaw::handle);
		INSTANCE.registerMessage(id++, CPReqPlayerInfo.class, CPReqPlayerInfo::toBytes, CPReqPlayerInfo::fromBytes, CPReqPlayerInfo::handle);
		INSTANCE.registerMessage(id++, CPToggleMode.class, CPToggleMode::toBytes, CPToggleMode::fromBytes, CPToggleMode::handle);
		INSTANCE.registerMessage(id++, CPSetPlayerTarget.class, CPSetPlayerTarget::toBytes, CPSetPlayerTarget::fromBytes, CPSetPlayerTarget::handle);
		INSTANCE.registerMessage(id++, CPChangeSkill.class, CPChangeSkill::toBytes, CPChangeSkill::fromBytes, CPChangeSkill::handle);
		
		INSTANCE.registerMessage(id++, SPChangeSkill.class, SPChangeSkill::toBytes, SPChangeSkill::fromBytes, SPChangeSkill::handle);
		INSTANCE.registerMessage(id++, SPSkillExecutionFeedback.class, SPSkillExecutionFeedback::toBytes, SPSkillExecutionFeedback::fromBytes, SPSkillExecutionFeedback::handle);
		INSTANCE.registerMessage(id++, SPMobInitialize.class, SPMobInitialize::toBytes, SPMobInitialize::fromBytes, SPMobInitialize::handle);
		INSTANCE.registerMessage(id++, SPChangeLivingMotion.class, SPChangeLivingMotion::toBytes, SPChangeLivingMotion::fromBytes, SPChangeLivingMotion::handle);
		INSTANCE.registerMessage(id++, SPSetSkillValue.class, SPSetSkillValue::toBytes, SPSetSkillValue::fromBytes, SPSetSkillValue::handle);
		INSTANCE.registerMessage(id++, SPChangePlayerYaw.class, SPChangePlayerYaw::toBytes, SPChangePlayerYaw::fromBytes, SPChangePlayerYaw::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimation.class, SPPlayAnimation::toBytes, SPPlayAnimation::fromBytes, SPPlayAnimation::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimationInstant.class, SPPlayAnimation::toBytes, SPPlayAnimationInstant::fromBytes, SPPlayAnimation::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimationAndSetTarget.class, SPPlayAnimationAndSetTarget::toBytes, SPPlayAnimationAndSetTarget::fromBytes, SPPlayAnimationAndSetTarget::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimationAndSyncTransform.class, SPPlayAnimationAndSyncTransform::toBytes, SPPlayAnimationAndSyncTransform::fromBytes, SPPlayAnimationAndSyncTransform::handle);
		INSTANCE.registerMessage(id++, SPPotion.class, SPPotion::toBytes, SPPotion::fromBytes, SPPotion::handle);
		INSTANCE.registerMessage(id++, SPModifySkillData.class, SPModifySkillData::toBytes, SPModifySkillData::fromBytes, SPModifySkillData::handle);
		INSTANCE.registerMessage(id++, SPChangeGamerule.class, SPChangeGamerule::toBytes, SPChangeGamerule::fromBytes, SPChangeGamerule::handle);
		INSTANCE.registerMessage(id++, SPTogglePlayerMode.class, SPTogglePlayerMode::toBytes, SPTogglePlayerMode::fromBytes, SPTogglePlayerMode::handle);
		INSTANCE.registerMessage(id++, SPAddSkill.class, SPAddSkill::toBytes, SPAddSkill::fromBytes, SPAddSkill::handle);
		INSTANCE.registerMessage(id++, SPDatapackSync.class, SPDatapackSync::toBytes, SPDatapackSync::fromBytes, SPDatapackSync::handle);
		INSTANCE.registerMessage(id++, SPSetAttackTarget.class, SPSetAttackTarget::toBytes, SPSetAttackTarget::fromBytes, SPSetAttackTarget::handle);
	}
}