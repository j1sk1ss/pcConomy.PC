package economy.pcconomy.backend.economy.bank;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.Capitalist;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.economy.town.towns.NpcTown;
import economy.pcconomy.backend.cash.Cash;

import economy.pcconomy.backend.cash.Balance;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;


@ExtensionMethod({Cash.class, Balance.class})
public class Bank extends Capitalist {
    public Bank() {
        budget = PcConomy.Config.getDouble("bank.start_budget", 15000d);
        usefulBudgetPercent = PcConomy.Config.getDouble("bank.start_useful_budget", .25d);
        vat                 = PcConomy.Config.getDouble("bank.start_VAT", .1d);
        depositPercent    = PcConomy.Config.getDouble("bank.start_deposit_percent", .05d);
        dayWithdrawBudget = budget * usefulBudgetPercent;
        trustCoefficient  = .5d;

        credit = new ArrayList<>();
    }

    @Getter @Setter private double budget;
    @Getter @Setter private double usefulBudgetPercent;
    @Getter @Setter private double vat;
    @Getter @Setter private double depositPercent;
    @Getter @Setter private double dayWithdrawBudget;
    @Setter private double trustCoefficient;
    @Getter private final List<Loan> credit;

    private double previousBudget = budget;
    private int recessionCount    = 0;

    /**
     * Give cash from bank player`s balance to player`s inventory
     * @param amount Amount of given cash
     * @param player Player that will take cash
     */
    public void giveCash2Player(double amount, Player player) {
        if (amount >= dayWithdrawBudget) return;
        if (player.solvent(amount)) return;

        player.takeMoney(amount);
        player.giveCashToPlayer(amount, false);

        budget -= amount;
        dayWithdrawBudget -= amount;
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

        budget += amount;
        dayWithdrawBudget += amount;
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
        var changePercent = (budget - previousBudget) / previousBudget;
        var isRecession  = (changePercent <= 0 || getAverageInflation() > 0) ? 1 : -1;

        Loan.takePercentFromBorrowers(this);
        if (isRecession < 0 && depositPercent > 0)
            Bukkit.getWhitelistedPlayers().parallelStream().forEach((player) -> {
                if (player.getPlayer() != null) {
                    var amount = (Balance.getBalance(player.getPlayer()) * depositPercent) / 12;
                    player.getPlayer().giveMoney(amount);
                    budget -= amount;
                }
            });

        vat                 += (vat * Math.abs(changePercent) / 2) * isRecession;
        usefulBudgetPercent -= usefulBudgetPercent * Math.abs(changePercent) / 2 * isRecession;
        depositPercent -= depositPercent * Math.abs(changePercent) / 2 * isRecession;
        trustCoefficient -= trustCoefficient * Math.abs(changePercent) * isRecession;

        if (isRecession > 0) {
            if (recessionCount++ >= 5) {
                budget += budget * (changePercent * recessionCount);
                recessionCount = 0;
            }
        }
        else recessionCount = 0;

        previousBudget    = budget;
        dayWithdrawBudget = budget * usefulBudgetPercent;
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
        budget += value * vat;
        return value - (value * vat);
    }

    /**
     * This function take value, add VAT and return
     * @param value value
     * @return value with VAT
     */
    public double addVAT(double value) {
        budget += value * vat;
        return value + value * vat;
    }

    /**
     * This value return value with VAT (not change BankBudget)
     * @param value value
     * @return value with VAT
      */
    public static double checkVat(double value) {
        return value + value * PcConomy.GlobalBank.getBank().vat;
    }

    @Override
    public void changeBudget(double amount) {
        budget += amount;
    }

    @Override
    public double getTrustCoefficient() { return trustCoefficient; }

    @Override
    public List<Loan> getCreditList() {
        return credit;
    }
}