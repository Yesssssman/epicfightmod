package yesman.epicfight.api.client.animation;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import net.minecraft.server.packs.resources.Resource;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public class AnimationDataReader {
	static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AnimationDataReader.class, new Deserializer()).create();
	static final TypeToken<AnimationDataReader> TYPE = new TypeToken<AnimationDataReader>() {
	};
	
	public static void readAndApply(StaticAnimation animation, Resource iresource) {
		InputStream inputstream = iresource.getInputStream();
        Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
        AnimationDataReader propertySetter = GsonHelper.fromJson(GSON, reader, TYPE);
        
        if (propertySetter.jointMaskEntry.isValid()) {
        	animation.addProperty(ClientAnimationProperties.POSE_MODIFIER, propertySetter.jointMaskEntry);
        }
        
        animation.addProperty(ClientAnimationProperties.PRIORITY, propertySetter.priority);
        animation.addProperty(ClientAnimationProperties.LAYER_TYPE, propertySetter.layerType);
	}
	
	private JointMaskEntry jointMaskEntry;
	private Layer.LayerType layerType;
	private Layer.Priority priority;
	
	private AnimationDataReader(JointMaskEntry jointMaskEntry, Layer.Priority priority, Layer.LayerType layerType) {
		this.jointMaskEntry = jointMaskEntry;
		this.priority = priority;
		this.layerType = layerType;
	}
	
	static class Deserializer implements JsonDeserializer<AnimationDataReader> {
		@Override
		public AnimationDataReader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			JointMaskEntry.Builder builder = JointMaskEntry.builder();
			Layer.Priority priority = jsonObject.has("priority") ? Layer.Priority.valueOf(GsonHelper.getAsString(jsonObject, "priority")) : Layer.Priority.LOWEST;
			Layer.LayerType layerType = jsonObject.has("layer") ? Layer.LayerType.valueOf(GsonHelper.getAsString(jsonObject, "layer")) : Layer.LayerType.BASE_LAYER;
			
			if (jsonObject.has("masks")) {
				builder.defaultMask(JointMaskEntry.NONE);
				JsonArray maskArray = jsonObject.get("masks").getAsJsonArray();
				maskArray.forEach((element) -> {
					JsonObject jointMaskEntry = element.getAsJsonObject();
					
					String livingMotionName = GsonHelper.getAsString(jointMaskEntry, "livingmotion");
					if (livingMotionName.equals("ALL")) {
						builder.defaultMask(getJointMaskEntry(GsonHelper.getAsString(jointMaskEntry, "type")));
					} else {
						LivingMotion livingMotion = LivingMotion.valueOf(livingMotionName);
						builder.mask(livingMotion, getJointMaskEntry(GsonHelper.getAsString(jointMaskEntry, "type")));
					}
				});
			}
			
			return new AnimationDataReader(builder.create(), priority, layerType);
		}
	}
	
	private static List<JointMask> getJointMaskEntry(String type) {
		switch (type) {
		case "none":
			return JointMaskEntry.NONE;
		case "arms":
			return JointMaskEntry.BIPED_ARMS;
		case "upper_joints":
			return JointMaskEntry.BIPED_UPPER_JOINTS;
		case "root_upper_joints":
			return JointMaskEntry.BIPED_UPPER_JOINTS_WITH_ROOT;
		default:
			return JointMaskEntry.NONE;
		}
	}
}