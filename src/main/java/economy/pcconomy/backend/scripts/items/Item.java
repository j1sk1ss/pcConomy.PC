package economy.pcconomy.backend.scripts.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Item extends ItemStack {
    /**
     * Paper with custom name
     * @param name Name
     */
    public Item(String name) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setType(Material.PAPER);
        super.setAmount(1);
    }

    /**
     * Paper with custom name and custom lore
     * @param name Name
     * @param lore Lore line (use delimiters)
     */
    public Item(String name, String lore) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, lore).getItemMeta());
        super.setType(Material.PAPER);
        super.setAmount(1);
    }

    /**
     * Custom item with custom name and custom lore
     * @param name Name
     * @param lore Lore line (use delimiters)
     * @param material Material
     */
    public Item(String name, String lore, Material material) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, lore).getItemMeta());
        super.setType(material);
        super.setAmount(1);
    }

    /**
     * Custom ItemStack
     * @param name Name
     * @param lore Lore line (use delimiters)
     * @param material Material
     * @param amount Amount
     */
    public Item(String name, String lore, Material material, int amount) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, lore).getItemMeta());
        super.setType(material);
        super.setAmount(amount);
    }

    /**
     * Custom ItemStack with custom model
     * @param name Name
     * @param lore Lore line (use delimiters)
     * @param material Material
     * @param amount Amount
     * @param dataModel Model data
     */
    public Item(String name, String lore, Material material, int amount, int dataModel) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, lore).getItemMeta());
        super.setType(material);
        super.setAmount(amount);

        super.getItemMeta().setCustomModelData(dataModel);
    }

    /**
     * ItemStack with changed name
     * @param itemStack ItemStack
     * @param name New name
     */
    public Item(ItemStack itemStack, String name) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, String.join(" ",ItemManager.getLore(itemStack))).getItemMeta());
        super.setType(itemStack.getType());
        super.setAmount(itemStack.getAmount());

        super.getItemMeta().setCustomModelData(itemStack.getItemMeta().getCustomModelData());
    }

    /**
     * ItemStack with changed name and lore
     * @param itemStack ItemStack
     * @param name New name
     * @param lore New lore
     */
    public Item(ItemStack itemStack, String name, String lore) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, lore).getItemMeta());
        super.setType(itemStack.getType());
        super.setAmount(itemStack.getAmount());

        super.getItemMeta().setCustomModelData(itemStack.getItemMeta().getCustomModelData());
    }

    /**
     * ItemStack with changed material
     * @param itemStack ItemStack
     * @param material New material
     */
    public Item(ItemStack itemStack, Material material) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, ItemManager.getName(itemStack)).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, String.join(" ", ItemManager.getLore(itemStack))).getItemMeta());
        super.setType(material);
        super.setAmount(itemStack.getAmount());

        super.getItemMeta().setCustomModelData(itemStack.getItemMeta().getCustomModelData());
    }

    /**
     * ItemStack with changed dataModel
     * @param itemStack ItemStack
     * @param dataModel New dataModel
     */
    public Item(ItemStack itemStack, int dataModel) {
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, ItemManager.getName(itemStack)).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, String.join(" ", ItemManager.getLore(itemStack))).getItemMeta());
        super.setType(itemStack.getType());
        super.setAmount(itemStack.getAmount());

        super.getItemMeta().setCustomModelData(dataModel);
    }
}
