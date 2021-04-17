package maninthehouse.epicfight.capabilities.entity.mob;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.capabilities.entity.DataKeys;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.client.CTSReqSpawnInfo;
import maninthehouse.epicfight.network.server.STCMobInitialSetting;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ZombieData<T extends EntityZombie> extends BipedMobData<T> {
	public ZombieData() {
		super(Faction.UNDEAD);
	}
	
	@Override
	public void onEntityJoinWorld(T entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(0.0F));
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		if (!this.isRemote()) {
			if (!this.orgEntity.canPickUpLoot()) {
				this.orgEntity.setCanPickUpLoot(isArmed());
			}
			
			if (this.orgEntity.isChild() && this.orgEntity.getRidingEntity() instanceof EntityChicken) {
				if(this.orgEntity.getHeldItemMainhand().getItem() == Items.AIR) {
					this.orgEntity.setHeldItem(EnumHand.MAIN_HAND, new ItemStack(Items.STONE_SWORD));
				}
			}
		} else {
			ModNetworkManager.sendToServer(new CTSReqSpawnInfo(this.orgEntity.getEntityId()));
		}
	}
	
	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.registerIfAbsent(ModAttributes.MAX_STUN_ARMOR);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttributeMap().getAttributeInstance(ModAttributes.IMPACT).setBaseValue(1.0F);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ZOMBIE_WALK);
		animatorClient.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animatorClient.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public STCMobInitialSetting sendInitialInformationToClient() {
		STCMobInitialSetting packet = new STCMobInitialSetting(this.orgEntity.getEntityId());
        ByteBuf buf = packet.getBuffer();
        buf.writeBoolean(this.orgEntity.canPickUpLoot());
        
		return packet;
	}
	
	@Override
	public void clientInitialSettings(ByteBuf buf) {
		AnimatorClient animator = this.getClientAnimator();
		
		if (buf.readBoolean()) {
			animator.addLivingAnimation(LivingMotion.IDLE, Animations.BIPED_IDLE);
			animator.addLivingAnimation(LivingMotion.WALKING, Animations.BIPED_WALK);
		}
	}
	
	@Override
	public void setAIAsUnarmed() {
		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.0D, false, Animations.ZOMBIE_CHASE, Animations.ZOMBIE_WALK, !orgEntity.isChild()));
		orgEntity.tasks.addTask(0, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 1.75D, true, MobAttackPatterns.ZOMBIE_NORAML));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_BIPED_64_32_TEX;
	}
}