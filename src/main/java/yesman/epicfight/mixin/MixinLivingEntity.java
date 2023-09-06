package yesman.epicfight.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import yesman.epicfight.api.utils.AttackResult;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@Mixin(value = LivingEntity.class)
public abstract class MixinLivingEntity {
	@Inject(at = @At(value = "TAIL"), method = "blockUsingShield(Lnet/minecraft/world/entity/LivingEntity;)V", cancellable = true)
	private void epicfight_blockUsingShield(LivingEntity p_21200_, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> opponentEntitypatch = EpicFightCapabilities.getEntityPatch(p_21200_, LivingEntityPatch.class);
		LivingEntityPatch<?> selfEntitypatch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
		
		if (opponentEntitypatch != null) {
			opponentEntitypatch.setLastAttackResult(AttackResult.blocked(0.0F));
			
			if (selfEntitypatch != null && opponentEntitypatch.getEpicFightDamageSource() != null) {
				opponentEntitypatch.onAttackBlocked(opponentEntitypatch.getEpicFightDamageSource().cast(), selfEntitypatch);
			}
		}
	}
	
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
	
	@Inject(at = @At(value = "HEAD"), method = "push(Lnet/minecraft/world/entity/Entity;)V", cancellable = true)
	private void epicfight_push(Entity p_20293_, CallbackInfo info) {
		LivingEntity self = (LivingEntity)((Object)this);
		LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(self, LivingEntityPatch.class);
		
		if (entitypatch != null && !entitypatch.canPush(p_20293_)) {
			info.cancel();
		}
	}
}