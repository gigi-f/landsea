package com.landsea.game.inventory;

public class ItemStack {
    private ItemType item;
    private int count;
    private float durability;
    private float maxDurability;

    public ItemStack(ItemType item, int count) {
        this.item = item;
        this.count = count;
        
        // Initialize durability for tools
        if (item == ItemType.AXE || item == ItemType.PICKAXE) {
            this.maxDurability = 100f;
            this.durability = 100f;
        } else {
            this.maxDurability = 0;
            this.durability = 0;
        }
    }

    public ItemType getItem() {
        return item;
    }

    public int getCount() {
        return count;
    }
    
    public float getDurability() {
        return durability;
    }
    
    public float getMaxDurability() {
        return maxDurability;
    }
    
    public void damage(float amount) {
        if (maxDurability > 0) {
            durability -= amount;
            if (durability < 0) durability = 0;
        }
    }
    
    public boolean isBroken() {
        return maxDurability > 0 && durability <= 0;
    }

    public void add(int amount) {
        this.count += amount;
    }

    public void remove(int amount) {
        this.count -= amount;
        if (this.count < 0) this.count = 0;
    }
    
    public boolean isEmpty() {
        return count <= 0;
    }
}
