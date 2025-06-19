package com.hexagram2021.time_feeds_villager.util;

import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.StackedContentsCompatible;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ListContainer implements Container, StackedContentsCompatible {
	private final NonNullList<ItemStack> items;
	@Nullable
	private List<ContainerListener> listeners;

	public ListContainer(NonNullList<ItemStack> items) {
		this.items = items;
	}

	public void addListener(ContainerListener listener) {
		if (this.listeners == null) {
			this.listeners = Lists.newArrayList();
		}

		this.listeners.add(listener);
	}

	public void removeListener(ContainerListener listener) {
		if (this.listeners != null) {
			this.listeners.remove(listener);
		}

	}

	@Override
	public ItemStack getItem(int index) {
		return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
	}

	public List<ItemStack> removeAllItems() {
		List<ItemStack> ret = this.items.stream().filter(itemStack -> !itemStack.isEmpty()).collect(Collectors.toList());
		this.clearContent();
		return ret;
	}

	@Override
	public ItemStack removeItem(int index, int count) {
		ItemStack ret = ContainerHelper.removeItem(this.items, index, count);
		if (!ret.isEmpty()) {
			this.setChanged();
		}

		return ret;
	}

	public ItemStack removeItemType(Item item, int count) {
		ItemStack ret = new ItemStack(item, 0);

		for(int i = this.items.size() - 1; i >= 0; --i) {
			ItemStack itemStack = this.getItem(i);
			if (itemStack.getItem().equals(item)) {
				int move = count - ret.getCount();
				ItemStack split = itemStack.split(move);
				ret.grow(split.getCount());
				if (ret.getCount() == count) {
					break;
				}
			}
		}

		if (!ret.isEmpty()) {
			this.setChanged();
		}

		return ret;
	}

	public ItemStack addItem(ItemStack itemStack) {
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		ItemStack ret = itemStack.copy();
		this.moveItemToOccupiedSlotsWithSameType(ret);
		if (ret.isEmpty()) {
			return ItemStack.EMPTY;
		}
		this.moveItemToEmptySlots(ret);
		return ret.isEmpty() ? ItemStack.EMPTY : ret;
	}

	public boolean canAddItem(ItemStack itemStack) {
		boolean ret = false;

		for(ItemStack itemstack : this.items) {
			if (itemstack.isEmpty() || ItemStack.isSameItemSameTags(itemstack, itemStack) && itemstack.getCount() < itemstack.getMaxStackSize()) {
				ret = true;
				break;
			}
		}

		return ret;
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		ItemStack ret = this.items.get(index);
		if (ret.isEmpty()) {
			return ItemStack.EMPTY;
		}
		this.items.set(index, ItemStack.EMPTY);
		return ret;
	}

	@Override
	public void setItem(int index, ItemStack itemStack) {
		this.items.set(index, itemStack);
		if (!itemStack.isEmpty() && itemStack.getCount() > this.getMaxStackSize()) {
			itemStack.setCount(this.getMaxStackSize());
		}

		this.setChanged();
	}

	@Override
	public int getContainerSize() {
		return this.items.size();
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public void setChanged() {
		if (this.listeners != null) {
			for(ContainerListener listener : this.listeners) {
				listener.containerChanged(this);
			}
		}

	}

	@Override
	public boolean stillValid(Player player) {
		return true;
	}

	@Override
	public void clearContent() {
		this.items.clear();
		this.setChanged();
	}

	@Override
	public void fillStackedContents(StackedContents contents) {
		for(ItemStack itemStack : this.items) {
			contents.accountStack(itemStack);
		}
	}

	@Override
	public String toString() {
		return this.items.stream().filter(itemStack -> !itemStack.isEmpty()).toList().toString();
	}

	private void moveItemToEmptySlots(ItemStack itemStack) {
		for(int i = 0; i < this.items.size(); ++i) {
			ItemStack containerItem = this.getItem(i);
			if (containerItem.isEmpty()) {
				this.setItem(i, itemStack.copyAndClear());
				return;
			}
		}

	}

	private void moveItemToOccupiedSlotsWithSameType(ItemStack itemStack) {
		for(int i = 0; i < this.items.size(); ++i) {
			ItemStack containerItem = this.getItem(i);
			if (ItemStack.isSameItemSameTags(containerItem, itemStack)) {
				this.moveItemsBetweenStacks(itemStack, containerItem);
				if (itemStack.isEmpty()) {
					return;
				}
			}
		}

	}

	private void moveItemsBetweenStacks(ItemStack itemStack, ItemStack other) {
		int maxStackSize = Math.min(this.getMaxStackSize(), other.getMaxStackSize());
		int move = Math.min(itemStack.getCount(), maxStackSize - other.getCount());
		if (move > 0) {
			other.grow(move);
			itemStack.shrink(move);
			this.setChanged();
		}
	}

	public void fromTag(ListTag containerNbt) {
		this.clearContent();

		for(int i = 0; i < containerNbt.size(); ++i) {
			ItemStack itemstack = ItemStack.of(containerNbt.getCompound(i));
			if (!itemstack.isEmpty()) {
				this.addItem(itemstack);
			}
		}
	}

	public ListTag createTag() {
		ListTag containerNbt = new ListTag();

		for(int i = 0; i < this.getContainerSize(); ++i) {
			ItemStack itemStack = this.getItem(i);
			if (!itemStack.isEmpty()) {
				containerNbt.add(itemStack.save(new CompoundTag()));
			}
		}

		return containerNbt;
	}
}
