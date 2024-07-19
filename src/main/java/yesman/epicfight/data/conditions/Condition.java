package yesman.epicfight.data.conditions;

import java.util.List;
import java.util.function.Function;

import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface Condition<T> {
	default Condition<T> read(JsonElement json) throws CommandSyntaxException {
		return this.read(TagParser.parseTag(json.toString()));
	}
	
	public Condition<T> read(CompoundTag tag);
	public CompoundTag serializePredicate();
	public boolean predicate(T target);
	
	@OnlyIn(Dist.CLIENT)
	public List<ParameterEditor> getAcceptingParameters(Screen screen);
	
	public static abstract class EntityPatchCondition implements Condition<LivingEntityPatch<?>> {
	}
	
	public static abstract class EntityCondition implements Condition<Entity> {
	}
	
	public static abstract class ItemStackCondition implements Condition<ItemStack> {
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class ParameterEditor {
		public static ParameterEditor of(Function<Object, Tag> toTag, Function<Tag, Object> fromTag, AbstractWidget editWidget) {
			return new ParameterEditor(toTag, fromTag, editWidget);
		}
		
		public final Function<Object, Tag> toTag;
		public final Function<Tag, Object> fromTag;
		public final AbstractWidget editWidget;
		
		private ParameterEditor(Function<Object, Tag> toTag, Function<Tag, Object> fromTag, AbstractWidget editWidget) {
			this.toTag = toTag;
			this.fromTag = fromTag;
			this.editWidget = editWidget;
		}
	}
}