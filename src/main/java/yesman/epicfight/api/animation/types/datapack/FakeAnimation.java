package yesman.epicfight.api.animation.types.datapack;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimationDataReader;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeAnimation extends StaticAnimation {
	private Class<? extends StaticAnimation> animationClass;
	private AnimationClip animationClip;
	private Map<String, Object> constructorParams = Maps.newLinkedHashMap();
	private JsonObject rawAnimation;
	private JsonObject properties = new JsonObject();
	
	public FakeAnimation(String path, Armature armature, AnimationClip clip, JsonObject animation) {
		super(new ResourceLocation(""), 0.0F, false, "", armature, true);
		
		this.animationClip = clip;
		this.rawAnimation = animation;
		this.constructorParams.put("path", path);
		this.constructorParams.put("armature", armature);
	}
	
	public Object getParameter(String key) {
		return this.constructorParams.get(key);
	}
	
	public void setParameter(String key, Object value) {
		if (this.constructorParams.containsKey(key)) {
			this.constructorParams.put(key, value);
		} else {
			throw new IllegalStateException("No key " + key);
		}
	}
	
	public Class<? extends StaticAnimation> getAnimationClass() {
		return this.animationClass;
	}
	
	public JsonObject getPropertiesJson() {
		return this.properties;
	}
	
	public void setAnimationClass(Class<? extends StaticAnimation> animationClass) {
		String prevPath = (String)this.getParameter("path");
		
		this.constructorParams.clear();
		PARAMETERS.get(animationClass).keySet().forEach((k) -> this.constructorParams.put(k, null));
		
		this.setParameter("armature", this.getArmature());
		this.setParameter("path", prevPath);
		
		this.animationClass = animationClass;
	}
	
	@Override
	public float getConvertTime() {
		Object convTime = this.getParameter("convertTime");
		return convTime == null ? 0.0F : (float)convTime;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.animationClip;
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return new ResourceLocation((String)this.constructorParams.get("path"));
	}
	
	public JsonObject getRawAnimationJson() {
		return this.rawAnimation;
	}
	
	public FakeAnimation deepCopy() {
		FakeAnimation fakeAnimation = new FakeAnimation((String)this.getParameter("path"), this.armature, this.animationClip, this.rawAnimation);
		fakeAnimation.animationClass = this.animationClass;
		fakeAnimation.constructorParams.clear();
		fakeAnimation.constructorParams.putAll(this.constructorParams);
		fakeAnimation.rawAnimation = this.rawAnimation;
		fakeAnimation.properties = this.properties;
		
		return fakeAnimation;
	}
	
	@SuppressWarnings("rawtypes")
	public ClipHoldingAnimation createAnimation() throws Exception {
		try {
			if (this.animationClass == null) {
				throw new IllegalStateException("Animation type is not defined.");
			}
			
			Map<String, Class<?>> map = PARAMETERS.get(this.animationClass);
			Class[] paramClasses = map.values().toArray(new Class[0]);
			Object[] params = this.constructorParams.values().toArray();
			Constructor<? extends ClipHoldingAnimation> constructor = switchType(this.animationClass).getConstructor(paramClasses);
			
			ClipHoldingAnimation animation = constructor.newInstance(params);
			animation.setAnimationClip(this.animationClip);
			animation.setCreator(this);
			
			ClientAnimationDataReader clientDataReader = ClientAnimationDataReader.DESERIALIZER.deserialize(this.properties, null, null);
			clientDataReader.applyClientData(animation.cast());
			
			return animation;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			
			StringBuilder sb = new StringBuilder();
			Iterator<Map.Entry<String, Object>> iter = this.constructorParams.entrySet().iterator();
			
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
	
	private static final Map<Class<? extends StaticAnimation>, Map<String, Class<?>>> PARAMETERS = Maps.newHashMap();
	private static final Map<Class<? extends StaticAnimation>, Class<? extends ClipHoldingAnimation>> FAKE_ANIMATIONS = Maps.newHashMap();
	
	static {
		Map<String, Class<?>> staticAnimationParameters = Maps.newLinkedHashMap();
		staticAnimationParameters.put("convertTime", float.class);
		staticAnimationParameters.put("isRepeat", boolean.class);
		staticAnimationParameters.put("path", String.class);
		staticAnimationParameters.put("armature", Armature.class);
		
		/**
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
		**/
		
		Map<String, Class<?>> attackAnimationParameters2 = Maps.newLinkedHashMap();
		attackAnimationParameters2.put("convertTime", float.class);
		attackAnimationParameters2.put("path", String.class);
		attackAnimationParameters2.put("armature", Armature.class);
		attackAnimationParameters2.put("phases", List.class);
		
		Map<String, Class<?>> phaseParameters = Maps.newLinkedHashMap();
		phaseParameters.put("start", float.class);
		phaseParameters.put("antic", float.class);
		phaseParameters.put("preDelay", float.class);
		phaseParameters.put("contact", float.class);
		phaseParameters.put("recovery", float.class);
		phaseParameters.put("end", float.class);
		phaseParameters.put("hand", InteractionHand.class);
		phaseParameters.put("colliders", List.class);
		
		PARAMETERS.put(StaticAnimation.class, staticAnimationParameters);
		PARAMETERS.put(MovementAnimation.class, staticAnimationParameters);
		PARAMETERS.put(AttackAnimation.class, attackAnimationParameters2);
		
		FAKE_ANIMATIONS.put(StaticAnimation.class, FakeStaticAnimation.class);
		FAKE_ANIMATIONS.put(MovementAnimation.class, FakeMovementAnimation.class);
		FAKE_ANIMATIONS.put(AttackAnimation.class, FakeAttackAnimation.class);
	}
	
	public static Class<? extends ClipHoldingAnimation> switchType(Class<? extends StaticAnimation> cls) {
		return FAKE_ANIMATIONS.get(cls);
	}
}