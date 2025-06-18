package com.hexagram2021.time_feeds_villager.mixin;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.time_feeds_villager.block.entity.IOpenersCounter;
import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.entity.IAgingEntity;
import com.hexagram2021.time_feeds_villager.entity.IContainerOwner;
import com.hexagram2021.time_feeds_villager.entity.IHungryEntity;
import com.hexagram2021.time_feeds_villager.entity.IInventoryCarrier;
import com.hexagram2021.time_feeds_villager.entity.behavior.VillagerExtraGoalPackages;
import com.hexagram2021.time_feeds_villager.register.TFVActivities;
import com.hexagram2021.time_feeds_villager.register.TFVDamageSources;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Villager.class)
public class VillagerEntityMixin implements IAgingEntity, IContainerOwner, IHungryEntity, IInventoryCarrier {
	@Mutable
	@Shadow @Final
	private static ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

	@Shadow
	private int foodLevel;
	@Unique
	private boolean time_feeds_villager$immuneToAging = false;
	@Unique
	private int time_feeds_villager$age = -1;
	@Unique
	private int time_feed_villager$nextHungerTick = TFVCommonConfig.INTERVAL_VILLAGER_FEEL_HUNGRY.get();
	@Unique
	private int time_feed_villager$remainingEatingTick = -1;
	@Unique @Nullable
	private IOpenersCounter time_feeds_villager$ownContainer = null;
	@Unique
	private final SimpleContainer time_feeds_villager$extraInventory = new SimpleContainer(8);

	@Override
	public int time_feeds_villager$getAge() {
		return this.time_feeds_villager$age;
	}
	@Override
	public void time_feeds_villager$setAge(int newAge) {
		this.time_feeds_villager$age = newAge;
	}

	@Override
	public boolean time_feeds_villager$isImmuneToAging() {
		return this.time_feeds_villager$immuneToAging;
	}
	@Override
	public void time_feeds_villager$setImmuneToAging() {
		this.time_feeds_villager$immuneToAging = true;
	}
	@Override
	public void time_feeds_villager$setImmuneToAging(boolean value) {
		this.time_feeds_villager$immuneToAging = value;
	}

	@Override
	public void time_feeds_villager$addOwnContainer(IOpenersCounter container) {
		this.time_feeds_villager$ownContainer = container;
	}

	@Override
	public void time_feeds_villager$removeOwnContainer(IOpenersCounter container) {
		if(this.time_feeds_villager$ownContainer == container) {
			this.time_feeds_villager$ownContainer = null;
		}
	}

	@Override
	public boolean time_feeds_villager$isOwnContainer(IOpenersCounter container) {
		return this.time_feeds_villager$ownContainer == container;
	}

	@Override
	public boolean time_feeds_villager$isHungry() {
		return this.time_feed_villager$nextHungerTick <= 0;
	}

	@Override
	public boolean time_feeds_villager$takeOutFoodAndStartEating() {
		Villager current = (Villager)(Object)this;
		for(int i = 0; i < this.time_feeds_villager$extraInventory.getContainerSize(); ++i) {
			ItemStack itemStack = this.time_feeds_villager$extraInventory.getItem(i);
			if(itemStack.isEdible()) {
				current.setItemInHand(InteractionHand.MAIN_HAND, itemStack.split(1));
				this.time_feed_villager$remainingEatingTick = 40;
				return true;
			}
		}
		SimpleContainer inventory = current.getInventory();
		for(int i = 0; i < inventory.getContainerSize(); ++i) {
			ItemStack itemStack = inventory.getItem(i);
			if(itemStack.isEdible()) {
				current.setItemInHand(InteractionHand.MAIN_HAND, itemStack.split(1));
				this.time_feed_villager$remainingEatingTick = 40;
				return true;
			}
		}
		return false;
	}

	@Override
	public void time_feeds_villager$finishEating() {
		Villager current = (Villager)(Object)this;
		ItemStack itemStack = current.getItemInHand(InteractionHand.MAIN_HAND);
		int foodPoint = Villager.FOOD_POINTS.getOrDefault(itemStack.getItem(), 0);
		this.foodLevel += foodPoint * itemStack.getCount();
		current.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		this.time_feed_villager$nextHungerTick = TFVCommonConfig.INTERVAL_VILLAGER_FEEL_HUNGRY.get();
		this.time_feed_villager$remainingEatingTick = -1;
		current.playSound(SoundEvents.PLAYER_BURP);
	}

	@Override
	public int time_feeds_villager$remainingEatingTicks() {
		return this.time_feed_villager$remainingEatingTick;
	}

	@Override
	public void time_feeds_villager$tickEating() {
		this.time_feed_villager$remainingEatingTick -= 1;
		if(this.time_feed_villager$remainingEatingTick % 2 == 0) {
			((Villager)(Object)this).playSound(SoundEvents.GENERIC_EAT);
		}
	}

	@Override
	public SimpleContainer time_feeds_villager$getExtraInventory() {
		return this.time_feeds_villager$extraInventory;
	}

