package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;

public class PhaseManagerPatch extends PhaseManager {
	private final IPhase[] patchedPhases = new IPhase[PhaseType.getCount()];
	
	public PhaseManagerPatch(EnderDragonEntity dragon, EnderDragonPatch dragonpatch) {
		super(dragon);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends IPhase> T getPhase(PhaseType<T> phase) {
		if (this.patchedPhases != null) {
			int i = phase.getId();
			
			if (this.patchedPhases[i] == null) {
				this.patchedPhases[i] = phase.createInstance(this.dragon);
			}
			
			return (T)this.patchedPhases[i];
		} else {
			return (T)phase.createInstance(this.dragon);
		}
	}
	
	@Override
	public void setPhase(PhaseType<?> phase) {
		if (phase.createInstance(this.dragon) instanceof PatchedDragonPhase || phase == PhaseType.DYING) {
			super.setPhase(phase);
		}
	}
}