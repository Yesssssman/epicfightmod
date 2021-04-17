package maninthehouse.epicfight.animation.types;

import java.util.List;

import com.google.common.collect.Lists;

import maninthehouse.epicfight.animation.AnimationPlayer;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.collada.AnimationDataExtractor;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.main.EpicFightMod;
import maninthehouse.epicfight.main.GameConstants;
import maninthehouse.epicfight.model.Armature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;

public class StaticAnimation extends DynamicAnimation {
	protected String animationDataPath;
	protected final int animationId;
	public List<SoundKey> soundStream;

	public StaticAnimation() {
		super();
		this.animationId = -1;
	}
	
	public StaticAnimation(int id, float convertTime, boolean isRepeat, String path) {
		super(convertTime, isRepeat);

		if (Animations.animationTable.keySet().contains(id)) {
			System.err.println("[ModError] : Duplicated Animation Key " + id);
		}

		this.animationDataPath = "models/animations/" + path;
		this.totalTime = 0;
		this.animationId = id;

		if (id >= 0)
			Animations.animationTable.put(id, this);
	}

	public StaticAnimation(String path) {
		this();
		this.animationDataPath = "models/animations/" + path;
	}
	
	public StaticAnimation(float convertTime, boolean repeatPlay, String path) {
		super(convertTime, repeatPlay);
		this.animationId = -1;
		this.animationDataPath = "models/animations/" + path;
	}

	public StaticAnimation(int id, boolean repeatPlay, String path) {
		this(id, GameConstants.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path);
	}
	
	public StaticAnimation bindFull(Armature armature) {
		if (animationDataPath != null) {
			AnimationDataExtractor.extractAnimation(new ResourceLocation(EpicFightMod.MODID, animationDataPath), this, armature);
			animationDataPath = null;
		}
		
		if(this.soundStream != null) {
			this.soundStream.sort(null);
		}
		
		return this;
	}
	
	public StaticAnimation bindOnlyClient(Armature armature, Side dist)
	{
		if(dist == Side.CLIENT) {
			bindFull(armature);
		}
		
		return this;
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		if (this.soundStream != null) {
			AnimationPlayer player = entitydata.getAnimator().getPlayerFor(this);
			float prevElapsed = player.getPrevElapsedTime();
			float elapsed = player.getElapsedTime();

			for (SoundKey key : this.soundStream) {
				if (key.time < prevElapsed || key.time >= elapsed)
					continue;
				else {
					if (entitydata.isRemote() == key.isRemote) {
						entitydata.playSound(key.sound, 0.0F, 0.0F);
					}
				}
			}
		}
	}
	
	public int getId() {
		return animationId;
	}
	
	public StaticAnimation registerSound(float time, SoundEvent sound, boolean isRemote) {
		if(this.soundStream == null) {
			this.soundStream = Lists.<SoundKey>newArrayList();
		}
		this.soundStream.add(new SoundKey(time, sound, isRemote));
		
		return this;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.getId());
	}
	
	protected static class SoundKey implements Comparable<SoundKey> {
		float time;
		SoundEvent sound;
		boolean isRemote;

		protected SoundKey(float time, SoundEvent sound, boolean isRemote) {
			this.time = time;
			this.sound = sound;
			this.isRemote = isRemote;
		}

		@Override
		public int compareTo(SoundKey arg0) {
			if(this.time == arg0.time) {
				return 0;
			} else {
				return this.time > arg0.time ? 1 : -1;
			}
		}
	}
}