	@Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
	private void time_feeds_villager$tryMakeVillagerImmune(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(!this.time_feeds_villager$isImmuneToAging()) {
			ItemStack handItem = player.getItemInHand(hand);
			ResourceLocation item = ForgeRegistries.ITEMS.getKey(handItem.getItem());
			Villager current = (Villager)(Object)this;
			boolean isClientSide = current.level().isClientSide;
			if(item != null && TFVCommonConfig.FOODS_HELP_IMMUNE_TO_AGING.get().contains(item.toString())) {
				if(!isClientSide) {
					if(!player.getAbilities().instabuild) {
						handItem.shrink(1);
					}
					current.playSound(SoundEvents.GENERIC_EAT);
					this.time_feeds_villager$setAge(16);
				}
				this.time_feeds_villager$setImmuneToAging();
				cir.setReturnValue(InteractionResult.sidedSuccess(isClientSide));
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	private void time_feeds_villager$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		if(!((Villager)(Object)this).isBaby()) {
			compound.putInt("TFV_Age", this.time_feeds_villager$getAge());
		}
		compound.putInt("TFV_NextHungerTick", this.time_feed_villager$nextHungerTick);
		compound.putInt("TFV_RemainingEatingTick", this.time_feed_villager$remainingEatingTick);
		if(this.time_feeds_villager$isImmuneToAging()) {
			compound.putBoolean("TFV_IsImmuneToAging", true);
		}
		compound.put("TFV_ExtraInventory", this.time_feeds_villager$extraInventory.createTag());
	}
	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	private void time_feeds_villager$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		if(compound.contains("TFV_Age", Tag.TAG_ANY_NUMERIC)) {
			this.time_feeds_villager$setAge(compound.getInt("TFV_Age"));
		} else {
			this.time_feeds_villager$setAge(-1);
		}
		if(compound.contains("TFV_NextHungerTick", Tag.TAG_ANY_NUMERIC)) {
			this.time_feed_villager$nextHungerTick = compound.getInt("TFV_NextHungerTick");
		} else {
			this.time_feed_villager$nextHungerTick = TFVCommonConfig.INTERVAL_VILLAGER_FEEL_HUNGRY.get();
		}
		if(compound.contains("TFV_RemainingEatingTick", Tag.TAG_ANY_NUMERIC)) {
			this.time_feed_villager$remainingEatingTick = compound.getInt("TFV_RemainingEatingTick");
		} else {
			this.time_feed_villager$remainingEatingTick = -1;
		}
		if(compound.contains("TFV_IsImmuneToAging")) {
			this.time_feeds_villager$setImmuneToAging(compound.getBoolean("TFV_IsImmuneToAging"));
		}
		if(compound.contains("TFV_ExtraInventory", Tag.TAG_LIST)) {
			this.time_feeds_villager$extraInventory.fromTag(compound.getList("TFV_ExtraInventory", Tag.TAG_COMPOUND));
		}
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void time_feeds_villager$tickAging(CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		if(current.level().isClientSide) {
			return;
		}
		if(this.time_feed_villager$nextHungerTick > 0) {
			this.time_feed_villager$nextHungerTick -= 1;
		}
		if(!current.isBaby()) {
			if(this.time_feeds_villager$isImmuneToAging()) {
				if(this.time_feeds_villager$getAge() >= 0) {
					this.time_feeds_villager$age -= 1;
					if(this.time_feeds_villager$age <= 0) {
						current.playSound(SoundEvents.PLAYER_BURP);
					} else if(this.time_feeds_villager$age % 2 == 0) {
						current.playSound(SoundEvents.GENERIC_EAT);
					}
				}
			} else {
				this.time_feeds_villager$age += 1;
			}
		}
		if(this.time_feeds_villager$getMaxAge() > 0 && this.time_feeds_villager$getAge() >= this.time_feeds_villager$getMaxAge()) {
			current.hurt(TFVDamageSources.dieInBed(current), 65536.0F);
		}
	}

	@Inject(method = "registerBrainGoals", at = @At(value = "HEAD"))
	private void time_feeds_villager$registerExtraBrainGoals(Brain<Villager> villagerBrain, CallbackInfo ci) {
		villagerBrain.addActivity(TFVActivities.FORAGE.get(), VillagerExtraGoalPackages.getForagePackage(0.6F));
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/Villager;MEMORY_TYPES:Lcom/google/common/collect/ImmutableList;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER))
	private static void time_feeds_villager$addCustomMemoryTypes(CallbackInfo ci) {
		MEMORY_TYPES = ImmutableList.<MemoryModuleType<?>>builder().addAll(MEMORY_TYPES)
				.add(TFVMemoryModuleTypes.NEAREST_CONTAINER.get(), TFVMemoryModuleTypes.LAST_TRIED_TO_STEAL_FOOD.get(), TFVMemoryModuleTypes.LAST_OPEN_CONTAINER.get()).build();
	}
}
