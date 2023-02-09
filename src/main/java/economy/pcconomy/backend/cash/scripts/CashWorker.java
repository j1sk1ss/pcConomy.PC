package economy.pcconomy.backend.cash.scripts;

import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CashWorker {

    public final static String currencyName = "Доллар США"; // Название валюты. Оно будет названием банкнот
    public final static String currencySigh = "$"; // Значок валюты. Он будет стоять после номинала в лоре

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

        return 0;
    }

    public double GetAmountFromCash(List<ItemStack> money) { // Получает итоговую сумму обьектов банкнот
        var amount = 0.0;

        for (var item:
             money) {
            if (isCash(item))
                amount += Double.parseDouble(ItemWorker.GetLore(item).get(0)
                        .replace(currencySigh,"")) * item.getAmount();
        }

        return amount;
    }

    public List<ItemStack> GetCashFromInventory(PlayerInventory inventory) { // Выдаёт лист всех купюр из инвентаря
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
            moneyStack.add(CashWorker.CreateCashObject(ChangeWorker.Denomination.get(i), change.get(i)));
        }

        return moneyStack;
    }

    public static boolean isCash(ItemStack item) { // Проверка обьекта на то, что это банкнота
        if (item == null) return false;
        if (ItemWorker.GetName(item).equals(currencyName))
            if (!Objects.equals(ItemWorker.GetLore(item).get(0), ""))
                return true;

        System.out.println("Это не деньги.");
        return false;
    }
}
