package maninthehouse.epicfight.client.renderer.entity;

import maninthehouse.epicfight.capabilities.entity.mob.ZombieData;
import net.minecraft.entity.monster.EntityZombieVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ZombieVillagerRenderer extends BipedRenderer<EntityZombieVillager, ZombieData<EntityZombieVillager>> {
	@Override
	protected ResourceLocation getEntityTexture(EntityZombieVillager entityIn) {
		return entityIn.getForgeProfession().getZombieSkin();
	}
}