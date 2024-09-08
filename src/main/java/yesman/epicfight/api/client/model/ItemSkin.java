package yesman.epicfight.api.client.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.property.TrailInfo;

@OnlyIn(Dist.CLIENT)
public record ItemSkin(TrailInfo trailInfo, boolean forceVanillaFirstPerson) {
	public static ItemSkin deserialize(JsonElement element) {
		JsonObject jsonObj = element.getAsJsonObject();
		TrailInfo trailInfo = jsonObj.has("trail") ? TrailInfo.deserialize(jsonObj.get("trail")) : null;
		boolean forceVanillaFirstPerson = jsonObj.has("force_vanilla_first_person") ? GsonHelper.getAsBoolean(jsonObj, "force_vanilla_first_person") : false;
		return new ItemSkin(trailInfo, forceVanillaFirstPerson);
	}
}