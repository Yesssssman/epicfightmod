package maninthehouse.epicfight.client.model;

import java.util.List;

import javax.vecmath.Matrix4f;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class DynamicPerspectiveModel implements IBakedModel {
	private final IBakedModel model_2d;
	private final IBakedModel model_3d;
	
	public DynamicPerspectiveModel(IBakedModel model_2d, IBakedModel model_3d) {
		this.model_2d = model_2d;
		this.model_3d = model_3d;
	}
	
	@Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(TransformType cameraTransformType) {
        if (cameraTransformType == TransformType.GUI || cameraTransformType == TransformType.GROUND || cameraTransformType == TransformType.FIXED) {
        	return model_2d.handlePerspective(cameraTransformType);
        } else {
        	return model_3d.handlePerspective(cameraTransformType);
        }
    }

	@Override
	public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
		return model_3d.getQuads(state, side, rand);
	}

	@Override
	public boolean isAmbientOcclusion() {
		return false;
	}

	@Override
	public boolean isGui3d() {
		return false;
	}

	@Override
	public boolean isBuiltInRenderer() {
		return false;
	}

	@Override
	public TextureAtlasSprite getParticleTexture() {
		return model_3d.getParticleTexture();
	}

	@Override
	public ItemOverrideList getOverrides() {
		return model_3d.getOverrides();
	}
}
