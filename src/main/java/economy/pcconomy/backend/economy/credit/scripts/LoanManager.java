package economy.pcconomy.backend.economy.credit.scripts;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.economy.IMoney;
import economy.pcconomy.backend.economy.credit.Borrower;
import economy.pcconomy.backend.economy.credit.Loan;
import economy.pcconomy.backend.scripts.BalanceManager;
import economy.pcconomy.backend.scripts.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class LoanManager {
    public static double trustCoefficient = 1.5d;

    /***
     * Gets percent of current loan
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @return Percent of this loan
     */
    public static double getPercent(double amount, double duration) {
        return Math.round((PcConomy.GlobalBank.getUsefulAmountOfBudget() / (amount * duration)) * 1000d) / 1000d;
    }

    /***
     * Gets daily payment of current loan
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @return Daily payment of this loan
     */
    public static double getDailyPayment(double amount, double duration, double percent) {
        return (amount + amount * (percent / 100d)) / duration;
    }

    /***
     * Gets safety factor of current loan
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @return safety factor of this loan
     */
    public static double getSafetyFactor(double amount, int duration, Borrower borrower) {
        var expired = 0;
        if (borrower == null) return ((duration / 100d)) /
                (expired + (amount / PcConomy.GlobalBank.getUsefulAmountOfBudget()));

        for (var loan: borrower.CreditHistory)
            expired += loan.expired;

        return (borrower.CreditHistory.size() + (duration / 100d)) /
                (expired + (amount / PcConomy.GlobalBank.getUsefulAmountOfBudget()));
    }

    /***
     * Loan status
     * @param loanAmount Amount of loan
     * @param duration Duration of loan
     * @param borrower Borrower who wants to take loan
     * @return Loan status for this borrower
     */
    public static boolean isSafeLoan(double loanAmount, int duration, Player borrower) {
        return (getSafetyFactor(loanAmount, duration, PcConomy.GlobalBorrowerManager.getBorrowerObject(borrower)) >= trustCoefficient
                && blackTown(PlayerManager.getCountryMens(borrower.getUniqueId()))
                && PlayerManager.getPlayerServerDuration(borrower) > 100);
    }

    /**
     * Checks if town have player, that not pay credit
     * @param uuids UUID of players from town
     * @return Status of town
     */
    public static boolean blackTown(List<UUID> uuids) {
        for (var uuid : uuids) {
            var loan = getLoan(uuid, PcConomy.GlobalBank);

            if (loan != null)
                if (loan.expired > 5) return true;
        }

        return false;
    }

    /***
     * Create loan object
     * @param amount Amount of loan
     * @param duration Duration of loan
     * @param player Player who takes loan
     * @param moneyGiver Money giver
     */
    public static void createLoan(double amount, int duration, Player player, IMoney moneyGiver) {
        var percentage = LoanManager.getPercent(amount, duration);
        var dailyPayment = LoanManager.getDailyPayment(amount, duration, percentage);

        moneyGiver.getCreditList().add(new Loan(amount + amount * percentage, percentage, duration, dailyPayment, player));
        moneyGiver.changeBudget(-amount);

        new BalanceManager().giveMoney(amount, player);
    }

    /***
     * Pay for all debt
     * @param player Player who close loan
     * @param creditOwner Credit owner
     */
    public static void payOffADebt(Player player, IMoney creditOwner) {
        var balance = new BalanceManager();
        var loan = getLoan(player.getUniqueId(), creditOwner);

        if (loan == null) return;
        if (balance.notSolvent(loan.amount, player)) return;

        balance.takeMoney(loan.amount, player);
        creditOwner.changeBudget(loan.amount);
        destroyLoan(player.getUniqueId(), creditOwner);
    }

    /***
     * Take percent from all borrowers
     * @param moneyTaker Money taker
     */
    public static void takePercentFromBorrowers(IMoney moneyTaker) {
        for (Loan loan: moneyTaker.getCreditList()) {
            if (loan.amount <= 0) {
                destroyLoan(loan.Owner, moneyTaker);
                return;
            }

            var balanceWorker = new BalanceManager();
            if (balanceWorker.notSolvent(loan.dailyPayment, Objects.requireNonNull(Bukkit.getPlayer(loan.Owner))))
                loan.expired += 1;

            balanceWorker.takeMoney(loan.dailyPayment, Bukkit.getPlayer(loan.Owner));
            loan.amount -= loan.dailyPayment;

            moneyTaker.changeBudget(loan.dailyPayment);
        }
    }

    /***
     * Get loan by UUID
     * @param player Player UUID
     * @param creditOwner Credit owner
     * @return Loan object
     */
    public static Loan getLoan(UUID player, IMoney creditOwner) {
        for (Loan loan: creditOwner.getCreditList())
            if (loan.Owner.equals(player)) return loan;

        return null;
    }

    /***
     * Destroy loan from credit owner`s list by player UUID
     * @param player Player UUID
     * @param creditOwner Credit owner
     */
    public static void destroyLoan(UUID player, IMoney creditOwner) {
        var credit = creditOwner.getCreditList();
        var loan = getLoan(player, creditOwner);
        var borrower = PcConomy.GlobalBorrowerManager.getBorrowerObject(Bukkit.getPlayer(player));

        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            PcConomy.GlobalBorrowerManager.setBorrowerObject(borrower);
        } else
            PcConomy.GlobalBorrowerManager.borrowers.add(new Borrower(Objects.requireNonNull(Bukkit.getPlayer(player)), loan));

        credit.remove(getLoan(player, creditOwner));
    }
}
