package economy.pcconomy.backend.bank;

import com.google.gson.GsonBuilder;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.interfaces.IMoney;
import economy.pcconomy.backend.bank.objects.Loan;
import economy.pcconomy.backend.bank.scripts.LoanManager;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.cash.scripts.CashManager;

import economy.pcconomy.backend.town.objects.TownObject;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Bank implements IMoney {
    public Bank() {
        Credit = new ArrayList<>();
    }

    public double BankBudget = PcConomy.Config.getDouble("bank.start_budget", 15000d);
    public double UsefulBudgetPercent = PcConomy.Config.getDouble("bank.start_useful_budget", .25d);
    public double VAT = PcConomy.Config.getDouble("bank.start_VAT", .1d);
    public final List<Loan> Credit;

    public void GiveCashToPlayer(double amount, Player player) {
        var balanceWorker = new BalanceWorker();
        var cash          = new Cash();

        if (amount > PcConomy.GlobalBank.GetUsefulAmountOfBudget()) return;
        if (balanceWorker.notSolvent(amount, player)) return;

        balanceWorker.TakeMoney(amount, player);
        PcConomy.GlobalBank.BankBudget -= amount;
        cash.GiveCashToPlayer(amount, player);
    }

    public void TakeCashFromPlayer(double amount, Player player) {
        var amountInventory = CashManager.GetAmountFromCash(CashManager.GetCashFromInventory(player.getInventory()));
        if (amount > amountInventory) return;

        new Cash().TakeCashFromInventory(amount, player);
        new BalanceWorker().GiveMoney(amount, player);
        PcConomy.GlobalBank.BankBudget += amount;
    }

    double previousBudget = BankBudget;
    int recessionCount = 0;

    public void LifeCycle() {
        var changePercent = (BankBudget - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && GetGlobalInflation() > 0) ? 1 : -1;

        VAT += (VAT * Math.abs(changePercent) / 2) * isRecession;
        UsefulBudgetPercent -= UsefulBudgetPercent * Math.abs(changePercent) / 2 * isRecession;
        LoanManager.trustCoefficient -= LoanManager.trustCoefficient * Math.abs(changePercent) * isRecession;

        if (isRecession > 0) recessionCount++;
        else recessionCount = 0;

        if (recessionCount >= 5) {
            BankBudget += BankBudget * (changePercent * recessionCount);
            recessionCount = 0;
        }

        LoanManager.takePercentFromBorrowers(this);
        previousBudget = BankBudget;
    }

    public double GetGlobalInflation() {
        var count = 0;
        var bigInflation = 0d;

        for (TownObject town :
                PcConomy.GlobalTownWorker.townObjects) {
            if (!town.isNPC) continue;

            count++;
            bigInflation += town.getLocalInflation();
        }

        return bigInflation / count;
    }

    public double GetUsefulAmountOfBudget() {
        return BankBudget * UsefulBudgetPercent;
    }

    public void ChangeBudget(double amount) {
        BankBudget += amount;
    }

    public List<Loan> GetCreditList() {
        return Credit;
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