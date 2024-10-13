package yesman.epicfight.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import de.teamlapen.werewolves.client.render.layer.HumanWerewolfLayer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

@Mixin(value = HumanWerewolfLayer.class)
public interface WerewolvesMixinHumanWerewolfLayer<T extends LivingEntity, A extends HumanoidModel<T>> {
	@Accessor
    public List<ResourceLocation> getTextures();
	@Accessor
	public A getModel();
}