package yesman.epicfight.client.renderer.patched.layer;

import java.util.NoSuchElementException;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;
import yesman.epicfight.api.client.model.Meshes;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.client.renderer.patched.entity.PatchedLivingEntityRenderer;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class LayerUtil {
	public static <E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends AnimatedMesh> void addLayer(PatchedLivingEntityRenderer<E, T, M, R, AM> renderer, JsonElement jsonElement) throws NoSuchElementException, ClassNotFoundException {
		JsonObject jsonobj = jsonElement.getAsJsonObject();
		
		if (!jsonobj.has("layer_type")) {
			throw new NoSuchElementException("Layer type undefined");
		}
		
		if (!jsonobj.has("target_layer")) {
			throw new NoSuchElementException("Target layer undefined");
		}
		
		String layerType = jsonobj.get("layer_type").getAsString();
		String targetLayer = jsonobj.get("target_layer").getAsString();
		
		Class<?> clss = Class.forName(targetLayer);
		
		if ("epicfight:invisible".equals(layerType)) {
			addInvisibleLayer(renderer, clss, jsonElement);
		} else if ("epicfight:eyes".equals(layerType)) {
			addEyesLayer(renderer, clss, jsonElement);
		} else if ("epicfight:model_original".equals(layerType)) {
			addOriginalModelLayer(renderer, clss, jsonElement);
		} else {
			throw new NoSuchElementException("No layer type " + layerType);
		}
	}
	
	private static <E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends AnimatedMesh> void addInvisibleLayer(PatchedLivingEntityRenderer<E, T, M, R, AM> renderer, Class<?> targetLayer, JsonElement jsonElement) {
		renderer.addPatchedLayer(targetLayer, new EmptyLayer<E, T, M> ());
	}
	
	private static <E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends AnimatedMesh> void addEyesLayer(PatchedLivingEntityRenderer<E, T, M, R, AM> renderer, Class<?> targetLayer, JsonElement jsonElement) {
		JsonObject properties = jsonElement.getAsJsonObject();
		
		if (!properties.has("texture")) {
			throw new NoSuchElementException("Layer type epicfight:eyes requires to specify texture");
		}
		
		if (!properties.has("model")) {
			throw new NoSuchElementException("Layer type epicfight:eyes requires to specify model");
		}
		
		ResourceLocation textureLocation = new ResourceLocation(properties.get("texture").getAsString());
		AnimatedMesh mesh = Meshes.getOrCreateAnimatedMesh(Minecraft.getInstance().getResourceManager(), new ResourceLocation(properties.get("model").getAsString()), AnimatedMesh::new);
		
		renderer.addPatchedLayer(targetLayer, new PatchedEyesLayer<> (textureLocation, mesh));
	}
	
	private static <E extends LivingEntity, T extends LivingEntityPatch<E>, M extends EntityModel<E>, R extends LivingEntityRenderer<E, M>, AM extends AnimatedMesh> void addOriginalModelLayer(PatchedLivingEntityRenderer<E, T, M, R, AM> renderer, Class<?> targetLayer, JsonElement jsonElement) {
		JsonObject properties = jsonElement.getAsJsonObject();
		Vec3f vec = new Vec3f();
		Vec3f rot = new Vec3f();
		
		if (!properties.has("joint")) {
			throw new NoSuchElementException("Layer type epicfight:model_original requires to specify joint");
		}
		
		if (properties.has("translation")) {
			JsonArray translationVector = GsonHelper.getAsJsonArray(properties, "translation");
			vec.x = translationVector.get(0).getAsFloat();
			vec.y = translationVector.get(1).getAsFloat();
			vec.z = translationVector.get(2).getAsFloat();
		}
		
		if (properties.has("rotation")) {
			JsonArray rotation = GsonHelper.getAsJsonArray(properties, "rotation");
			rot.x = rotation.get(0).getAsFloat();
			rot.y = rotation.get(1).getAsFloat();
			rot.z = rotation.get(2).getAsFloat();
		}
		
		renderer.addPatchedLayer(targetLayer, new RenderOriginalModelLayer<> (properties.get("joint").getAsString(), vec, rot));
	}
}