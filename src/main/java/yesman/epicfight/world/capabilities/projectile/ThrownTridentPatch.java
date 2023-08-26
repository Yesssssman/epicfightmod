package yesman.epicfight.world.capabilities.projectile;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.weaponinnate.EverlastingAllegiance;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.IndirectEpicFightDamageSource;
import yesman.epicfight.world.damagesource.SourceTags;
import yesman.epicfight.world.damagesource.StunType;

public class ThrownTridentPatch extends ProjectilePatch<ThrownTrident> {
	private boolean innateActivated;
	private int returnTick;
	private float independentXRotO;
	private float independentXRot;
	
	@Override
	public void onStartTracking(ServerPlayer trackingPlayer) {
		if (this.innateActivated) {
			SPSpawnData packet = new SPSpawnData(this.original.getId());
			packet.getBuffer().writeInt(this.returnTick);
			packet.getBuffer().writeInt(this.original.tickCount);
			
			EpicFightNetworkManager.sendToPlayer(packet, trackingPlayer);
		}
	}
	
	@Override
	public void processSpawnData(ByteBuf buf) {
		this.innateActivated = true;
		this.returnTick = buf.readInt();
		this.original.tickCount = buf.readInt();
	}
	
	@Override
	protected void setMaxStrikes(ThrownTrident projectileEntity, int maxStrikes) {
		projectileEntity.setPierceLevel((byte)(maxStrikes - 1));
	}
	
	@Override
	public void onJoinWorld(ThrownTrident projectileEntity, EntityJoinWorldEvent event) {
		super.onJoinWorld(projectileEntity, event);
		
		if (!this.isLogicalClient()) {
			ServerPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(projectileEntity.getOwner(), ServerPlayerPatch.class);
			
			if (playerpatch != null) {
				SkillContainer container = playerpatch.getSkill(SkillSlots.WEAPON_INNATE);
				
				if (container.getSkill() instanceof EverlastingAllegiance) {
					EverlastingAllegiance.setThrownTridentEntityId(playerpatch.getOriginal(), container, projectileEntity.getId());
				}
			}
			
			this.armorNegation = 20.0F;
		}
	}
	
	public void tickEnd() {
		if (!this.isLogicalClient()) {
			if (this.original.dealtDamage) {
				ServerPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(this.original.getOwner(), ServerPlayerPatch.class);
				
				if (playerpatch != null) {
					SkillContainer container = playerpatch.getSkill(SkillSlots.WEAPON_INNATE);
					
					if (container.getSkill() instanceof EverlastingAllegiance) {
						if (EverlastingAllegiance.getThrownTridentEntityId(container) > -1) {
							EverlastingAllegiance.setThrownTridentEntityId(playerpatch.getOriginal(), container, -1);
						}
					}
				}
			}
			
			if (this.innateActivated) {
				List<Entity> entities = this.original.level.getEntities(this.original, this.original.getBoundingBox().inflate(1.0D, 1.0D, 1.0D));
				EpicFightDamageSource source = new IndirectEpicFightDamageSource("trident", this.original.getOwner(), this.original, StunType.HOLD)
						.addTag(SourceTags.WEAPON_INNATE)
						.addExtraDamage(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create())
						.setDamageModifier(ValueModifier.multiplier(1.4F))
						.setArmorNegation(30.0F);
				
				for (Entity entity : entities) {
					if (entity.is(this.original.getOwner())) {
						continue;
					}
					
					float f = 8.0F;
					
					if (entity instanceof LivingEntity livingentity) {
						f += EnchantmentHelper.getDamageBonus(this.original.tridentItem, livingentity.getMobType());
						
						if (entity.hurt(source.cast(), f)) {
							entity.playSound(EpicFightSounds.BLADE_HIT, 1.0F, 1.0F);
							((ServerLevel)entity.level).sendParticles(EpicFightParticles.HIT_BLADE.get()
									, entity.position().x, entity.position().y + entity.getBbHeight() * 0.5D, entity.position().z, 0, 0, 0, 0, 1.0D);
						}
					}
				}
			}
		}
		
		if (this.innateActivated) {
			int elapsedTicks = Math.max(this.original.tickCount - this.returnTick - 10, 0);
			Vec3 toOwner = this.original.getOwner().getEyePosition().subtract(this.original.position());
			double length = toOwner.length();
			double speed = Math.min(Math.pow(elapsedTicks, 2.0D) * 0.0005D + Math.abs(elapsedTicks * 0.05D), Math.min(10.0D, length));
			Vec3 toMaster = toOwner.normalize().scale(speed);
			this.original.setDeltaMovement(new Vec3(0, 0, 0));
			Vec3 pos = this.original.position();
			this.original.setPos(pos.x + toMaster.x, pos.y + toMaster.y, pos.z + toMaster.z);
			
			this.original.setXRot(0.0F);
			this.original.xRotO = 0.0F;
			
			this.original.setYRot(0.0F);
			this.original.yRotO = 0.0F;
			
			this.independentXRotO = this.independentXRot;
			this.independentXRot += 60.0F;
			
			this.original.xRotO = this.independentXRotO;
			this.original.setXRot(this.independentXRot);
			
			if (this.original.tickCount % 3 == 0) {
				this.original.playSound(EpicFightSounds.WHOOSH_ROD, 3.0F, 1.0F);
			}
		}
	}
	
	public boolean isInnateActivated() {
		return this.innateActivated;
	}
	
	public void catchByPlayer(PlayerPatch<?> playerpatch) {
		playerpatch.playAnimationSynchronized(Animations.EVERLASTING_ALLEGIANCE_CATCH, 0.0F);
	}
	
	public void recalledBySkill() {
		this.original.playSound(SoundEvents.TRIDENT_RETURN, 10.0F, 1.0F);
		this.original.dealtDamage = true;
		this.innateActivated = true;
		this.independentXRot = this.original.getXRot();
		this.returnTick = this.original.tickCount;
		this.initialFirePosition = this.original.position();
	}
}