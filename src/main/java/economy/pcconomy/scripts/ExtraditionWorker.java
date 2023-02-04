package economy.pcconomy.scripts;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class ExtraditionWorker {

    public static void giveItems(List<ItemStack> itemStacks, Player player) {
        if (getEmptySlots(player) < itemStacks.size()) return;

        for (ItemStack item:
             itemStacks) {
            player.getInventory().addItem(item);
        }
    }

    public static void giveItems(ItemStack itemStack, Player player) {
        if (getEmptySlots(player) < 1) return;
        player.getInventory().addItem(itemStack);
    }

    public static int getEmptySlots(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack[] cont = inventory.getContents();
        int i = 0;
        for (ItemStack item : cont)
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        return 36 - i;
    }
}
