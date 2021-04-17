package maninthehouse.epicfight.collada;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import maninthehouse.epicfight.animation.Joint;
import maninthehouse.epicfight.client.model.Mesh;
import maninthehouse.epicfight.collada.xml.XmlNode;
import maninthehouse.epicfight.collada.xml.XmlParser;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ColladaModelLoader {
	@SideOnly(Side.CLIENT)
	public static Mesh getMeshData(ResourceLocation path) throws IOException {
		BufferedReader bufreader = null;
		try {
			bufreader = new BufferedReader(new InputStreamReader(getInputStream(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		GeometryDataExtractor geometry = new GeometryDataExtractor(rootNode.getChild("library_geometries").getChild("geometry").getChild("mesh"));
		SkinDataExtractor skin = new SkinDataExtractor(rootNode.getChild("library_controllers").getChild("controller").getChild("skin"));
		List<VertexData> vertices = geometry.extractVertexNumber();
		skin.extractSkinData(vertices);
		geometry.extractGeometryData(vertices);
		Mesh meshdata = VertexData.loadVertexInformation(vertices, geometry.getIndices(), true);

		return meshdata;
	}

	public static Armature getArmature(ResourceLocation path) throws IOException {
		BufferedReader bufreader = null;

		try {
			bufreader = new BufferedReader(new InputStreamReader(getInputStream(path)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		XmlNode rootNode = XmlParser.loadXmlFile(bufreader);
		SkinDataExtractor skin = new SkinDataExtractor(
				rootNode.getChild("library_controllers").getChild("controller").getChild("skin"));
		JointDataExtractor skeleton = new JointDataExtractor(rootNode.getChild("library_visual_scenes")
				.getChild("visual_scene").getChildWithAttribute("node", "id", "Armature"), skin.getRawJoints());
		Joint joint = skeleton.extractSkeletonData();
		joint.setInversedModelTransform(new VisibleMatrix4f());
		Armature armature = new Armature(skeleton.getJointNumber(), joint, skeleton.getJointTable());
		return armature;
	}

	protected static BufferedInputStream getInputStream(ResourceLocation resourceLocation) throws FileNotFoundException {
		BufferedInputStream inputStream = new BufferedInputStream(
				EpicFightMod.class.getResourceAsStream("/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath()));
		return inputStream;
	}
}