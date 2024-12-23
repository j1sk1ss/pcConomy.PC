package economy.pcconomy.backend.cash;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.apache.commons.math3.util.Precision;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.util.List;
import java.util.ArrayList;


@ExtensionMethod({Manager.class})
public class Wallet {
    /**
     * New empty wallet
     */
    public Wallet() {
        Amount   = 0.0d;
        Level    = 1;
        Capacity = Level * 500d;
        Body     = new Item("Кошелёк", Amount + " Алеф\nВместимость: " + Level, Material.BOOK, 1, walletDataModel);

        Body.setInteger2Container(1, "wallet");
    }

    /**
     * Wallet from wallet item
     * @param wallet Wallet itemStack
     */
    public Wallet(ItemStack wallet) {
        Amount   = Double.parseDouble(wallet.getLoreLines().get(0).split(" ")[0]);
        Level    = Integer.parseInt(wallet.getLoreLines().get(1).split(" ")[1]);
        Capacity = Level * 500d;
        Body     = wallet;

        Body.setInteger2Container(1, "wallet");
    }

    @Getter @Setter private double Amount;
    @Getter private final double Capacity;
    @Getter private final int Level;
    private final ItemStack Body;

    private static final int walletDataModel = 17050;

    /**
     * Give this wallet to player
     * @param player Player that will take this wallet
     */
    public void giveWallet(Player player) {
        Body.setLore(Precision.round(Amount, 3) + " " + Cash.getCurrencyNameByNum((int)Amount) + "\nВместимость: " + Level);
        Body.giveItems(player);
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
        for (var item : player.getInventory()) {
            if (item != null) {
                if (isWallet(item)) {
                    for (var i = 0; i < item.getAmount(); i++)
                        list.add(new Wallet(item));
                }
            }
        }

        return list;
    }

    /**
     * Check item
     * @param itemStack Item
     * @return Wallet status
     */
    public static boolean isWallet(ItemStack itemStack) {
        return itemStack.getIntegerFromContainer("wallet") == 1;
    }

    /**
     * Gets amount of wallet
     * @param wallets Wallets
     * @return Amount
     */
    public static double getWalletAmount(List<Wallet> wallets) {
        var amount = 0d;
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
                cashAmount = Capacity - Amount;
                Amount = Capacity;
            } else {
                Amount += cashAmount;
                cashAmount = 0;
            }
        }
        else {
            if (Amount - cashAmount <= 0) {
                cashAmount -= Amount;
                Amount = 0;
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
        var wallets = getWallets(player);
        var cashAmount = Math.abs(amount);

        for (var wallet : wallets) wallet.takeWallet(player);
        for (var wallet : wallets) {
            if (cashAmount <= 0) break;

            wallet.takeWallet(player);
            cashAmount = wallet.changeCashInWallet(cashAmount);
            wallet.giveWallet(player);
        }

        for (var wallet : wallets) wallet.giveWallet(player);
        return cashAmount;
    }
}
