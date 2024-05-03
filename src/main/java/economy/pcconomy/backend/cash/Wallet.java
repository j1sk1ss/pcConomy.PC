package economy.pcconomy.backend.cash;

import lombok.experimental.ExtensionMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math3.util.Precision;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.util.ArrayList;
import java.util.List;


@ExtensionMethod({Manager.class})
public class Wallet {
    /**
     * New empty wallet
     */
    public Wallet() {
        Amount   = 0.0d;
        Level    = 1;
        Capacity = Level * 500;
        Body     = new Item("Кошелёк", Amount + " Алеф\nВместимость: " + Level,
                     Material.BOOK, 1, walletDataModel);
    }

    /**
     * Wallet from wallet item
     * @param wallet Wallet itemStack
     */
    public Wallet(ItemStack wallet) {
        Amount   = Double.parseDouble(wallet.getLoreLines().get(0).split(" ")[0]);
        Level    = Integer.parseInt(wallet.getLoreLines().get(1).split(" ")[1]);
        Capacity = Level * 500;
        Body     = wallet;
    }

    public double Amount;
    public final double Capacity;
    public final int Level;
    private final ItemStack Body;

    private static final int walletDataModel = 17050; //TODO: DATA MODEL

    /**
     * Give this wallet to player
     * @param player Player that will take this wallet
     */
    public void giveWallet(Player player) {
        new Item("Кошелёк", Precision.round(Amount, 3) + " " + CashManager.getCurrencyNameByNum((int)Amount)
                + "\nВместимость: " + Level, Material.BOOK, 1, walletDataModel).giveItems(player);
    }

    /**
     * Take this wallet from player
     * @param player Player that will take this wallet
     */
    public void takeWallet(Player player) {
        Body.takeItems(player);
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
                if (isWallet(item))
                    for (var i = 0; i < item.getAmount(); i++)
                        list.add(new Wallet(item));

        return list;
    }

    /**
     * Check item
     * @param itemStack Item
     * @return Wallet status
     */
    public static boolean isWallet(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (itemStack.getLoreLines() == null) return false;

        if (itemStack.getLoreLines().size() == 0) return false;
        return StringUtils.containsAny(itemStack.getLoreLines().get(0).toLowerCase(), "алеф") &&
                    StringUtils.containsAny(itemStack.getLoreLines().get(1).toLowerCase(), "вместимость") &&
                    itemStack.getName().contains("Кошелёк");
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
     * Change cash in players wallet
     * @param amount Amount (If amount lower than 0 - we take money from wallet)
     * @return Amount of cash that can't be stored
     */
    public double changeCashInWallet(double amount) {
        var cashAmount = Math.abs(amount);

        if (amount > 0) {
            if (Amount + cashAmount > Capacity) {
                Amount = Capacity;
                cashAmount -= Capacity;
            } else {
                Amount += cashAmount;
                cashAmount = 0;
            }
        }
        else {
            if (Amount - cashAmount <= 0) {
                Amount = 0;
                cashAmount -= Amount;
            } else {
                Amount -= cashAmount;
                cashAmount = 0;
            }
        }

        return cashAmount;
    }

    /**
     * Change cash in players wallet
     * @param player Player
     * @param amount Amount (If amount lower than 0 - we take money from wallet)
     * @return Amount of cash that can't be stored
     */
    public static double changeCashInWallets(Player player, double amount) {

        // ==============================
        // Found all wallets in inventory
        // ==============================

        var wallets = getWallets(player);
        var cashAmount = Math.abs(amount);

        // ==============================
        // Found all wallets in inventory
        // ==============================
        // Take them from inventory
        // ==============================

        for (var wallet : wallets)
            wallet.takeWallet(player);

        // ==============================
        // Take them from inventory
        // ==============================
        // Change value from all wallets
        // ==============================

        for (var wallet : wallets) {
            if (cashAmount <= 0) break;

            wallet.takeWallet(player);
            cashAmount = wallet.changeCashInWallet(cashAmount);
            wallet.giveWallet(player);
        }

        // ==============================
        // Change value from all wallets
        // ==============================
        // Give wallets to player
        // ==============================

        for (var wallet : wallets)
            wallet.giveWallet(player);

        // ==============================
        // Give wallets to player
        // ==============================

        return cashAmount;
    }
}
