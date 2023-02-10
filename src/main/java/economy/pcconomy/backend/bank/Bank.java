package economy.pcconomy.backend.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import economy.pcconomy.backend.bank.objects.LoanObject;
import economy.pcconomy.backend.bank.scripts.BorrowerWorker;
import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Bank {
    public Bank() {
        Credit = new Hashtable<>();
    }

    public double BankBudget = 5000.0d;
    public double UsefulBudgetPercent = .2d;
    public Dictionary<Player, LoanObject> Credit;

    public void PlayerWithdrawCash(double amount, Player player) {
        // Метод снятия денег в городе из банка игроком (если в городе есть на это бюджет)
        var balanceWorker = new BalanceWorker();
        var cash = new Cash();

        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;
        if (balanceWorker.isSolvent(amount, player)) return;

        balanceWorker.TakeMoney(amount, player);
        PcConomy.GlobalBank.BankBudget -= amount;
        cash.GiveCashToPlayer(amount, player);
    }

    public void PlayerPutCash(ItemStack money, Player player) { // Метод внесения купюры в городе в банк
        if (!CashWorker.isCash(money)) return;

        var amount = new CashWorker().GetAmountFromCash(money);
        ItemWorker.TakeItems(money, player);
        new BalanceWorker().GiveMoney(amount, player);
        PcConomy.GlobalBank.BankBudget += amount;
    }

    public void PlayerPutCash(double amount, Player player) { // Метод внесения денег в городе в банк
        var amountInventory = new CashWorker().GetAmountFromCash(
                new CashWorker().GetCashFromInventory(player.getInventory()));
        if (amount > amountInventory) return;

        new Cash().TakeCashFromInventory(amount, player);
        new BalanceWorker().GiveMoney(amount, player);
        PcConomy.GlobalBank.BankBudget += amount;
    }

    public void CreateLoan(double amount, int duration, Player player) {
        // Создание кредита на игрока
        var percentage = LoanWorker.getPercent(amount, duration); // процент по кредиту
        var dailyPayment = LoanWorker.getDailyPayment(amount, duration, percentage); // дневной платёж

        Credit.put(player, new LoanObject(amount + amount * percentage, percentage, duration, dailyPayment));

        BankBudget -= amount;
        new BalanceWorker().GiveMoney(amount, player);
    }

    public void TakePercentFromBorrowers() {
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

            if (balanceWorker.isSolvent(loan.dailyPayment, player)) {
                loan.expired += 1;
                return;
            }

            balanceWorker.TakeMoney(loan.dailyPayment, player);
            loan.amount -= loan.dailyPayment;

            BankBudget += loan.dailyPayment;
        }
    }

    public void DestroyLoan(Player player) {
        // Закрытие кредита
        var loan = Credit.get(player);

        var borrower = BorrowerWorker.getBorrowerObject(player);
        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            BorrowerWorker.setBorrowerObject(borrower);
        } else {
            BorrowerWorker.borrowerObjects.add(new BorrowerObject(player, loan));
        }

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
