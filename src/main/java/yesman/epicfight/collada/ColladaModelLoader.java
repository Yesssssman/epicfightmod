package yesman.epicfight.collada;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.client.model.Mesh;
import yesman.epicfight.collada.xml.XmlNode;
import yesman.epicfight.collada.xml.XmlParser;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Armature;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class ColladaModelLoader {
	@OnlyIn(Dist.CLIENT)
	public static Mesh getMeshData(IResourceManager resourceManager, ResourceLocation path) throws IOException {
		BufferedReader bufreader = getBufferedReaderUnsafe(resourceManager, path);
		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		GeometryDataExtractor geometry = new GeometryDataExtractor(rootNode.getChild("library_geometries").getChild("geometry").getChild("mesh"));
		SkinDataExtractor skin = new SkinDataExtractor(rootNode.getChild("library_controllers").getChild("controller").getChild("skin"));
		List<VertexData> vertices = geometry.extractVertexNumber();
		skin.extractSkinData(vertices);
		geometry.extractGeometryData(vertices);
		Mesh meshdata = VertexData.loadVertexInformation(vertices, geometry.getIndices(), true);
		return meshdata;
	}
	
	public static Armature getArmature(IResourceManager resourceManager, ResourceLocation path) throws IOException {
		BufferedReader bufreader = getBufferedReaderUnsafe(resourceManager, path);
		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		SkinDataExtractor skin = new SkinDataExtractor(rootNode.getChild("library_controllers").getChild("controller").getChild("skin"));
		JointDataExtractor skeleton = new JointDataExtractor(rootNode.getChild("library_visual_scenes").getChild("visual_scene").getChildWithAttribute("node", "id", "Armature"), skin.getRawJoints());
		Joint joint = skeleton.extractSkeletonData();
		joint.setInversedModelTransform(new OpenMatrix4f());
		Armature armature = new Armature(skeleton.getJointNumber(), joint, skeleton.getJointMap());
		return armature;
	}
	
	public static BufferedReader getBufferedReaderUnsafe(IResourceManager resourceManager, ResourceLocation resourceLocation) {
		if (resourceManager == null) {
			return new BufferedReader(new InputStreamReader(new BufferedInputStream(EpicFightMod.class.getResourceAsStream("/assets/" + resourceLocation.getNamespace() + "/" + resourceLocation.getPath()))));
		} else {
			try {
				return new BufferedReader(new InputStreamReader(resourceManager.getResource(resourceLocation).getInputStream()));
			} catch (IOException e) {
				System.err.println(e);
				throw new IllegalArgumentException("[EpicFightMod] : Had a problem with reading file " + resourceLocation);
			}
		}
	}
}