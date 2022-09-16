package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;

public class SeperativeMotionSkill extends SpecialAttackSkill {
	protected final StaticAnimation[] attackAnimations;
	protected final Function<ServerPlayerPatch, Integer> selector;
	
	public SeperativeMotionSkill(Builder<? extends Skill> builder, Function<ServerPlayerPatch, Integer> func, StaticAnimation... animations) {
		super(builder);
		this.properties = Lists.<Map<AttackPhaseProperty<?>, Object>>newArrayList();
		this.attackAnimations = animations;
		this.selector = func;
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strikes:");
		
		return list;
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		for (StaticAnimation animation : this.attackAnimations) {
			AttackAnimation anim = ((AttackAnimation)animation);
			for (Phase phase : anim.phases) {
				phase.addProperties(this.properties.get(0).entrySet());
			}
		}
		
		return this;
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, PacketBuffer args) {
		executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		super.executeOnServer(executer, args);
	}
	
	public int getAnimationInCondition(ServerPlayerPatch executer) {
		return selector.apply(executer);
	}
}