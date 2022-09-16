package yesman.epicfight.world.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;

public class DroppedNetherStar extends ItemEntity {
	
	public DroppedNetherStar(EntityType<? extends DroppedNetherStar> entityType, World level) {
		super(entityType, level);
	}
	
	public DroppedNetherStar(World level, double x, double y, double z, ItemStack itemstack, double dx, double dy, double dz) {
		this(EpicFightEntities.DROPPED_NETHER_STAR.get(), level);
		this.setPos(x, y, z);
		this.setDeltaMovement(dx, dy, dz);
		this.setItem(itemstack);
		this.lifespan = (itemstack.getItem() == null ? 6000 : itemstack.getEntityLifespan(level));
		this.noPhysics = true;
		this.setPickUpDelay(30);
		this.setNoGravity(true);
	}
	
	public DroppedNetherStar(World level, Vector3d position, Vector3d deltaMovement) {
		this(level, position.x, position.y, position.z, new ItemStack(Items.NETHER_STAR), deltaMovement.x, deltaMovement.y, deltaMovement.z);
	}
	
	@Override
	public void tick() {
		super.tick();
		
		if (this.tickCount % 70 == 0) {
			this.level.playLocalSound(this.getX(), this.getY(), this.getZ(), EpicFightSounds.NETHER_STAR_GLITTER, this.getSoundSource(), 1.0F, 1.0F, false);
		}
		
		Vector3d deltaMove = this.getDeltaMovement();
		
		if (this.level.isClientSide()) {
			Vector3d particleDeltaMove = new Vector3d(-deltaMove.x, -1.0D, -deltaMove.z).normalize().add((this.random.nextFloat() - 0.5F) * 0.1F, 0.0D, (this.random.nextFloat() - 0.5F) * 0.1F);
			this.level.addParticle(EpicFightParticles.NORMAL_DUST.get(), this.getX() + (this.random.nextFloat() - 0.5F) * this.getBbWidth(), this.getY() + this.getBbHeight() * 2.5D, this.getZ() + (this.random.nextFloat() - 0.5F) * this.getBbWidth(), particleDeltaMove.x, 0.0D, particleDeltaMove.z);
		}
		
		this.setDeltaMovement(deltaMove.multiply(0.68D, 0.68D, 0.68D));
	}
	
	@Override
	public boolean isOnFire() {
		return true;
	}
	
	@Override
	public boolean displayFireAnimation() {
		return false;
	}
}