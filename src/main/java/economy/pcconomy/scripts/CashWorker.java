package economy.pcconomy.scripts;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
        if (ItemWorker.GetName(money).equals("Доллар США")) {
            if (!Objects.equals(ItemWorker.GetLore(money).get(0), "")) {
                return Double.parseDouble(ItemWorker.GetLore(money).get(0).replace("$","")) * money.getAmount();
            } else System.out.println("Подделка.");
        } else System.out.println("Это не деньги.");

        return 0;
    }
}
