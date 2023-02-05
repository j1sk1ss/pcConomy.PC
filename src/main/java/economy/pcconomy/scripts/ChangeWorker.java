package economy.pcconomy.scripts;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeWorker {
    public static final List<Integer> Denomination = Arrays.asList(5000, 2000, 1000, 500, 200, 100, 50, 10, 1);

    public static List<Integer> getChange(double amount) { // Получение кол-ва каждой из всех банкнот для сдачи
        List<Integer> change = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0);

        for (int i = 0; i < Denomination.size(); i++) {
            while (amount - Denomination.get(i) >= 0) {
                amount -= Denomination.get(i);
                change.set(i, change.get(i) + 1);
            }
        }

        return change;
    }

    public static List<ItemStack> getChangeInCash(double amount) { // Получение листа обьектов из сдачи
        List<ItemStack> moneyStack = new ArrayList<>();
        List<Integer> change = getChange(amount);

        for (int i = 0; i < Denomination.size(); i++) {
            moneyStack.add(CashWorker.CreateCashObject(Denomination.get(i), change.get(i)));
        }

        return moneyStack;
    }
}
