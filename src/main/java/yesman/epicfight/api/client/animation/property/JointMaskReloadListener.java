package yesman.epicfight.api.client.animation.property;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.compress.utils.Lists;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;

@OnlyIn(Dist.CLIENT)
public class JointMaskReloadListener extends SimpleJsonResourceReloadListener {
	private static final BiMap<ResourceLocation, List<JointMask>> JOINT_MASKS = HashBiMap.create();
	private static final Map<String, JointMask.BindModifier> BIND_MODIFIERS = Maps.newHashMap();
	
	static {
		BIND_MODIFIERS.put("keep_child_locrot", JointMask.KEEP_CHILD_LOCROT);
	}
	
	public static List<JointMask> getJointMaskEntry(String type) {
		ResourceLocation rl = new ResourceLocation(type);
		return JOINT_MASKS.getOrDefault(rl, JointMaskEntry.ALL);
	}
	
	public static ResourceLocation getKey(List<JointMask> type) {
		return JOINT_MASKS.inverse().get(type);
	}
	
	public static Set<Map.Entry<ResourceLocation, List<JointMask>>> entries() {
		return JOINT_MASKS.entrySet();
	}
	
	public JointMaskReloadListener() {
		super((new GsonBuilder()).create(), "animmodels/joint_mask");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profileFiller) {
		JOINT_MASKS.clear();
		
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			List<JointMask> masks = Lists.newArrayList();
			JsonObject object = entry.getValue().getAsJsonObject();
			JsonArray joints = object.getAsJsonArray("joints");
			JsonObject bindModifiers = object.has("bind_modifiers") ? object.getAsJsonObject("bind_modifiers") : null;
			
			for (JsonElement joint : joints) {
				String jointName = joint.getAsString();
				BindModifier modifier = null;
				
				if (bindModifiers != null) {
					String modifierName = bindModifiers.has(jointName) ? bindModifiers.get(jointName).getAsString() : null;
					modifier = BIND_MODIFIERS.get(modifierName);
				}
				
				masks.add(JointMask.of(jointName, modifier));
			}
			
			String path = entry.getKey().toString();
			ResourceLocation key = new ResourceLocation(entry.getKey().getNamespace(), path.substring(path.lastIndexOf("/") + 1));
			
			JOINT_MASKS.put(key, masks);
		}
	}
}