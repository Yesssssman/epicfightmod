package maninhouse.epicfight.skill;

import java.util.List;
import java.util.function.Function;

import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.animation.types.AttackAnimation.Phase;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;

public class SelectiveAttackSkill extends SpecialAttackSkill {
	protected final StaticAnimation[] attackAnimations;
	protected final Function<ServerPlayerData, Integer> selector;
	
	public SelectiveAttackSkill(float consumption, String skillName, Function<ServerPlayerData, Integer> func, StaticAnimation... animations) {
		super(consumption, skillName);
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	public SelectiveAttackSkill(float consumption, int duration, String skillName, Function<ServerPlayerData, Integer> func, StaticAnimation... animations) {
		super(consumption, duration, ActivateType.ONE_SHOT, skillName);
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strikes:");
		
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		for(StaticAnimation animation : this.attackAnimations) {
			AttackAnimation anim = ((AttackAnimation)animation);
			for(Phase phase : anim.phases) {
				phase.addProperties(this.properties.get(0).entrySet());
			}
		}
		
		return this;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
		super.executeOnServer(executer, args);
	}
	
	public int getAnimationInCondition(ServerPlayerData executer) {
		return selector.apply(executer);
	}
}