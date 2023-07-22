package economy.pcconomy.backend.economy.bank;

import com.google.gson.GsonBuilder;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.Capitalist;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.credit.scripts.LoanManager;
import economy.pcconomy.backend.economy.town.NpcTown;
import economy.pcconomy.backend.cash.CashManager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Bank extends Capitalist {
    public Bank() {
        Credit = new ArrayList<>();
    }

    public double BankBudget = PcConomy.Config.getDouble("bank.start_budget", 15000d);
    public double UsefulBudgetPercent = PcConomy.Config.getDouble("bank.start_useful_budget", .25d);
    public double VAT = PcConomy.Config.getDouble("bank.start_VAT", .1d);
    public double DepositPercent = PcConomy.Config.getDouble("bank.start_deposit_percent", .05d);
    public double DayWithdrawBudget = BankBudget * UsefulBudgetPercent;

    public final List<Loan> Credit;

    /**
     * Give cash from bank player`s balance to player`s inventory
     * @param amount Amount of given cash
     * @param player Player that will take cash
     */
    public void giveCashToPlayer(double amount, Player player) {
        if (amount >= DayWithdrawBudget) return;
        if (PcConomy.GlobalBalanceManager.solvent(amount, player)) return;

        PcConomy.GlobalBalanceManager.takeMoney(amount, player);
        CashManager.giveCashToPlayer(amount, player, false);

        BankBudget -= amount;
        DayWithdrawBudget -= amount;
    }

    /**
     * Take cash from player`s inventory to bank player`s balance
     * @param amount Amount of taken cash
     * @param player Player that will lose cash
     */
    public void takeCashFromPlayer(double amount, Player player) {
        if (amount > CashManager.amountOfCashInInventory(player, false)) return;

        CashManager.takeCashFromPlayer(amount, player, false);
        PcConomy.GlobalBalanceManager.giveMoney(amount, player);

        BankBudget += amount;
        DayWithdrawBudget += amount;
    }

    private double previousBudget = BankBudget;
    private int recessionCount = 0;

    /**
     * Life cycle of bank working
     * First part - pay for deposit (Online players)
     * Second part - calculate change percent of budget and recession status
     * Third part - changing VAT, useful budget and trust coefficient
     * Fourth part - updating day budget
     */
    @Override
    public void newDay() {
        var changePercent = (BankBudget - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 || getAverageInflation() > 0) ? 1 : -1;

        LoanManager.takePercentFromBorrowers(this);
        if (isRecession < 0)
            for (var player : Bukkit.getOnlinePlayers()) {
                var amount = (PcConomy.GlobalBalanceManager.getBalance(player) * DepositPercent) / 12;
                PcConomy.GlobalBalanceManager.giveMoney(amount, player);
                BankBudget -= amount;
            }

        VAT += (VAT * Math.abs(changePercent) / 2) * isRecession;
        UsefulBudgetPercent -= UsefulBudgetPercent * Math.abs(changePercent) / 2 * isRecession;
        DepositPercent -= DepositPercent * Math.abs(changePercent) / 2 * isRecession;
        LoanManager.trustCoefficient -= LoanManager.trustCoefficient * Math.abs(changePercent) * isRecession;

        if (isRecession > 0) {
            if (recessionCount++ >= 5) {
                BankBudget += BankBudget * (changePercent * recessionCount);
                recessionCount = 0;
            }
        } else recessionCount = 0;

        previousBudget    = BankBudget;
        DayWithdrawBudget = BankBudget * UsefulBudgetPercent;
    }

    /**
     * Gets global inflation of all towns
     * @return Inflation rate
     */
    public double getAverageInflation() {
        var count = 0;
        var bigInflation = 0d;

        for (var town : PcConomy.GlobalTownManager.towns)
            if (town instanceof NpcTown npcTown) {
                count++;
                bigInflation += npcTown.getLocalInflation();
            }

        return bigInflation / count;
    }

    /**
     * Change bank budget
     * @param amount Amount of changing
     */
    @Override
    public void changeBudget(double amount) {
        BankBudget += amount;
    }

    /**
     * Gets list of loans
     * @return List of loans
     */
    @Override
    public List<Loan> getCreditList() {
        return Credit;
    }

    /**
     * Saves bank into .json file
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveBank(String fileName) throws IOException {
        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}