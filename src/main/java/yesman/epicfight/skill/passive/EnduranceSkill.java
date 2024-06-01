package yesman.epicfight.skill.passive;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.screen.SkillBookScreen;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

public class EnduranceSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("12ce9f7a-0457-11ee-be56-0242ac120002");
	
	private float staminaRatio;
	
	public EnduranceSkill(Builder<? extends Skill> builder) {
		super(builder);
	}
	
	@Override
	public void setParams(CompoundTag parameters) {
		super.setParams(parameters);
		
		this.staminaRatio = parameters.getFloat("stamina_ratio");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		
		PlayerEventListener listener = container.getExecuter().getEventListener();
		
		listener.addEventListener(EventType.HURT_EVENT_PRE, EVENT_UUID, (event) -> {
			if (container.getStack() > 0 && container.getExecuter().getEntityState().getLevel() == 1 && !container.getExecuter().isLogicalClient()) {
				float staminaConsume = Math.max(container.getExecuter().getStamina() * this.staminaRatio, 1.5F);
				
				if (container.getExecuter().consumeForSkill(this, Skill.Resource.STAMINA, staminaConsume)) {
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					buf.writeFloat(staminaConsume);
					this.executeOnServer((ServerPlayerPatch)container.getExecuter(), buf);
				}
			}
		});
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		super.executeOnServer(executer, args);
		
		float staminaConsume = args.readFloat();
		executer.setMaxStunShield(staminaConsume);
		executer.setStunShield(staminaConsume);
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		super.onRemoved(container);
		
		container.getExecuter().getEventListener().removeListener(EventType.HURT_EVENT_PRE, EVENT_UUID);
	}
	
	@Override
	public void cancelOnServer(ServerPlayerPatch executer, FriendlyByteBuf args) {
		executer.setStunShield(0.0F);
		executer.setMaxStunShield(0.0F);
		
		super.cancelOnServer(executer, args);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		return container.getStack() == 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgsOfScreen(List<Object> list) {
		list.add(String.format("%d", this.maxDuration / 20));
		return list;
	}
	
	@Override
	public boolean getCustomConsumptionTooltips(SkillBookScreen.AttributeIconList consumptionList) {
		consumptionList.add(Component.translatable("attribute.name.epicfight.cooldown.consume.tooltip"), Component.translatable("attribute.name.epicfight.cooldown.consume", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.getConsumption())), SkillBookScreen.COOLDOWN_TEXTURE_INFO);
		consumptionList.add(Component.translatable("attribute.name.epicfight.stamina.consume.tooltip"), Component.translatable("attribute.name.epicfight.stamina_current_ratio.consume", ItemStack.ATTRIBUTE_MODIFIER_FORMAT.format(this.staminaRatio * 100.0F)), SkillBookScreen.STAMINA_TEXTURE_INFO);
		
		return true;
	}
}