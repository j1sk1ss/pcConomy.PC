package economy.pcconomy.scripts;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChangeWorker {
    private static final List<Integer> Denomination = Arrays.asList(1,5,10,50,100,500,1000,5000);

    public static List<Integer> getChange(double amount) {
        List<Integer> change = new ArrayList<>();

        for (int sum:Denomination) {
            while (amount - sum > 0) {
                amount -= sum;
                change.add(sum);
            }
        }

        return change;
    }

    public static List<ItemStack> getChangeInCash(double amount) {
        List<ItemStack> moneyStack = new ArrayList<>();

        for (int money:
             getChange(amount)) {
            moneyStack.add(CashWorker.CreateCashObject(money));
        }

        return moneyStack;
    }
}
