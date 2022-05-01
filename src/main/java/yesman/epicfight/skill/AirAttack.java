package yesman.epicfight.skill;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class AirAttack extends Skill {
	public static Skill.Builder<AirAttack> createBuilder() {
		return (new Skill.Builder<AirAttack>(new ResourceLocation(EpicFightMod.MODID, "air_attack"))).setCategory(SkillCategory.AIR_ATTACK).setConsumption(2.0F).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.STAMINA);
	}
	
	public AirAttack(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		EpicFightNetworkManager.sendToServer(new CPExecuteSkill(this.category.getIndex(), true, args));
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		Player player = executer.getOriginal();
		return !(player.isPassenger() || player.isSpectator() || player.isFallFlying() || executer.currentLivingMotion == LivingMotion.FALL
				|| !playerState.canBasicAttack());
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