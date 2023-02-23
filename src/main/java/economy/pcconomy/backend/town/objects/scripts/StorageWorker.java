package economy.pcconomy.backend.town.objects.scripts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StorageWorker {

    public static void CreateResources(int maxAmount, List<ItemStack> Storage) { // Только для НПС города
        for (ItemStack item:
                Storage) {
            SetAmountOfResource(item, GetAmountOfResource(item, Storage)
                    + new Random().nextInt() % maxAmount, Storage);
        }
    }

    public static void UseResources(int maxAmount, List<ItemStack> Storage) {
        for (ItemStack item:
                Storage) {
            if (item.getAmount() < maxAmount) return;
            SetAmountOfResource(item, GetAmountOfResource(item, Storage) - new Random().nextInt() % maxAmount,
                    Storage);
        }
    }

    public static void AddResource(Material item, int amount, List<ItemStack> Storage) {
        Storage.add(new ItemStack(item, amount));
    }

    public static void DeleteResource(Material item, List<ItemStack> Storage) {
        Storage.removeIf(itemStack -> itemStack.getType().equals(item));
    }

    public static void SetAmountOfResource(ItemStack item, int amount, List<ItemStack> Storage) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.isSimilar(item)) {
                Storage.set(Storage.indexOf(itemStack), new ItemStack(item.getType(), amount));
            }
        }
    }

    public static int GetAmountOfResource(ItemStack item, List<ItemStack> Storage) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.isSimilar(item)) {
                return itemStack.getAmount();
            }
        }
        return 0;
    }

    public static ItemStack GetResource(ItemStack item, List<ItemStack> Storage) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.getType().equals(item.getType())) {
                return itemStack;
            }
        }
        return null;
    }

    public static int GetAmountOfStorage(List<ItemStack> Storage) {
        var amount = 0;

        for (ItemStack itemStack : Storage) {
            amount += itemStack.getAmount();
        }
        return amount;
    }

}
