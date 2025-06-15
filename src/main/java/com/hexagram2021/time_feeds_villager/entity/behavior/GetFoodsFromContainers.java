package com.hexagram2021.time_feeds_villager.entity.behavior;

import com.hexagram2021.time_feeds_villager.block.entity.IContainerWithOpenersCounter;
import com.hexagram2021.time_feeds_villager.block.entity.IOpenersCounter;
import com.hexagram2021.time_feeds_villager.config.TFVCommonConfig;
import com.hexagram2021.time_feeds_villager.entity.IContainerOwner;
import com.hexagram2021.time_feeds_villager.register.TFVMemoryModuleTypes;
import com.mojang.datafixers.kinds.IdF;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.Container;
import net.minecraft.world.LockCode;
import net.minecraft.world.entity.ai.behavior.BlockPosTracker;
import net.minecraft.world.entity.ai.behavior.OneShot;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public final class GetFoodsFromContainers {
	private GetFoodsFromContainers() {
	}

	public static OneShot<Villager> setWalkTarget(float speedModifier, int closeEnoughDist, int tooFarDistance, int tooLongUnreachableDuration) {
		return BehaviorBuilder.create(instance ->
				instance.group(
						instance.registered(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE),
						instance.absent(MemoryModuleType.WALK_TARGET),
						instance.present(TFVMemoryModuleTypes.NEAREST_CONTAINER.get())
				).apply(instance, (cantReachWalkTargetSince, walkTarget, nearestContainer) -> (level, villager, tick) -> {
							GlobalPos globalPos = instance.get(nearestContainer);
							Optional<Long> optional = instance.tryGet(cantReachWalkTargetSince);
							if (globalPos.dimension() == level.dimension() && (optional.isEmpty() || level.getGameTime() - optional.get() <= tooLongUnreachableDuration)) {
								if (globalPos.pos().distManhattan(villager.blockPosition()) > tooFarDistance) {
									Vec3 vec3 = null;
									int tries = 0;

									while(vec3 == null || BlockPos.containing(vec3).distManhattan(villager.blockPosition()) > tooFarDistance) {
										vec3 = DefaultRandomPos.getPosTowards(villager, 15, 7, Vec3.atBottomCenterOf(globalPos.pos()), Math.PI / 2.0D);
										++tries;
										if (tries == 256) {
											nearestContainer.erase();
											cantReachWalkTargetSince.set(tick);
											return true;
										}
									}

									walkTarget.set(new WalkTarget(vec3, speedModifier, closeEnoughDist));
								} else if (globalPos.pos().distManhattan(villager.blockPosition()) > closeEnoughDist) {
									walkTarget.set(new WalkTarget(globalPos.pos(), speedModifier, closeEnoughDist));
								} else {
									return false;
								}
							} else {
								nearestContainer.erase();
								cantReachWalkTargetSince.set(tick);
							}

							return true;
						})
		);
	}

	public static OneShot<Villager> findContainerToSteal() {
		return BehaviorBuilder.create(instance ->
				instance.group(instance.absent(TFVMemoryModuleTypes.NEAREST_CONTAINER.get()), instance.registered(TFVMemoryModuleTypes.LAST_TRIED_TO_STEAL_FOOD.get()), instance.absent(TFVMemoryModuleTypes.LAST_OPEN_CONTAINER.get()))
						.apply(instance, (nearestContainer, lastTriedToStealFood, lastOpenContainer) -> (level, villager, tick) -> {
							long lastTime = instance.tryGet(lastTriedToStealFood).orElse(0L);
							if(level.getGameTime() - lastTime < TFVCommonConfig.INTERVAL_VILLAGER_STEAL_FOODS.get()) {
								return false;
							}
							lastTriedToStealFood.set(level.getGameTime());
							BlockPos target = findNearestContainerToSteal(level, villager);
							if(target != null) {
								nearestContainer.set(GlobalPos.of(level.dimension(), target));
								return true;
							}
							return false;
						})
		);
	}

	public static OneShot<Villager> openContainer(int closeEnoughDistSqr) {
		return BehaviorBuilder.create(instance ->
				instance.group(instance.present(TFVMemoryModuleTypes.NEAREST_CONTAINER.get()), instance.absent(TFVMemoryModuleTypes.LAST_OPEN_CONTAINER.get()), instance.registered(MemoryModuleType.LOOK_TARGET))
						.apply(instance, (nearestContainer, lastOpenContainer, lookTarget) -> (level, villager, tick) -> {
							GlobalPos globalPos = instance.get(nearestContainer);
							BlockPos blockPos = globalPos.pos();
							if(globalPos.dimension() != level.dimension() || villager.distanceToSqr(blockPos.getCenter()) > closeEnoughDistSqr) {
								return false;
							}
							if(level.getBlockEntity(blockPos) instanceof IContainerWithOpenersCounter containerWithOpenersCounter &&
									containerWithOpenersCounter.time_feeds_villager$getOpenersCounter() instanceof IOpenersCounter openersCounter) {
								openersCounter.time_feeds_villager$incrementEntityOpeners(villager, level, blockPos, level.getBlockState(blockPos));
								if(villager instanceof IContainerOwner containerOwner) {
									containerOwner.time_feeds_villager$addOwnContainer(openersCounter);
								}
								lookTarget.set(new BlockPosTracker(blockPos));
							}
							lastOpenContainer.set(level.getGameTime());
							return true;
						})
		);
	}

	public static OneShot<Villager> closeContainer(int closeEnoughDistSqr, int tooLongOpenContainerDuration) {
		return BehaviorBuilder.create(instance ->
				instance.group(instance.present(TFVMemoryModuleTypes.NEAREST_CONTAINER.get()), instance.present(TFVMemoryModuleTypes.LAST_OPEN_CONTAINER.get()))
						.apply(instance, (nearestContainer, lastOpenContainer) -> (level, villager, tick) -> {
							GlobalPos globalPos = instance.get(nearestContainer);
							BlockPos blockPos = globalPos.pos();
							if(globalPos.dimension() != level.dimension()) {
								Level originalLevel = level.getServer().getLevel(globalPos.dimension());
								if(originalLevel != null) {
									tryCloseContainerAndStealFood(originalLevel, blockPos, villager, nearestContainer, lastOpenContainer);
								}
								return true;
							}
							if(villager.distanceToSqr(blockPos.getCenter()) > closeEnoughDistSqr) {
								tryCloseContainerAndStealFood(level, blockPos, villager, nearestContainer, lastOpenContainer);
								return true;
							}
							if(level.getGameTime() - instance.get(lastOpenContainer) >= tooLongOpenContainerDuration) {
								tryCloseContainerAndStealFood(level, blockPos, villager, nearestContainer, lastOpenContainer);
								return true;
							}
							return false;
						})
		);
	}
	public static void tryCloseContainerAndStealFood(Level level, BlockPos blockPos, Villager villager, MemoryAccessor<IdF.Mu, GlobalPos> memory1, MemoryAccessor<IdF.Mu, Long> memory2) {
		if(level.getBlockEntity(blockPos) instanceof Container container && container instanceof IContainerWithOpenersCounter containerWithOpenersCounter &&
				containerWithOpenersCounter.time_feeds_villager$getOpenersCounter() instanceof IOpenersCounter openersCounter) {
			openersCounter.time_feeds_villager$decrementEntityOpeners(villager, level, blockPos, level.getBlockState(blockPos));
			if(villager instanceof IContainerOwner containerOwner) {
				containerOwner.time_feeds_villager$removeOwnContainer(openersCounter);
			}
			if(ForgeEventFactory.getMobGriefingEvent(level, villager)) {
				for (int i = 0; i < container.getContainerSize(); ++i) {
					ItemStack itemStack = container.getItem(i);
					if (canSteal(itemStack, villager)) {
						ItemStack ret = itemStack.split(1);
						if(Villager.FOOD_POINTS.containsKey(itemStack.getItem())) {
							villager.getInventory().addItem(ret);
						}
					}
				}
			}
		}
		memory1.erase();
		memory2.erase();
	}

	@Nullable
	private static BlockPos findNearestContainerToSteal(Level level, Villager villager) {
		BlockPos currentPos = villager.blockPosition();
		ChunkPos chunkPos = new ChunkPos(currentPos);
		Optional<BlockPos> blockPos = findContainerToStealFromChunk(level, level.getChunk(chunkPos.x, chunkPos.z), villager);
		if(blockPos.isPresent()) {
			return blockPos.get();
		}
		List<ChunkPos> chunks = Util.toShuffledList(Stream.of(
				new ChunkPos(chunkPos.x - 1, chunkPos.z),
				new ChunkPos(chunkPos.x, chunkPos.z - 1),
				new ChunkPos(chunkPos.x + 1, chunkPos.z),
				new ChunkPos(chunkPos.x, chunkPos.z + 1)
		), villager.getRandom());
		for(ChunkPos checkingPos: chunks) {
			blockPos = findContainerToStealFromChunk(level, level.getChunk(checkingPos.x, checkingPos.z), villager);
			if(blockPos.isPresent()) {
				return blockPos.get();
			}
		}
		chunks = Util.toShuffledList(Stream.of(
				new ChunkPos(chunkPos.x - 1, chunkPos.z - 1),
				new ChunkPos(chunkPos.x - 1, chunkPos.z + 1),
				new ChunkPos(chunkPos.x + 1, chunkPos.z - 1),
				new ChunkPos(chunkPos.x + 1, chunkPos.z + 1)
		), villager.getRandom());
		for(ChunkPos checkingPos: chunks) {
			blockPos = findContainerToStealFromChunk(level, level.getChunk(checkingPos.x, checkingPos.z), villager);
			if(blockPos.isPresent()) {
				return blockPos.get();
			}
		}
		chunks = Util.toShuffledList(Stream.of(
				new ChunkPos(chunkPos.x - 2, chunkPos.z),
				new ChunkPos(chunkPos.x, chunkPos.z - 2),
				new ChunkPos(chunkPos.x + 2, chunkPos.z),
				new ChunkPos(chunkPos.x, chunkPos.z + 2)
		), villager.getRandom());
		for(ChunkPos checkingPos: chunks) {
			blockPos = findContainerToStealFromChunk(level, level.getChunk(checkingPos.x, checkingPos.z), villager);
			if(blockPos.isPresent()) {
				return blockPos.get();
			}
		}
		chunks = Util.toShuffledList(Stream.of(
				new ChunkPos(chunkPos.x - 2, chunkPos.z - 1),
				new ChunkPos(chunkPos.x - 1, chunkPos.z - 2),
				new ChunkPos(chunkPos.x + 1, chunkPos.z - 2),
				new ChunkPos(chunkPos.x + 2, chunkPos.z - 1),
				new ChunkPos(chunkPos.x + 2, chunkPos.z + 1),
				new ChunkPos(chunkPos.x + 1, chunkPos.z + 2),
				new ChunkPos(chunkPos.x - 1, chunkPos.z + 2),
				new ChunkPos(chunkPos.x - 2, chunkPos.z + 1)
		), villager.getRandom());
		for(ChunkPos checkingPos: chunks) {
			blockPos = findContainerToStealFromChunk(level, level.getChunk(checkingPos.x, checkingPos.z), villager);
			if(blockPos.isPresent()) {
				return blockPos.get();
			}
		}
		return null;
	}

	private static Optional<BlockPos> findContainerToStealFromChunk(Level level, ChunkAccess chunkAccess, Villager villager) {
		double minDistance = Double.MAX_VALUE;
		BlockPos ret = null;
		for (BlockPos pos : chunkAccess.getBlockEntitiesPos()) {
			if (canStealContainer(level, pos, villager)) {
				double distance = villager.distanceToSqr(pos.getCenter());
				if (distance < minDistance) {
					minDistance = distance;
					ret = pos;
				}
			}
		}
		return Optional.ofNullable(ret);
	}

	private static boolean canStealContainer(Level level, BlockPos containerPos, Villager villager) {
		if(level.getBlockEntity(containerPos) instanceof Container container) {
			if(container instanceof BaseContainerBlockEntity baseContainerBlockEntity && baseContainerBlockEntity.lockKey != LockCode.NO_LOCK) {
				return false;
			}
			for(int i = 0; i < container.getContainerSize(); ++i) {
				if(canSteal(container.getItem(i), villager)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean canSteal(ItemStack itemStack, Villager villager) {
		if(itemStack.isEmpty()) {
			return false;
		}
		FoodProperties foodProperties = itemStack.getFoodProperties(villager);
		if(foodProperties == null) {
			return false;
		}
		return foodProperties.getEffects().stream().allMatch(pair -> pair.getFirst().getEffect().isBeneficial());
	}
}
