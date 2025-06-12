package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.entity.IAgingEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public class VillagerEntityMixin implements IAgingEntity {
	@Unique
	private boolean time_feeds_villager$immuneToAging = false;

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

	@Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
	private void time_feeds_villager$tryMakeVillagerImmune(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		if(!this.time_feeds_villager$isImmuneToAging()) {
			ItemStack handItem = player.getItemInHand(hand);
			ResourceLocation item = ForgeRegistries.ITEMS.getKey(handItem.getItem());
			boolean isClientSide = ((Villager)(Object)this).level().isClientSide;
			if(item != null && TFVCommonConfig.FOODS_HELPS_IMMUNE_TO_AGING.get().contains(item.toString())) {
				if(!isClientSide && !player.getAbilities().instabuild) {
					handItem.shrink(1);
				}
				this.time_feeds_villager$setImmuneToAging();
				cir.setReturnValue(InteractionResult.sidedSuccess(isClientSide));
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
	private void time_feeds_villager$addAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		if(this.time_feeds_villager$isImmuneToAging()) {
			compound.putBoolean("TFV_IsImmuneToAging", true);
		}
	}
	@Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
	private void time_feeds_villager$readAdditionalSaveData(CompoundTag compound, CallbackInfo ci) {
		if(compound.contains("TFV_IsImmuneToAging")) {
			this.time_feeds_villager$setImmuneToAging(compound.getBoolean("TFV_IsImmuneToAging"));
		}
	}
}
