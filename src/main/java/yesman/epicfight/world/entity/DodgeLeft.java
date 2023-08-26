package yesman.epicfight.world.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DodgeLeft extends LivingEntity {
	private LivingEntityPatch<?> entitypatch;
	
	public DodgeLeft(EntityType<? extends LivingEntity> type, Level level) {
		super(type, level);
	}
	
	public DodgeLeft(LivingEntityPatch<?> entitypatch) {
		this(EpicFightEntities.DODGE_LEFT.get(), entitypatch.getOriginal().level);
		
		this.entitypatch = entitypatch;
		Vec3 pos = entitypatch.getOriginal().position();
		double x = pos.x;
		double y = pos.y;
		double z = pos.z;
		
		this.setPos(x, y, z);
		this.setBoundingBox(entitypatch.getOriginal().getBoundingBox().expandTowards(1.0D, 0.0D, 1.0D));
	}
	
	@Override
	public void tick() {
		if (this.tickCount > 5) {
			this.discard();
		}
	}
	
	@Override
	public boolean hurt(DamageSource damageSource, float amount) {
		if (!DodgeAnimation.DODGEABLE_SOURCE_VALIDATOR.apply(damageSource).dealtDamage()) {
			this.entitypatch.onDodgeSuccess(damageSource);
		}
		
		this.discard();
		return false;
	}
	
	@Override
	public Iterable<ItemStack> getArmorSlots() {
		return null;
	}

	@Override
	public ItemStack getItemBySlot(EquipmentSlot p_21127_) {
		return ItemStack.EMPTY;
	}

	@Override
	public void setItemSlot(EquipmentSlot p_21036_, ItemStack p_21037_) {
		
	}

	@Override
	public HumanoidArm getMainArm() {
		return null;
	}
}