package economy.pcconomy.backend.scripts;

import economy.pcconomy.backend.cash.scripts.CashManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ItemManager {
    /***
     * Sets lore of itemStack
     * @param item ItemStack that should take new lore
     * @param loreLine Lore line
     * @return ItemStack with lore
     */
    public static ItemStack setLore(ItemStack item, String loreLine) {
        var itemMeta = Objects.requireNonNull(item).getItemMeta();
        List<Component> lore = List.of(Component.text(loreLine));

        itemMeta.lore(lore);
        Objects.requireNonNull(item).setItemMeta(itemMeta);
        return item;
    }

    /***
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

    /***
     * Sets lore of itemStack
     * @param item ItemStack that should take new material
     * @param material New material
     * @return ItemStack with material
     */
    public static ItemStack setMaterial(ItemStack item, Material material) {
        var itemMeta = item.getItemMeta();
        var itemStack = new ItemStack(material, item.getAmount());
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /***
     * Gets material of itemStack
     * @param item ItemStack
     * @return Material of itemStack
     */
    public static Material getMaterial(ItemStack item) {
        return item.getType();
    }

    /***
     * Gets lore of itemStack
     * @param item ItemStack
     * @return Lore of itemStack
     */
    public static List<String> getLore(ItemStack item) {
        return Objects.requireNonNull(item.getItemMeta().lore()).stream().map(Object::toString).collect(Collectors.toList());
    }

    /***
     * Gets name of itemStack
     * @param item ItemStack
     * @return Name of itemStack
     */
    public static String getName(ItemStack item){
        return Objects.requireNonNull(item.getItemMeta().getDisplayName());
    }

    /***
     * Give list of items to player
     * @param itemStacks List of items
     * @param player Player that will take this list
     */
    public static void giveItems(List<ItemStack> itemStacks, Player player) {
        if (getEmptySlots(player) < itemStacks.size()) return;

        for (var item : itemStacks)
            player.getInventory().addItem(item);
    }

    /***
     * Give item to player
     * @param itemStack Item
     * @param player Player that will take this item
     */
    public static void giveItems(ItemStack itemStack, Player player) {
        if (getEmptySlots(player) < 1) return;
        player.getInventory().addItem(itemStack);
    }

    /***
     * Give items to player without any lore
     * @param itemStacks List of items
     * @param player Player that will take this list
     */
    public static void giveItemsWithoutLore(List<ItemStack> itemStacks, Player player) {
        if (getEmptySlots(player) < itemStacks.size()) return;

        for (var item : itemStacks)
            player.getInventory().addItem(ItemManager.setLore(item, ""));
    }

    /***
     * Give item to player without any lore
     * @param itemStack Item
     * @param player Player that will take this item
     */
    public static void giveItemsWithoutLore(ItemStack itemStack, Player player) {
        if (getEmptySlots(player) < 1) return;
        player.getInventory().addItem(ItemManager.setLore(itemStack, ""));
    }

    /***
     * Takes items from player
     * @param itemStacks List of items that should be taken
     * @param player Player that will lose this list of items
     */
    public static void takeItems(List<ItemStack> itemStacks, Player player) {
        for (var item : itemStacks) {
            if (item == null) continue;

            for (var playerItem:
                 player.getInventory()) {
                if (playerItem == null) continue;

                if (playerItem.equals(item)) player.getInventory().removeItem(playerItem);
            }
        }
    }

    /***
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

    /***
     * Gets count of empty slots
     * @param player Player that's inventory will be checked
     * @return Count of empty slots
     */
    public static int getEmptySlots(Player player) {
        var inventory = player.getInventory();
        var cont = inventory.getContents();
        int i = 0;
        for (var item : cont)
            if (item != null && item.getType() != Material.AIR) i++;

        return 36 - i;
    }

    /***
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