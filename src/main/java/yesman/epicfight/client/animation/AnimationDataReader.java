package yesman.epicfight.client.animation;

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

import net.minecraft.resources.IResource;
import net.minecraft.util.JSONUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public class AnimationDataReader {
	static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(AnimationDataReader.class, new Deserializer()).create();
	static final TypeToken<AnimationDataReader> TYPE = new TypeToken<AnimationDataReader>() {
	};
	
	public static void readAndApply(StaticAnimation animation, IResource iresource) {
		InputStream inputstream = iresource.getInputStream();
        Reader reader = new InputStreamReader(inputstream, StandardCharsets.UTF_8);
        AnimationDataReader propertySetter = JSONUtils.fromJSONUnlenient(GSON, reader, TYPE);
        if (propertySetter.bindModifier.isValid()) {
        	animation.addProperty(ClientAnimationProperties.POSE_MODIFIER, propertySetter.bindModifier);
        }
        animation.addProperty(ClientAnimationProperties.PRIORITY, propertySetter.priority);
	}
	
	private PoseModifier bindModifier;
	private Layer.Priority priority;
	
	private AnimationDataReader(PoseModifier bindModifier, Layer.Priority priority) {
		this.bindModifier = bindModifier;
		this.priority = priority;
	}
	
	static class Deserializer implements JsonDeserializer<AnimationDataReader> {
		@Override
		public AnimationDataReader deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
			JsonObject jsonObject = json.getAsJsonObject();
			PoseModifier.Builder builder = PoseModifier.builder();
			Layer.Priority priority = jsonObject.has("priority") ? Layer.Priority.valueOf(JSONUtils.getString(jsonObject, "priority")) : Layer.Priority.LOWEST;
			if (jsonObject.has("masks")) {
				builder.setDefaultData(PoseModifier.NONE);
				JsonArray maskArray = jsonObject.get("masks").getAsJsonArray();
				maskArray.forEach((element) -> {
					JsonObject modifierEntry = element.getAsJsonObject();
					String livingMotionString = JSONUtils.getString(modifierEntry, "livingmotion");
					if (livingMotionString.equals("ALL")) {
						builder.setDefaultData(getBindData(JSONUtils.getString(modifierEntry, "type")));
					} else {
						LivingMotion livingMotion = LivingMotion.valueOf(livingMotionString);
						builder.addEntry(livingMotion, getBindData(JSONUtils.getString(modifierEntry, "type")));
					}
				});
			}
			return new AnimationDataReader(builder.create(), priority);
		}
	}
	
	private static List<PoseModifyingEntry> getBindData(String type) {
		switch (type) {
		case "none":
			return PoseModifier.NONE;
		case "arms":
			return PoseModifier.BIPED_ARMS;
		case "upper_joints":
			return PoseModifier.BIPED_UPPER_JOINTS;
		case "root_upper_joints":
			return PoseModifier.BIPED_UPPER_JOINTS_ROOT;
		default:
			return PoseModifier.NONE;
		}
	}
}