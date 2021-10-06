package yesman.epicfight.skill;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.CapabilityItem.Style;
import yesman.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.client.gui.BattleModeGui;
import yesman.epicfight.entity.eventlistener.HitEvent;
import yesman.epicfight.entity.eventlistener.PlayerEventListener.EventType;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.gamedata.Sounds;
import yesman.epicfight.particle.HitParticleType;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.utils.game.IExtendedDamageSource;

public class GuardSkill extends Skill {
	protected static final SkillDataKey<Integer> LAST_HIT_TICK = SkillDataKey.createDataKey(SkillDataManager.ValueType.INTEGER);
	protected static final SkillDataKey<Float> PENALTY = SkillDataKey.createDataKey(SkillDataManager.ValueType.FLOAT);
	protected static final UUID EVENT_UUID = UUID.fromString("b422f7a0-f378-11eb-9a03-0242ac130003");
	
	private static final Map<WeaponCategory, BiFunction<CapabilityItem, PlayerData<?>, StaticAnimation>> AVAILABLE_WEAPON_TYPES = 
			Maps.<WeaponCategory, BiFunction<CapabilityItem, PlayerData<?>, StaticAnimation>>newLinkedHashMap();
	
	static {
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.AXE, (item, player) -> Animations.SWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.GREATSWORD, (item, player) -> Animations.GREATSWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.KATANA, (item, player) -> Animations.KATANA_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.LONGSWORD, (item, player) -> Animations.LONGSWORD_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.SPEAR, (item, player) -> item.getStyle(player) == Style.TWO_HAND ? Animations.SPEAR_GUARD_HIT : null);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.SWORD, (item, player) -> item.getStyle(player) == Style.ONE_HAND ? Animations.SWORD_GUARD_HIT : Animations.SWORD_DUAL_GUARD_HIT);
		AVAILABLE_WEAPON_TYPES.put(WeaponCategory.TACHI, (item, player) -> Animations.LONGSWORD_GUARD_HIT);
	}
	
	public GuardSkill(String skillName) {
		super(SkillCategory.GUARD, 0, 0, ActivateType.ONE_SHOT, Resource.STAMINA, skillName);
	}
	
	@Override
	public void onInitiate(SkillContainer container) {
		container.getDataManager().registerData(LAST_HIT_TICK);
		container.getDataManager().registerData(PENALTY);
		
		container.executer.getEventListener().addEventListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerData().getHeldItemCapability(Hand.MAIN_HAND);
			if (AVAILABLE_WEAPON_TYPES.getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, event.getPlayerData()) != null
					&& this.isExecutableState(event.getPlayerData())) {
				event.getPlayerData().getOriginalEntity().setActiveHand(Hand.MAIN_HAND);
			}
			return false;
		});
		
		container.executer.getEventListener().addEventListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerData().getHeldItemCapability(Hand.MAIN_HAND);
			if (AVAILABLE_WEAPON_TYPES.getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, event.getPlayerData()) != null
					&& this.isExecutableState(event.getPlayerData())) {
				event.getPlayerData().getOriginalEntity().setActiveHand(Hand.MAIN_HAND);
			}
			return false;
		});
		
		container.executer.getEventListener().addEventListener(EventType.SERVER_ITEM_STOP_EVENT, EVENT_UUID, (event) -> {
			ServerPlayerEntity serverplayerentity = event.getPlayerData().getOriginalEntity();
			container.getDataManager().setDataSync(LAST_HIT_TICK, serverplayerentity.ticksExisted, serverplayerentity);
			return false;
		});
		
		container.executer.getEventListener().addEventListener(EventType.DEALT_DAMAGE_POST_EVENT, EVENT_UUID, (event) -> {
			container.getDataManager().setDataSync(PENALTY, 0.0F, event.getPlayerData().getOriginalEntity());
			return false;
		});
		
		container.executer.getEventListener().addEventListener(EventType.HIT_EVENT, EVENT_UUID, (event) -> {
			CapabilityItem itemCapability = event.getPlayerData().getHeldItemCapability(event.getPlayerData().getOriginalEntity().getActiveHand());
			if (this.getHitMotion(event.getPlayerData(), itemCapability, 0) != null && event.getPlayerData().getOriginalEntity().isHandActive()
					&& this.isExecutableState(event.getPlayerData())) {
				DamageSource damageSource = event.getForgeEvent().getSource();
				boolean isFront = false;
				Vector3d damageLocation = damageSource.getDamageLocation();
				
				if (damageLocation != null) {
					Vector3d vector3d = event.getPlayerData().getOriginalEntity().getLook(1.0F);
					Vector3d vector3d1 = damageLocation.subtractReverse(event.getPlayerData().getOriginalEntity().getPositionVec()).normalize();
					vector3d1 = new Vector3d(vector3d1.x, 0.0D, vector3d1.z);
					if (vector3d1.dotProduct(vector3d) < 0.0D) {
						isFront = true;
					}
				}
				
				if (isFront) {
					float impact = 0.5F;
					float knockback = 0.25F;
					if (event.getForgeEvent().getSource() instanceof IExtendedDamageSource) {
						impact = ((IExtendedDamageSource)event.getForgeEvent().getSource()).getImpact();
						knockback += impact * 0.1F;
					}
					
					return this.guard(container, itemCapability, event, knockback, impact, false);
				}
			}
			return false;
		});
	}
	
	public boolean guard(SkillContainer container, CapabilityItem itemCapability, HitEvent event, float knockback, float impact, boolean reinforced) {
		DamageSource damageSource = event.getForgeEvent().getSource();
		if (this.isBlockableSource(event.getForgeEvent().getSource(), reinforced)) {
			event.getPlayerData().playSound(Sounds.CLASH, -0.05F, 0.1F);
			Entity playerentity = event.getPlayerData().getOriginalEntity();
			Particles.HIT_BLUNT.get().spawnParticleWithArgument(((ServerWorld)playerentity.world), HitParticleType.POSITION_MIDDLE_OF_EACH_ENTITY,
					HitParticleType.ARGUMENT_ZERO, playerentity, damageSource.getImmediateSource());
			
			if (damageSource.getImmediateSource() instanceof LivingEntity) {
				knockback += EnchantmentHelper.getKnockbackModifier((LivingEntity)damageSource.getImmediateSource()) * 0.1F;
			}
			
			float penalty = container.getDataManager().getDataValue(PENALTY) + this.getPenaltyStamina(itemCapability);
			event.getPlayerData().knockBackEntity(damageSource.getImmediateSource(), knockback);
			float stamina = event.getPlayerData().getStamina() - penalty * impact;
			event.getPlayerData().setStamina(stamina);
			container.getDataManager().setDataSync(PENALTY, penalty, event.getPlayerData().getOriginalEntity());
			
			StaticAnimation animation = this.getHitMotion(event.getPlayerData(), itemCapability, stamina >= 0 ? 1 : 0);
			
			if (animation != null) {
				event.getPlayerData().playAnimationSynchronize(animation, 0);
			}
			
			return stamina >= 0.0F;
		} else {
			return false;
		}
	}
	
	protected float getPenaltyStamina(CapabilityItem itemCapapbility) {
		return 0.6F;
	}
	
	protected Map<WeaponCategory, BiFunction<CapabilityItem, PlayerData<?>, StaticAnimation>> getAvailableWeaponTypes(int meta) {
		return AVAILABLE_WEAPON_TYPES;
	}
	
	@Nullable
	protected StaticAnimation getHitMotion(PlayerData<?> playerdata, CapabilityItem itemCapability, int meta) {
		return this.getAvailableWeaponTypes(meta).getOrDefault(itemCapability.getWeaponCategory(), (a, b) -> null).apply(itemCapability, playerdata);
	}
	
	@Override
	public void updateContainer(SkillContainer container) {
		super.updateContainer(container);
		if (!container.executer.isRemote() && !container.executer.getOriginalEntity().isHandActive()) {
			float penalty = container.getDataManager().getDataValue(PENALTY);
			if (penalty > 0) {
				int hitTick = container.getDataManager().getDataValue(LAST_HIT_TICK);
				if (container.executer.getOriginalEntity().ticksExisted - hitTick > 40) {
					container.getDataManager().setDataSync(PENALTY, 0.0F, (ServerPlayerEntity)container.executer.getOriginalEntity());
				}
			}
		} else {
			container.executer.resetActionTick();
		}
	}
	
	@Override
	public void onRemoved(SkillContainer container) {
		container.executer.getEventListener().removeListener(EventType.HIT_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.CLIENT_ITEM_USE_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.SERVER_ITEM_STOP_EVENT, EVENT_UUID);
		container.executer.getEventListener().removeListener(EventType.DEALT_DAMAGE_POST_EVENT, EVENT_UUID);
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isElytraFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canExecuteSkill());
	}
	
	protected boolean isBlockableSource(DamageSource damageSource, boolean specialSourceBlockCondition) {
		return !damageSource.isUnblockable() && !damageSource.isProjectile() && !damageSource.isExplosion() && !damageSource.isMagicDamage() && !damageSource.isFireDamage();
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public List<Object> getTooltipArgs() {
		List<Object> list = Lists.<Object>newArrayList();
		list.add(String.format("%s, %s, %s, %s, %s, %s, %s", AVAILABLE_WEAPON_TYPES.keySet().toArray(new Object[0])).toLowerCase());
		return list;
	}
	
	@OnlyIn(Dist.CLIENT)
	public boolean shouldDraw(SkillContainer container) {
		return container.getDataManager().getDataValue(PENALTY) > 0.0F;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void drawOnGui(BattleModeGui gui, SkillContainer container, MatrixStack matStackIn, float x, float y, float scale, int width, int height) {
		matStackIn.push();
		matStackIn.scale(scale, scale, 1.0F);
		matStackIn.translate(0, (float)gui.getSlidingProgression() * 1.0F / scale, 0);
		Minecraft.getInstance().getTextureManager().bindTexture(Skills.GUARD.getSkillTexture());
		float scaleMultiply = 1.0F / scale;
		gui.drawTexturedModalRectFixCoord(matStackIn.getLast().getMatrix(), (width - x) * scaleMultiply, (height - y) * scaleMultiply, 0, 0, 255, 255);
		matStackIn.scale(scaleMultiply, scaleMultiply, 1.0F);
		gui.font.drawStringWithShadow(matStackIn, String.format("x%.1f", container.getDataManager().getDataValue(PENALTY)), ((float)width - x), ((float)height - y+6), 16777215);
	}
	
	protected boolean isHighTierGuard() {
		return false;
	}
}