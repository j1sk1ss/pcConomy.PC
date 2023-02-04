package economy.pcconomy.scripts;

import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemWorker {
    public static ItemStack SetLore(ItemStack item, String loreLine) {
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        var lore = Arrays.stream(loreLine.split("\n")).toList();

        itemMeta.setLore(lore);
        Objects.requireNonNull(item).setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack SetName(ItemStack item, String name) {
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        itemMeta.setDisplayName(name);
        Objects.requireNonNull(item).setItemMeta(itemMeta);

        return item;
    }

    public static List<String> GetLore(ItemStack item) {
        return item.getItemMeta().getLore();
    }

    public static String GetName(ItemStack item){
        return item.getI18NDisplayName();
    }
}