package com.hexagram2021.time_feeds_villager.menu;

import com.hexagram2021.time_feeds_villager.util.ListContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class VillagerExtraInventoryMenu extends AbstractContainerMenu {
	private final Container villagerExtraContainer;
	private final Villager villager;
	private final Container armorContainers;

	public VillagerExtraInventoryMenu(int containerId, Inventory playerInventory, Container villagerExtraContainer, final Villager villager) {
		super(null, containerId);
		this.villagerExtraContainer = villagerExtraContainer;
		this.villager = villager;
		this.armorContainers = new ListContainer(villager.armorItems);
		villagerExtraContainer.startOpen(playerInventory.player);
		for(int i = 0; i < this.armorContainers.getContainerSize(); ++i) {
			this.addSlot(new Slot(this.armorContainers, i, 90 + 18 * i, 18) {
				@Override
				public boolean mayPlace(ItemStack itemStack) {
					EquipmentSlot equipmentSlot = Mob.getEquipmentSlotForItem(itemStack);
					return equipmentSlot.isArmor() && equipmentSlot.getIndex() == this.getSlotIndex();
				}

				@Override
				public int getMaxStackSize() {
					return 1;
				}
			});
		}
		for(int i = 0; i < 2; ++i) {
			for(int j = 0; j < 4; ++j) {
				this.addSlot(new Slot(this.villagerExtraContainer, i * 4 + j, 90 + 18 * j, 36 + 18 * i));
			}
		}

		for(int i = 0; i < 3; ++i) {
			for(int j = 0; j < 9; ++j) {
				this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
			}
		}

		for(int i = 0; i < 9; ++i) {
			this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return this.villagerExtraContainer.stillValid(player) && this.villager.isAlive() && this.villager.distanceTo(player) < 8.0F;
	}

	@SuppressWarnings("ConstantValue")
	@Override
	public ItemStack quickMoveStack(Player player, int index) {
		ItemStack ret = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack slotItem = slot.getItem();
			ret = slotItem.copy();
			int villagerExtraInventorySize = this.armorContainers.getContainerSize() + this.villagerExtraContainer.getContainerSize();
			if (index < villagerExtraInventorySize) {
				if (!this.moveItemStackTo(slotItem, villagerExtraInventorySize, villagerExtraInventorySize + 36, true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.moveItemStackTo(slotItem, 0, villagerExtraInventorySize, false)) {
				int useRowStart = villagerExtraInventorySize + 27;
				int useRowEnd = useRowStart + 9;
				if (index >= useRowStart && index < useRowEnd) {
					if (!this.moveItemStackTo(slotItem, villagerExtraInventorySize, useRowStart, false)) {
						return ItemStack.EMPTY;
					}
				} else if (index < useRowStart) {
					if (!this.moveItemStackTo(slotItem, useRowStart, useRowEnd, false)) {
						return ItemStack.EMPTY;
					}
				} else if (!this.moveItemStackTo(slotItem, useRowStart, useRowStart, false)) {
					return ItemStack.EMPTY;
				}

				return ItemStack.EMPTY;
			}

			if (slotItem.isEmpty()) {
				slot.setByPlayer(ItemStack.EMPTY);
			} else {
				slot.setChanged();
			}
		}

		return ret;
	}

	@Override
	public void removed(Player player) {
		super.removed(player);
		this.villagerExtraContainer.stopOpen(player);
	}

	public Container getContainer() {
		return this.villagerExtraContainer;
	}
}
