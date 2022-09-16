package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.util.DamageSource;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.DragonCrystalLinkPhase;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.PatchedPhases;

@Mixin(value = EnderCrystalEntity.class)
public abstract class MixinEndCrystal {
	@Inject(at = @At(value = "HEAD"), method = "hurt(Lnet/minecraft/util/DamageSource;F)Z", cancellable = true)
	private void epicfight_hurt(DamageSource damagesource, float amount, CallbackInfoReturnable<Boolean> info) {
		EnderCrystalEntity self = (EnderCrystalEntity)((Object)this);
		
		if (!self.level.isClientSide()) {
			EnderDragonPatch dragonpatch = EnderDragonPatch.INSTANCE_SERVER;
			
			if (dragonpatch != null) {
				IPhase currentPhase = dragonpatch.getOriginal().getPhaseManager().getCurrentPhase();
				
				if (currentPhase.getPhase() == PatchedPhases.CRYSTAL_LINK) {
					DragonCrystalLinkPhase phase = (DragonCrystalLinkPhase)currentPhase;
					
					if (phase.getLinkingCrystal() == self) {
						info.cancel();
						info.setReturnValue(false);
					}
				}
			}
		}
	}
}