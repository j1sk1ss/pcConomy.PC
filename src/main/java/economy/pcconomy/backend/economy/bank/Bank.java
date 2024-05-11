package economy.pcconomy.backend.economy.bank;

import com.google.gson.GsonBuilder;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.db.Loadable;
import economy.pcconomy.backend.economy.Capitalist;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.town.towns.NpcTown;
import economy.pcconomy.backend.cash.Cash;

import economy.pcconomy.backend.cash.Balance;
import lombok.experimental.ExtensionMethod;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


@ExtensionMethod({Cash.class, Balance.class})
public class Bank extends Capitalist {
    public Bank() {
        BankBudget          = PcConomy.Config.getDouble("bank.start_budget", 15000d);
        UsefulBudgetPercent = PcConomy.Config.getDouble("bank.start_useful_budget", .25d);
        VAT                 = PcConomy.Config.getDouble("bank.start_VAT", .1d);
        DepositPercent      = PcConomy.Config.getDouble("bank.start_deposit_percent", .05d);
        DayWithdrawBudget   = BankBudget * UsefulBudgetPercent;
        TrustCoefficient    = .5d;

        Credit = new ArrayList<>();
    }

    public double BankBudget;
    public double UsefulBudgetPercent;
    public double VAT;
    public double DepositPercent;
    public double DayWithdrawBudget;
    public double TrustCoefficient;
    public final List<Loan> Credit;

    private double previousBudget = BankBudget;
    private int recessionCount    = 0;

    /**
     * Give cash from bank player`s balance to player`s inventory
     * @param amount Amount of given cash
     * @param player Player that will take cash
     */
    public void giveCash2Player(double amount, Player player) {
        if (amount >= DayWithdrawBudget) return;
        if (player.solvent(amount)) return;

        player.takeMoney(amount);
        player.giveCashToPlayer(amount, false);

        BankBudget -= amount;
        DayWithdrawBudget -= amount;
    }

    /**
     * Take cash from player`s inventory to bank player`s balance
     * @param amount Amount of taken cash
     * @param player Player that will lose cash
     */
    public void takeCashFromPlayer(double amount, Player player) {
        if (amount > player.amountOfCashInInventory(false)) return;

        player.takeCashFromPlayer(amount, false);
        player.giveMoney(amount);

        BankBudget += amount;
        DayWithdrawBudget += amount;
    }

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

        Loan.takePercentFromBorrowers(this);
        if (isRecession < 0 && DepositPercent > 0)
            Bukkit.getWhitelistedPlayers().parallelStream().forEach((player) -> {
                if (player.getPlayer() != null) {
                    var amount = (Balance.getBalance(player.getPlayer()) * DepositPercent) / 12;
                    player.getPlayer().giveMoney(amount);
                    BankBudget -= amount;
                }
            });

        VAT                 += (VAT * Math.abs(changePercent) / 2) * isRecession;
        UsefulBudgetPercent -= UsefulBudgetPercent * Math.abs(changePercent) / 2 * isRecession;
        DepositPercent      -= DepositPercent * Math.abs(changePercent) / 2 * isRecession;
        TrustCoefficient    -= TrustCoefficient * Math.abs(changePercent) * isRecession;

        if (isRecession > 0) {
            if (recessionCount++ >= 5) {
                BankBudget += BankBudget * (changePercent * recessionCount);
                recessionCount = 0;
            }
        }
        else recessionCount = 0;

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

        for (var town : PcConomy.GlobalTown.Towns)
            if (town instanceof NpcTown npcTown) {
                bigInflation += npcTown.getLocalInflation();
                count++;
            }

        return bigInflation / count;
    }

    /**
     * This function take value, change it with VAT, and return
     * @param value value
     * @return value without VAT
     */
    public double deleteVAT(double value) {
        BankBudget += value * VAT;
        return value - (value * VAT);
    }

    /**
     * This function take value, add VAT and return
     * @param value value
     * @return value with VAT
     */
    public double addVAT(double value) {
        BankBudget += value * VAT;
        return value + value * VAT;
    }

    /**
     * This value return value with VAT (not change BankBudget)
     * @param value value
     * @return value with VAT
      */
    public static double checkVat(double value) {
        return value + value * PcConomy.GlobalBank.getMainBank().VAT;
    }

    @Override
    public void changeBudget(double amount) {
        BankBudget += amount;
    }

    @Override
    public double getTrustCoefficient() { return TrustCoefficient; }

    @Override
    public List<Loan> getCreditList() {
        return Credit;
    }
}