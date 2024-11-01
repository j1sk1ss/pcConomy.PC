package economy.pcconomy.backend.cash;

import lombok.experimental.ExtensionMethod;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import org.j1sk1ss.itemmanager.manager.Item;
import org.j1sk1ss.itemmanager.manager.Manager;

import java.util.*;


@ExtensionMethod({Manager.class, Wallet.class})
public class Cash {
    /**
     * Currency name that will be used in all plugin
     */
    public final static String currencyName = "Алеф";

    /**
     * Currency sigh that will be used in all plugin
     */
    public final static String currencySigh = "$";

    /**
     * Declination of currency name
     */
    private static final HashMap<String, String> currencyNameCases = new HashMap<>();
    static {
        currencyNameCases.put("is", "Алеф");
        currencyNameCases.put("rs", "Алефа");
        currencyNameCases.put("ds", "Алефу");
        currencyNameCases.put("vs", "Алеф");
        currencyNameCases.put("ts", "Алефом");
        currencyNameCases.put("ps", "Алефе");

        currencyNameCases.put("ip", "Алефы");
        currencyNameCases.put("rp", "Алефов");
        currencyNameCases.put("dp", "Алефам");
        currencyNameCases.put("vp", "Алефы");
        currencyNameCases.put("tp", "Алефами");
        currencyNameCases.put("pp", "Алефах");
    }

    /**
     * List of nominations
     */
    public static final List<Double> Denomination =
        Arrays.asList(5000.0, 2000.0, 1000.0, 500.0, 200.0, 100.0, 50.0, 10.0, 1.0, 0.5, 0.1, 0.05, 0.01);

    /**
     * Creates itemStack object
     * @param amount Amount of cash object
     * @param count Count of objects
     * @return ItemStack object
     */
    public static ItemStack createCashObject(double amount, int count) {
        var cashBody = new Item(currencyName, amount + currencySigh, Material.PAPER, count, 20000 + (int)amount);
        cashBody.setDouble2Container(amount, "cash-value");
        return cashBody;
    }

    /**
     * Get double value of amount from itemStack cash object
     * @param money ItemStack cash object
     * @return Double value
     */
    public static double getAmountFromCash(ItemStack money) {
        if (isCash(money)) return money.getDoubleFromContainer("cash-value") * money.getAmount();
        else return 0;
    }

    /**
     * Get double value of amount from list of itemStack cash objects
     * @param money ItemStack cash objects
     * @return Double value
     */
    public static double getAmountFromCash(List<ItemStack> money) {
        var amount = 0.0;
        for (var item : money) {
            if (isCash(item))
                amount += getAmountFromCash(item);
        }

        return amount;
    }

    /**
     * Get list of itemStacks
     * @param inventory Inventory
     * @return List of cash objects from inventory
     */
    public static List<ItemStack> getCashFromInventory(PlayerInventory inventory) {
        var moneys = new ArrayList<ItemStack>();
        for (var item : inventory) {
            if (isCash(item))
                moneys.add(item);
        }

        return moneys;
    }

    /**
     * Get change from list
     * @param change Change
     * @return List of cash objects
     */
    public static List<ItemStack> getChangeInCash(List<Integer> change) {
        var moneyStack = new ArrayList<ItemStack>();
        for (int i = 0; i < Denomination.size(); i++)
            moneyStack.add(createCashObject(Denomination.get(i), change.get(i)));

        return moneyStack;
    }

    /**
     * Cash object status
     * @param item Item that should be checked
     * @return Cash object status
     */
    public static boolean isCash(ItemStack item) {
        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        if (item.getType() != Material.PAPER) return false;
        return item.getDoubleFromContainer("cash-value") != -1.0;
    }

    /**
     * Gives to player items of cash
     * @param amount Amount of cash
     * @param player Player that will take this cash
     * @param ignoreWallet Ignoring of wallet status
     */
    public static void giveCashToPlayer(Player player, double amount, boolean ignoreWallet) {
        System.out.println("Try to give cash to player: " + player.getName() + ", amount: " + amount);
        getChangeInCash(getChange(ignoreWallet ? amount : player.changeCashInWallets(amount))).giveItems(player);
    }

    /**
     * Takes cash from player
     * @param amount Amount of cash
     * @param player Player that will lose cash
     * @param ignoreWallet Ignoring wallet status
     */
    public static void takeCashFromPlayer(Player player, double amount, boolean ignoreWallet) {
        System.out.println("Try to take cash from player: " + player.getName() + ", amount: " + amount);
        var playerCashAmount = amountOfCashInInventory(player, ignoreWallet);
        if (playerCashAmount < amount) return;

        getCashFromInventory(player.getInventory()).takeItems(player);
        getChangeInCash(getChange(playerCashAmount - (ignoreWallet ? amount : player.changeCashInWallets(-amount)))).giveItems(player);
    }

    /**
     * Get count of every cash by denomination
     * @param amount Amount
     * @return Change
     */
    public static List<Integer> getChange(double amount) {
        var change = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        for (int i = 0; i < Denomination.size(); i++)
            while (amount - Denomination.get(i) >= 0) {
                amount -= Denomination.get(i);
                change.set(i, change.get(i) + 1);
            }

        return change;
    }

    /**
     * Amount of cash in player inventory
     * @param player Player that will be checked
     * @param ignoreWallet Ignore wallet during calculations
     * @return Amount of cash in player`s inventory
     */
    public static double amountOfCashInInventory(Player player, boolean ignoreWallet) {
        return getAmountFromCash(getCashFromInventory(player.getInventory())) + (ignoreWallet ? 0 : player.getWallets().getWalletAmount());
    }

    /**
     * Get currency name-case
     * @param currencyName Currency name
     * @return Name-case
     */
    public static String getCurrencyNameCase(String currencyName) { // Получение склонённого названия валюты
    	return currencyNameCases.get(currencyName);
    }

    /**
     * Get currency name for wallet
     * @param num Amount of cash
     * @return Name of currency
     */
    public static String getCurrencyNameByNum(int num) {
    	if (num % 10 == 1 && num % 100 != 11) return currencyNameCases.get("is");
    	if (num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 12 || num % 100 > 14)) return currencyNameCases.get("rs");
    	return currencyNameCases.get("rp");
    }
}