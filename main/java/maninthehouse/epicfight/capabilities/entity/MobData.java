package maninthehouse.epicfight.capabilities.entity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.mob.Faction;
import maninthehouse.epicfight.entity.ai.EntityAIArcher;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.network.server.STCMobInitialSetting;
import maninthehouse.epicfight.utils.math.MathUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAIAttackRangedBow;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class MobData<T extends EntityCreature> extends LivingData<T> {
	protected final Faction mobFaction;

	public MobData() {
		this(Faction.NATURAL);
	}

	public MobData(Faction faction) {
		super();
		this.mobFaction = faction;
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		initAI();
	}
	
	protected void initAI() {
		resetCombatAI();
	}
	
	protected void resetCombatAI() {
		Iterator<EntityAITasks.EntityAITaskEntry> iterator = orgEntity.tasks.taskEntries.iterator();
		List<EntityAIBase> removeTasks = new ArrayList<EntityAIBase>();

		while (iterator.hasNext()) {
            EntityAITasks.EntityAITaskEntry entityaitasks$entityaitaskentry = iterator.next();
            EntityAIBase entityAI = entityaitasks$entityaitaskentry.action;
            
            if (entityAI instanceof EntityAIAttackMelee || entityAI instanceof EntityAIAttackMelee || entityAI instanceof EntityAIAttackRangedBow ||
            		entityAI instanceof EntityAIAttackPattern || entityAI instanceof EntityAIArcher || entityAI instanceof EntityAIChase ||
            		entityAI instanceof EntityAIAttackRanged) {
            	removeTasks.add(entityAI);
            }
        }
        
		for (EntityAIBase AI : removeTasks) {
			orgEntity.tasks.removeTask(AI);
		}
	}
	
	public STCMobInitialSetting sendInitialInformationToClient() {
		return null;
	}

	public void clientInitialSettings(ByteBuf buf) {

	}
	
	@Override
	public boolean isTeam(Entity entityIn) {
		CapabilityEntity<?> cap = entityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		if(cap != null && cap instanceof MobData)
			if (((MobData<?>) cap).mobFaction.equals(this.mobFaction)) {
				Optional<EntityLivingBase> opt = Optional.ofNullable(this.getAttackTarget());
				return opt.map((attackTarget)->!attackTarget.isEntityEqual(entityIn)).orElse(true);
			}
		
		return super.isTeam(entityIn);
	}
	
	@Override
	public EntityLivingBase getAttackTarget() {
		return this.orgEntity.getAttackTarget();
	}
	
	@Override
	public float getAttackDirectionPitch() {
		Entity attackTarget = this.getAttackTarget();
		if (attackTarget != null) {
			float partialTicks = EpicFightMod.isPhysicalClient() ? Minecraft.getMinecraft().getRenderPartialTicks() : 1.0F;
			
			Vec3d target = new Vec3d(MathUtils.lerp((double)partialTicks, attackTarget.prevPosX, attackTarget.posX),
	        		MathUtils.lerp((double)partialTicks, attackTarget.prevPosY, attackTarget.posY) + (double)attackTarget.getEyeHeight(),
	        		MathUtils.lerp((double)partialTicks, attackTarget.prevPosZ, attackTarget.posZ));
	        Vec3d vector3d = new Vec3d(MathUtils.lerp((double)partialTicks, this.orgEntity.prevPosX, this.orgEntity.posX),
	        		MathUtils.lerp((double)partialTicks, this.orgEntity.prevPosY, this.orgEntity.posY) + (double)this.orgEntity.getEyeHeight(),
	        		MathUtils.lerp((double)partialTicks, this.orgEntity.prevPosZ, this.orgEntity.posZ));
	        
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