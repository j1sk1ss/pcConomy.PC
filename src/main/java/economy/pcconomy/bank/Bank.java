package economy.pcconomy.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.bank.objects.BorrowerObject;
import economy.pcconomy.bank.objects.LoanObject;
import economy.pcconomy.cash.Cash;
import economy.pcconomy.scripts.BalanceWorker;
import economy.pcconomy.scripts.CashWorker;
import economy.pcconomy.scripts.ItemWorker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.List;

public class Bank {
    public double BankBudget = .0d;
    public double UsefulBudgetPercent = .2d;
    public Dictionary<Player, LoanObject> Credit;
    public List<BorrowerObject> borrowerObjects = new ArrayList<>();
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

        if (getBorrowerObject(player) != null) // есть кредитная история
            if (getBorrowerObject(player).getSafetyFactor(amount) > 1) return; // коэффициент надёжности

        var percentage = getPercent(amount, duration); // процент по кредиту
        var dailyPayment = getDailyPayment(amount, duration, percentage); // дневной платёж

        Credit.put(player, new LoanObject(amount, percentage, duration, dailyPayment));

        BankBudget -= amount;
        new BalanceWorker().GiveMoney(amount, player);
    }

    public double getPercent(double amount, double duration) {
        // Выдать процент под параметры
        return (amount / duration) / 100d;
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

            if (!balanceWorker.isSolvent(loan.dailyPayment, player)) {
                loan.expired += 1;
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

        var borrower = getBorrowerObject(player);
        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            setBorrowerObject(borrower);
        } else {
            borrowerObjects.add(new BorrowerObject(player, loan));
        }

        Credit.remove(player);
    }

    private BorrowerObject getBorrowerObject(Player player) {
        for (BorrowerObject borrower:
             borrowerObjects) {
            if (borrower.Borrower.equals(player)) return borrower;
        }
        return null;
    }

    private void setBorrowerObject(BorrowerObject borrowerObject) {
        for (BorrowerObject borrower:
                borrowerObjects) {
            if (borrower.Borrower.equals(borrowerObject.Borrower)) {
                borrowerObjects.remove(borrower);
                borrowerObjects.add(borrowerObject);
            }
        }
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
