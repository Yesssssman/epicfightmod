package yesman.epicfight.skill;

import java.util.List;
import java.util.UUID;

import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.platform.GlStateManager;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.client.events.engine.ControllEngine;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCResetBasicAttackCool;
import yesman.epicfight.network.server.STCSkillExecutionFeedback;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;

public class BladeRushSkill extends SpecialAttackSkill {
	private static final SkillDataKey<Integer> COMBO_COUNT = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	private static final UUID EVENT_UUID = UUID.fromString("444a1a6a-c2f1-11eb-8529-0242ac130003");
	
	public BladeRushSkill(float consumption, String skillName) {
		super(consumption, 1, 4, ActivateType.TOGGLE, skillName);
	}
	
	@Override
	public PacketBuffer gatherArguments(ClientPlayerData executer, ControllEngine controllEngine) {
		PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
		buf.writeBoolean(true);
		return buf;
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		super.onInitiate(container);
		container.getDataManager().registerData(COMBO_COUNT);
		container.executer.getEventListener().addEventListener(EventType.DEALT_DAMAGE_POST_EVENT, EVENT_UUID, (event) -> {
			int animationId = event.getDamageSource().getSkillId();
			if (animationId >= Animations.BLADE_RUSH_FIRST.getId() && animationId <= Animations.BLADE_RUSH_FINISHER.getId() 
					&& !event.getTarget().isAlive()) {
				this.setStackSynchronize(event.getPlayerData(), container.stack + 1);
			}
			return false;
		});
		
		container.executer.getEventListener().addEventListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID, (event) -> {
			if (event.getPlayerData().getSkill(this.slot).isActivated()) {
				PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
				buf.writeBoolean(false);
				this.executeOnServer(event.getPlayerData(), buf);
				return true;
			}
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.DEALT_DAMAGE_POST_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.BASIC_ATTACK_EVENT, EVENT_UUID);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		if (executer.getSkill(this.slot).isActivated() && args.readBoolean()) {
			this.cancelOnServer(executer, null);
		} else {
			int firstComboId = Animations.BLADE_RUSH_FIRST.getId();
			int animationId = firstComboId + executer.getSkill(this.slot).getDataManager().getDataValue(COMBO_COUNT);
			executer.playAnimationSynchronize(EpicFightMod.getInstance().animationManager.findAnimation(EpicFightMod.MODID.hashCode(), animationId), 0);
			ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
			executer.getSkill(this.slot).getDataManager().setData(COMBO_COUNT, (animationId - firstComboId + 1) % 4);
			this.setDurationSynchronize(executer, this.maxDuration);
			this.setStackSynchronize(executer, executer.getSkill(this.slot).getStack() - 1);
			executer.getSkill(this.slot).activate();
		}
	}
	
	@Override
	public void cancelOnClient(ClientPlayerData executer, PacketBuffer args) {
		executer.getSkill(this.slot).deactivate();
	}
	
	@Override
	public void cancelOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.getSkill(this.slot).deactivate();
		executer.getSkill(this.slot).getDataManager().setData(COMBO_COUNT, 0);
		ModNetworkManager.sendToPlayer(new STCSkillExecutionFeedback(this.slot.getIndex(), false), executer.getOriginalEntity());
	}
	
	@Override
	public List<ITextComponent> getTooltipOnItem(ItemStack itemStack, CapabilityItem cap, PlayerData<?> playerCap) {
		List<ITextComponent> list = super.getTooltipOnItem(itemStack, cap, playerCap);
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(0), "Each Strike:");
		this.generateTooltipforPhase(list, itemStack, cap, playerCap, this.properties.get(1), "Last Strike:");
		return list;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onScreen(ClientPlayerData playerdata, float resolutionX, float resolutionY) {
		if (playerdata.getSkill(this.slot).isActivated()) {
			Minecraft.getInstance().getTextureManager().bindTexture(new ResourceLocation(EpicFightMod.MODID, "textures/gui/screen/blade_rush.png"));
			GlStateManager.enableBlend();
			GlStateManager.disableDepthTest();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.color4f(1, 1, 1, 1);
			Tessellator tessellator = Tessellator.getInstance();
		    BufferBuilder bufferbuilder = tessellator.getBuffer();
		    bufferbuilder.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX);
		    bufferbuilder.pos(0, 0, 1).tex(0, 0).endVertex();
		    bufferbuilder.pos(0, resolutionY, 1).tex(0, 1).endVertex();
		    bufferbuilder.pos(resolutionX, resolutionY, 1).tex(1, 1).endVertex();
		    bufferbuilder.pos(resolutionX, 0, 1).tex(1, 0).endVertex();
		    tessellator.draw();
		}
	}
	
	@Override
	public SpecialAttackSkill registerPropertiesToAnimation() {
		((AttackAnimation)Animations.BLADE_RUSH_FIRST).phases[0].addProperties(this.properties.get(0).entrySet());
		((AttackAnimation)Animations.BLADE_RUSH_SECOND).phases[0].addProperties(this.properties.get(0).entrySet());
		((AttackAnimation)Animations.BLADE_RUSH_THIRD).phases[0].addProperties(this.properties.get(0).entrySet());
		((AttackAnimation)Animations.BLADE_RUSH_FINISHER).phases[0].addProperties(this.properties.get(1).entrySet());
		return this;
	}
}