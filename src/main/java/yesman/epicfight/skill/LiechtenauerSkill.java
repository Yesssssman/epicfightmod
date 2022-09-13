package yesman.epicfight.skill;

import java.util.UUID;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class LiechtenauerSkill extends SpecialAttackSkill {
	private static final UUID EVENT_UUID = UUID.fromString("244c57c0-a837-11eb-bcbc-0242ac130002");
	private static final SkillDataKey<Integer> PARRY_MOTION_COUNTER = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	
	public LiechtenauerSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(PARRY_MOTION_COUNTER);
		
		if (!container.getExecuter().isLogicalClient()) {
			this.setMaxDurationSynchronize((ServerPlayerPatch)container.getExecuter(), this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, container.getExecuter().getOriginal()));
		}
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.isActivated()) {
				if (!event.getTarget().isAlive()) {
					this.setDurationSynchronize(event.getPlayerPatch(), container.duration + 1);
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			if (event.getAmount() > 0.0F && container.duration > 0 && this.isExecutableState(event.getPlayerPatch()) && this.canExecute(event.getPlayerPatch()) && isBlockableSource(event.getDamageSource())) {
				DamageSource damageSource = event.getDamageSource();
				boolean isFront = false;
				Vec3 sourceLocation = damageSource.getSourcePosition();
				
				if (sourceLocation != null) {
					Vec3 viewVector = event.getPlayerPatch().getOriginal().getViewVector(1.0F);
					Vec3 toSourceLocation = sourceLocation.subtract(event.getPlayerPatch().getOriginal().position()).normalize();
					
					if (toSourceLocation.dot(viewVector) > 0.0D) {
						isFront = true;
					}
				}
				
				if (isFront) {
					this.setDurationSynchronize(event.getPlayerPatch(), container.duration - 1);
					
					if (event.getPlayerPatch().getSkill(SkillCategories.GUARD).containingSkill == Skills.ACTIVE_GUARD) {
						SkillDataManager dataManager = event.getPlayerPatch().getSkill(this.getCategory()).getDataManager();
						int motionCounter = dataManager.getDataValue(PARRY_MOTION_COUNTER);
						dataManager.setDataF(PARRY_MOTION_COUNTER, (v) -> v + 1);
						motionCounter %= 2;
						
						event.getPlayerPatch().playAnimationSynchronized((motionCounter == 0) ?
								Animations.LONGSWORD_GUARD_ACTIVE_HIT1 : Animations.LONGSWORD_GUARD_ACTIVE_HIT2, 0);
					} else {
						event.getPlayerPatch().playAnimationSynchronized(Animations.LONGSWORD_GUARD_HIT, 0);
					}
					
					event.getPlayerPatch().playSound(EpicFightSounds.CLASH, -0.05F, 0.1F);
					Entity playerentity = event.getPlayerPatch().getOriginal();
					EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerLevel)playerentity.level), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, playerentity, damageSource.getDirectEntity());
					
					float knockback = 0.25F;
					
					if (damageSource instanceof ExtendedDamageSource) {
						knockback += Math.min(((ExtendedDamageSource)damageSource).getImpact() * 0.1F, 1.0F);
					}
					
					if (damageSource.getDirectEntity() instanceof LivingEntity) {
						knockback += EnchantmentHelper.getKnockbackBonus((LivingEntity)damageSource.getDirectEntity()) * 0.1F;
					}
					
					event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
					event.setCanceled(true);
					event.setResult(AttackResult.ResultType.BLOCKED);
				}
			}
		}, 0);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerPatch().getSkill(this.category).isActivated()) {
				LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
				clientPlayer.setSprinting(false);
				clientPlayer.sprintTriggerTime = -1;
				Minecraft mc = Minecraft.getInstance();
				ClientEngine.instance.controllEngine.setKeyBind(mc.options.keySprint, false);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID, 0);
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID);
		container.getExecuter().getEventListener().removeListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		if (executer.getSkill(this.category).isActivated()) { 
			super.cancelOnServer(executer, args);
			this.setConsumptionSynchronize(executer, this.consumption * ((float)executer.getSkill(this.category).duration / (this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()) + 1)));
			this.setDurationSynchronize(executer, 0);
			this.setStackSynchronize(executer, executer.getSkill(this.category).getStack() - 1);
			executer.modifyLivingMotionByCurrentItem();
		} else {
			this.setDurationSynchronize(executer, this.maxDuration + EnchantmentHelper.getEnchantmentLevel(Enchantments.SWEEPING_EDGE, executer.getOriginal()));
			executer.getSkill(this.category).activate();
			executer.modifyLivingMotionByCurrentItem();
		}
	}
	
	@Override
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.cancelOnServer(executer, args);
		this.setConsumptionSynchronize(executer, 0);
		this.setStackSynchronize(executer, executer.getSkill(this.category).getStack() - 1);
		executer.modifyLivingMotionByCurrentItem();
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return super.canExecute(executer);
		} else {
			return executer.getHoldingItemCapability(InteractionHand.MAIN_HAND).getSpecialAttack(executer) == this && executer.getOriginal().getVehicle() == null;
		}
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		return this;
	}
	
	private static boolean isBlockableSource(DamageSource damageSource) {
		return !damageSource.isBypassInvul() && !damageSource.isExplosion();
	}
}