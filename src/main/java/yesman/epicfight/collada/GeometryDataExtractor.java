package yesman.epicfight.collada;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import yesman.epicfight.collada.xml.XmlNode;
import yesman.epicfight.utils.math.OpenMatrix4f;
import yesman.epicfight.utils.math.Vec2f;
import yesman.epicfight.utils.math.Vec3f;
import yesman.epicfight.utils.math.Vec4f;

public class GeometryDataExtractor
{
	private static final OpenMatrix4f CORRECTION = new OpenMatrix4f().rotate((float) Math.toRadians(-90), new Vec3f(1, 0, 0));
	
	private XmlNode geometryNode;
	private List<Integer> indexList = new ArrayList<Integer> ();
	
	public GeometryDataExtractor(XmlNode geometry)
	{
		this.geometryNode = geometry;
	}
	
	public List<VertexData> extractVertexNumber()
	{
		int vertexCount = Integer.parseInt(getVertexNumber(geometryNode));
		List<VertexData> vertices = new ArrayList<VertexData> ();
		
		for(int i = 0; i < vertexCount; i++)
		{
			vertices.add(new VertexData());
		}
		
		return vertices;
	}
	
	public void extractGeometryData(List<VertexData> vertices)
	{
		String[] rawPositionData = getPositions(geometryNode);
		String[] rawNormalData = getNormals(geometryNode);
		String[] rawTextureCoordData = getTextureCoords(geometryNode);
		String[] polyList = getPolyList(geometryNode);
		for(int i = 0; i < rawPositionData.length; i+=3)
		{
			Vec4f original = new Vec4f(Float.parseFloat(rawPositionData[i]), Float.parseFloat(rawPositionData[i+1]), Float.parseFloat(rawPositionData[i+2]), 1);
			OpenMatrix4f.transform(CORRECTION, original, original);
			Vec3f corrected = new Vec3f(original.x, original.y, original.z);
			vertices.get(i/3).setPosition(corrected);
		}
		
		for(int i = 0; i < polyList.length; i+=3)
		{
			int positionIndex = Integer.parseInt(polyList[i]);
			int normalIndex = Integer.parseInt(polyList[i+1]);
			int textureIndex = Integer.parseInt(polyList[i+2]);
			
			float normX = Float.parseFloat(rawNormalData[normalIndex*3]);
			float normY = Float.parseFloat(rawNormalData[normalIndex*3 + 1]);
			float normZ = Float.parseFloat(rawNormalData[normalIndex*3 + 2]);
			
			float coordX = Float.parseFloat(rawTextureCoordData[textureIndex*2]);
			float coordY = Float.parseFloat(rawTextureCoordData[textureIndex*2 + 1]);
			
			Vec2f textureCoord = new Vec2f(coordX, (1-coordY));
			Vec4f normal = new Vec4f(normX, normY, normZ, 1.0f);
			OpenMatrix4f.transform(CORRECTION, normal, normal);
			Vec3f normalCorrected = new Vec3f(normal.x, normal.y, normal.z);
			VertexData vertex = vertices.get(positionIndex);
			
			switch(vertex.compareTextureCoordinateAndNormal(normalCorrected, textureCoord))
			{
			case EMPTY:
				vertex.setTextureCoordinate(textureCoord);
				vertex.setNormal(normalCorrected);
				indexList.add(positionIndex);
				break;
			case EQUAL:
				indexList.add(positionIndex);
				break;
			case DIFFERENT:
				VertexData newVertex = new VertexData(vertex);
				newVertex.setNormal(normalCorrected);
				newVertex.setTextureCoordinate(textureCoord);
				indexList.add(vertices.size());
				vertices.add(newVertex);
				break;
			}
		}
	}
	
	public int[] getIndices()
	{
		return ArrayUtils.toPrimitive(indexList.toArray(new Integer[0]));
	}
	
	private String getVertexNumber(XmlNode node)
	{
		String positionsId = node.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XmlNode vertexData = node.getChildWithAttribute("source", "id", positionsId).getChild("technique_common").getChild("accessor");
		
		return vertexData.getAttribute("count");
	}
	
	private String[] getPositions(XmlNode node)
	{
		String positionsId = node.getChild("vertices").getChild("input").getAttribute("source").substring(1);
		XmlNode positionsData = node.getChildWithAttribute("source", "id", positionsId).getChild("float_array");
		
		return positionsData.getData().split(" ");
	}
	
	private String[] getNormals(XmlNode node)
	{
		String noramlId = searchNode(node).getChildWithAttribute("input", "semantic", "NORMAL")
				.getAttribute("source").substring(1);
		XmlNode noramlData = node.getChildWithAttribute("source", "id", noramlId).getChild("float_array");
		
		return noramlData.getData().split(" ");
	}
	
	private String[] getTextureCoords(XmlNode node)
	{
		String textureCoordId = searchNode(node).getChildWithAttribute("input", "semantic", "TEXCOORD")
				.getAttribute("source").substring(1);
		XmlNode textureCoordData = node.getChildWithAttribute("source", "id", textureCoordId).getChild("float_array");
		
		return textureCoordData.getData().split(" ");
	}
	
	private String[] getPolyList(XmlNode node)
	{
		return searchNode(node).getChild("p").getData().split(" ");
	}
	
	private XmlNode searchNode(XmlNode node) {
		if (node.hasChild("triangles")) {
			return node.getChild("triangles");
		} else if (node.hasChild("polylist")) {
			return node.getChild("polylist");
		}
		return null;
	}
}