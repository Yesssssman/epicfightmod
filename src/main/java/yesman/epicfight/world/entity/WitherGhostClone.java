package yesman.epicfight.world.entity;

import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class WitherGhostClone extends FlyingEntity {
	public WitherGhostClone(EntityType<? extends FlyingEntity> entityType, World level) {
		super(entityType, level);
		this.setNoGravity(true);
		this.noPhysics = true;
	}
	
	public WitherGhostClone(ServerWorld level, Vector3d position, LivingEntity target) {
		this(EpicFightEntities.WITHER_GHOST_CLONE.get(), level);
		this.setPos(position.x, position.y, position.z);
		this.lookAt(EntityAnchorArgument.Type.FEET, target.position());
		this.setTarget(target);
	}
	
	@Override
	public boolean hurt(DamageSource damagesource, float damage) {
		return false;
	}
	
	public static AttributeModifierMap.MutableAttribute createAttributes() {
		return MobEntity.createMobAttributes().add(EpicFightAttributes.WEIGHT.get()).add(EpicFightAttributes.ARMOR_NEGATION.get()).add(EpicFightAttributes.IMPACT.get()).add(EpicFightAttributes.MAX_STRIKES.get()).add(Attributes.ATTACK_DAMAGE);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		
	}
	
	@Override
	public void aiStep() {
		super.aiStep();
		
		if (!this.isNoAi()) {
			if (this.tickCount >= 40) {
				this.remove(false);
			}
		}
	}
	
	@Override
	public CreatureAttribute getMobType() {
		return CreatureAttribute.UNDEAD;
	}
}