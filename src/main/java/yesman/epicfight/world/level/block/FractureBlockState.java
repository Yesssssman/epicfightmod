package yesman.epicfight.world.level.block;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class FractureBlockState extends BlockState {
	private Vector3f translate;
	private Quaternion rotation;
	private double bouncing;
	private int maxLifeTime;
	private static final Map<Integer, BlockState> ORIGINAL_BLOCK_STATE_CACHE = Maps.newHashMap();
	
	public static void remove(BlockPos blockPos) {
		ORIGINAL_BLOCK_STATE_CACHE.remove(blockPos.hashCode());
	}
	
	public static void reset() {
		ORIGINAL_BLOCK_STATE_CACHE.clear();
	}
	
	public FractureBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> propertyMap, MapCodec<BlockState> mapCodec) {
		super(block, propertyMap, mapCodec);
	}
	
	public void setFractureInfo(BlockPos bp, BlockState originalState, Vector3f translate, Quaternion rotation, double bouncing, int maxLifeTime) {
		ORIGINAL_BLOCK_STATE_CACHE.put(bp.hashCode(), originalState);
		this.translate = translate;
		this.rotation = rotation;
		this.bouncing = bouncing;
		this.maxLifeTime = maxLifeTime;
	}
	
	public Vector3f getTranslate() {
		return this.translate;
	}
	
	public Quaternion getRotation() {
		return this.rotation;
	}
	
	public BlockState getOriginalBlockState(BlockPos blockPos) {
		return ORIGINAL_BLOCK_STATE_CACHE.get(blockPos.hashCode());
	}
	
	public double getBouncing() {
		return this.bouncing;
	}
	
	public int getLifeTime() {
		return this.maxLifeTime;
	}
	
	@Override
	public boolean hasBlockEntity() {
		return true;
	}
	
	@Override
	public boolean hidesNeighborFace(BlockGetter level, BlockPos pos, BlockState neighborState, Direction dir) {
		return false;
	}
	
	@Override
	public boolean supportsExternalFaceHiding() {
		return false;
	}
	
	@Override
	public int getLightEmission(BlockGetter level, BlockPos blockPos) {
		return this.owner.getLightEmission(this, level, blockPos);
	}
	
	@Override
	public VoxelShape getShape(BlockGetter level, BlockPos blockPos) {
		return Shapes.empty();
	}
	
	@Override
	public VoxelShape getShape(BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
		return Shapes.empty();
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockGetter level, BlockPos blockPos) {
		BlockState blockState = this.getOriginalBlockState(blockPos);
		
		if (blockState == null) {
			return Shapes.empty();
		}
		
		return blockState.getCollisionShape(level, blockPos, CollisionContext.empty());
	}
	
	@Override
	public VoxelShape getCollisionShape(BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
		BlockState blockState = this.getOriginalBlockState(blockPos);
		
		if (blockState == null) {
			return Shapes.empty();
		}
		
		return blockState.getCollisionShape(level, blockPos, collisionContext);
	}
	
	@Override
	public VoxelShape getVisualShape(BlockGetter level, BlockPos blockPos, CollisionContext collisionContext) {
		return Shapes.empty();
	}
	
	@Override
	public RenderShape getRenderShape() {
		return RenderShape.INVISIBLE;
	}
}