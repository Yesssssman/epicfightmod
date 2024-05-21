package yesman.epicfight.data.conditions.entity;

import java.util.List;
import java.util.Locale;

import io.netty.util.internal.StringUtil;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.utils.ParseUtil;
import yesman.epicfight.client.gui.datapack.widgets.ComboBox;
import yesman.epicfight.data.conditions.Condition.PlayerPatchCondition;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.item.WeaponCategory;

public class OffhandItemCategory extends PlayerPatchCondition {
	private WeaponCategory category;
	
	@Override
	public OffhandItemCategory read(CompoundTag tag) {
		if (!tag.contains("category") || StringUtil.isNullOrEmpty(tag.getString("category"))) {
			throw new IllegalArgumentException("Undefined weapon category");
		}
		
		this.category = WeaponCategory.ENUM_MANAGER.getOrThrow(tag.getString("category"));
		
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		
		tag.putString("category", this.category.toString());
		
		return tag;
	}
	
	@Override
	public boolean predicate(PlayerPatch<?> target) {
		return target.getHoldingItemCapability(InteractionHand.OFF_HAND).getWeaponCategory() == this.category;
	}
	
	@OnlyIn(Dist.CLIENT)
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		AbstractWidget comboBox = new ComboBox<>(screen, screen.getMinecraft().font, 0, 0, 0, 0, null, null, 4, Component.literal("category"), List.copyOf(WeaponCategory.ENUM_MANAGER.universalValues()), ParseUtil::snakeToSpacedCamel, null);
		
		return List.of(ParameterEditor.of((value) -> StringTag.valueOf(value.toString().toLowerCase(Locale.ROOT)), (tag) -> WeaponCategory.ENUM_MANAGER.get(ParseUtil.nullOrToString(tag, Tag::getAsString)), comboBox));
	}
}