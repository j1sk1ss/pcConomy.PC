package economy.pcconomy.backend.cash;

import economy.pcconomy.backend.scripts.items.Item;
import economy.pcconomy.backend.scripts.items.ItemManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CashManager {
    public final static String currencyName = "Алеф";
    public final static String currencySigh = "$";
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

        for (int i = 0; i < ChangeManager.Denomination.size(); i++)
            moneyStack.add(CashManager.createCashObject(ChangeManager.Denomination.get(i), change.get(i)));

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
    public void giveCashToPlayer(double amount, Player player) {
        if (!ChangeManager.Denomination.contains(amount)) {
            giveSpecialAmountOfCashToPlayer(amount, player);
            return;
        }

        if (ItemManager.getEmptySlots(player) < 1) return;

        ItemManager.giveItems(CashManager.createCashObject(amount), player);
    }

    /**
     * Gives to player items of cash with special amount
     * @param amount Amount of cash
     * @param player Player that will take this cash
     */
    public void giveSpecialAmountOfCashToPlayer(double amount, Player player) {
        var changeNumeric = ChangeManager.getChange(amount);
        if (ItemManager.getEmptySlots(player) < changeNumeric.size()) return;

        List<ItemStack> change = CashManager.getChangeInCash(changeNumeric);
        ItemManager.giveItems(change, player);
    }

    /**
     * Amount of cash in player inventory
     * @param player Player that will be checked
     * @return Amount of cah in player`s inventory
     */
    public double amountOfCashInInventory(Player player) {
        return CashManager.getAmountFromCash(CashManager.getCashFromInventory(player.getInventory()));
    }

    /**
     * Take cash from player
     * @param amount Amount that will be taken
     * @param player Player that will lose this amount
     */
    public void takeCashFromInventory(double amount, Player player) {
        var playerCashAmount = amountOfCashInInventory(player);

        if (playerCashAmount < amount) return;
        if (ItemManager.getEmptySlots(player) < ChangeManager.getChange(amount).size()) return;

        ItemManager.takeItems(CashManager.getCashFromInventory(player.getInventory()), player);
        giveSpecialAmountOfCashToPlayer(playerCashAmount - amount, player);
    }
    
    public static String getCurrencyNameCase(String currencyName) { // Получение склонённого названия валюты
    	return currencyNameCases.get(currencyName);
    }
    
    // TODO заменить везде слова валюты с количеством денег на этот метод
    // Например ("В кошельке лежит " + amount + CashWorker.getCurrencyNameByNum(amount))
    public static String getCurrencyNameByNum(int num) { // Получение склонённого названия валюты в зависимости от суммы денег (в именительном падеже)
    	if (num % 10 == 1 && num % 100 != 11) return currencyNameCases.get("is");
    	if (num % 10 >= 2 && num % 10 <= 4 && (num % 100 < 12 || num % 100 > 14)) return currencyNameCases.get("rs");
    	return currencyNameCases.get("rp");
    }
}
