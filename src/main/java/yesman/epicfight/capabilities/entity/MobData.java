package yesman.epicfight.capabilities.entity;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.ai.goal.RangedAttackGoal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.mob.Faction;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.entity.ai.RangeAttackMobGoal;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.STCMobInitialSetting;

public abstract class MobData<T extends MobEntity> extends LivingData<T> {
	protected final Faction mobFaction;
	
	public MobData() {
		this.mobFaction = Faction.NATURAL;
	}
	
	public MobData(Faction faction) {
		this.mobFaction = faction;
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.initAI();
	}
	
	protected void initAI() {
		this.resetCombatAI();
	}

	protected void resetCombatAI() {
		Set<PrioritizedGoal> goals = this.orgEntity.goalSelector.goals;
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		List<Goal> toRemove = Lists.<Goal>newArrayList();
		
		while (iterator.hasNext()) {
        	PrioritizedGoal goal = iterator.next();
            Goal inner = goal.getGoal();
			if (inner instanceof MeleeAttackGoal || inner instanceof ChasingGoal || inner instanceof RangedAttackGoal
					|| inner instanceof RangeAttackMobGoal || inner instanceof AttackPatternGoal) {
            	toRemove.add(inner);
            }
        }
        
		for (Goal AI : toRemove) {
        	this.orgEntity.goalSelector.removeGoal(AI);
        }
	}
	
	public STCMobInitialSetting sendInitialInformationToClient() {
		return null;
	}

	public void clientInitialSettings(ByteBuf buf) {

	}
	
	@Override
	public void updateArmor(CapabilityItem fromCap, CapabilityItem toCap, EquipmentSlotType slotType) {
		if(this.orgEntity.getAttributeManager().hasAttributeInstance(EpicFightAttributes.STUN_ARMOR.get())) {
			if(fromCap != null) {
				this.orgEntity.getAttributeManager().removeModifiers(fromCap.getAttributeModifiers(slotType, this));
			}
			if(toCap != null) {
				this.orgEntity.getAttributeManager().reapplyModifiers(toCap.getAttributeModifiers(slotType, this));
			}
		}
	}
	
	@Override
	public boolean canAttack(Entity entityIn) {
		CapabilityEntity<?> cap = entityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY).orElse(null);
		if (cap != null && cap instanceof MobData) {
			if (((MobData<?>) cap).mobFaction.equals(this.mobFaction)) {
				Optional<LivingEntity> opt = Optional.ofNullable(this.getAttackTarget());
				return opt.map((attackTarget) -> !attackTarget.isEntityEqual(entityIn)).orElse(true);
			}
		}
		
		return super.canAttack(entityIn);
	}
	
	@Override
	public LivingEntity getAttackTarget() {
		return this.orgEntity.getAttackTarget();
	}
	
	@Override
	public float getAttackDirectionPitch() {
		Entity attackTarget = this.getAttackTarget();
		if (attackTarget != null) {
			float partialTicks = EpicFightMod.isPhysicalClient() ? Minecraft.getInstance().getRenderPartialTicks() : 1.0F;
			Vector3d target = attackTarget.getEyePosition(partialTicks);
			Vector3d vector3d = this.orgEntity.getEyePosition(partialTicks);
			double d0 = target.x - vector3d.x;
			double d1 = target.y - vector3d.y;
			double d2 = target.z - vector3d.z;
			double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
			return MathHelper.clamp(MathHelper.wrapDegrees((float) ((MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI)))), -30.0F, 30.0F);
		} else {
			return super.getAttackDirectionPitch();
		}
	}
}