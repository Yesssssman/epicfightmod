package yesman.epicfight.skill;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AirAttack extends Skill {
	public static Skill.Builder<AirAttack> createBuilder() {
		return (new Skill.Builder<AirAttack>(new ResourceLocation(EpicFightMod.MODID, "air_attack"))).setCategory(SkillCategories.AIR_ATTACK).setConsumption(2.0F).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
	}
	
	public AirAttack(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		Player player = executer.getOriginal();
		return !(player.isPassenger() || player.isSpectator() || executer.isUnstable() || !playerState.canBasicAttack());
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		List<StaticAnimation> motions = executer.getHoldingItemCapability(InteractionHand.MAIN_HAND).getAutoAttckMotion(executer);
		StaticAnimation attackMotion = motions.get(motions.size() - 1);
		
		if (attackMotion != null) {
			super.executeOnServer(executer, args);
			executer.playAnimationSynchronized(attackMotion, 0);
		}
	}
}