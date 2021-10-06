package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.animation.types.AttackAnimation.Phase;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCResetBasicAttackCool;

public class SimpleSpecialAttackSkill extends SpecialAttackSkill {
	protected final StaticAnimation attackAnimation;
	
	public SimpleSpecialAttackSkill(float consumption, String skillName, StaticAnimation animation) {
		this(consumption, 0, skillName, animation);
	}
	
	public SimpleSpecialAttackSkill(float consumption, int duration, String skillName, StaticAnimation animation) {
		super(consumption, duration, ActivateType.ONE_SHOT, skillName);
		this.properties = Lists.<Map<AttackPhaseProperty<?>, Object>>newArrayList();
		this.attackAnimation = animation;
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(this.attackAnimation, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strike:");
		
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		AttackAnimation anim = ((AttackAnimation)this.attackAnimation);
		for(Phase phase : anim.phases) {
			phase.addProperties(this.properties.get(0).entrySet());
		}
		
		return this;
	}
}