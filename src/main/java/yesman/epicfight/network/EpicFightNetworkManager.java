package yesman.epicfight.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.PacketDistributor.PacketTarget;
import net.minecraftforge.network.simple.SimpleChannel;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.client.CPChangePlayerMode;
import yesman.epicfight.network.client.CPChangeSkill;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.network.client.CPPlayAnimation;
import yesman.epicfight.network.client.CPRotateEntityModelYRot;
import yesman.epicfight.network.client.CPSetPlayerTarget;
import yesman.epicfight.network.server.SPAddLearnedSkill;
import yesman.epicfight.network.server.SPAddOrRemoveSkillData;
import yesman.epicfight.network.server.SPChangeGamerule;
import yesman.epicfight.network.server.SPChangeLivingMotion;
import yesman.epicfight.network.server.SPChangePlayerMode;
import yesman.epicfight.network.server.SPChangeSkill;
import yesman.epicfight.network.server.SPClearSkills;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.network.server.SPDatapackSyncSkill;
import yesman.epicfight.network.server.SPFracture;
import yesman.epicfight.network.server.SPModifyPlayerData;
import yesman.epicfight.network.server.SPModifySkillData;
import yesman.epicfight.network.server.SPMoveAndPlayAnimation;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.network.server.SPPlayAnimationAndSetTarget;
import yesman.epicfight.network.server.SPPlayAnimationInstant;
import yesman.epicfight.network.server.SPPotion;
import yesman.epicfight.network.server.SPRemoveSkill;
import yesman.epicfight.network.server.SPSetAttackTarget;
import yesman.epicfight.network.server.SPSetSkillValue;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.network.server.SPUpdatePlayerInput;

public class EpicFightNetworkManager {
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(EpicFightMod.MODID, "network_manager"),
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public static <MSG> void sendToServer(MSG message) {
		INSTANCE.sendToServer(message);
	}
	
	public static <MSG> void sendToClient(MSG message, PacketTarget packetTarget) {
		INSTANCE.send(packetTarget, message);
	}
	
	public static <MSG> void sendToAll(MSG message) {
		sendToClient(message, PacketDistributor.ALL.noArg());
	}

	public static <MSG> void sendToAllPlayerTrackingThisEntity(MSG message, Entity entity) {
		sendToClient(message, PacketDistributor.TRACKING_ENTITY.with(() -> entity));
	}
	
