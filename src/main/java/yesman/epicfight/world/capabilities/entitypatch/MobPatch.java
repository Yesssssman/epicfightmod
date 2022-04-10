package yesman.epicfight.world.capabilities.entitypatch;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPMobInitialize;
import yesman.epicfight.network.server.SPSetAttackTarget;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.mob.Faction;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.AttackPatternGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.RangeAttackMobGoal;

public abstract class MobPatch<T extends Mob> extends LivingEntityPatch<T> {
	protected final Faction mobFaction;
	
	public MobPatch() {
		this.mobFaction = Faction.NATURAL;
	}
	
	public MobPatch(Faction faction) {
		this.mobFaction = faction;
	}
	
	@Override
	public void onJoinWorld(T entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		
		if (!entityIn.level.isClientSide()) {
			this.initAI();
		}
	}
	
	protected void initAI() {
		this.resetCombatAI();
	}

	protected void resetCombatAI() {
		Set<WrappedGoal> goals = this.original.goalSelector.getAvailableGoals();
		Iterator<WrappedGoal> iterator = goals.iterator();
		List<Goal> toRemove = Lists.<Goal>newArrayList();
		
		while (iterator.hasNext()) {
        	WrappedGoal goal = iterator.next();
            Goal inner = goal.getGoal();
			if (inner instanceof MeleeAttackGoal || inner instanceof ChasingGoal || inner instanceof RangedAttackGoal
					|| inner instanceof RangeAttackMobGoal || inner instanceof AttackPatternGoal) {
            	toRemove.add(inner);
            }
        }
        
		for (Goal AI : toRemove) {
        	this.original.goalSelector.removeGoal(AI);
        }
	}
	
	public SPMobInitialize sendInitialInformationToClient() {
		return null;
	}

	public void clientInitialSettings(ByteBuf buf) {

	}
	
	@Override
	public void updateArmor(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlot slotType) {
		if(this.original.getAttributes().hasAttribute(EpicFightAttributes.STUN_ARMOR.get())) {
			if(fromCap != null) {
				this.original.getAttributes().removeAttributeModifiers(fromCap.getAttributeModifiers(slotType, this));
			}
			if(toCap != null) {
				this.original.getAttributes().addTransientAttributeModifiers(toCap.getAttributeModifiers(slotType, this));
			}
		}
	}
	
	@Override
	public boolean isTeammate(Entity entityIn) {
		EntityPatch<?> cap = entityIn.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
		
		if (cap != null && cap instanceof MobPatch) {
			if (((MobPatch<?>) cap).mobFaction.equals(this.mobFaction)) {
				Optional<LivingEntity> opt = Optional.ofNullable(this.getAttackTarget());
				return opt.map((attackTarget) -> !attackTarget.is(entityIn)).orElse(true);
			}
		}
		
		return super.isTeammate(entityIn);
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		return this.original.getTarget();
	}
	
	public void setAttakTargetSync(LivingEntity entityIn) {
		if (!this.original.level.isClientSide()) {
			this.original.setTarget(entityIn);
			EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPSetAttackTarget(this.original.getId(), entityIn != null ? entityIn.getId() : -1), this.original);
		}
	}
	
	@Override
	public float getAttackDirectionPitch() {
		Entity attackTarget = this.getAttackTarget();
		if (attackTarget != null) {
			float partialTicks = EpicFightMod.isPhysicalClient() ? Minecraft.getInstance().getFrameTime() : 1.0F;
			Vec3 target = attackTarget.getEyePosition(partialTicks);
			Vec3 vector3d = this.original.getEyePosition(partialTicks);
			double d0 = target.x - vector3d.x;
			double d1 = target.y - vector3d.y;
			double d2 = target.z - vector3d.z;
			double d3 = (double) Math.sqrt(d0 * d0 + d2 * d2);
			return Mth.clamp(Mth.wrapDegrees((float) ((Mth.atan2(d1, d3) * (double) (180F / (float) Math.PI)))), -30.0F, 30.0F);
		} else {
			return super.getAttackDirectionPitch();
		}
	}
}