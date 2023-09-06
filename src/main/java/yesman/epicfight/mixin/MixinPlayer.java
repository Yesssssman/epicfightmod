package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = Player.class)
public abstract class MixinPlayer {
	@Redirect(at = @At( value = "INVOKE", 
					   target = "Lnet/minecraft/world/damagesource/CombatTracker;recordDamage(Lnet/minecraft/world/damagesource/DamageSource;FF)V"),
		  method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V")
	private void epicfight_recordDamage(CombatTracker self, DamageSource damagesource, float health, float damage) {
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(damagesource.getEntity(), LivingEntityPatch.class);
		
		if (entitypatch != null) {
			entitypatch.setLastAttackEntity(self.getMob());
		}
		
		self.recordDamage(damagesource, health, damage);
	}
}