package com.hexagram2021.time_feeds_villager.mixin;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.time_feeds_villager.block.entity.IOpenersCounter;
import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.entity.IAgingEntity;
import com.hexagram2021.time_feeds_villager.entity.IContainerOwner;
import com.hexagram2021.time_feeds_villager.register.TFVDamageSources;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
public class VillagerEntityMixin implements IAgingEntity, IContainerOwner {
	@Mutable
	@Shadow @Final
	private static ImmutableList<MemoryModuleType<?>> MEMORY_TYPES;

	@Unique
	private boolean time_feeds_villager$immuneToAging = false;
	@Unique
	private int time_feeds_villager$age = -1;
	@Unique @Nullable
	private IOpenersCounter time_feeds_villager$ownContainer = null;

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

	@Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
	private void time_feeds_villager$tryMakeVillagerImmune(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(!this.time_feeds_villager$isImmuneToAging()) {
			ItemStack handItem = player.getItemInHand(hand);
			ResourceLocation item = ForgeRegistries.ITEMS.getKey(handItem.getItem());
			Villager current = (Villager)(Object)this;
			boolean isClientSide = current.level().isClientSide;
			if(item != null && TFVCommonConfig.FOODS_HELPS_IMMUNE_TO_AGING.get().contains(item.toString())) {
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
		if(this.time_feeds_villager$isImmuneToAging()) {
			compound.putBoolean("TFV_IsImmuneToAging", true);
		}
	}
	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	private void time_feeds_villager$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		if(compound.contains("TFV_Age", Tag.TAG_ANY_NUMERIC)) {
			this.time_feeds_villager$setAge(compound.getInt("TFV_Age"));
		} else {
			this.time_feeds_villager$setAge(-1);
		}
		if(compound.contains("TFV_IsImmuneToAging")) {
			this.time_feeds_villager$setImmuneToAging(compound.getBoolean("TFV_IsImmuneToAging"));
		}
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void time_feeds_villager$tickAging(CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		if(current.level().isClientSide) {
			return;
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
		if(this.time_feeds_villager$getAge() >= this.time_feeds_villager$getMaxAge()) {
			current.hurt(TFVDamageSources.dieInBed(current), 65536.0F);
		}
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/Villager;MEMORY_TYPES:Lcom/google/common/collect/ImmutableList;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER))
	private static void time_feeds_villager$addCustomMemoryTypes(CallbackInfo ci) {
		MEMORY_TYPES = ImmutableList.<MemoryModuleType<?>>builder().addAll(MEMORY_TYPES)
				.add(TFVMemoryModuleTypes.NEAREST_CONTAINER.get(), TFVMemoryModuleTypes.LAST_TRIED_TO_STEAL_FOOD.get(), TFVMemoryModuleTypes.LAST_OPEN_CONTAINER.get()).build();
	}
}
