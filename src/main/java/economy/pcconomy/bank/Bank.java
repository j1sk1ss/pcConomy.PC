package economy.pcconomy.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.bank.objects.LoanObject;
import economy.pcconomy.cash.Cash;
import economy.pcconomy.scripts.BalanceWorker;
import economy.pcconomy.scripts.CashWorker;
import economy.pcconomy.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.Buffer;
import java.util.Arrays;
import java.util.Dictionary;

public class Bank {
    public double BankBudget = .0d;
    public double UsefulBudgetPercent = .2d;
    public Dictionary<Player, LoanObject> Credit;

    public void WithdrawCash(double amount, Player player) {
        // Метод снятия денег в городе из банка игроком (если в городе есть на это бюджет)
        var balanceWorker = new BalanceWorker();
        var cash = new Cash();

        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;
        if (balanceWorker.isSolvent(amount, player)) return;

        balanceWorker.TakeMoney(amount, player);
        PcConomy.GlobalBank.BankBudget -= amount;
        cash.GiveCashToPlayer(amount, player);
    }

    public void PutCash(ItemStack money, Player player) { // Метод внесения денег в городе в банк
        if (!CashWorker.isCash(money)) return;

        var amount = new CashWorker().GetAmountFromCash(money);
        ItemWorker.TakeItems(money, player);
        new BalanceWorker().GiveMoney(amount, player);
        PcConomy.GlobalBank.BankBudget += amount;
    }

    public void CreateLoan(double amount, int duration, Player player) {
        // Создание кредита на игрока
        if (GetUsefulAmountOfBudget() * 2 < amount) return; // предел кредитования

        var percentage = getPercent(amount, duration);
        var dailyPayment = getDailyPayment(amount, duration, percentage);

        Credit.put(player, new LoanObject(amount, percentage, duration, dailyPayment));

        BankBudget -= amount;
        new BalanceWorker().GiveMoney(amount, player);
    }

    public double getPercent(double amount, double duration) {
        // Выдать процент под параметры
        return (amount / duration) / 10000d;
    }

    public double getDailyPayment(double amount, double duration, double percent) {
        // Выдать дневной платёж по параметрам
        return (amount + amount * percent) / duration;
    }

    public void LoanTakePercent() {
        // Взятие процента со счёта игрока
        var keys = Credit.keys();

        for (var i = 0; i < Credit.size(); i++) {
            var balanceWorker = new BalanceWorker();

            var player = keys.nextElement();
            var loan = Credit.get(player);

            if (loan.amount <= 0) {
                DestroyLoan(player);
                return;
            }

            balanceWorker.TakeMoney(loan.dailyPayment, player);
            loan.amount -= balanceWorker.getBalance(player);

            BankBudget += loan.dailyPayment;
        }
    }

    public void DestroyLoan(Player player) {
        // Закрытие кредита
        var loan = Credit.get(player);
        if (loan.amount > 0) return;

        Credit.remove(player);
    }

    public void PrintMoneys(double amount) {
        // Создание денег банком
        BankBudget += amount;
    }

    public double GetUsefulAmountOfBudget() {
        // Получение обьёма бюджета пригодного для операции
        return BankBudget * UsefulBudgetPercent;
    }
}
