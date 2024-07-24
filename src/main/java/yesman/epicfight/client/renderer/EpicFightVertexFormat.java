package yesman.epicfight.client.renderer;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EpicFightVertexFormat {
	public static final VertexFormatElement ELEMENT_POSITION = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.POSITION, 3);
	public static final VertexFormatElement ELEMENT_UV0 = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.UV, 2);
	public static final VertexFormatElement ELEMENT_NORMAL = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.BYTE, VertexFormatElement.Usage.NORMAL, 3);
	public static final VertexFormatElement ELEMENT_JOINTS = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.SHORT, VertexFormatElement.Usage.GENERIC, 3);
	public static final VertexFormatElement ELEMENT_WEIGHTS = new EpicFightVertexFormatElement(0, VertexFormatElement.Type.FLOAT, VertexFormatElement.Usage.GENERIC, 3);
	
	private static final Map<String, String> ELEMENT_NAME_MAPPING = ImmutableMap.of(
			"Position", "Position_a",
			"Normal", "Normal_a"
		);
	
	public static final Map<VertexFormatElement, VertexFormatElement> VERTEX_FORMAT_MAPPING = ImmutableMap.of(
			DefaultVertexFormat.ELEMENT_POSITION, ELEMENT_POSITION,
			DefaultVertexFormat.ELEMENT_UV0, ELEMENT_UV0,
			DefaultVertexFormat.ELEMENT_NORMAL, ELEMENT_NORMAL
		);
	
	public static String toAnimationShaderAttributeName(String attrName) {
		return ELEMENT_NAME_MAPPING.getOrDefault(attrName, attrName);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class AnimationVertexFormat extends VertexFormat {
		public AnimationVertexFormat(ImmutableMap<String, VertexFormatElement> attributesMap) {
			super(attributesMap);
		}
	}
}