	public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
		sendToClient(message, PacketDistributor.PLAYER.with(() -> player));
	}
	
	public static <MSG> void sendToAllPlayerTrackingThisEntityWithSelf(MSG message, ServerPlayer entity) {
		sendToClient(message, PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity));
	}
	
	public static <MSG> void sendToAllPlayerTrackingThisChunkWithSelf(MSG message, LevelChunk chunk) {
		sendToClient(message, PacketDistributor.TRACKING_CHUNK.with(() -> chunk));
	}
	
	public static void registerPackets() {
		int id = 0;
		
		INSTANCE.registerMessage(id++, CPExecuteSkill.class, CPExecuteSkill::toBytes, CPExecuteSkill::fromBytes, CPExecuteSkill::handle);
		INSTANCE.registerMessage(id++, CPPlayAnimation.class, CPPlayAnimation::toBytes, CPPlayAnimation::fromBytes, CPPlayAnimation::handle);
		INSTANCE.registerMessage(id++, CPRotateEntityModelYRot.class, CPRotateEntityModelYRot::toBytes, CPRotateEntityModelYRot::fromBytes, CPRotateEntityModelYRot::handle);
		INSTANCE.registerMessage(id++, CPChangePlayerMode.class, CPChangePlayerMode::toBytes, CPChangePlayerMode::fromBytes, CPChangePlayerMode::handle);
		INSTANCE.registerMessage(id++, CPSetPlayerTarget.class, CPSetPlayerTarget::toBytes, CPSetPlayerTarget::fromBytes, CPSetPlayerTarget::handle);
		INSTANCE.registerMessage(id++, CPChangeSkill.class, CPChangeSkill::toBytes, CPChangeSkill::fromBytes, CPChangeSkill::handle);
		INSTANCE.registerMessage(id++, SPChangeSkill.class, SPChangeSkill::toBytes, SPChangeSkill::fromBytes, SPChangeSkill::handle);
		INSTANCE.registerMessage(id++, SPSkillExecutionFeedback.class, SPSkillExecutionFeedback::toBytes, SPSkillExecutionFeedback::fromBytes, SPSkillExecutionFeedback::handle);
		INSTANCE.registerMessage(id++, SPSpawnData.class, SPSpawnData::toBytes, SPSpawnData::fromBytes, SPSpawnData::handle);
		INSTANCE.registerMessage(id++, SPChangeLivingMotion.class, SPChangeLivingMotion::toBytes, SPChangeLivingMotion::fromBytes, SPChangeLivingMotion::handle);
		INSTANCE.registerMessage(id++, SPSetSkillValue.class, SPSetSkillValue::toBytes, SPSetSkillValue::fromBytes, SPSetSkillValue::handle);
		INSTANCE.registerMessage(id++, SPModifyPlayerData.class, SPModifyPlayerData::toBytes, SPModifyPlayerData::fromBytes, SPModifyPlayerData::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimation.class, SPPlayAnimation::toBytes, SPPlayAnimation::fromBytes, SPPlayAnimation::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimationInstant.class, SPPlayAnimation::toBytes, SPPlayAnimationInstant::fromBytes, SPPlayAnimation::handle);
		INSTANCE.registerMessage(id++, SPPlayAnimationAndSetTarget.class, SPPlayAnimationAndSetTarget::toBytes, SPPlayAnimationAndSetTarget::fromBytes, SPPlayAnimationAndSetTarget::handle);
		INSTANCE.registerMessage(id++, SPMoveAndPlayAnimation.class, SPMoveAndPlayAnimation::toBytes, SPMoveAndPlayAnimation::fromBytes, SPMoveAndPlayAnimation::handle);
		INSTANCE.registerMessage(id++, SPPotion.class, SPPotion::toBytes, SPPotion::fromBytes, SPPotion::handle);
		INSTANCE.registerMessage(id++, SPModifySkillData.class, SPModifySkillData::toBytes, SPModifySkillData::fromBytes, SPModifySkillData::handle);
		INSTANCE.registerMessage(id++, SPChangeGamerule.class, SPChangeGamerule::toBytes, SPChangeGamerule::fromBytes, SPChangeGamerule::handle);
		INSTANCE.registerMessage(id++, SPChangePlayerMode.class, SPChangePlayerMode::toBytes, SPChangePlayerMode::fromBytes, SPChangePlayerMode::handle);
		INSTANCE.registerMessage(id++, SPAddLearnedSkill.class, SPAddLearnedSkill::toBytes, SPAddLearnedSkill::fromBytes, SPAddLearnedSkill::handle);
		INSTANCE.registerMessage(id++, SPDatapackSync.class, SPDatapackSync::toBytes, SPDatapackSync::fromBytes, SPDatapackSync::handle);
		INSTANCE.registerMessage(id++, SPDatapackSyncSkill.class, SPDatapackSyncSkill::toBytes, SPDatapackSyncSkill::fromBytes, SPDatapackSync::handle);
		INSTANCE.registerMessage(id++, SPSetAttackTarget.class, SPSetAttackTarget::toBytes, SPSetAttackTarget::fromBytes, SPSetAttackTarget::handle);
		INSTANCE.registerMessage(id++, SPClearSkills.class, SPClearSkills::toBytes, SPClearSkills::fromBytes, SPClearSkills::handle);
		INSTANCE.registerMessage(id++, SPRemoveSkill.class, SPRemoveSkill::toBytes, SPRemoveSkill::fromBytes, SPRemoveSkill::handle);
		INSTANCE.registerMessage(id++, SPFracture.class, SPFracture::toBytes, SPFracture::fromBytes, SPFracture::handle);
		INSTANCE.registerMessage(id++, SPUpdatePlayerInput.class, SPUpdatePlayerInput::toBytes, SPUpdatePlayerInput::fromBytes, SPUpdatePlayerInput::handle);
		INSTANCE.registerMessage(id++, SPAddOrRemoveSkillData.class, SPAddOrRemoveSkillData::toBytes, SPAddOrRemoveSkillData::fromBytes, SPAddOrRemoveSkillData::handle);
	}
}