package economy.pcconomy.town.objects;

import economy.pcconomy.PcConomy;
import economy.pcconomy.cash.Cash;
import economy.pcconomy.scripts.BalanceWorker;
import economy.pcconomy.scripts.CashWorker;
import economy.pcconomy.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BankObject {
    public double BankBudget;
    public double UsefulBudgetPercent = .2d;

    public void WithdrawCash(double amount, Player player, String townName) {
        // Метод снятия денег в городе из банка игроком (если в городе есть на это бюджет)
        var balanceWorker = new BalanceWorker();
        var cash = new Cash();

        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;
        if (balanceWorker.isSolvent(amount, player)) return;

        balanceWorker.TakeMoney(amount, player);
        PcConomy.GlobalBank.BankBudget -= amount;
        cash.GiveCashToPlayer(amount, player);
    }

    public void PutCash(ItemStack money, Player player, String townName) { // Метод внесения денег в городе в банк
        if (!CashWorker.isCash(money)) return;

        var amount = new CashWorker().GetAmountFromCash(money);
        ItemWorker.TakeItems(money, player);
        new BalanceWorker().GiveMoney(amount, player);
        PcConomy.GlobalBank.BankBudget += amount;
    }

    public void PrintMoneys(double amount) { // Только для НПС города
        BankBudget += amount;
    }

    public double GetUsefulAmountOfBudget() {
        return BankBudget * UsefulBudgetPercent;
    }
}
