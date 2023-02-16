package economy.pcconomy.backend.bank;

import com.google.gson.GsonBuilder;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import economy.pcconomy.backend.bank.objects.LoanObject;
import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Bank {
    public Bank() {
        Credit = new ArrayList<>();
    }
    public double BankBudget = 15000.0d;
    public double UsefulBudgetPercent = .25d;
    public List<LoanObject> Credit;

    public void PlayerWithdrawCash(double amount, Player player) {
        // Метод снятия денег в городе из банка игроком (если в городе есть на это бюджет)
        var balanceWorker = new BalanceWorker();
        var cash          = new Cash();

        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;
        if (balanceWorker.isSolvent(amount, player)) return;

        balanceWorker.TakeMoney(amount, player);
        PcConomy.GlobalBank.BankBudget -= amount;
        cash.GiveCashToPlayer(amount, player);
    }

    public void PlayerPutCash(ItemStack money, Player player) { // Метод внесения купюры в городе в банк
        if (!CashWorker.isCash(money)) return;
        ItemWorker.TakeItems(money, player);

        var amount = new CashWorker().GetAmountFromCash(money);
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

        Credit.add(new LoanObject(amount + amount * percentage, percentage, duration, dailyPayment, player));

        BankBudget -= amount;
        new BalanceWorker().GiveMoney(amount, player);
    }

    public void TakePercentFromBorrowers() {
        // Взятие процента со счёта игрока
        for (LoanObject loanObject : Credit) {
            if (loanObject.amount <= 0) {
                DestroyLoan(loanObject.Owner);
                return;
            }

            var balanceWorker = new BalanceWorker();
            if (balanceWorker.isSolvent(loanObject.dailyPayment, Bukkit.getPlayer(loanObject.Owner)))
                loanObject.expired += 1;

            balanceWorker.TakeMoney(loanObject.dailyPayment, Bukkit.getPlayer(loanObject.Owner));
            loanObject.amount -= loanObject.dailyPayment;

            BankBudget += loanObject.dailyPayment;
        }
    }

    public LoanObject GetLoan(UUID player) {
        for (LoanObject loan:
             Credit) {
            if (loan.Owner.equals(player)) return loan;
        }

        return null;
    }

    public void DestroyLoan(UUID player) {
        // Закрытие кредита
        var loan = GetLoan(player);

        var borrower = PcConomy.GlobalBorrowerWorker.getBorrowerObject(Bukkit.getPlayer(player));
        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            PcConomy.GlobalBorrowerWorker.setBorrowerObject(borrower);
        } else {
            PcConomy.GlobalBorrowerWorker.borrowerObjects.add(new BorrowerObject(Bukkit.getPlayer(player), loan));
        }

        Credit.remove(GetLoan(player));
    }

    public void PrintMoneys(double amount) {
        // Создание денег банком
        BankBudget += amount;
    }

    public double GetUsefulAmountOfBudget() {
        // Получение обьёма бюджета пригодного для операции
        return BankBudget * UsefulBudgetPercent;
    }

    public void SaveBank(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".txt", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
