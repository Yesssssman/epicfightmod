package yesman.epicfight.api.client.model;

import com.google.gson.JsonElement;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.animation.property.TrailInfo;

@OnlyIn(Dist.CLIENT)
public class ItemSkin {
	public final TrailInfo trailInfo;
	
	public ItemSkin(TrailInfo trailInfo) {
		this.trailInfo = trailInfo;
	}
	
	public static ItemSkin deserialize(JsonElement element) {
		TrailInfo trailInfo = TrailInfo.deserialize(element.getAsJsonObject().get("trail"));
		
		return new ItemSkin(trailInfo);
	}
}