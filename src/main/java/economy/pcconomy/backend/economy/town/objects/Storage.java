package economy.pcconomy.backend.economy.town.objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class Storage {
    /**
     * Storage class object
     * @param storage List of storage
     */
    public Storage(List<ItemStack> storage) {
        StorageBody = storage;
    }

    public List<ItemStack> StorageBody;

    /**
     * Create resource in storage
     * @param maxAmount Max amount of random generated count
     */
    public void createResources(int maxAmount) {
        for (var item : StorageBody)
            setAmountOfResource(item, getAmountOfResource(item) + new Random().nextInt() % maxAmount);
    }

    /**
     * Delete resource in storage
     * @param maxAmount Max amount of random generated count
     */
    public void useResources(int maxAmount) {
        for (var item : StorageBody) {
            if (item.getAmount() < maxAmount) return;
            setAmountOfResource(item, getAmountOfResource(item) - new Random().nextInt() % maxAmount);
        }
    }

    /**
     * Create resource in storage
     * @param item Type of item
     * @param amount Amount of items
     */
    public void addResource(Material item, int amount) {
        StorageBody.add(new ItemStack(item, amount));
    }

    /**
     * Set amount of specified item
     * @param item Specified item
     * @param amount New amount
     */
    public void setAmountOfResource(ItemStack item, int amount) {
        for (var itemStack : StorageBody)
            if (itemStack.isSimilar(item))
                StorageBody.set(StorageBody.indexOf(itemStack), new ItemStack(item.getType(), amount > 0 ? amount : 1));
    }

    /**
     * Get amount of specified item
     * @param item Specified item
     * @return Amount of resource
     */
    public int getAmountOfResource(ItemStack item) {
        for (var itemStack : StorageBody)
            if (itemStack.isSimilar(item))
                return itemStack.getAmount();

        return 0;
    }

    /**
     * Get specified items from storage
     * @param item Specified item
     * @return ItemStack with same characteristics
     */
    public ItemStack getResource(ItemStack item) {
        for (var itemStack : StorageBody)
            if (itemStack.getType().equals(item.getType()))
                return itemStack;

        return null;
    }

    /**
     * Get amount of storage
     * @return Amount of specified resource
     */
    public int getAmountOfStorage() {
        var amount = 0;

        for (var itemStack : StorageBody)
            amount += itemStack.getAmount();

        return amount;
    }
}
