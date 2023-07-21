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

    public final List<Loan> Credit;

    /**
     * Give cash from bank player`s balance to player`s inventory
     * @param amount Amount of given cash
     * @param player Player that will take cash
     */
    public void giveCashToPlayer(double amount, Player player) {
        if (amount >= dayWithdrawBudget) return;
        if (PcConomy.GlobalBalanceManager.notSolvent(amount, player)) return;

        dayWithdrawBudget -= amount;

        PcConomy.GlobalBalanceManager.takeMoney(amount, player);
        CashManager.giveCashToPlayer(amount, player, false);

        BankBudget -= amount;
    }

    /**
     * Take cash from player`s inventory to bank player`s balance
     * @param amount Amount of taken cash
     * @param player Player that will lose cash
     */
    public void takeCashFromPlayer(double amount, Player player) {
        var amountInventory = CashManager.amountOfCashInInventory(player, false);
        if (amount > amountInventory) return;

        CashManager.takeCashFromPlayer(amount, player, false);
        PcConomy.GlobalBalanceManager.giveMoney(amount, player);

        BankBudget += amount;
        dayWithdrawBudget += amount;
    }

    private double previousBudget = BankBudget;
    private double dayWithdrawBudget = BankBudget * UsefulBudgetPercent;
    private int recessionCount = 0;

    /**
     * Life cycle of bank working
     * First part - pay for deposit from moneys, that was taken from loans percent
     * Second part - calculate change percent of budget and recession status
     * Third part - changing VAT, useful budget and trust coefficient
     * Fourth part - updating day budget
     */
    @Override
    public void newDay() {
        var amount = LoanManager.takePercentFromBorrowers(this) * UsefulBudgetPercent;
        var players = Bukkit.getOnlinePlayers();
        for (var player : players) {
            PcConomy.GlobalBalanceManager.giveMoney(amount / players.size(), player);
            BankBudget -= amount / players.size();
        }

        var changePercent = (BankBudget - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 && getGlobalInflation() > 0) ? 1 : -1;

        VAT                          += (VAT * Math.abs(changePercent) / 2) * isRecession;
        UsefulBudgetPercent          -= UsefulBudgetPercent * Math.abs(changePercent) / 2 * isRecession;
        LoanManager.trustCoefficient -= LoanManager.trustCoefficient * Math.abs(changePercent) * isRecession;

        if (isRecession > 0) recessionCount++;
        else recessionCount = 0;

        if (recessionCount >= 5) {
            BankBudget += BankBudget * (changePercent * recessionCount);
            recessionCount = 0;
        }

        previousBudget    = BankBudget;
        dayWithdrawBudget = BankBudget * UsefulBudgetPercent;
    }

    /**
     * Gets global inflation of all towns
     * @return Inflation rate
     */
    public double getGlobalInflation() {
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
     * Get useful amount of budget
     * @return Useful amount of budget
     */
    public double getUsefulAmountOfBudget() {
        return dayWithdrawBudget;
    }

    /**
     * Set useful amount of budget
     */
    public void setUsefulBudgetPercent(double amount) {
        dayWithdrawBudget = amount;
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