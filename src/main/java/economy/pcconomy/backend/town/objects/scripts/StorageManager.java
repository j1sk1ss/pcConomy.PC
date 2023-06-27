package economy.pcconomy.backend.town.objects.scripts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StorageManager {
    public static void createResources(int maxAmount, List<ItemStack> Storage) {
        for (ItemStack item : Storage)
            setAmountOfResource(item, getAmountOfResource(item, Storage)
                    + new Random().nextInt() % maxAmount, Storage);
    }

    public static void useResources(int maxAmount, List<ItemStack> Storage) {
        for (ItemStack item : Storage) {
            if (item.getAmount() < maxAmount) return;
            setAmountOfResource(item, getAmountOfResource(item, Storage) - new Random().nextInt() % maxAmount,
                    Storage);
        }
    }

    public static void addResource(Material item, int amount, List<ItemStack> Storage) {
        Storage.add(new ItemStack(item, amount));
    }

    public static void deleteResource(Material item, List<ItemStack> Storage) {
        Storage.removeIf(itemStack -> itemStack.getType().equals(item));
    }

    public static void setAmountOfResource(ItemStack item, int amount, List<ItemStack> Storage) {
        for (ItemStack itemStack : Storage)
            if (itemStack.isSimilar(item))
                Storage.set(Storage.indexOf(itemStack), new ItemStack(item.getType(), amount));
    }

    public static int getAmountOfResource(ItemStack item, List<ItemStack> Storage) {
        for (ItemStack itemStack : Storage)
            if (itemStack.isSimilar(item))
                return itemStack.getAmount();

        return 0;
    }

    public static ItemStack getResource(ItemStack item, List<ItemStack> Storage) {
        for (ItemStack itemStack : Storage) {
            if (itemStack.getType().equals(item.getType()))
                return itemStack;
        }
        return null;
    }

    public static int getAmountOfStorage(List<ItemStack> Storage) {
        var amount = 0;

        for (ItemStack itemStack : Storage)
            amount += itemStack.getAmount();

        return amount;
    }
}
