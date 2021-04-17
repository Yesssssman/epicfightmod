package maninthehouse.epicfight.physics;

import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Collider {
	protected final Vec3f modelCenter;
	protected AxisAlignedBB hitboxAABB;

	protected Vec3f worldCenter;

	public Collider(Vec3f pos, @Nullable AxisAlignedBB entityCallAABB) {
		this.modelCenter = pos;
		this.hitboxAABB = entityCallAABB;
		this.worldCenter = new Vec3f();
	}

	public void transform(VisibleMatrix4f mat) {
		Vec4f temp = new Vec4f(0,0,0,1);
		
		temp.x = modelCenter.x;
		temp.y = modelCenter.y;
		temp.z = modelCenter.z;
		VisibleMatrix4f.transform(mat, temp, temp);
		worldCenter.x = temp.x;
		worldCenter.y = temp.y;
		worldCenter.z = temp.z;
	}
	
	/** Display on debugging **/
	@SideOnly(Side.CLIENT)
	public abstract void draw(VisibleMatrix4f pose, float partialTicks, boolean red);
	
	public abstract boolean isCollideWith(Entity opponent);
	
	public void extractHitEntities(List<Entity> entities) {
		Iterator<Entity> iterator = entities.iterator();

		while (iterator.hasNext()) {
			Entity entity = iterator.next();

			if (!isCollideWith(entity)) {
				iterator.remove();
			}
		}
	}

	public Vec3d getCenter() {
		return new Vec3d(worldCenter.x, worldCenter.y, worldCenter.z);
	}

	public AxisAlignedBB getHitboxAABB() {
		return hitboxAABB.offset(-worldCenter.x, worldCenter.y, -worldCenter.z);
	}
}