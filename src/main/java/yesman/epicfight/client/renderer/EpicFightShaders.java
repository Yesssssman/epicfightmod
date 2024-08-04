package yesman.epicfight.client.renderer;

import java.io.IOException;

import javax.annotation.Nullable;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.client.renderer.shader.VanillaAnimationShader;
import yesman.epicfight.main.EpicFightMod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class EpicFightShaders {
	public static ShaderInstance positionColorNormalShader;
	public static ShaderInstance animationPositionColorNormalShader;
	
	@Nullable
	public static ShaderInstance getPositionColorNormalShader() {
		return positionColorNormalShader;
	}
	
	@Nullable
	public static ShaderInstance getAnimationPositionColorNormalShader() {
		return animationPositionColorNormalShader;
	}
	
	@SubscribeEvent
	public static void registerShadersEvent(RegisterShadersEvent event) throws IOException {
		event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(EpicFightMod.MODID, "solid_model"), DefaultVertexFormat.POSITION_COLOR_NORMAL), (reloadedShader) -> {
			EpicFightShaders.positionColorNormalShader = reloadedShader;
		});
		
		event.registerShader(new VanillaAnimationShader(event.getResourceProvider(), new ResourceLocation(EpicFightMod.MODID, "animation_solid_model"), EpicFightVertexFormat.SOLID_MODEL), (reloadedShader) -> {
			EpicFightShaders.animationPositionColorNormalShader = reloadedShader;
		});
	}
}
