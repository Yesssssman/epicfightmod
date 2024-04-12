package yesman.epicfight.api.animation.types.datapack;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeAnimation extends StaticAnimation {
	private static final Map<Class<? extends StaticAnimation>, Map<String, Class<?>>> PARAMETERS = Maps.newHashMap();
	private static final Map<Class<? extends StaticAnimation>, Class<? extends ClipHoldingAnimation>> FAKE_ANIMATIONS = Maps.newHashMap();
	
	static {
		Map<String, Class<?>> staticAnimationParameters = Maps.newLinkedHashMap();
		staticAnimationParameters.put("convertTime", float.class);
		staticAnimationParameters.put("isRepeat", boolean.class);
		staticAnimationParameters.put("path", String.class);
		staticAnimationParameters.put("armature", Armature.class);
		
		Map<String, Class<?>> attackAnimationParameters = Maps.newLinkedHashMap();
		attackAnimationParameters.put("convertTime", float.class);
		attackAnimationParameters.put("antic", float.class);
		attackAnimationParameters.put("preDelay", float.class);
		attackAnimationParameters.put("contact", float.class);
		attackAnimationParameters.put("recovery", float.class);
		attackAnimationParameters.put("hand", InteractionHand.class);
		attackAnimationParameters.put("collider", Collider.class);
		attackAnimationParameters.put("colliderJoint", Joint.class);
		attackAnimationParameters.put("path", String.class);
		attackAnimationParameters.put("armature", Armature.class);
		
		PARAMETERS.put(StaticAnimation.class, staticAnimationParameters);
		PARAMETERS.put(MovementAnimation.class, staticAnimationParameters);
		PARAMETERS.put(AttackAnimation.class, attackAnimationParameters);
		
		FAKE_ANIMATIONS.put(StaticAnimation.class, FakeStaticAnimation.class);
		FAKE_ANIMATIONS.put(MovementAnimation.class, FakeMovementAnimation.class);
		FAKE_ANIMATIONS.put(AttackAnimation.class, FakeAttackAnimation.class);
	}
	
	private File file;
	private Class<? extends StaticAnimation> animationClass;
	private AnimationClip animationClip;
	private Map<String, Object> parameters = Maps.newLinkedHashMap();
	
	public FakeAnimation(File file, String path, Armature armature, AnimationClip clip) {
		super(new ResourceLocation(""), 0.0F, false, "", armature, true);
		
		this.file = file;
		this.animationClip = clip;
		this.parameters.put("path", path);
		this.parameters.put("armature", armature);
	}
	
	public Object getParameter(String key) {
		return this.parameters.get(key);
	}
	
	public void setParameter(String key, Object value) {
		if (this.parameters.containsKey(key)) {
			this.parameters.put(key, value);
		} else {
			throw new IllegalStateException("No key " + key);
		}
	}
	
	public File getFile() {
		return this.file;
	}
	
	public Class<? extends StaticAnimation> getAnimationClass() {
		return this.animationClass;
	}
	
	public void setAnimationClass(Class<? extends StaticAnimation> animationClass) {
		String prevPath = (String)this.getParameter("path");
		
		this.parameters.clear();
		PARAMETERS.get(animationClass).keySet().forEach((k) -> this.parameters.put(k, null));
		
		this.setParameter("armature", this.getArmature());
		this.setParameter("path", prevPath);
		
		this.animationClass = animationClass;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.animationClip;
	}
	
	public FakeAnimation deepCopy() {
		FakeAnimation fakeAnimation = new FakeAnimation(this.file, (String)this.getParameter("path"), this.armature, this.animationClip);
		fakeAnimation.animationClass = this.animationClass;
		fakeAnimation.parameters.clear();
		fakeAnimation.parameters.putAll(this.parameters);
		
		return fakeAnimation;
	}
	
	@SuppressWarnings("rawtypes")
	public StaticAnimation createAnimation() throws Exception {
		try {
			if (this.animationClass == null) {
				throw new IllegalStateException("Animation type is not defined.");
			}
			
			Map<String, Class<?>> map = PARAMETERS.get(this.animationClass);
			Class[] paramClasses = map.values().toArray(new Class[0]);
			Object[] params = this.parameters.values().toArray();
			Constructor<? extends ClipHoldingAnimation> constructor = FAKE_ANIMATIONS.get(this.animationClass).getConstructor(paramClasses);
			
			ClipHoldingAnimation animation = constructor.newInstance(params);
			animation.setAnimationClip(this.animationClip);
			
			return (StaticAnimation)animation;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			
			StringBuilder sb = new StringBuilder();
			
			Iterator<Map.Entry<String, Object>> iter = this.parameters.entrySet().iterator();
			
			while (iter.hasNext()) {
				Map.Entry<String, Object> entry = iter.next();
				sb.append(String.format(iter.hasNext() ? "%s(%s:%s), " : "%s(%s:%s)", entry.getKey(), entry.getValue(), entry.getValue() == null ? null : entry.getValue().getClass().getSimpleName()));
			}
			
			throw new IllegalArgumentException(String.format("Invalid arguments for %s: %s", this.animationClass.getSimpleName(), sb.toString()));
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
}