package economy.pcconomy.backend.scripts.items;

import economy.pcconomy.backend.cash.CashManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ItemManager {
    /**
     * Sets lore of itemStack
     * @param item ItemStack that should take new lore
     * @param loreLine Lore line
     * @return ItemStack with lore
     */
    public static ItemStack setLore(ItemStack item, String loreLine) {
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        var lore = new ArrayList<Component>();

        var lines = loreLine.split("\n");
        for (var line : lines)
            lore.add(Component.text(line));

        itemMeta.lore(lore);
        Objects.requireNonNull(item).setItemMeta(itemMeta);
        return item;
    }

    /**
     * Sets lore of itemStack
     * @param item ItemStack that should take new name
     * @param name New name
     * @return ItemStack with name
     */
    public static ItemStack setName(ItemStack item, String name) {
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        itemMeta.displayName(Component.text(name));
        Objects.requireNonNull(item).setItemMeta(itemMeta);

        return item;
    }

    /**
     * Gets material of itemStack
     * @param item ItemStack
     * @return Material of itemStack
     */
    public static Material getMaterial(ItemStack item) {
        return item.getType();
    }

    /**
     * Gets lore of itemStack
     * @param item ItemStack
     * @return Lore of itemStack
     */
    public static List<String> getLore(ItemStack item) {
        return item.getItemMeta().getLore();
    }

    /**
     * Gets name of itemStack
     * @param item ItemStack
     * @return Name of itemStack
     */
    public static String getName(ItemStack item){
        return Objects.requireNonNull(item.getItemMeta().getDisplayName());
    }

    /**
     * Give list of items to player
     * @param itemStacks List of items
     * @param player Player that will take this list
     */
    public static void giveItems(List<ItemStack> itemStacks, Player player) {
        for (var item : itemStacks)
            player.getInventory().addItem(item).forEach((index, itemStack) ->
                    player.getWorld().dropItem(player.getLocation(), itemStack));
    }

    /**
     * Give item to player
     * @param item Item
     * @param player Player that will take this item
     */
    public static void giveItems(ItemStack item, Player player) {
        player.getInventory().addItem(item).forEach((index, itemStack) ->
                player.getWorld().dropItem(player.getLocation(), itemStack));
    }

    /**
     * Give items to player without any lore
     * @param itemStacks List of items
     * @param player Player that will take this list
     */
    public static void giveItemsWithoutLore(List<ItemStack> itemStacks, Player player) {
        for (var item : itemStacks)
            player.getInventory().addItem(ItemManager.setLore(item, ""))
                    .forEach((index, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));;
    }

    /**
     * Give item to player without any lore
     * @param item Item
     * @param player Player that will take this item
     */
    public static void giveItemsWithoutLore(ItemStack item, Player player) {
        player.getInventory().addItem(ItemManager.setLore(item, ""))
                .forEach((index, itemStack) -> player.getWorld().dropItem(player.getLocation(), itemStack));
    }

    /**
     * Takes items from player
     * @param itemStacks List of items that should be taken
     * @param player Player that will lose this list of items
     */
    public static void takeItems(List<ItemStack> itemStacks, Player player) {
        for (var item : itemStacks) {
            if (item == null) continue;

            for (var playerItem: player.getInventory()) {
                if (playerItem == null) continue;
                takeItems(item, player);
            }
        }
    }

    /**
     * Takes item from player
     * @param itemStack Item that should be taken
     * @param player Player that will lose this item
     */
    public static void takeItems(ItemStack itemStack, Player player) {
        for (var playerItem : player.getInventory()) {
            if (playerItem == null) continue;
            if (playerItem.equals(itemStack)) player.getInventory().removeItem(playerItem);

            break;
        }
    }

    /**
     * Gets double formatted price that wrote in lore
     * @param itemStack Item that price will be given
     * @param loreLine Lore line position that includes price
     * @return Price
     */
    public static double getPriceFromLore(ItemStack itemStack, int loreLine) {
        return Double.parseDouble(ItemManager.getLore(itemStack).get(loreLine)
                .replace(CashManager.currencySigh, ""));
    }
}