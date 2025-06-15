package com.hexagram2021.time_feeds_villager.mixin;

import com.hexagram2021.time_feeds_villager.block.entity.IOpenersCounter;
import com.hexagram2021.time_feeds_villager.entity.IContainerOwner;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.ContainerOpenersCounter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.function.Predicate;

@Mixin(ContainerOpenersCounter.class)
public abstract class ContainerOpenersCounterMixin implements IOpenersCounter {
	@Shadow
	private int openCount;

	@Shadow
	protected abstract void onOpen(Level level, BlockPos pos, BlockState state);

	@Shadow
	private static void scheduleRecheck(Level pLevel, BlockPos pPos, BlockState pState) {
		throw new UnsupportedOperationException("Replaced by Mixin");
	}

	@Shadow
	protected abstract void openerCountChanged(Level pLevel, BlockPos pPos, BlockState pState, int pCount, int pOpenCount);

	@Shadow
	protected abstract void onClose(Level pLevel, BlockPos pPos, BlockState pState);

	@Override
	public void time_feeds_villager$incrementEntityOpeners(Entity entity, Level level, BlockPos pos, BlockState state) {
		int i = this.openCount++;
		if (i == 0) {
			this.onOpen(level, pos, state);
			level.gameEvent(entity, GameEvent.CONTAINER_OPEN, pos);
			scheduleRecheck(level, pos, state);
		}

		this.openerCountChanged(level, pos, state, i, this.openCount);
	}

	@Override
	public void time_feeds_villager$decrementEntityOpeners(Entity entity, Level level, BlockPos pos, BlockState state) {
		int i = this.openCount--;
		if (this.openCount == 0) {
			this.onClose(level, pos, state);
			level.gameEvent(entity, GameEvent.CONTAINER_CLOSE, pos);
		}

		this.openerCountChanged(level, pos, state, i, this.openCount);
	}

	@WrapOperation(method = "getOpenCount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getEntities(Lnet/minecraft/world/level/entity/EntityTypeTest;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;"))
	private <T extends Entity> List<Entity> time_feeds_villager$tweakCounterEntities(Level instance, EntityTypeTest<Entity, T> entityTypeTest, AABB bounds, Predicate<T> predicate, Operation<List<Entity>> original) {
		List<Entity> list = Lists.newArrayList();
		list.addAll(original.call(instance, entityTypeTest, bounds, predicate));
		list.addAll(instance.getEntities(EntityTypeTest.forClass(Villager.class), bounds, entity -> entity instanceof IContainerOwner containerOwner && containerOwner.time_feeds_villager$isOwnContainer(this)));
		return list;
	}
}
