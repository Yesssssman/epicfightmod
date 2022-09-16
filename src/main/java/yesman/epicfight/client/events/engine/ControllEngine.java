package yesman.epicfight.client.events.engine;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.UseAction;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent.KeyInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseInputEvent;
import net.minecraftforge.client.event.InputEvent.MouseScrollEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.gui.screen.SkillEditScreen;
import yesman.epicfight.client.input.EpicFightKeyMappings;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.entity.eventlistener.MovementInputEvent;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener.EventType;

@OnlyIn(Dist.CLIENT)
public class ControllEngine {
	private Map<KeyBinding, BiConsumer<KeyBinding, Integer>> keyFunctions;
	private Set<Object> packets = Sets.newHashSet();
	private Minecraft minecraft;
	private ClientPlayerEntity player;
	private LocalPlayerPatch playerpatch;
	private KeyBindingMap keyHash;
	private int mouseLeftPressCounter = 0;
	private int sneakPressCounter = 0;
	private int reservedKey;
	private int reserveCounter;
	private int lastHotbarLockedTime;
	private boolean sneakPressToggle = false;
	private boolean mouseLeftPressToggle = false;
	private boolean hotbarLocked;
	private boolean lightPress;
	public GameSettings options;
	
	public ControllEngine() {
		Events.controllEngine = this;
		this.minecraft = Minecraft.getInstance();
		this.options = this.minecraft.options;
		this.keyFunctions = Maps.newHashMap();
		this.keyFunctions.put(this.options.keyAttack, this::attackKeyPressed);
		this.keyFunctions.put(this.options.keySwapOffhand, this::swapHandKeyPressed);
		this.keyFunctions.put(EpicFightKeyMappings.SWITCH_MODE, this::switchModeKeyPressed);
		this.keyFunctions.put(EpicFightKeyMappings.DODGE, this::dodgeKeyPressed);
		this.keyFunctions.put(EpicFightKeyMappings.SPECIAL_SKILL, this::specialSkillKeyPressed);
		
		try {
			this.keyHash = (KeyBindingMap) ObfuscationReflectionHelper.findField(KeyBinding.class, "field_74514_b").get(null);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlayerPatch(LocalPlayerPatch playerpatch) {
		this.mouseLeftPressCounter = 0;
		this.mouseLeftPressToggle = false;
		this.sneakPressCounter = 0;
		this.sneakPressToggle = false;
		this.lightPress = false;
		this.player = playerpatch.getOriginal();
		this.playerpatch = playerpatch;
	}
	
	public boolean canPlayerMove(EntityState playerState) {
		return !playerState.movementLocked() || this.player.isRidingJumpable();
	}
	
	public boolean canPlayerRotate(EntityState playerState) {
		return !playerState.turningLocked() || this.player.isRidingJumpable();
	}
	
	private void attackKeyPressed(KeyBinding key, int action) {
		if (action == 1 && this.playerpatch.isBattleMode()) {
			this.setKeyBind(key, false);
			while (key.consumeClick());
			
			UseAction useAnim = this.playerpatch.getHoldingItemCapability(this.playerpatch.getOriginal().getUsedItemHand()).getUseAnimation(this.playerpatch);
			
			if (this.player.getUseItemRemainingTicks() == 0 || (useAnim == UseAction.BLOCK)) {
				if (!this.mouseLeftPressToggle) {
					this.mouseLeftPressToggle = true;
				}
			}
		}
	}
	
	private void dodgeKeyPressed(KeyBinding key, int action) {
		if (action == 1 && this.playerpatch.isBattleMode()) {
			if (key.getKey().getValue() == this.options.keyShift.getKey().getValue()) {
				if (this.player.getVehicle() == null) {
					if (!this.sneakPressToggle) {
						this.sneakPressToggle = true;
					}
				}
			} else {
				SkillCategory skillCategory = (this.playerpatch.getEntityState().knockDown()) ? SkillCategories.KNOCKDOWN_WAKEUP : SkillCategories.DODGE;
				SkillContainer skill = this.playerpatch.getSkill(skillCategory);
				
				if (skill.canExecute(this.playerpatch) && skill.getSkill().isExecutableState(this.playerpatch)) {
					skill.sendExecuteRequest(this.playerpatch, this.packets);
				}
			}
		}
	}
	
	private void swapHandKeyPressed(KeyBinding key, int action) {
		if (this.playerpatch.getEntityState().inaction() || (!this.playerpatch.getHoldingItemCapability(Hand.MAIN_HAND).canBePlacedOffhand())) {
			while (key.consumeClick()) {}
			this.setKeyBind(key, false);
		}
	}
	
	private void switchModeKeyPressed(KeyBinding key, int action) {
		if (action == 1) {
			this.playerpatch.toggleMode();
		}
	}
	
	private void specialSkillKeyPressed(KeyBinding key, int action) {
		if (action == 1 && this.playerpatch.isBattleMode()) {
			if (key.getKey().getValue() != 0) {
				if (!this.playerpatch.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK).sendExecuteRequest(this.playerpatch, this.packets)) {
					if (!this.player.isSpectator()) {
						this.reserveKey(SkillCategories.WEAPON_SPECIAL_ATTACK);
					}
				} else {
					this.lockHotkeys();
				}
			} else {
				if (this.options.keyAttack.equals(EpicFightKeyMappings.SPECIAL_SKILL)) {
					KeyBinding.click(this.options.keyAttack.getKey());
				}
			}
		}
	}
	
	public void tick() {
		if (this.playerpatch == null) {
			return;
		}
		
		if (this.player.tickCount - this.lastHotbarLockedTime > 20 && this.hotbarLocked) {
			this.unlockHotkeys();
		}
		
		if (this.mouseLeftPressToggle) {
			if (!this.isKeyDown(this.options.keyAttack)) {
				this.lightPress = true;
				this.mouseLeftPressToggle = false;
				this.mouseLeftPressCounter = 0;
			} else {
				if (EpicFightKeyMappings.SPECIAL_SKILL.getKey().equals(this.options.keyAttack.getKey())) {
					if (this.mouseLeftPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
						if (!this.playerpatch.getSkill(SkillCategories.WEAPON_SPECIAL_ATTACK).sendExecuteRequest(this.playerpatch, this.packets)) {
							if (!this.player.isSpectator()) {
								this.reserveKey(SkillCategories.WEAPON_SPECIAL_ATTACK);
							}
						} else {
							this.lockHotkeys();
						}
						
						this.mouseLeftPressToggle = false;
						this.mouseLeftPressCounter = 0;
					} else {
						this.setKeyBind(this.options.keyAttack, false);
						this.mouseLeftPressCounter++;
					}
				}
			}
		}
		
		if (this.lightPress) {
			SkillCategory slot = (!this.player.isOnGround() && !this.player.isInWater() && this.player.getDeltaMovement().y > -0.05D)
					? SkillCategories.AIR_ATTACK : SkillCategories.BASIC_ATTACK;
			
			if (this.playerpatch.getSkill(slot).sendExecuteRequest(this.playerpatch, this.packets)) {
				this.player.resetAttackStrengthTicker();
				this.lightPress = false;
				this.resetReservedKey();
				this.lockHotkeys();
			} else {
				if (!(this.player.isSpectator() || slot == SkillCategories.AIR_ATTACK)) {
					this.reserveKey(slot);
				}
			}
			
			this.lightPress = false;
			this.mouseLeftPressToggle = false;
			this.mouseLeftPressCounter = 0;
		}
		
		if (this.sneakPressToggle) {
			if (!this.isKeyDown(this.options.keyShift)) {
				SkillCategory skillCategory = (this.playerpatch.getEntityState().knockDown()) ? SkillCategories.KNOCKDOWN_WAKEUP : SkillCategories.DODGE;
				SkillContainer skill = this.playerpatch.getSkill(skillCategory);
				
				if (!skill.sendExecuteRequest(this.playerpatch, this.packets)) {
					this.reserveKey(SkillCategories.DODGE);
				}
				
				this.sneakPressToggle = false;
				this.sneakPressCounter = 0;
			} else {
				if (this.sneakPressCounter > EpicFightMod.CLIENT_INGAME_CONFIG.longPressCount.getValue()) {
					this.sneakPressToggle = false;
					this.sneakPressCounter = 0;
				} else {
					this.sneakPressCounter++;
				}
			}
		}
		
		if (this.reservedKey >= 0) {
			if (this.reserveCounter > 0) {
				SkillContainer skill = this.playerpatch.getSkill(this.reservedKey);
				this.reserveCounter--;
				
				if (skill.getSkill() != null) {
					if (skill.sendExecuteRequest(this.playerpatch, this.packets)) {
						this.resetReservedKey();
					} else {
						this.lockHotkeys();
					}
				}
			} else {
				this.resetReservedKey();
			}
		}
		
		if (this.playerpatch.getEntityState().inaction() || this.hotbarLocked) {
			for (int i = 0; i < 9; ++i) {
				while (this.options.keyHotbarSlots[i].consumeClick());
			}
		}
		
		if (EpicFightKeyMappings.SKILL_EDIT.consumeClick()) {
			if (this.playerpatch.getSkillCapability() != null) {
				Minecraft.getInstance().setScreen(new SkillEditScreen(this.playerpatch.getSkillCapability()));
			}
		}
		
		if (this.minecraft.isPaused()) {
			this.minecraft.mouseHandler.setup(Minecraft.getInstance().getWindow().getWindow());
		}
	}
	
	private void reserveKey(SkillCategory slot) {
		this.reservedKey = slot.universalOrdinal();
		this.reserveCounter = 8;
	}
	
	private void resetReservedKey() {
		this.reservedKey = -1;
		this.reserveCounter = -1;
	}
	
	public boolean isKeyDown(KeyBinding key) {
		if (key.getKey().getType() == InputMappings.Type.KEYSYM) {
			return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		} else if(key.getKey().getType() == InputMappings.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(Minecraft.getInstance().getWindow().getWindow(), key.getKey().getValue()) > 0;
		} else {
			return false;
		}
	}
	
	public void setKeyBind(KeyBinding key, boolean setter) {
		KeyBinding.set(key.getKey(), setter);
	}
	
	public void lockHotkeys() {
		this.hotbarLocked = true;
		this.lastHotbarLockedTime = this.player.tickCount;
		
		for (int i = 0; i < 9; ++i) {
			while (this.options.keyHotbarSlots[i].consumeClick());
		}
	}
	
	public void unlockHotkeys() {
		this.hotbarLocked = false;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT)
	public static class Events {
		static ControllEngine controllEngine;
		
		@SubscribeEvent
		public static void mouseEvent(MouseInputEvent event) {
			if (controllEngine.minecraft.player != null && Minecraft.getInstance().screen == null) {
				InputMappings.Input input = InputMappings.Type.MOUSE.getOrCreate(event.getButton());
				
				for (KeyBinding keybinding : controllEngine.keyHash.lookupAll(input)) {
					if (controllEngine.keyFunctions.containsKey(keybinding)) {
						controllEngine.keyFunctions.get(keybinding).accept(keybinding, event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void mouseScrollEvent(MouseScrollEvent event) {
			if (controllEngine.minecraft.player != null && controllEngine.playerpatch != null && controllEngine.playerpatch.getEntityState().inaction()) {
				if (controllEngine.minecraft.screen == null) {
					event.setCanceled(true);
				}
			}
		}
		
		@SubscribeEvent
		public static void keyboardEvent(KeyInputEvent event) {
			if (controllEngine.minecraft.player != null && Minecraft.getInstance().screen == null) {
				InputMappings.Input input = InputMappings.Type.KEYSYM.getOrCreate(event.getKey());
				
				for (KeyBinding keybinding : controllEngine.keyHash.lookupAll(input)) {
					if (controllEngine.keyFunctions.containsKey(keybinding)) {
						controllEngine.keyFunctions.get(keybinding).accept(keybinding, event.getAction());
					}
				}
			}
		}
		
		@SubscribeEvent
		public static void moveInputEvent(InputUpdateEvent event) {
			if (controllEngine.playerpatch == null) {
				return;
			}
			
			EntityState playerState = controllEngine.playerpatch.getEntityState();
			
			if (!controllEngine.canPlayerMove(playerState)) {
				event.getMovementInput().forwardImpulse = 0F;
				event.getMovementInput().leftImpulse = 0F;
				event.getMovementInput().up = false;
				event.getMovementInput().down = false;
				event.getMovementInput().left = false;
				event.getMovementInput().right = false;
				event.getMovementInput().jumping = false;
				event.getMovementInput().shiftKeyDown = false;
				
				ClientPlayerEntity clientPlayer = ((ClientPlayerEntity)event.getPlayer());
				
				clientPlayer.setSprinting(false);
				clientPlayer.sprintTriggerTime = -1;
				controllEngine.setKeyBind(controllEngine.options.keySprint, false);
			}
			
			if (event.getPlayer().isAlive()) {
				controllEngine.playerpatch.getEventListener().triggerEvents(EventType.MOVEMENT_INPUT_EVENT, new MovementInputEvent(controllEngine.playerpatch, event.getMovementInput()));
			}
		}
		
		@SubscribeEvent
		public static void preProcessKeyBindings(TickEvent.ClientTickEvent event) {
			if (event.phase == TickEvent.Phase.START) {
				if (controllEngine.minecraft.player != null) {
					controllEngine.tick();
				}
			} else {
				for (Object packet : controllEngine.packets) {
					EpicFightNetworkManager.sendToServer(packet);
				}
				
				controllEngine.packets.clear();
			}
		}
	}
}