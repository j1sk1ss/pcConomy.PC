package economy.pcconomy.backend.cash.items;

import economy.pcconomy.backend.cash.CashManager;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;

import org.apache.commons.lang.StringUtils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Wallet {
    /**
     * New empty wallet
     */
    public Wallet() {
        Amount = 0;
        Level  = 1;

        Capacity = Level * 500;

        Body = new Item("Кошелёк", "0.0 Алеф\nВместимость: 1" + Level, Material.BOOK, 1, walletDataModel);
    }

    /**
     * Wallet from wallet item
     * @param wallet Wallet itemStack
     */
    public Wallet(ItemStack wallet) {
        Amount = Double.parseDouble(ItemManager.getLore(wallet).get(0).split(" ")[0]);
        Level  = Integer.parseInt(ItemManager.getLore(wallet).get(1).split(" ")[1]);

        Capacity = Level * 500;

        Body = wallet;
    }

    public double Amount;
    public double Capacity;
    public int Level;

    private final ItemStack Body;
    private static final int walletDataModel = 17050; //TODO: DATA MODEL

    /**
     * Create new wallet
     * @param player Player that will take this wallet
     */
    public void giveWallet(Player player) {
        ItemManager.giveItems(new Item("Кошелёк", Amount + " " + CashManager.getCurrencyNameByNum((int)Amount)
                + "\nВместимость: " + Level, Material.BOOK, 1, walletDataModel), player);
    }

    /**
     * Get wallet from inventory
     * @param player Player
     * @return Wallet
     */
    public static List<Wallet> getWallets(Player player) {
        var list = new ArrayList<Wallet>();
        for (var item : player.getInventory())
            if (item != null)
                if (isWallet(item)) list.add(new Wallet(item));

        return list;
    }

    /**
     * Get free space in wallets
     * @param wallets Wallets
     * @return Free space
     */
    public static double getFreeSpace(List<Wallet> wallets) {
        var amount = 0d;
        for (var wallet : wallets)
            amount += wallet.Capacity - wallet.Amount;

        return amount;
    }

    /**
     * Check item
     * @param itemStack Item
     * @return Wallet status
     */
    public static boolean isWallet(ItemStack itemStack) {
        return StringUtils.containsAny(ItemManager.getLore(itemStack).get(0).toLowerCase(), "алеф") &&
                    StringUtils.containsAny(ItemManager.getLore(itemStack).get(1).toLowerCase(), "вместимость") &&
                ItemManager.getName(itemStack).contains("Кошелёк");
    }

    /**
     * Gets amount of wallet
     * @param wallets Wallets
     * @return Amount
     */
    public static double getWalletAmount(List<Wallet> wallets) {
        var amount = 0;
        for (var wallet : wallets)
            amount += wallet.Amount;

        return amount;
    }

    /**
     * Put cash into players wallet
     * @param player Player
     * @param amount Amount
     */
    public static void changeCashInWallet(Player player, double amount) {
        var wallets = getWallets(player);
        var cashAmount = amount;

        for (var wallet : wallets) {
            ItemManager.takeItems(wallet.Body, player);

            if (cashAmount == 0) wallet.giveWallet(player);
            if (amount > 0)
                if (wallet.Amount + cashAmount > wallet.Capacity) {
                    wallet.Amount = wallet.Capacity;
                    cashAmount -= wallet.Capacity;
                } else {
                    wallet.Amount += cashAmount;
                    cashAmount = 0;
                }
            else
                if (wallet.Amount + cashAmount > 0) {
                    wallet.Amount -= cashAmount;
                    cashAmount = 0;
                } else {
                    cashAmount += wallet.Amount;
                    wallet.Amount += 0;
                }

            wallet.giveWallet(player);
        }
    }
}
