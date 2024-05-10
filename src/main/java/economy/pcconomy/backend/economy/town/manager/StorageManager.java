package economy.pcconomy.backend.economy.town.manager;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StorageManager {
    /**
     * Create resource in storage
     * @param maxAmount Max amount of random generated count
     */
    public static void createResources(List<ItemStack> storage, int maxAmount) {
        for (var item : storage)
            setAmountOfResource(storage, item, getAmountOfResource(storage, item) + new Random().nextInt() % maxAmount);
    }

    /**
     * Delete resource in storage
     * @param maxAmount Max amount of random generated count
     */
    public static void useResources(List<ItemStack> storage, int maxAmount) {
        for (var item : storage) {
            if (item.getAmount() < maxAmount) return;
            setAmountOfResource(storage, item, getAmountOfResource(storage, item) - new Random().nextInt() % maxAmount);
        }
    }

    /**
     * Create resource in storage
     * @param item Type of item
     * @param amount Amount of items
     */
    public static void addResource(List<ItemStack> storage, Material item, int amount) {
        storage.add(new ItemStack(item, amount));
    }

    /**
     * Set amount of specified item
     * @param item Specified item
     * @param amount New amount
     */
    public static void setAmountOfResource(List<ItemStack> storage, ItemStack item, int amount) {
        for (var itemStack : storage)
            if (itemStack.isSimilar(item))
                storage.set(storage.indexOf(itemStack), new ItemStack(item.getType(), amount > 0 ? amount : 1));
    }

    /**
     * Get amount of specified item
     * @param item Specified item
     * @return Amount of resource
     */
    public static int getAmountOfResource(List<ItemStack> storage, ItemStack item) {
        for (var itemStack : storage)
            if (itemStack.isSimilar(item))
                return itemStack.getAmount();

        return 0;
    }

    /**
     * Get specified items from storage
     * @param item Specified item
     * @return ItemStack with same characteristics
     */
    public static ItemStack getResource(List<ItemStack> storage, ItemStack item) {
        for (var itemStack : storage)
            if (itemStack.getType().equals(item.getType()))
                return itemStack;

        return null;
    }

    /**
     * Get amount of storage
     * @return Amount of specified resource
     */
    public static int getAmountOfStorage(List<ItemStack> storage) {
        var amount = 0;
        for (var itemStack : storage) amount += itemStack.getAmount();
        return amount;
    }
}
