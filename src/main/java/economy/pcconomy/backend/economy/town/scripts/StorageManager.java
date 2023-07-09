package economy.pcconomy.backend.economy.town.scripts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StorageManager {
    /**
     * Create resource in storage
     * @param maxAmount Max amount of random generated count
     * @param Storage Storage that will take new resource
     */
    public static void createResources(int maxAmount, List<ItemStack> Storage) {
        for (var item : Storage)
            setAmountOfResource(item, getAmountOfResource(item, Storage) + new Random().nextInt() % maxAmount, Storage);
    }

    /**
     * Delete resource in storage
     * @param maxAmount Max amount of random generated count
     * @param Storage Storage that will lose new resource
     */
    public static void useResources(int maxAmount, List<ItemStack> Storage) {
        for (var item : Storage) {
            if (item.getAmount() < maxAmount) return;
            setAmountOfResource(item, getAmountOfResource(item, Storage) - new Random().nextInt() % maxAmount, Storage);
        }
    }

    /**
     * Create resource in storage
     * @param item Type of item
     * @param amount Amount of items
     * @param Storage Storage that will take new resource
     */
    public static void addResource(Material item, int amount, List<ItemStack> Storage) {
        Storage.add(new ItemStack(item, amount));
    }

    /**
     * Delete resource in storage
     * @param item Type of item
     * @param Storage Storage that will take new resource
     */
    public static void deleteResource(Material item, List<ItemStack> Storage) {
        Storage.removeIf(itemStack -> itemStack.getType().equals(item));
    }

    /**
     * Set amount of specified item
     * @param item Specified item
     * @param amount New amount
     * @param Storage Storage that will update resource
     */
    public static void setAmountOfResource(ItemStack item, int amount, List<ItemStack> Storage) {
        for (var itemStack : Storage)
            if (itemStack.isSimilar(item))
                Storage.set(Storage.indexOf(itemStack), new ItemStack(item.getType(), amount));
    }

    /**
     * Get amount of specified item
     * @param item Specified item
     * @param Storage Storage where will be checked resource
     * @return Amount of resource
     */
    public static int getAmountOfResource(ItemStack item, List<ItemStack> Storage) {
        for (var itemStack : Storage)
            if (itemStack.isSimilar(item))
                return itemStack.getAmount();

        return 0;
    }

    /**
     * Get specified items from storage
     * @param item Specified item
     * @param Storage Specified storage
     * @return ItemStack with same characteristics
     */
    public static ItemStack getResource(ItemStack item, List<ItemStack> Storage) {
        for (var itemStack : Storage)
            if (itemStack.getType().equals(item.getType()))
                return itemStack;

        return null;
    }

    /**
     * Get amount of storage
     * @param Storage Specified storage
     * @return Amount of specified resource
     */
    public static int getAmountOfStorage(List<ItemStack> Storage) {
        var amount = 0;

        for (var itemStack : Storage)
            amount += itemStack.getAmount();

        return amount;
    }
}
