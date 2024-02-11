package yesman.epicfight.skill.weaponinnate;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.Lists;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class LiechtenauerSkill extends WeaponInnateSkill {
	private static final UUID EVENT_UUID = UUID.fromString("244c57c0-a837-11eb-bcbc-0242ac130002");
	private int returnDuration;
	
	public LiechtenauerSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		this.returnDuration = parameters.getInt("return_duration");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_POST, EVENT_UUID, (event) -> {
			if (container.isActivated() && !container.isDisabled()) {
				if (!event.getTarget().isAlive()) {
					this.setDurationSynchronize(event.getPlayerPatch(), Math.min(this.maxDuration, container.getRemainDuration() + this.returnDuration));
				}
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			int phaseLevel = event.getPlayerPatch().getEntityState().getLevel();
			
			if (event.getAmount() > 0.0F && container.isActivated() && !container.isDisabled() && phaseLevel > 0 && phaseLevel < 3 && 
				this.canExecute(event.getPlayerPatch()) && isBlockableSource(event.getDamageSource())) {
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
					event.getPlayerPatch().playSound(EpicFightSounds.CLASH.get(), -0.05F, 0.1F);
					ServerPlayer playerentity = event.getPlayerPatch().getOriginal();
					EpicFightParticles.HIT_BLUNT.get().spawnParticleWithArgument(playerentity.serverLevel(), HitParticleType.FRONT_OF_EYES, HitParticleType.ZERO, playerentity, damageSource.getDirectEntity());

					float knockback = 0.25F;
					
					if (damageSource instanceof EpicFightDamageSource epicfightSource) {
						knockback += Math.min(epicfightSource.getImpact() * 0.1F, 1.0F);
					}
					
					if (damageSource.getDirectEntity() instanceof LivingEntity livingentity) {
						knockback += EnchantmentHelper.getKnockbackBonus(livingentity) * 0.1F;
					}
					
					LivingEntityPatch<?> attackerpatch = EpicFightCapabilities.getEntityPatch(event.getDamageSource().getEntity(), LivingEntityPatch.class);
					
					if (attackerpatch != null) {
						attackerpatch.setLastAttackEntity(event.getPlayerPatch().getOriginal());
					}
					
					event.getPlayerPatch().knockBackEntity(damageSource.getDirectEntity().position(), knockback);
					event.setCanceled(true);
					event.setResult(AttackResult.ResultType.BLOCKED);
				}
			}
		}, 0);
		
		container.getExecuter().getEventListener().addEventListener(EventType.MOVEMENT_INPUT_EVENT, EVENT_UUID, (event) -> {
			SkillContainer skillContainer = event.getPlayerPatch().getSkill(this);
			
			if (skillContainer.isActivated()) {
				LocalPlayer clientPlayer = event.getPlayerPatch().getOriginal();
				clientPlayer.setSprinting(false);
				clientPlayer.sprintTriggerTime = -1;
				Minecraft mc = Minecraft.getInstance();
				ClientEngine.getInstance().controllEngine.setKeyBind(mc.options.keySprint, false);
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
		executer.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F);
		
		if (executer.getSkill(this).isActivated()) {
			this.cancelOnServer(executer, args);
		} else {
			super.executeOnServer(executer, args);
			executer.getSkill(this).activate();
			executer.modifyLivingMotionByCurrentItem();
			executer.playAnimationSynchronized(Animations.BIPED_LIECHTENAUER_READY, 0.0F);
		}
	}
	
	@Override
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		executer.getSkill(this).deactivate();
		super.cancelOnServer(executer, args);
		executer.modifyLivingMotionByCurrentItem();
	}
	
	@Override
	public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnClient(executer, args);
		executer.getSkill(this).activate();
	}
	
	@Override
	public void cancelOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		super.cancelOnClient(executer, args);
		executer.getSkill(this).deactivate();
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		if (executer.isLogicalClient()) {
			return super.canExecute(executer);
		} else {
			return executer.getOriginal().getVehicle() == null;
		}
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
	
	private static boolean isBlockableSource(DamageSource damageSource) {
		return !damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY) && !damageSource.is(DamageTypeTags.IS_EXPLOSION);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemstack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = Lists.newArrayList();
		List<Object> tooltipArgs = Lists.newArrayList();
		String traslatableText = this.getTranslationKey();
		
		tooltipArgs.add(this.maxDuration / 20);
		tooltipArgs.add(this.returnDuration / 20);
		
		list.add(Component.translatable(traslatableText).withStyle(ChatFormatting.WHITE).append(Component.literal(String.format("[%.0f]", this.consumption)).withStyle(ChatFormatting.AQUA)));
		list.add(Component.translatable(traslatableText + ".tooltip", tooltipArgs.toArray(new Object[0])).withStyle(ChatFormatting.DARK_GRAY));
		
		return list;
	}
	
	public enum Stance {
		VOM_TAG, PFLUG, OCHS
	}
}