package com.landsea.game.inventory;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<ItemStack> slots;
    private int capacity;

    public Inventory(int capacity) {
        this.capacity = capacity;
        this.slots = new ArrayList<>();
    }

    public boolean addItem(ItemType item, int amount) {
        // Try to stack with existing items
        for (ItemStack slot : slots) {
            if (slot.getItem() == item) {
                slot.add(amount);
                return true;
            }
        }

        // If not found, add new slot if space permits
        if (slots.size() < capacity) {
            slots.add(new ItemStack(item, amount));
            return true;
        }

        return false; // Inventory full
    }

    public boolean removeItem(ItemType item, int amount) {
        for (int i = 0; i < slots.size(); i++) {
            ItemStack slot = slots.get(i);
            if (slot.getItem() == item) {
                if (slot.getCount() >= amount) {
                    slot.remove(amount);
                    if (slot.isEmpty()) {
                        slots.remove(i);
                    }
                    return true;
                }
                return false; // Not enough items
            }
        }
        return false; // Item not found
    }
    
    public boolean hasItem(ItemType item) {
        for (ItemStack slot : slots) {
            if (slot.getItem() == item) {
                return true;
            }
        }
        return false;
    }
    
    public ItemStack findItem(ItemType item) {
        for (ItemStack slot : slots) {
            if (slot.getItem() == item) return slot;
        }
        return null;
    }
    
    public void removeStack(ItemStack stack) {
        slots.remove(stack);
    }

    public int getItemCount(ItemType item) {
        for (ItemStack slot : slots) {
            if (slot.getItem() == item) {
                return slot.getCount();
            }
        }
        return 0;
    }

    public List<ItemStack> getSlots() {
        return slots;
    }
    
    public int getCapacity() {
        return capacity;
    }
}
