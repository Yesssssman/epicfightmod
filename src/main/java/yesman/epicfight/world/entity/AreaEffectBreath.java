package yesman.epicfight.world.entity;

import java.util.List;

import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.IndirectEpicFightDamageSource;

public class AreaEffectBreath extends AreaEffectCloudEntity {
	private static final DataParameter<Boolean> DATA_HORIZONTAL = EntityDataManager.defineId(AreaEffectBreath.class, DataSerializers.BOOLEAN);
	private Vector3d initialFirePosition;
	
	public AreaEffectBreath(EntityType<? extends AreaEffectBreath> entityType, World level) {
		super(entityType, level);
		this.setDuration(5);
		this.addEffect(new EffectInstance(Effects.HARM, 1, 1));
	}
	
	public AreaEffectBreath(World level, double x, double y, double z) {
		this(EpicFightEntities.AREA_EFFECT_BREATH.get(), level);
		this.setPos(x, y, z);
		this.initialFirePosition = new Vector3d(x, y, z);
	}
	
	@Override
	public void tick() {
		this.move(MoverType.SELF, this.getDeltaMovement());
		
		if (!this.level.isClientSide) {
			if (this.tickCount >= this.getDuration()) {
				this.remove();
				return;
			}
			
			float f = this.getRadius();
			float radiusPerTick = this.radiusPerTick;
			
			if (radiusPerTick != 0.0F) {
				f += radiusPerTick;
				if (f < 0.5F) {
					this.remove();
					return;
				}
				
				this.setRadius(f);
			}
			
			this.victims.entrySet().removeIf((p_146784_) -> {
				return this.tickCount >= p_146784_.getValue();
			});
			
			List<LivingEntity> list1 = this.level.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox());
			
			if (!list1.isEmpty()) {
				for (LivingEntity livingentity : list1) {
					if (!this.victims.containsKey(livingentity) && livingentity.isAffectedByPotions()) {
						double d8 = livingentity.getX() - this.getX();
						double d1 = livingentity.getZ() - this.getZ();
						double d3 = d8 * d8 + d1 * d1;
						
						if (d3 <= (double) (f * f)) {
							this.victims.put(livingentity, this.tickCount + 3);
							livingentity.invulnerableTime = 0;
							IndirectEpicFightDamageSource damagesource = new IndirectEpicFightDamageSource("indirectMagic", this.getOwner(), this, StunType.SHORT);
							damagesource.setInitialPosition(this.initialFirePosition);
							damagesource.bypassArmor().setMagic();
							damagesource.setImpact(2.0F);
							livingentity.hurt(damagesource, 3.0F);
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.getEntityData().define(DATA_HORIZONTAL, true);
	}
	
	protected void setHorizontal(boolean setter) {
		this.getEntityData().set(DATA_HORIZONTAL, setter);
	}
	
	public boolean isHorizontal() {
		return this.getEntityData().get(DATA_HORIZONTAL);
	}
	
	@Override
	public EntitySize getDimensions(Pose pose) {
		boolean horizontal = this.isHorizontal();
		float width = horizontal ? this.getRadius() * 2.0F : 1.0F;
		float height = horizontal ? 5.0F : this.getRadius() * 2.0F;
		
		return EntitySize.scalable(width, height);
	}
}