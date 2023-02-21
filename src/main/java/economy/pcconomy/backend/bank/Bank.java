package economy.pcconomy.backend.bank;

import com.google.gson.GsonBuilder;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.interfaces.IMoney;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import economy.pcconomy.backend.bank.objects.LoanObject;
import economy.pcconomy.backend.bank.scripts.LoanWorker;
import economy.pcconomy.backend.cash.Cash;
import economy.pcconomy.backend.scripts.BalanceWorker;
import economy.pcconomy.backend.cash.scripts.CashWorker;
import economy.pcconomy.backend.scripts.ItemWorker;

import economy.pcconomy.backend.town.objects.TownObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Bank implements IMoney {
    public Bank() {
        Credit = new ArrayList<>();
    }
    public double BankBudget = 15000.0d;
    public double UsefulBudgetPercent = .25d;
    public double VAT = .1d;
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

    double previousBudget = BankBudget;
    int recessionCount = 0;

    public void LifeCycle() {
        var changePercent = (BankBudget - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && GetGlobalInflation() > 0) ? 1 : -1;

        VAT += (VAT * Math.abs(changePercent) / 2) * isRecession;
        UsefulBudgetPercent -= UsefulBudgetPercent * Math.abs(changePercent) / 2 * isRecession;
        LoanWorker.trustCoefficient -= LoanWorker.trustCoefficient * Math.abs(changePercent) * isRecession;

        if (isRecession > 0) recessionCount++;
        else recessionCount = 0;

        if (recessionCount >= 5) {
            BankBudget += BankBudget * (changePercent * recessionCount);
            recessionCount = 0;
        }

        LoanWorker.takePercentFromBorrowers(this);
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
        // Получение обьёма бюджета пригодного для операции
        return BankBudget * UsefulBudgetPercent;
    }

    public void ChangeBudget(double amount) {
        BankBudget += amount;
    }

    public List<LoanObject> GetCreditList() {
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
