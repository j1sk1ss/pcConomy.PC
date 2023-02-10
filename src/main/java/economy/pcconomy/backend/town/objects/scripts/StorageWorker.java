package economy.pcconomy.backend.town.objects.scripts;

import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;

public class StorageWorker {

    public static void CreateResources(int maxAmount, List<ItemStack> Storage) { // Только для НПС города
        for (ItemStack item:
                Storage) {
            setAmountOfResource(item, getAmountOfResource(item, Storage) + new Random().nextInt() % maxAmount, Storage);
        }
    }

    public static void UseResources(int maxAmount, List<ItemStack> Storage) {
        for (ItemStack item:
                Storage) {
            if (item.getAmount() < 10) return;
            setAmountOfResource(item, getAmountOfResource(item, Storage) - new Random().nextInt() % maxAmount, Storage);
        }
    }

    public static void setAmountOfResource(ItemStack item, int amount, List<ItemStack> Storage) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.isSimilar(item)) {
                Storage.set(Storage.indexOf(itemStack), new ItemStack(item.getType(), amount));
            }
        }
    }

    public static int getAmountOfResource(ItemStack item, List<ItemStack> Storage) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.isSimilar(item)) {
                return itemStack.getAmount();
            }
        }
        return 0;
    }

    public static ItemStack getResource(ItemStack item, List<ItemStack> Storage) {
        for (ItemStack itemStack:
                Storage) {
            if (itemStack.getType().equals(item.getType())) {
                return itemStack;
            }
        }
        return null;
    }

    public static ItemStack getPercentOfResource(double percent, ItemStack resource) {
        var item = new ItemStack(resource);
        item.setAmount((int)(resource.getAmount() * percent));
        return item;
    }

    public static int getAmountOfStorage(List<ItemStack> Storage) {
        var amount = 0;

        for (var i = 0; i < Storage.size(); i++) {
            amount += Storage.get(i).getAmount();
        }
        return amount;
    }

}
