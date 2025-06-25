package com.hexagram2021.time_feeds_villager.mixin;

import com.google.common.collect.ImmutableList;
import com.hexagram2021.time_feeds_villager.TimeFeedsVillager;
import com.hexagram2021.time_feeds_villager.block.entity.IOpenersCounter;
import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.entity.*;
import com.hexagram2021.time_feeds_villager.entity.behavior.VillagerExtraGoalPackages;
import com.hexagram2021.time_feeds_villager.network.*;
import com.hexagram2021.time_feeds_villager.register.TFVActivities;
import com.hexagram2021.time_feeds_villager.register.TFVDamageSources;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import com.hexagram2021.time_feeds_villager.util.CommonUtils;
import com.hexagram2021.time_feeds_villager.util.SimpleContainerOpenersCounter;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

@Mixin(Villager.class)
public class VillagerEntityMixin implements IAgingEntity, IContainerOwner, IHungryEntity, IInventoryCarrier, IHasCustomSkinEntity, ISwitchableEntity {
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
	private final SimpleContainer time_feeds_villager$extraInventory = new SimpleContainer(8) {
		@Override
		public void startOpen(Player player) {
			Villager current = (Villager)(Object)VillagerEntityMixin.this;
			if (!current.isAlive() && !player.isSpectator()) {
				VillagerEntityMixin.this.time_feeds_villager$extraInventoryOpenersCounter.incrementOpeners();
			}

		}

		@Override
		public void stopOpen(Player player) {
			Villager current = (Villager)(Object)VillagerEntityMixin.this;
			if (!current.isAlive() && !player.isSpectator()) {
				VillagerEntityMixin.this.time_feeds_villager$extraInventoryOpenersCounter.decrementOpeners();
			}
		}
	};
	@Unique @Nullable
	private ResourceLocation time_feeds_villager$customSkin = null;
	@Unique
	private final SimpleContainerOpenersCounter time_feeds_villager$extraInventoryOpenersCounter = new SimpleContainerOpenersCounter();
	@Unique @Nullable
	private Mode time_feeds_villager$mode = null;

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
		int extraTicks = current.getRandom().nextInt(20);
		FoodProperties foodProperties = itemStack.getFoodProperties(current);
		if(foodProperties != null) {
			extraTicks += Math.round(foodProperties.getNutrition() * foodProperties.getSaturationModifier() * 40.0F);
			foodProperties.getEffects().forEach(pair -> {
				if (!current.level().isClientSide && pair.getFirst() != null && current.getRandom().nextFloat() < pair.getSecond()) {
					current.addEffect(new MobEffectInstance(pair.getFirst()));
				}
			});
		}
		current.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
		this.time_feed_villager$nextHungerTick = TFVCommonConfig.INTERVAL_VILLAGER_FEEL_HUNGRY.get() + extraTicks;
		this.time_feed_villager$remainingEatingTick = -1;
	}

	@Override
	public int time_feeds_villager$remainingEatingTicks() {
		return this.time_feed_villager$remainingEatingTick;
	}

	@Override
	public void time_feeds_villager$tickEating() {
		this.time_feed_villager$remainingEatingTick -= 1;
		if(this.time_feed_villager$remainingEatingTick % 2 == 0) {
			Villager current = (Villager)(Object)this;
			current.playSound(SoundEvents.GENERIC_EAT);
			if(current.level() instanceof ServerLevel serverLevel) {
				serverLevel.players().forEach(serverPlayer -> {
					if(current.distanceTo(serverPlayer) < 32.0D) {
						TimeFeedsVillager.packetHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundVillagerEatFoodPacket(current.getId()));
					}
				});
			}
		}
	}

	@Override
	public SimpleContainer time_feeds_villager$getExtraInventory() {
		return this.time_feeds_villager$extraInventory;
	}

	@Override
	public boolean time_feeds_villager$isOpenedByAnyPlayer() {
		return this.time_feeds_villager$extraInventoryOpenersCounter.getOpenerCount() != 0;
	}

	@Override
	public ResourceLocation time_feeds_villager$getCustomSkin() {
		Villager current = (Villager)(Object)this;
		return Objects.requireNonNullElseGet(this.time_feeds_villager$customSkin, () -> {
			this.time_feeds_villager$customSkin = TimeFeedsVillager.VILLAGER_BASE_SKIN;
			if(current.level().isClientSide) {
				TimeFeedsVillager.packetHandler.send(PacketDistributor.SERVER.noArg(), new ServerboundRequestVillagerSkinPacket(current.getUUID()));
			}
			return this.time_feeds_villager$customSkin;
		});
	}

	@Override
	public void time_feeds_villager$setCustomSkin(ResourceLocation skin) {
		this.time_feeds_villager$customSkin = skin;
		Villager current = (Villager)(Object)this;
		if(current.level() instanceof ServerLevel serverLevel) {
			serverLevel.players().forEach(serverPlayer -> TimeFeedsVillager.packetHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundUpdateVillagerSkinPacket(current.getId(), skin)));
		}
	}

	@Override
	public Mode time_feeds_villager$getMode() {
		Villager current = (Villager)(Object)this;
		return Objects.requireNonNullElseGet(this.time_feeds_villager$mode, () -> {
			this.time_feeds_villager$mode = Mode.WORK;
			if(current.level().isClientSide) {
				TimeFeedsVillager.packetHandler.send(PacketDistributor.SERVER.noArg(), new ServerboundRequestVillagerModePacket(current.getUUID()));
			}
			return this.time_feeds_villager$mode;
		});
	}

	@Override
	public void time_feeds_villager$setMode(Mode mode) {
		this.time_feeds_villager$mode = mode;
		Villager current = (Villager)(Object)this;
		if(current.level() instanceof ServerLevel serverLevel) {
			serverLevel.players().forEach(serverPlayer -> TimeFeedsVillager.packetHandler.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new ClientboundUpdateVillagerModePacket(current.getId(), mode)));
		}
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/npc/VillagerType;)V", at = @At(value = "TAIL"))
	private void time_feeds_villager$constructor(EntityType<? extends Villager> entityType, Level level, VillagerType villagerType, CallbackInfo ci) {
		if(level instanceof ServerLevel) {
			List<? extends String> skins = TFVCommonConfig.VILLAGER_SKINS.get();
			this.time_feeds_villager$customSkin = skins.isEmpty() ? TimeFeedsVillager.VILLAGER_BASE_SKIN : new ResourceLocation(skins.get(level.getRandom().nextInt(skins.size())));
		}
	}

	@Inject(method = "mobInteract", at = @At(value = "HEAD"), cancellable = true)
	private void time_feeds_villager$mobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
		// try to make villager immune to aging
		if(!this.time_feeds_villager$isImmuneToAging()) {
			ItemStack handItemStack = player.getItemInHand(hand);
			Item handItem = handItemStack.getItem();
			ResourceLocation item = ForgeRegistries.ITEMS.getKey(handItem);
			Villager current = (Villager)(Object)this;
			boolean isClientSide = current.level().isClientSide;
			if(item != null && TFVCommonConfig.FOODS_HELP_IMMUNE_TO_AGING.get().contains(item.toString())) {
				if(!isClientSide) {
					if(!player.getAbilities().instabuild) {
						handItemStack.shrink(1);
					}
					current.playSound(SoundEvents.GENERIC_EAT);
					this.time_feeds_villager$setAge(16);
					current.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(handItem));
					this.time_feeds_villager$finishEating();
				}
				this.time_feeds_villager$setImmuneToAging();
				cir.setReturnValue(InteractionResult.sidedSuccess(isClientSide));
			}
		}
		// try to open extra inventory screen
		if(player.isShiftKeyDown()) {
			Villager current = (Villager)(Object)this;
			boolean isClientSide = current.level().isClientSide;
			if(!isClientSide && player instanceof ServerPlayer serverPlayer) {
				CommonUtils.openVillagerExtraInventory(serverPlayer, current, this.time_feeds_villager$extraInventory);
			}
			cir.setReturnValue(InteractionResult.sidedSuccess(isClientSide));
		}
	}

	@WrapOperation(method = "mobInteract", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/trading/MerchantOffers;isEmpty()Z", ordinal = 0, remap = false))
	private boolean time_feeds_villager$unhappyIfHungry(MerchantOffers instance, Operation<Boolean> original) {
		return original.call(instance) || this.time_feeds_villager$isHungry();
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
		if(this.time_feeds_villager$customSkin != null) {
			compound.putString("TFV_CustomSkin", this.time_feeds_villager$customSkin.toString());
		}
		if(this.time_feeds_villager$mode != null) {
			compound.putString("TFV_Mode", this.time_feeds_villager$mode.name());
		}
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
		if(compound.contains("TFV_CustomSkin", Tag.TAG_STRING)) {
			this.time_feeds_villager$setCustomSkin(new ResourceLocation(compound.getString("TFV_CustomSkin")));
		}
		if(compound.contains("TFV_Mode", Tag.TAG_STRING)) {
			Mode mode = Mode.WORK;
			try {
				mode = Mode.valueOf(compound.getString("TFV_Mode"));
			} catch (Exception ignored) {
			}
			this.time_feeds_villager$setMode(mode);
		}
	}

	@Inject(method = "tick", at = @At(value = "TAIL"))
	private void time_feeds_villager$tickAging(CallbackInfo ci) {
		Villager current = (Villager)(Object)this;
		if(current.level().isClientSide) {
			return;
		}
		if(!current.isSleeping() && this.time_feed_villager$nextHungerTick > 0) {
			this.time_feed_villager$nextHungerTick -= 1;
			MobEffectInstance effectInstance = current.getEffect(MobEffects.HUNGER);
			if(effectInstance != null) {
				this.time_feed_villager$nextHungerTick -= 1 + effectInstance.getAmplifier();
			}
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
		villagerBrain.addActivity(TFVActivities.STAY.get(), VillagerExtraGoalPackages.getStayPackage(0.5F));
	}

	@Inject(method = "<clinit>", at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/npc/Villager;MEMORY_TYPES:Lcom/google/common/collect/ImmutableList;", opcode = Opcodes.PUTSTATIC, shift = At.Shift.AFTER))
	private static void time_feeds_villager$addCustomMemoryTypes(CallbackInfo ci) {
		MEMORY_TYPES = ImmutableList.<MemoryModuleType<?>>builder().addAll(MEMORY_TYPES)
				.add(TFVMemoryModuleTypes.NEAREST_CONTAINER.get(), TFVMemoryModuleTypes.LAST_TRIED_TO_STEAL_FOOD.get(), TFVMemoryModuleTypes.LAST_OPEN_CONTAINER.get()).build();
	}
}
