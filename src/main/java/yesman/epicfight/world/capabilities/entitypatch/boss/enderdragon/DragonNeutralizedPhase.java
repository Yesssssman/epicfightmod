package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.util.SoundCategory;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;

public class DragonNeutralizedPhase extends PatchedDragonPhase {
	public DragonNeutralizedPhase(EnderDragonEntity dragon) {
		super(dragon);
	}
	
	@Override
	public void begin() {
		this.dragonpatch.getAnimator().playAnimation(Animations.DRAGON_NEUTRALIZED, 0.0F);
		
		if (this.dragonpatch.isLogicalClient()) {
			Minecraft.getInstance().getSoundManager().stop(EpicFightSounds.ENDER_DRAGON_CRYSTAL_LINK.getLocation(), SoundCategory.HOSTILE);
			this.dragon.level.addParticle(EpicFightParticles.FORCE_FIELD_END.get(), this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), 0, 0, 0);
		}
	}
	
	@Override
	public PhaseType<DragonNeutralizedPhase> getPhase() {
		return PatchedPhases.NEUTRALIZED;
	}
	
	@Override
	public boolean isSitting() {
		return true;
	}
}