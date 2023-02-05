package economy.pcconomy.scripts;

import economy.pcconomy.cash.Cash;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CashWorker {

    final static String currencyName = "Доллар США";
    final static String currencySigh = "$";

    public static ItemStack CreateCashObject(double amount) { // Создаёт обьект банкноты в одном эксземпляре
        return ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER, 1),
                "" + amount + currencySigh), currencyName);
    }

    public static ItemStack CreateCashObject(double amount, int count) { // Создаёт обьекты банкнот
        return ItemWorker.SetName(ItemWorker.SetLore(new ItemStack(Material.PAPER, count),
                "" + amount + currencySigh), currencyName);
    }

    public double GetAmountFromCash(ItemStack money) { // Получает итоговую сумму обьекта банкнот
        if (isCash(money))
            return Double.parseDouble(ItemWorker.GetLore(money).get(0)
                    .replace(currencySigh,"")) * money.getAmount();
        else System.out.println("Подделка.");

        return 0;
    }

    public double GetAmountFromCash(List<ItemStack> money) { // Получает итоговую сумму обьектов банкнот
        var amount = 0.0;

        for (var item:
             money) {
            if (isCash(item))
                amount += Double.parseDouble(ItemWorker.GetLore(item).get(0)
                        .replace(currencySigh,"")) * item.getAmount();
            else System.out.println("Подделка.");
        }

        return amount;
    }

    public List<ItemStack> GetCashFromInventory(PlayerInventory inventory) { // Выдаёт отсартированный лист всех купюр
        List<ItemStack> moneys = new ArrayList<>();

        for (var item: // Формируем лист купюр
             inventory) {
            if (isCash(item)) moneys.add(item);
        }

        return moneys;
    }

    public static boolean isCash(ItemStack item) { // Проверка обьекта на то, что это банкнота
        if (ItemWorker.GetName(item).equals(currencyName))
            if (!Objects.equals(ItemWorker.GetLore(item).get(0), ""))
                return true;

        System.out.println("Это не деньги.");
        return false;
    }
}
