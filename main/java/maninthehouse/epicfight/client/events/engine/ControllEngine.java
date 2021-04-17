package maninthehouse.epicfight.client.events.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.client.input.ModKeys;
import maninthehouse.epicfight.config.ConfigurationIngame;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSPlayAnimation;
import maninthehouse.epicfight.skill.SkillContainer;
import maninthehouse.epicfight.skill.SkillSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.MouseInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ControllEngine {
	private Map<KeyBinding, Consumer<KeyBinding>> keyFunctionMap;
	private EntityPlayerSP player;
	private ClientPlayerData playerdata;
	private int comboHoldCounter;
	private int comboCounter;
	private int mouseLeftPressCounter = 0;
	private int sneakPressCounter = 0;
	private int reservedSkill;
	private int skillReserveCounter;
	private boolean sneakPressToggle = false;
	private boolean mouseLeftPressToggle = false;
	private boolean lightPress;
	
	public GameSettings gameSettings;
	
	public ControllEngine() {
		Events.controllEngine = this;
		this.gameSettings = Minecraft.getMinecraft().gameSettings;
		this.keyFunctionMap = new HashMap<KeyBinding, Consumer<KeyBinding>>();
		this.keyFunctionMap.put(this.gameSettings.keyBindAttack, this::attackKeyPressed);
		this.keyFunctionMap.put(this.gameSettings.keyBindSwapHands, this::swapHandKeyPressed);
		this.keyFunctionMap.put(this.gameSettings.keyBindSneak, this::sneakKeyPressed);
		this.keyFunctionMap.put(ModKeys.SWITCH_MODE, this::switchModeKeyPressed);
		this.keyFunctionMap.put(ModKeys.DODGE, this::dodgeKeyPressed);
	}
	
	public void setGamePlayer(ClientPlayerData playerdata) {
		this.comboCounter = 0;
		this.mouseLeftPressCounter = 0;
		this.mouseLeftPressToggle = false;
		this.sneakPressCounter = 0;
		this.sneakPressToggle = false;
		this.lightPress = false;
		this.player = playerdata.getOriginalEntity();
		this.playerdata = playerdata;
	}
	
	public boolean playerCanMove(EntityState playerState) {
		return !playerState.isMovementLocked() || this.player.isRidingHorse();
	}

	public boolean playerCanRotate(EntityState playerState) {
		return !playerState.isCameraRotationLocked() || this.player.isRidingHorse();
	}

	public boolean playerCanAct(EntityState playerState) {
		return !this.player.isSpectator() && !(this.player.isElytraFlying() || this.playerdata.currentMotion == LivingMotion.FALL || playerState.isMovementLocked());
	}

	public boolean playerCanDodging(EntityState playerState) {
		return !this.player.isSpectator() && !(this.player.isElytraFlying() || this.playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}

	public boolean playerCanExecuteSkill(EntityState playerState) {
		return !this.player.isSpectator() && !(this.player.isElytraFlying() || this.playerdata.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	private void attackKeyPressed(KeyBinding key) {
		if (ClientEngine.INSTANCE.isBattleMode()) {
			this.setKeyBind(this.gameSettings.keyBindAttack, false);
			this.gameSettings.keyBindAttack.isPressed();
			if (this.player.getItemInUseCount() == 0) {
				if (!this.mouseLeftPressToggle) {
					this.mouseLeftPressToggle = true;
				}
			}
		} else {
			this.gameSettings.keyBindAttack.pressTime++;
		}
		
		if (this.player.getCooledAttackStrength(0) < 0.9F) {
			this.gameSettings.keyBindAttack.pressTime = 0;
		}
	}
	
	private void dodgeKeyPressed(KeyBinding key) {
		if (!Keyboard.isRepeatEvent()) {
			if (key.getKeyCode() == this.gameSettings.keyBindSneak.getKeyCode()) {
				if (this.player.getRidingEntity() == null && ClientEngine.INSTANCE.isBattleMode()) {
					if (!this.sneakPressToggle) {
						this.sneakPressToggle = true;
					}
				}
			} else {
				SkillContainer skill = this.playerdata.getSkill(SkillSlot.DODGE);
				if (skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata)) {
					skill.execute(this.playerdata);
				}
			}
		}
	}
	
	private void sneakKeyPressed(KeyBinding key) {
		if (ModKeys.DODGE.getKeyCode() == this.gameSettings.keyBindSneak.getKeyCode()) {
			if (!this.sneakPressToggle) {
				this.dodgeKeyPressed(ModKeys.DODGE);
			}
		}
	}
	
	private void swapHandKeyPressed(KeyBinding key) {
		CapabilityItem cap = this.playerdata.getHeldItemCapability(EnumHand.MAIN_HAND);
		if (this.playerdata.isInaction() || (cap != null && !cap.canUsedInOffhand())) {
			key.pressTime = 0;
			this.setKeyBind(key, false);
		} else {
			key.pressTime++;
		}
	}
	
	private void switchModeKeyPressed(KeyBinding key) {
		if (!Keyboard.isRepeatEvent()) {
			ClientEngine.INSTANCE.toggleActingMode();
		}
	}
	
	public void tick() {
		if (this.playerdata == null) {
			return;
		}
		EntityState playerState = this.playerdata.getEntityState();

		if (this.mouseLeftPressToggle) {
			if (!this.isKeyDown(this.gameSettings.keyBindAttack)) {
				lightPress = true;
				this.mouseLeftPressToggle = false;
				this.mouseLeftPressCounter = 0;
			} else {
				if (this.mouseLeftPressCounter > ConfigurationIngame.longPressCount) {
					if (this.playerCanExecuteSkill(playerState)) {
						CapabilityItem itemCap = this.playerdata.getHeldItemCapability(EnumHand.MAIN_HAND);
						if(itemCap != null) {
							this.playerdata.getSkill(SkillSlot.WEAPON_SPECIAL_ATTACK).execute(this.playerdata);
						}
					} else {
						if (!this.player.isSpectator()) {
							this.reserveSkill(SkillSlot.WEAPON_SPECIAL_ATTACK);
						}
					}
					this.mouseLeftPressToggle = false;
					this.mouseLeftPressCounter = 0;
					this.resetAttackCounter();
				} else {
					this.setKeyBind(this.gameSettings.keyBindAttack, false);
					this.mouseLeftPressCounter++;
				}
			}
		}
		
		if (this.lightPress) {
			if (this.playerCanAct(playerState)) {
				playAttackMotion(this.player.getHeldItemMainhand(), this.player.isSprinting());
				this.player.resetCooldown();
				this.lightPress = false;
			} else {
				if (this.player.isSpectator() || playerState.getLevel() < 2) {
					this.lightPress = false;
				}
			}
			
			this.lightPress = false;
			this.mouseLeftPressToggle = false;
			this.mouseLeftPressCounter = 0;
		}

		if (this.sneakPressToggle) {
			if (!this.isKeyDown(this.gameSettings.keyBindSneak)) {
				SkillContainer skill = this.playerdata.getSkill(SkillSlot.DODGE);
				
				if (skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata)) {
					skill.execute(this.playerdata);
				} else {
					this.reserveSkill(SkillSlot.DODGE);
				}
				
				this.sneakPressToggle = false;
				this.sneakPressCounter = 0;
			} else {
				if (this.sneakPressCounter > ConfigurationIngame.longPressCount) {
					this.sneakPressToggle = false;
					this.sneakPressCounter = 0;
				} else {
					this.sneakPressCounter++;
				}
			}
		}
		
		if (this.reservedSkill >= 0) {
			if (skillReserveCounter > 0) {
				SkillContainer skill = this.playerdata.getSkill(this.reservedSkill);
				this.skillReserveCounter--;
				if (skill.getContaining() != null && skill.canExecute(this.playerdata) && skill.getContaining().isExecutableState(this.playerdata)) {
					skill.execute(this.playerdata);
					this.reservedSkill = -1;
					this.skillReserveCounter = -1;
				}
			} else {
				this.reservedSkill = -1;
				this.skillReserveCounter = -1;
			}
		}
		
		if (this.comboHoldCounter > 0) {
			float f = this.player.getCooledAttackStrength(0);
			
			if (!playerState.isMovementLocked() && !playerState.isCameraRotationLocked() && f >= 1.0F) {
				--this.comboHoldCounter;
				
				if (this.comboHoldCounter == 0) {
					this.resetAttackCounter();
				}
			}
		}
		
		for (int i = 0; i < 9; ++i) {
			if (isKeyDown(this.gameSettings.keyBindsHotbar[i])) {
				if (this.playerdata.isInaction()) {
					this.gameSettings.keyBindsHotbar[i].isPressed();
				}
			}
		}
	}
	
	private void playAttackMotion(ItemStack holdItem, boolean dashAttack) {
		CapabilityItem cap = holdItem.getCapability(ModCapabilities.CAPABILITY_ITEM, null);
		StaticAnimation attackMotion = null;
		
		if (this.player.getRidingEntity() != null) {
			if (this.player.isRidingHorse() && cap != null && cap.canUseOnMount()) {
				attackMotion = cap.getMountAttackMotion().get(this.comboCounter);
				this.comboCounter += 1;
				this.comboCounter %= cap.getMountAttackMotion().size();
			}
		} else {
			List<StaticAnimation> combo = null;
			
			if(combo == null) {
				combo = (cap != null) ? combo = cap.getAutoAttckMotion(this.playerdata) : CapabilityItem.getBasicAutoAttackMotion();
			}
			
			int comboSize = combo.size();
			if(dashAttack) {
				this.comboCounter = comboSize - 1;
			} else {
				this.comboCounter %= comboSize - 1;
			}
			
			attackMotion = combo.get(this.comboCounter);
			this.comboCounter = dashAttack ? 0 : this.comboCounter+1;
		}
		
		this.comboHoldCounter = 10;
		
		if(attackMotion != null) {
			this.playerdata.getAnimator().playAnimation(attackMotion, 0);
			ModNetworkManager.sendToServer(new CTSPlayAnimation(attackMotion, 0, false, false));
		}
	}
	
	private void reserveSkill(SkillSlot slot) {
		this.reservedSkill = slot.getIndex();
		this.skillReserveCounter = 8;
	}
	
	public boolean isKeyDown(KeyBinding key) {
		int keyCode = key.getKeyCode();
		
		if (keyCode > 0) {
			return Keyboard.isKeyDown(key.getKeyCode());
		} else {
			return Mouse.isButtonDown(key.getKeyCode() + 100);
		}
	}
	
	public void setKeyBind(KeyBinding key, boolean setter) {
		KeyBinding.setKeyBindState(key.getKeyCode(), setter);
	}
	
	public void resetAttackCounter() {
		comboCounter = 0;
	}
	
	@SideOnly(Side.CLIENT)
	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Side.CLIENT)
	public static class Events {
		static ControllEngine controllEngine;
		
		@SubscribeEvent
		public static void mouseEvent(MouseInputEvent event) {
			if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().currentScreen == null) {
				for (Map.Entry<KeyBinding, Consumer<KeyBinding>> entry : controllEngine.keyFunctionMap.entrySet()) {
					if (entry.getKey().pressTime > 0) {
						entry.getKey().pressTime--;
						controllEngine.keyFunctionMap.get(entry.getKey()).accept(entry.getKey());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void keyboardEvent(KeyInputEvent event) {
			if (Minecraft.getMinecraft().player != null && Minecraft.getMinecraft().currentScreen == null) {
				for (Map.Entry<KeyBinding, Consumer<KeyBinding>> entry : controllEngine.keyFunctionMap.entrySet()) {
					if (entry.getKey().pressTime > 0) {
						entry.getKey().pressTime--;
						controllEngine.keyFunctionMap.get(entry.getKey()).accept(entry.getKey());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void mouseEvent(MouseEvent event) {
			int dwheel = event.getDwheel();
			if (dwheel != 0) {
				if (controllEngine.playerdata != null) {
					if (controllEngine.playerdata.isInaction()) {
						event.setCanceled(true);
					} else {
						controllEngine.playerdata.cancelUsingItem();
						controllEngine.resetAttackCounter();
					}
				}
			}
		}
		
		/**
		@SubscribeEvent
		public static void mouseScrollEvent(MouseScrollEvent event) {
			if (Minecraft.getMinecraft().player != null && controllEngine.playerdata != null && controllEngine.playerdata.isInaction()) {
				if(Minecraft.getMinecraft().currentScreen == null) {
					event.setCanceled(true);
				}
			}
		}**/
		
		@SubscribeEvent
		public static void cancelPlayerRotationWhenInaction(TickEvent.RenderTickEvent event) {
			if (controllEngine.playerdata != null) {
				EntityState playerState = controllEngine.playerdata.getEntityState();
				if (!controllEngine.playerCanRotate(playerState) && controllEngine.player.isEntityAlive()) {
					Mouse.getDX();
					Mouse.getDY();
				}
			}
		}
		
		@SubscribeEvent
		public static void moveInputEvent(InputUpdateEvent event) {
			if(controllEngine.playerdata == null) {
				return;
			}
			
			EntityState playerState = controllEngine.playerdata.getEntityState();
			
			if (!controllEngine.playerCanMove(playerState)) {
				event.getMovementInput().moveForward = 0F;
				event.getMovementInput().moveStrafe = 0F;
				event.getMovementInput().forwardKeyDown = false;
				event.getMovementInput().backKeyDown = false;
				event.getMovementInput().leftKeyDown = false;
				event.getMovementInput().rightKeyDown = false;
				event.getMovementInput().jump = false;
				event.getMovementInput().sneak = false;
				((EntityPlayerSP)event.getEntityPlayer()).sprintToggleTimer = -1;
			}
		}
		
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.START) {
				if (Minecraft.getMinecraft().player != null) {
					controllEngine.tick();
				}
			}
		}
	}
}