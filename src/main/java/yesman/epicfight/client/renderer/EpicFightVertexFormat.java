package yesman.epicfight.client.renderer;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.client.model.AnimatedMesh;

@OnlyIn(Dist.CLIENT)
public class EpicFightVertexFormat {
	public static final VertexFormatElement ELEMENT_POSITION = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3, AnimatedMesh::pointPositionsBuffer);
	public static final VertexFormatElement ELEMENT_UV0 = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2, AnimatedMesh::uvPositionsBuffer);
	public static final VertexFormatElement ELEMENT_NORMAL = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3, AnimatedMesh::normalPositionsBuffer);
	public static final VertexFormatElement ELEMENT_JOINTS = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.GENERIC, 3, AnimatedMesh::jointPositionsBuffer);
	public static final VertexFormatElement ELEMENT_WEIGHTS = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 3, AnimatedMesh::weightPositionsBuffer);
	public static final VertexFormat SOLID_MODEL = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("Normal", ELEMENT_NORMAL).put("Joints", ELEMENT_JOINTS).put("Weights", ELEMENT_WEIGHTS).build());
	
	private static final Set<VertexFormatElement> FILTERTED_FORMATS = ImmutableSet.of(DefaultVertexFormat.ELEMENT_COLOR, DefaultVertexFormat.ELEMENT_UV1, DefaultVertexFormat.ELEMENT_UV2);
	
	private static final Map<VertexFormatElement, VertexFormatElement> VERTEX_FORMAT_MAPPING = ImmutableMap.of(
			DefaultVertexFormat.ELEMENT_POSITION, ELEMENT_POSITION,
			DefaultVertexFormat.ELEMENT_UV0, ELEMENT_UV0,
			DefaultVertexFormat.ELEMENT_NORMAL, ELEMENT_NORMAL
		);
	
	public static boolean keep(VertexFormatElement vertexFormatElement) {
		return !FILTERTED_FORMATS.contains(vertexFormatElement);
	}
	
	public static VertexFormatElement convert(VertexFormatElement vertexFormatElement) {
		return VERTEX_FORMAT_MAPPING.getOrDefault(vertexFormatElement, vertexFormatElement);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class AnimationVertexFormat extends VertexFormat {
		public AnimationVertexFormat(ImmutableMap<String, VertexFormatElement> attributesMap) {
			super(attributesMap);
		}
	}
}
