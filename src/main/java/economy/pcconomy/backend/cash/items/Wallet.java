package economy.pcconomy.backend.cash.items;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Wallet {
    private static final int walletDataModel = 17050; //TODO: DATA MODEL

    /**
     * Create new wallet
     * @param player Player that will take this wallet
     */
    public static void giveWallet(Player player) {
        ItemManager.giveItems(new Item("Кошелёк", "0 Алеф", Material.BOOK, 1, walletDataModel), player);
    }

    /**
     * Create wallet with special amount of cash
     * @param player Player that will take this wallet
     * @param amount Special amount
     */
    public static void giveWallet(Player player, double amount) {
        ItemManager.giveItems(new Item("Кошелёк", amount + " " + CashManager.getCurrencyNameByNum((int)amount),
                Material.BOOK, 1, walletDataModel), player);
    }

    /**
     * Get wallet from inventory
     * @param player Player
     * @return Wallet
     */
    public static ItemStack getWallet(Player player) {
        for (var item : player.getInventory())
            if (item != null)
                if (isWallet(item)) return item;

        return null;
    }

    /**
     * Check item
     * @param itemStack Item
     * @return Wallet status
     */
    public static boolean isWallet(ItemStack itemStack) {
        return StringUtils.containsAny(ItemManager.getLore(itemStack).get(0).toLowerCase(), "алеф");
    }

    /**
     * Gets amount of wallet
     * @param wallet Wallet
     * @return Amount
     */
    public static double getWalletAmount(ItemStack wallet) {
        return Double.parseDouble(ItemManager.getLore(wallet).get(0).split(" ")[0]);
    }

    /**
     * Put cash into players wallet
     * @param player Player
     * @param amount Amount
     */
    public static void changeCashInWallet(Player player, double amount) {
        var wallet = getWallet(player);
        ItemManager.takeItems(wallet, player);

        giveWallet(player, getWalletAmount(wallet) + amount);
    }
}
