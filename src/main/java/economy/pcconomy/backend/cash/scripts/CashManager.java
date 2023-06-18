package economy.pcconomy.backend.cash.scripts;

import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class CashManager {
    public final static String currencyName = "Алеф"; // Название валюты. Оно будет названием банкнот
    public final static String currencySigh = "$"; // Значок валюты. Он будет стоять после номинала в лоре
    private static final HashMap<String, String> currencyNameCases = new HashMap<>(); // Список склонений названия валюты по падежам и числам
    static { // Инициализация падежей
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

    public static ItemStack CreateCashObject(double amount) { // Создаёт обьект банкноты в одном эксземпляре
        return ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER, 1),
                "" + amount + currencySigh), currencyName);
    }

    public static ItemStack CreateCashObject(double amount, int count) { // Создаёт обьекты банкнот
        return ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER, count),
                "" + amount + currencySigh), currencyName);
    }

    public static double GetAmountFromCash(ItemStack money) { // Получает итоговую сумму обьекта банкнот
        if (isCash(money))
            return Double.parseDouble(ItemWorker.GetLore(money).get(0)
                    .replace(currencySigh,"")) * money.getAmount();

        return 0;
    }

    public static double GetAmountFromCash(List<ItemStack> money) { // Получает итоговую сумму обьектов банкнот
        var amount = 0.0;

        for (var item:
             money) {
            if (isCash(item))
                amount += Double.parseDouble(ItemWorker.GetLore(item).get(0)
                        .replace(currencySigh,"")) * item.getAmount();
        }

        return amount;
    }

    public static List<ItemStack> GetCashFromInventory(PlayerInventory inventory) { // Выдаёт лист всех купюр из инвентаря
        List<ItemStack> moneys = new ArrayList<>();

        for (var item: // Формируем лист купюр
             inventory) {
            if (isCash(item)) moneys.add(item);
        }

        return moneys;
    }

    public static List<ItemStack> getChangeInCash(List<Integer> change) { // Получение листа обьектов из сдачи
        List<ItemStack> moneyStack = new ArrayList<>();

        for (int i = 0; i < ChangeWorker.Denomination.size(); i++) {
            moneyStack.add(CashManager.CreateCashObject(ChangeWorker.Denomination.get(i), change.get(i)));
        }

        return moneyStack;
    }

    public static boolean isCash(ItemStack item) { // Проверка обьекта на то, что это банкнота
        if (item == null) return false;
        if (ItemWorker.GetName(item).contains(currencyName))
            return !Objects.equals(ItemWorker.GetLore(item).get(0), "");

        return false;
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
