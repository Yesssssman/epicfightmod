package yesman.epicfight.data.conditions.itemstack;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import io.netty.util.internal.StringUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.ItemStackCondition;

public class TagValueCondition extends ItemStackCondition {
	private String key;
	private String value;
	
	@Override
	public TagValueCondition read(CompoundTag tag) {
		this.key = tag.getString("key");
		this.value = tag.get("value").getAsString();
		
		if (this.key == null) {
			throw new IllegalArgumentException("No key provided!");
		}
		
		if (this.value == null) {
			throw new IllegalArgumentException("No value provided!");
		}
		
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		
		tag.putString("key", this.key);
		tag.putString("value", this.value);
		
		return tag;
	}
	
	@Override
	public boolean predicate(ItemStack itemstack) {
		String[] keys = this.key.split("[.]");
		List<Tag> visitTags = List.of(itemstack.getTag());
		
		for (int i = 0; i < keys.length; i++) {
			visitTags = visitTags(keys[i], visitTags);
		}
		
		for (Tag tag : visitTags) {
			if (tag.getAsString().equals(this.value)) {
				return true;
			}
		}
		
		return false;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters() {
		return ImmutableMap.of("key", (Object)"", "value", (Object)"").entrySet();
	}
	
	private static List<Tag> visitTags(String key, List<Tag> compoundTag) {
		Pattern pattern = Pattern.compile("\\[[0-9]*\\]", Pattern.MULTILINE);
		Matcher matcher = pattern.matcher(key);
		List<Tag> childs = Lists.newArrayList();
		
		if (matcher.find()) {
			String sIndex = matcher.group().replaceAll("[\\[\\]]", "");
			String arrayKey = matcher.replaceAll("");
			
			if (StringUtil.isNullOrEmpty(sIndex)) {
				for (Tag tag : compoundTag) {
					if (tag instanceof CompoundTag compTag && compTag.contains(arrayKey)) {
						ListTag listTag = (ListTag)compTag.get(arrayKey);
						
						for (Tag listTagElement : listTag) {
							childs.add(listTagElement);
						}
					}
				}
			} else {
				int index = Integer.valueOf(sIndex);
				
				for (Tag tag : compoundTag) {
					if (tag instanceof CompoundTag compTag && compTag.contains(arrayKey)) {
						ListTag listTag = (ListTag)compTag.get(arrayKey);
						childs.add(listTag.get(index));
					}
				}
			}
		} else {
			for (Tag tag : compoundTag) {
				if (tag instanceof CompoundTag compTag && compTag.contains(key)) {
					childs.add(compTag.get(key));
				}
			}
		}
		
		return childs;
	}
}