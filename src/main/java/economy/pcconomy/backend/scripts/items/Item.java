package economy.pcconomy.backend.scripts.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Item extends ItemStack {
    /**
     * Paper with custom name
     * @param name Name
     */
    public Item(String name) {
        super(Material.PAPER);
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
        super(Material.PAPER);
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
        super(Material.PAPER);
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
        super(Material.PAPER);
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
        super(Material.PAPER);
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
        super(Material.PAPER);
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setType(itemStack.getType());
        super.setAmount(itemStack.getAmount());

        if (cloned.getItemMeta().hasCustomModelData())
            super.getItemMeta().setCustomModelData(itemStack.getItemMeta().getCustomModelData());

        if (cloned.getItemMeta().hasLore())
            super.setItemMeta(ItemManager.setLore(cloned, String.join("\n", ItemManager.getLore(itemStack))).getItemMeta());
    }

    /**
     * ItemStack with changed name and lore
     * @param itemStack ItemStack
     * @param name New name
     * @param lore New lore
     */
    public Item(ItemStack itemStack, String name, String lore) {
        super(Material.PAPER);
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, name).getItemMeta());
        super.setItemMeta(ItemManager.setLore(cloned, lore).getItemMeta());
        super.setType(itemStack.getType());
        super.setAmount(itemStack.getAmount());

        if (cloned.getItemMeta().hasLore())
            super.setItemMeta(ItemManager.setLore(cloned, String.join("\n", ItemManager.getLore(itemStack))).getItemMeta());
    }

    /**
     * ItemStack with changed material
     * @param itemStack ItemStack
     * @param material New material
     */
    public Item(ItemStack itemStack, Material material) {
        super(Material.PAPER);
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, ItemManager.getName(itemStack)).getItemMeta());
        super.setType(material);
        super.setAmount(itemStack.getAmount());

        if (cloned.getItemMeta().hasCustomModelData())
            super.getItemMeta().setCustomModelData(itemStack.getItemMeta().getCustomModelData());

        if (cloned.getItemMeta().hasLore())
            super.setItemMeta(ItemManager.setLore(cloned, String.join("\n", ItemManager.getLore(itemStack))).getItemMeta());
    }

    /**
     * ItemStack with changed dataModel
     * @param itemStack ItemStack
     * @param dataModel New dataModel
     */
    public Item(ItemStack itemStack, int dataModel) {
        super(Material.PAPER);
        var cloned = super.clone();

        super.setItemMeta(ItemManager.setName(cloned, ItemManager.getName(itemStack)).getItemMeta());
        super.setType(itemStack.getType());
        super.setAmount(itemStack.getAmount());

        if (cloned.getItemMeta().hasCustomModelData())
            super.getItemMeta().setCustomModelData(itemStack.getItemMeta().getCustomModelData());

        if (cloned.getItemMeta().hasLore())
            super.setItemMeta(ItemManager.setLore(cloned, String.join("\n", ItemManager.getLore(itemStack))).getItemMeta());
    }
}
