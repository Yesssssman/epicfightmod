package yesman.epicfight.client.renderer.patched.item;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.item.EpicFightItems;
import yesman.epicfight.world.item.SkillBookItem;

@OnlyIn(Dist.CLIENT)
public class EpicFightItemProperties {
	public static void registerItemProperties() {
		ItemProperties.register(EpicFightItems.SKILLBOOK.get(), new ResourceLocation("skill"), (itemstack, level, entity, i) -> {
			Skill skill = SkillBookItem.getContainSkill(itemstack);
			
			if (skill != null) {
				if (skill.getCategory() == SkillCategories.GUARD) {
					return 1;
				} else if (skill.getCategory() == SkillCategories.PASSIVE) {
					return 2;
				} else if (skill.getCategory() == SkillCategories.DODGE) {
					return 3;
				} else if (skill.getCategory() == SkillCategories.IDENTITY) {
					return 4;
				} else if (skill.getCategory() == SkillCategories.MOVER) {
					return 5;
				}
			}
			
			return 0;
		});
		
		ItemProperties.register(Items.SHIELD, new ResourceLocation("blocking"), (itemstack, level, entity, i) -> {
			LivingEntityPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(entity, LivingEntityPatch.class);
			
			if (entitypatch instanceof PlayerPatch) {
				if (((PlayerPatch<?>)entitypatch).isBattleMode()) {
					return 0.0F;
				}
			} else {
				return 0.0F;
			}
			
			return entity != null && entity.isUsingItem() && entity.getUseItem() == itemstack ? 1.0F : 0.0F;
		});
	}
}