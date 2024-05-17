package economy.pcconomy.backend.economy.credit;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.Capitalist;
import economy.pcconomy.backend.cash.Balance;
import economy.pcconomy.backend.economy.PlayerManager;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.ExtensionMethod;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@ExtensionMethod({Balance.class, PlayerManager.class, BorrowerManager.class})
public class Loan {
    /**
     * Loan object
     * @param amount Amount what was got as loan
     * @param percentage Percent of loan
     * @param duration Duration of loan
     * @param dayPayment Daily payment
     * @param player Loan owner
     */
    public Loan(double amount, double percentage, int duration, double dayPayment, Player player) {
        this.amount       = amount;
        this.percentage   = percentage;
        this.duration     = duration;
        this.dailyPayment = dayPayment;

        Owner = player.getUniqueId();
    }

    @Getter private final UUID Owner;
    @Getter @Setter private double amount;
    @Getter private final double percentage;
    @Getter private final int duration;
    @Getter private final double dailyPayment;
    @Getter @Setter private int expired;

    /**
     * Add loan to loan owner (Pays starts)
     * @param loanOwner LoanOwner (For example Bank)
     */
    public void addLoan(Capitalist loanOwner) {
        loanOwner.getCreditList().add(this);
        loanOwner.changeBudget(-amount);

        Objects.requireNonNull(Bukkit.getPlayer(Owner)).giveMoney(amount);
    }

    /**
     * Gets percent of current loan
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @return Percent of this loan
     */
    public static double getPercent(double amount, double duration) {
        return Math.round((PcConomy.GlobalBank.getBank().getDayWithdrawBudget() / (amount * duration)) * 1000d) / 1000d;
    }

    /**
     * Gets daily payment of current loan
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @return Daily payment of this loan
     */
    public static double getDailyPayment(double amount, double duration, double percent) {
        return (amount + amount * (percent / 100d)) / duration;
    }

    /**
     * Gets safety factor of current loan
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @return safety factor of this loan
     */
    public static double getSafetyFactor(double amount, int duration, Borrower borrower) {
        var expired = 0;
        if (borrower == null) return ((duration / 100d)) / (expired + (amount / PcConomy.GlobalBank.getBank().getDayWithdrawBudget()));
        for (var loan : borrower.CreditHistory) expired += loan.expired;
        return (borrower.CreditHistory.size() + (duration / 100d)) / (expired + (amount / PcConomy.GlobalBank.getBank().getDayWithdrawBudget()));
    }

    /**
     * Loan status
     * @param loanAmount Amount of loan
     * @param duration Duration of loan
     * @param borrower Borrower who wants to take loan
     * @return Loan status for this borrower
     */
    public static boolean isSafeLoan(double loanAmount, int duration, Capitalist loaner, Player borrower) {
        return (getSafetyFactor(loanAmount, duration, borrower.getBorrowerObject()) >= loaner.getTrustCoefficient()
                && blackTown(borrower.getUniqueId().getCountryMens())
                && borrower.getPlayerServerDuration() > 100);
    }

    /**
     * Checks if town have player, that not pay credit
     * @param uuids UUID of players from town
     * @return Status of town
     */
    public static boolean blackTown(List<UUID> uuids) {
        return uuids.parallelStream().anyMatch(uuid -> {
            var loan = getLoan(uuid, PcConomy.GlobalBank.getBank());
            return loan != null && loan.expired > 5;
        });
    }

    /**
     * Create loan object
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @param player Player who takes loan
     */
    public static Loan createLoan(double amount, int duration, Player player) {
        var percentage   = getPercent(amount, duration);
        var dailyPayment = getDailyPayment(amount, duration, percentage);
        return new Loan(amount + amount * percentage, percentage, duration, dailyPayment, player);
    }

    /**
     * Pay for all debt
     * @param player Player who close loan
     * @param creditOwner Credit owner
     */
    public static void payOffADebt(Player player, Capitalist creditOwner) {
        var loan = getLoan(player.getUniqueId(), creditOwner);

        if (loan == null) return;
        if (player.solvent(loan.amount)) return;

        player.takeMoney(loan.amount);
        creditOwner.changeBudget(loan.amount);
        destroyLoan(player.getUniqueId(), creditOwner);
    }

    /**
     * Take percent from all borrowers
     * @param moneyTaker Money taker
     */
    public static void takePercentFromBorrowers(Capitalist moneyTaker) {
        for (var loan: moneyTaker.getCreditList()) {
            var owner = Bukkit.getPlayer(loan.Owner);
            if (owner == null) return;
            if (loan.amount <= 0) {
                destroyLoan(loan.Owner, moneyTaker);
                return;
            }

            if (owner.solvent(loan.dailyPayment)) {
                loan.expired += 1;
                continue;
            }

            owner.takeMoney(loan.dailyPayment);
            loan.amount -= loan.dailyPayment;

            moneyTaker.changeBudget(loan.dailyPayment);
        }
    }

    /**
     * Get loan by UUID
     * @param player Player UUID
     * @param creditOwner Credit owner
     * @return Loan object
     */
    public static Loan getLoan(UUID player, Capitalist creditOwner) {
        for (var loan: creditOwner.getCreditList())
            if (loan.Owner.equals(player)) return loan;

        return null;
    }

    /**
     * Destroy loan from credit owner`s list by player UUID
     * @param player Player UUID
     * @param creditOwner Credit owner
     */
    public static void destroyLoan(UUID player, Capitalist creditOwner) {
        var credit   = creditOwner.getCreditList();
        var loan     = getLoan(player, creditOwner);
        var borrower = Bukkit.getPlayer(player).getBorrowerObject();

        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            borrower.setBorrowerObject();
        } else PcConomy.GlobalBorrower.borrowers.add(new Borrower(Objects.requireNonNull(Bukkit.getPlayer(player)), loan));

        credit.remove(getLoan(player, creditOwner));
    }
}
