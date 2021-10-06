package yesman.epicfight.skill;

import java.util.UUID;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.entity.eventlistener.PlayerEventListener;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;

public class BerserkerSkill extends PassiveSkill {
	private static final UUID EVENT_UUID = UUID.fromString("fdc09ee8-fcfc-11eb-9a03-0242ac130003");
	
	public BerserkerSkill() {
		super("berserker");
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		PlayerEventListener listener = container.executer.getEventListener();
		listener.addEventListener(EventType.ATTACK_SPEED_GET_EVENT, EVENT_UUID, (event) -> {
			PlayerEntity player = event.getPlayerData().getOriginalEntity();
			float health = player.getHealth();
			float maxHealth = player.getMaxHealth();
			float lostHealthPercentage = (maxHealth - health) / maxHealth;
			lostHealthPercentage = (float)Math.floor(lostHealthPercentage * 100.0F) * 0.005F;
			float attackSpeed = event.getAttackSpeed();
			event.setAttackSpeed(Math.min(5.0F, attackSpeed * (1.0F + lostHealthPercentage)));
			return false;
		});
		
		listener.addEventListener(EventType.DEALT_DAMAGE_PRE_EVENT, EVENT_UUID, (event) -> {
			PlayerEntity player = event.getPlayerData().getOriginalEntity();
			float health = player.getHealth();
			float maxHealth = player.getMaxHealth();
			float lostHealthPercentage = (maxHealth - health) / maxHealth;
			lostHealthPercentage = (float)Math.floor(lostHealthPercentage * 100.0F) * 0.003F;
			float attackDamage = event.getAttackDamage();
			event.setAttackDamage(attackDamage * (1.0F + lostHealthPercentage));
			return false;
		});
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.ATTACK_SPEED_GET_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.DEALT_DAMAGE_PRE_EVENT, EVENT_UUID);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public boolean shouldDraw(SkillContainer container) {
		PlayerEntity player = container.executer.getOriginalEntity();
		float health = player.getHealth();
		float maxHealth = player.getMaxHealth();
		return (maxHealth - health) > 0.0F;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, MatrixStack matStackIn, float x, float y, float scale, int width, int height) {
		matStackIn.push();
		matStackIn.scale(scale, scale, 1.0F);
		matStackIn.translate(0, (float)gui.getSlidingProgression() * 1.0F / scale, 0);
		Minecraft.getInstance().getTextureManager().bindTexture(this.getSkillTexture());
		float scaleMultiply = 1.0F / scale;
		gui.drawTexturedModalRectFixCoord(matStackIn.getLast().getMatrix(), (width - x) * scaleMultiply, (height - y) * scaleMultiply, 0, 0, 255, 255);
		matStackIn.scale(scaleMultiply, scaleMultiply, 1.0F);
		
		PlayerEntity player = container.executer.getOriginalEntity();
		float health = player.getHealth();
		float maxHealth = player.getMaxHealth();
		float lostHealthPercentage = (maxHealth - health) / maxHealth;
		lostHealthPercentage = (float)Math.floor(lostHealthPercentage * 100.0F);
		
		gui.font.drawStringWithShadow(matStackIn, String.format("%.0f%%", lostHealthPercentage), ((float)width - x+4), ((float)height - y+6), 16777215);
		matStackIn.pop();
	}
}