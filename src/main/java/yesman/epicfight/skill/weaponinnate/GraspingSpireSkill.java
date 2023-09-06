package yesman.epicfight.skill.weaponinnate;

import java.util.List;
import java.util.UUID;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class GraspingSpireSkill extends WeaponInnateSkill {
	private static final SkillDataKey<Integer> LAST_HIT_COUNT = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final UUID EVENT_UUID = UUID.fromString("3fa26bbc-d14e-11ed-afa1-0242ac120002");
	
	private AttackAnimation first;
	private AttackAnimation second;
	
	public GraspingSpireSkill(Builder<? extends Skill> builder) {
		super(builder);
		this.first = (AttackAnimation)Animations.GRASPING_SPIRAL_FIRST;
		this.second = (AttackAnimation)Animations.GRASPING_SPIRAL_SECOND;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		container.getDataManager().registerData(LAST_HIT_COUNT);
		
		container.getExecuter().getEventListener().addEventListener(EventType.ATTACK_ANIMATION_END_EVENT, EVENT_UUID, (event) -> {
			if (this.first.equals(event.getAnimation())) {
				container.getDataManager().setDataSync(LAST_HIT_COUNT, event.getPlayerPatch().getCurrenltyHurtEntities().size(), event.getPlayerPatch().getOriginal());
			}
		});
		
		container.getExecuter().getEventListener().addEventListener(EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID, (event) -> {
			if (this.second.equals(event.getDamageSource().getAnimation())) {
				float impact = event.getDamageSource().getImpact();
				event.getDamageSource().setImpact(impact + container.getDataManager().getDataValue(LAST_HIT_COUNT) * 0.4F);
			}
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.getExecuter().getEventListener().removeListener(EventType.DEALT_DAMAGE_EVENT_PRE, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		executer.playAnimationSynchronized(this.first, 0.0F);
		super.executeOnServer(executer, args);
	}
	
	@Override
	public List<Component> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerPatch<?> playerCap) {
		List<Component> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Pierce:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Second Strike:");
		
		return list;
	}
	
	@Override
	public WeaponInnateSkill registerPropertiesToAnimation() {
		this.first.phases[0].addProperties(this.properties.get(0).entrySet());
		this.second.phases[0].addProperties(this.properties.get(1).entrySet());
		
		return this;
	}
}