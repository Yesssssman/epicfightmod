package yesman.epicfight.skill.mover;

import java.util.UUID;

import net.minecraft.client.KeyMapping;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.LevelUtil;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPPlayAnimation;
import yesman.epicfight.network.server.SPSkillExecutionFeedback;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.ChargeableSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class DemolitionLeapSkill extends Skill implements ChargeableSkill {
	private static final SkillDataKey<Boolean> PROTECT_NEXT_FALL = SkillDataKey.createDataKey(SkillDataManager.ValueType.BOOLEAN);
	private static final UUID EVENT_UUID = UUID.fromString("3d142bf4-0dcd-11ee-be56-0242ac120002");
	
	public static Skill.Builder<DemolitionLeapSkill> createChargeJumpBuilder() {
		return (new Builder<DemolitionLeapSkill>())
					.setCategory(SkillCategories.MOVER)
					.setResource(Resource.STAMINA);
	}
	
	private StaticAnimation chargingAnimation;
	private StaticAnimation shootAnimation;
	
	public DemolitionLeapSkill(Builder<? extends Skill> builder) {
		super(builder);
		
		this.chargingAnimation = Animations.BIPED_DEMOLITION_LEAP_CHARGING;
		this.shootAnimation = Animations.BIPED_DEMOLITION_LEAP;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(PROTECT_NEXT_FALL);
		
		PlayerEventListener listener = container.getExecuter().getEventListener();
		
		listener.addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().isChargingSkill(this)) {
				event.getMovementInput().jumping = false;
			}
		});
		
		listener.addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			if (event.getDamageSource().isFall() && container.getDataManager().getDataValue(PROTECT_NEXT_FALL)) {
				float damage = event.getAmount();
				event.setAmount(damage * 0.5F);
				event.setCanceled(true);
				
				container.getDataManager().setData(PROTECT_NEXT_FALL, false);
			}
		}, 1);
		
		listener.addEventListener(EventType.FALL_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setData(PROTECT_NEXT_FALL, false);
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID, 1);
		container.getExecuter().getEventListener().removeListener(EventType.FALL_EVENT, EVENT_UUID);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		return super.isExecutableState(executer) && executer.getOriginal().isOnGround();
	}
	
	@Override
	public void cancelOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		super.cancelOnClient(executer, args);
		executer.resetSkillCharging();
		executer.playAnimationSynchronized(Animations.BIPED_IDLE, 0.0F);
	}
	
	@Override
	public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		args.readInt(); // discard raw charging ticks
		int ticks = args.readInt();
		int modifiedTicks = (int)(7.4668F * Math.log10(ticks + 1.0F) / Math.log10(2));
		Vec3f jumpDirection = new Vec3f(0, modifiedTicks * 0.05F, 0);
		float xRot = Mth.clamp(70.0F + Mth.clamp(executer.getCameraXRot(), -90.0F, 0.0F), 0.0F, 70.0F);
		
		jumpDirection.add(0.0F, (xRot / 70.0F) * 0.05F, 0.0F);
		jumpDirection.rotate(xRot, Vec3f.X_AXIS);
		jumpDirection.rotate(-executer.getCameraYRot(), Vec3f.Y_AXIS);
		
		executer.getOriginal().setDeltaMovement(jumpDirection.toDoubleVector());
		executer.resetSkillCharging();
	}
	
	@Override
	public void gatherChargingArguemtns(LocalPlayerPatch caster, ControllEngine controllEngine, FriendlyByteBuf buffer) {
		// Set player charging skill cause it won't be fired on feedback packet cause it jumped
		controllEngine.setChargingKey(SkillSlots.MOVER, this.getKeyMapping());
		caster.startSkillCharging(this);
	}
	
	@Override
	public void startCharging(PlayerPatch<?> caster) {
		caster.getAnimator().playAnimation(this.chargingAnimation, 0.0F);
		
		if (!caster.isLogicalClient()) {
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPPlayAnimation(this.chargingAnimation, 0.0F, caster), caster.getOriginal());
		}
	}
	
	@Override
	public void resetCharging(PlayerPatch<?> caster) {
	}
	
	@Override
	public void castSkill(ServerPlayerPatch caster, SkillContainer skillContainer, int chargingTicks, SPSkillExecutionFeedback feedbackPacket, boolean onMaxTick) {
		if (onMaxTick) {
			feedbackPacket.setFeedbackType(SPSkillExecutionFeedback.FeedbackType.EXPIRED);
		} else {
			caster.playSound(EpicFightSounds.ROCKET_JUMP, 1.0F, 0.0F, 0.0F);
			caster.playSound(EpicFightSounds.ENTITY_MOVE, 1.0F, 0.0F, 0.0F);
			
			int accumulatedTicks = caster.getChargingAmount();
			
			LevelUtil.circleSlamFracture(null, caster.getOriginal().level, caster.getOriginal().position().subtract(0, 1, 0), accumulatedTicks * 0.05D, true, false, false);
			Vec3 entityEyepos = caster.getOriginal().getEyePosition();
			EpicFightParticles.AIR_BURST.get().spawnParticleWithArgument(caster.getOriginal().getLevel(), entityEyepos.x, entityEyepos.y, entityEyepos.z, 0.0D, 0.0D, 2 + 0.05D * chargingTicks);
			
			caster.playAnimationSynchronized(this.shootAnimation, 0.0F);
			feedbackPacket.getBuffer().writeInt(accumulatedTicks);
			skillContainer.getDataManager().setData(PROTECT_NEXT_FALL, true);
		}
	}
	
	@Override
	public int getAllowedMaxChargingTicks() {
		return 80;
	}
	
	@Override
	public int getMaxChargingTicks() {
		return 40;
	}
	
	@Override
	public int getMinChargingTicks() {
		return 12;
	}
	
	@Override
	public KeyMapping getKeyMapping() {
		return EpicFightKeyMappings.MOVER_SKILL;
	}
	
	@Override
	public void chargingTick(PlayerPatch<?> caster) {
		int chargingTicks = caster.getSkillChargingTicks();
		
		if (chargingTicks % 5 == 0 && caster.getAccumulatedChargeAmount() < this.getMaxChargingTicks()) {
			if (caster.consumeStamina(this.consumption)) {
				caster.setChargingAmount(caster.getChargingAmount() + 5);
			}
		}
	}
}