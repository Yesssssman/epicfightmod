package yesman.epicfight.api.model;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

public class Model {
	protected Armature armature;
	protected ResourceLocation location;

	public Model(ResourceLocation location) {
		this.location = new ResourceLocation(location.getNamespace(), "animmodels/" + location.getPath() + ".json");
	}
	
	public void loadArmatureData(ResourceManager resourceManager) {
		JsonModelLoader loader = new JsonModelLoader(resourceManager, this.location);
		this.armature = loader.getArmature();
	}
	
	public void loadArmatureData(Armature armature) {
		this.armature = armature;
	}

	public Armature getArmature() {
		return armature;
	}
}