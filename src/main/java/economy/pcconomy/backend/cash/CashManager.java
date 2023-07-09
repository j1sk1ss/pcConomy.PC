package economy.pcconomy.backend.cash;

import economy.pcconomy.backend.cash.items.Wallet;
import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class CashManager {
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
        currencyNameCases.put("is", "Алеф"); // Единственное число
        currencyNameCases.put("rs", "Алефа");
        currencyNameCases.put("ds", "Алефу");
        currencyNameCases.put("vs", "Алеф");
        currencyNameCases.put("ts", "Алефом");
        currencyNameCases.put("ps", "Алефе");

        currencyNameCases.put("ip", "Алефы"); // Множественное число
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
     * @return ItemStack object
     */
    public static ItemStack createCashObject(double amount) {
        return new Item(currencyName, "" + amount + currencySigh, Material.PAPER, 1, 17000); //TODO: DATA MODEL
    }

    /**
     * Creates itemStack object
     * @param amount Amount of cash object
     * @param count Count of objects
     * @return ItemStack object
     */
    public static ItemStack createCashObject(double amount, int count) {
        return new Item(currencyName, "" + amount + currencySigh, Material.PAPER, count, 17000); //TODO: DATA MODEL
    }

    /**
     * Get double value of amount from itemStack cash object
     * @param money ItemStack cash object
     * @return Double value
     */
    public static double getAmountFromCash(ItemStack money) {
        if (isCash(money))
            return Double.parseDouble(ItemManager.getLore(money).get(0).replace(currencySigh,"")) * money.getAmount();

        return 0;
    }

    /**
     * Get double value of amount from list of itemStack cash objects
     * @param money ItemStack cash objects
     * @return Double value
     */
    public static double getAmountFromCash(List<ItemStack> money) {
        var amount = 0.0;

        for (var item : money) if (isCash(item)) amount += getAmountFromCash(item);

        return amount;
    }

    /**
     * Get list of itemStacks
     * @param inventory Inventory
     * @return List of cash objects from inventory
     */
    public static List<ItemStack> getCashFromInventory(PlayerInventory inventory) {
        List<ItemStack> moneys = new ArrayList<>();

        for (var item : inventory) if (isCash(item)) moneys.add(item);

        return moneys;
    }

    /**
     * Get change from list
     * @param change Change
     * @return List of cash objects
     */
    public static List<ItemStack> getChangeInCash(List<Integer> change) {
        List<ItemStack> moneyStack = new ArrayList<>();

        for (int i = 0; i < Denomination.size(); i++)
            moneyStack.add(CashManager.createCashObject(Denomination.get(i), change.get(i)));

        return moneyStack;
    }

    /**
     * Cash object status
     * @param item Item that should be checked
     * @return Cash object status
     */
    public static boolean isCash(ItemStack item) {
        if (item == null) return false;
        if (ItemManager.getName(item).contains(currencyName))
            return !Objects.equals(ItemManager.getLore(item).get(0), "");

        return false;
    }

    /**
     * Gives to player items of cash
     * @param amount Amount of cash
     * @param player Player that will take this cash
     */
    public static void giveCashToPlayer(double amount, Player player) {
        var playerCashAmount = amountOfCashInInventory(player);

        var wallet = Wallet.getWallet(player);
        var walletAmount = 0d;

        if (wallet != null) {
            if (Wallet.getWalletAmount(wallet) + amount < 0) {
                walletAmount = -Wallet.getWalletAmount(wallet);
                Wallet.changeCashInWallet(player, walletAmount);
            }
            else {
                Wallet.changeCashInWallet(player, amount);
                return;
            }
        }

        ItemManager.giveItems(CashManager.getChangeInCash(getChange(playerCashAmount + (amount - walletAmount))), player);
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
     * @return Amount of cah in player`s inventory
     */
    public static double amountOfCashInInventory(Player player) {
        var playerCashAmount = CashManager.getAmountFromCash(CashManager.getCashFromInventory(player.getInventory()));
        var wallet = Wallet.getWallet(player);
        if (wallet != null)
            playerCashAmount += Wallet.getWalletAmount(wallet);

        return playerCashAmount;
    }

    /**
     * Take cash from player
     * @param amount Amount that will be taken
     * @param player Player that will lose this amount
     */
    public static void takeCashFromInventory(double amount, Player player) {
        var playerCashAmount = amountOfCashInInventory(player);
        if (playerCashAmount < amount) return;

        ItemManager.takeItems(CashManager.getCashFromInventory(player.getInventory()), player);
        giveCashToPlayer(-amount, player);
    }
    
    public static String getCurrencyNameCase(String currencyName) { // Получение склонённого названия валюты
    	return currencyNameCases.get(currencyName);
    }

    /**
     * Get currency name for wallet
     * @param num Amount of cash
     * @return Name of currency
     */
    public static String getCurrencyNameByNum(int num) { // Получение склонённого названия валюты в зависимости от суммы денег (в именительном падеже)
    	if (num % 10 == 1 && num % 100 != 11) return currencyNameCases.get("is");
    	if (num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 12 || num % 100 > 14)) return currencyNameCases.get("rs");
    	return currencyNameCases.get("rp");
    }
}
