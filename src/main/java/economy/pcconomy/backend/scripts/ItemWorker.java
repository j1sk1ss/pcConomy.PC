package economy.pcconomy.backend.scripts;

import economy.pcconomy.backend.cash.scripts.CashManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemWorker {
    public static ItemStack SetLore(ItemStack item, String loreLine) { // Устанавливает лор для предмета
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        List<Component> lore = Arrays.asList(Component.text(loreLine));

        itemMeta.lore(lore);
        Objects.requireNonNull(item).setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack SetName(ItemStack item, String name) { // Устанавливает имя для предмета
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        itemMeta.displayName(Component.text(name));
        Objects.requireNonNull(item).setItemMeta(itemMeta);

        return item;
    }

    public static ItemStack SetMaterial(ItemStack item, Material material) {
        var itemMeta = item.getItemMeta();
        var itemStack = new ItemStack(material, item.getAmount());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    public static Material GetMaterial(ItemStack item) {
        return item.getType();
    }

    public static List<String> GetLore(ItemStack item) { // Возможно лучшего всего сделать статический метод с этими манипуляциями
        return item.getItemMeta().lore().stream().map(Object::toString).collect(Collectors.toList());
    } // Получает лор

    public static String GetName(ItemStack item){
        return item.getItemMeta().displayName().toString();
    } // Получает имя

    public static void GiveItems(List<ItemStack> itemStacks, Player player) { // Выдаёт лист предметов игроку
        if (GetEmptySlots(player) < itemStacks.size()) return;

        for (var item:
                itemStacks)
            player.getInventory().addItem(item);
    }

    public static void GiveItems(ItemStack itemStack, Player player) { // Выдаёт один предмет игроку
        if (GetEmptySlots(player) < 1) return;
        player.getInventory().addItem(itemStack);
    }

    public static void GiveItemsWithoutLore(List<ItemStack> itemStacks, Player player) {
        if (GetEmptySlots(player) < itemStacks.size()) return;

        for (var item:
                itemStacks)
            player.getInventory().addItem(ItemWorker.SetLore(item, ""));
    }

    public static void GiveItemsWithoutLore(ItemStack itemStack, Player player) {
        if (GetEmptySlots(player) < 1) return;
        player.getInventory().addItem(ItemWorker.SetLore(itemStack, ""));
    }

    public static void TakeItems(List<ItemStack> itemStacks, Player player) {
        for (var item:
             itemStacks) {
            if (item == null) continue;

            for (var playerItem:
                 player.getInventory()) {
                if (playerItem == null) continue;

                if (playerItem.equals(item)) player.getInventory().removeItem(playerItem);
            }
        }
    }

    public static void TakeItems(ItemStack itemStack, Player player) {
        for (var playerItem:
                player.getInventory()) {
            if (playerItem == null) continue;
            if (playerItem.equals(itemStack)) player.getInventory().removeItem(playerItem);
            break;
        }
    }

    public static int GetEmptySlots(Player player) { // Получает кол-во пустых слотов у игрока
        var inventory = player.getInventory();
        var cont = inventory.getContents();
        int i = 0;
        for (var item : cont)
            if (item != null && item.getType() != Material.AIR) {
                i++;
            }
        return 36 - i;
    }

    public static double GetPriceFromLore(ItemStack itemStack, int loreLine) {
        return Double.parseDouble(ItemWorker.GetLore(itemStack).get(loreLine)
                .replace(CashManager.currencySigh, ""));
    }
}