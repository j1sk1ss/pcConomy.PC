package economy.pcconomy.backend.economy.bank;

import com.google.gson.GsonBuilder;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.IMoney;
import economy.pcconomy.backend.economy.bank.objects.Loan;
import economy.pcconomy.backend.economy.bank.scripts.LoanManager;
import economy.pcconomy.backend.scripts.BalanceManager;
import economy.pcconomy.backend.cash.CashManager;

import economy.pcconomy.backend.economy.town.objects.TownObject;
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

    /***
     * Give cash from bank player`s balance to player`s inventory
     * @param amount Amount of given cash
     * @param player Player that will take cash
     */
    public void giveCashToPlayer(double amount, Player player) {
        var balanceWorker = new BalanceManager();
        var cash          = new CashManager();

        if (amount > PcConomy.GlobalBank.getUsefulAmountOfBudget()) return;
        if (balanceWorker.notSolvent(amount, player)) return;

        balanceWorker.takeMoney(amount, player);
        PcConomy.GlobalBank.BankBudget -= amount;
        cash.giveCashToPlayer(amount, player);
    }

    /***
     * Take cash from player`s inventory to bank player`s balance
     * @param amount Amount of taken cash
     * @param player Player that will lose cash
     */
    public void takeCashFromPlayer(double amount, Player player) {
        var amountInventory = CashManager.getAmountFromCash(CashManager.getCashFromInventory(player.getInventory()));
        if (amount > amountInventory) return;

        new CashManager().takeCashFromInventory(amount, player);
        new BalanceManager().giveMoney(amount, player);
        PcConomy.GlobalBank.BankBudget += amount;
    }

    double previousBudget = BankBudget;
    int recessionCount = 0;

    /***
     * Life cycle of bank working
     */
    public void lifeCycle() {
        var changePercent = (BankBudget - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && getGlobalInflation() > 0) ? 1 : -1;

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

    /***
     * Gets global inflation of all towns
     * @return Inflation rate
     */
    public double getGlobalInflation() {
        var count = 0;
        var bigInflation = 0d;

        for (TownObject town : PcConomy.GlobalTownWorker.townObjects) {
            if (!town.isNPC) continue;

            count++;
            bigInflation += town.getLocalInflation();
        }

        return bigInflation / count;
    }

    /***
     * Get useful amount of budget
     * @return Useful amount of budget
     */
    public double getUsefulAmountOfBudget() {
        return BankBudget * UsefulBudgetPercent;
    }

    /***
     * Change bank budget
     * @param amount Amount of changing
     */
    public void changeBudget(double amount) {
        BankBudget += amount;
    }

    /***
     * Gets list of loans
     * @return List of loans
     */
    public List<Loan> getCreditList() {
        return Credit;
    }

    /***
     * Saves bank into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveBank(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}