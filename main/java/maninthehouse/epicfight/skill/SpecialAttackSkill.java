package maninthehouse.epicfight.skill;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.AnimationProperty;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.animation.types.attack.AttackAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.capabilities.item.CapabilityItem;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSExecuteSkill;
import maninthehouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SpecialAttackSkill extends Skill {
	protected final StaticAnimation attackAnimation;
	protected Map<AnimationProperty<?>, Object> propertyMap;

	public SpecialAttackSkill(SkillSlot index, float restriction, String skillName, StaticAnimation animation) {
		this(index, restriction, 0, skillName, animation);
	}
	
	public SpecialAttackSkill(SkillSlot index, float restriction, int duration, String skillName, StaticAnimation animation) {
		super(index, restriction, duration, true, skillName);
		this.propertyMap = new HashMap<AnimationProperty<?>, Object>();
		this.attackAnimation = animation;
	}

	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		ModNetworkManager.sendToServer(new CTSExecuteSkill(this.slot.getIndex(), true, args));
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		executer.playAnimationSynchronize(attackAnimation, 0);
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}

	@Override
	public float getRegenTimePerTick(PlayerData<?> player) {
		return 0;
	}
	
	@Override
	public boolean canExecute(PlayerData<?> executer) {
		CapabilityItem item = executer.getHeldItemCapability(EnumHand.MAIN_HAND);
		if (item != null) {
			Skill skill = item.getSpecialAttack(executer);
			return skill == this && executer.getOriginalEntity().getRidingEntity() == null;
		}
		
		return false;
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		EntityState playerState = executer.getEntityState();
		return !(executer.getOriginalEntity().isElytraFlying() || executer.currentMotion == LivingMotion.FALL || !playerState.canAct());
	}
	
	@SideOnly(Side.CLIENT)
	public List<ITextComponent> getTooltip() {
		List<ITextComponent> list = Lists.<ITextComponent>newArrayList();
		
		list.add(new TextComponentTranslation("skill." + EpicFightMod.MODID + "." + this.registryName.getResourcePath())
				.appendSibling(new TextComponentString(TextFormatting.AQUA + String.format("[%.0f]", this.cooldown))));
		list.add(new TextComponentTranslation(TextFormatting.DARK_GRAY + "skill." + EpicFightMod.MODID + "." + this.registryName.getResourcePath() + "_tooltip"));
		
		StringBuilder damageFormat = new StringBuilder("");
		
		if(this.propertyMap.containsKey(AnimationProperty.DAMAGE_MULTIPLIER)) {
			damageFormat.append(String.format("%.0f%%", this.getProperty(AnimationProperty.DAMAGE_MULTIPLIER) * 100.0F));
		} else {
			damageFormat.append("100%");
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.DAMAGE_ADDER)) {
			damageFormat.append(String.format(" + %.0f", this.getProperty(AnimationProperty.DAMAGE_ADDER)));
		}
		damageFormat.append(TextFormatting.DARK_GRAY + " damage");
		list.add(new TextComponentString(TextFormatting.DARK_RED + damageFormat.toString()));
		
		if(this.propertyMap.containsKey(AnimationProperty.IMPACT)) {
			list.add(new TextComponentString(String.format(TextFormatting.GOLD + "%.1f" + TextFormatting.DARK_GRAY + " impact", this.getProperty(AnimationProperty.IMPACT))));
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.ARMOR_NEGATION)) {
			list.add(new TextComponentString(String.format(TextFormatting.GOLD + "%.0f%%" + TextFormatting.DARK_GRAY + " armor negation", this.getProperty(AnimationProperty.ARMOR_NEGATION))));
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.HIT_AT_ONCE)) {
			list.add(new TextComponentString(String.format(TextFormatting.DARK_GRAY + "hit" + TextFormatting.WHITE + " %d " + TextFormatting.DARK_GRAY + "enemies", this.getProperty(AnimationProperty.HIT_AT_ONCE))));
		}
		
		if(this.propertyMap.containsKey(AnimationProperty.STUN_TYPE)) {
			list.add(new TextComponentString(TextFormatting.DARK_GRAY + "apply " + (this.getProperty(AnimationProperty.STUN_TYPE).toString())));
		}
		
		return list;
	}
	
	protected <T> T getProperty(AnimationProperty<T> propertyType)
	{
		return (T) this.propertyMap.get(propertyType);
	}
	
	public <T> SpecialAttackSkill addProperty(AnimationProperty<T> attribute, T object) {
		this.propertyMap.put(attribute, object);
		return this;
	}
	
	public SpecialAttackSkill registerPropertiesToAnimation() {
		((AttackAnimation)this.attackAnimation).addProperties(this.propertyMap.entrySet());
		return this;
	}
}