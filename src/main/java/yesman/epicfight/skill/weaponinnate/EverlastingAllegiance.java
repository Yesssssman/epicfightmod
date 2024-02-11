package yesman.epicfight.skill.weaponinnate;

import java.util.List;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.projectile.ThrownTridentPatch;

public class EverlastingAllegiance extends WeaponInnateSkill {
	public static void setThrownTridentEntityId(ServerPlayer serverPlayer, SkillContainer skillContainer, int entityId) {
		skillContainer.getDataManager().setDataSync(SkillDataKeys.THROWN_TRIDENT_ENTITY_ID.get(), entityId, serverPlayer);
	}
	
	public static int getThrownTridentEntityId(SkillContainer skillContainer) {
		return skillContainer.getDataManager().getDataValue(SkillDataKeys.THROWN_TRIDENT_ENTITY_ID.get());
	}
	
	private final StaticAnimation callingAnimation;
	
	public EverlastingAllegiance(Builder<? extends Skill> builder) {
		super(builder);
		
		this.callingAnimation = Animations.EVERLASTING_ALLEGIANCE_CALL;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
	}
	
	@Override
	public boolean checkExecuteCondition(PlayerPatch<?> executer) {
		return executer.getSkill(this).getDataManager().getDataValue(SkillDataKeys.THROWN_TRIDENT_ENTITY_ID.get()) >= 0;
	}
	
	@Override
	public boolean canExecute(PlayerPatch<?> executer) {
		return this.checkExecuteCondition(executer);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		
		if (executer.getOriginal().level().getEntity(executer.getSkill(this).getDataManager().getDataValue(SkillDataKeys.THROWN_TRIDENT_ENTITY_ID.get())) instanceof ThrownTrident trident) {
			ThrownTridentPatch tridentPatch = EpicFightCapabilities.getEntityPatch(trident, ThrownTridentPatch.class);
			tridentPatch.recalledBySkill();
			executer.playAnimationSynchronized(this.callingAnimation, 0.0F);
			
			this.cancelOnServer(executer, args);
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void cancelOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		super.cancelOnClient(executer, args);
		
		if (executer.getOriginal().level().getEntity(executer.getSkill(this).getDataManager().getDataValue(SkillDataKeys.THROWN_TRIDENT_ENTITY_ID.get())) instanceof ThrownTrident trident) {
			ThrownTridentPatch tridentPatch = EpicFightCapabilities.getEntityPatch(trident, ThrownTridentPatch.class);
			tridentPatch.recalledBySkill();
		}
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		
		int thrownTrident = container.getDataManager().getDataValue(SkillDataKeys.THROWN_TRIDENT_ENTITY_ID.get());
		
		if (container.isDisabled() && thrownTrident >= 0) {
			container.setDisabled(false);
		} else if (!container.isDisabled() && thrownTrident < 0) {
			container.setDisabled(true);
		}
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Returning Trident:");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		return this;
	}
}