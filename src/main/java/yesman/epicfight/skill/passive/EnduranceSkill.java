package yesman.epicfight.skill.passive;

import java.util.List;
import java.util.UUID;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
			if (container.getStack() > 0 && container.getExecuter().getEntityState().getLevel() == 1 && container.getExecuter() instanceof ServerPlayerPatch serverPlayerPatch) {
				float staminaConsume = container.getExecuter().getStamina() * this.staminaRatio;
				
				if (staminaConsume > 1.0F) {
					FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
					buf.writeFloat(staminaConsume);
					
					this.executeOnServer(serverPlayerPatch, buf);
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
		executer.consumeStamina(staminaConsume);
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
		list.add(String.format("%.0f", this.consumption));
		list.add(String.format("%.1f", this.staminaRatio * 100.0F));
		list.add(String.format("%d", this.maxDuration / 20));
		
		return list;
	}
}