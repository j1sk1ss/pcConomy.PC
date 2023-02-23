package economy.pcconomy.backend.bank.scripts;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.interfaces.IMoney;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import economy.pcconomy.backend.bank.objects.LoanObject;
import economy.pcconomy.backend.scripts.BalanceWorker;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class LoanWorker {
    public static double trustCoefficient = 1.5d;

    public static double getPercent(double amount, double duration) {
        // Выдать процент под параметры
        return Math.round((PcConomy.GlobalBank.GetUsefulAmountOfBudget() / (amount * duration)) * 1000d) / 1000d;
    }

    public static double getDailyPayment(double amount, double duration, double percent) {
        // Выдать дневной платёж по параметрам
        return (amount + amount * (percent / 100d)) / duration;
    }

    public static double getSafetyFactor(double amount, int duration, BorrowerObject borrowerObject) {
        var expired = 0;
        if (borrowerObject == null) return ((duration / 100d)) /
                (expired + (amount / PcConomy.GlobalBank.GetUsefulAmountOfBudget()));

        for (LoanObject loan:
                borrowerObject.CreditHistory) {
            expired += loan.expired;
        }

        return (borrowerObject.CreditHistory.size() + (duration / 100d)) /
                (expired + (amount / PcConomy.GlobalBank.GetUsefulAmountOfBudget()));
    }

    public static boolean isSafeLoan(double loanAmount, int duration, Player borrower) {
        return (getSafetyFactor(loanAmount, duration,
                PcConomy.GlobalBorrowerWorker.getBorrowerObject(borrower)) >= trustCoefficient); // коэффициент надёжности
    }

    public static void createLoan(double amount, int duration, Player player, List<LoanObject> Credit, IMoney moneyGiver) {
        // Создание кредита на игрока
        var percentage = LoanWorker.getPercent(amount, duration); // процент по кредиту
        var dailyPayment = LoanWorker.getDailyPayment(amount, duration, percentage); // дневной платёж

        Credit.add(new LoanObject(amount + amount * percentage, percentage, duration, dailyPayment, player));

        moneyGiver.ChangeBudget(-amount);
        new BalanceWorker().GiveMoney(amount, player);
    }

    public static void payOffADebt(Player player, IMoney creditOwner) {
        var balance = new BalanceWorker();
        var loan = getLoan(player.getUniqueId(), creditOwner);

        if (loan == null) return;
        if (balance.notSolvent(loan.amount, player)) return;

        balance.TakeMoney(loan.amount, player);
        creditOwner.ChangeBudget(loan.amount);
        destroyLoan(player.getUniqueId(), creditOwner);
    }

    public static void takePercentFromBorrowers(IMoney moneyTaker) {
        for (LoanObject loan:
                moneyTaker.GetCreditList()) {
            if (loan.amount <= 0) {
                destroyLoan(loan.Owner, moneyTaker);
                return;
            }

            var balanceWorker = new BalanceWorker();
            if (balanceWorker.notSolvent(loan.dailyPayment, Bukkit.getPlayer(loan.Owner)))
                loan.expired += 1;

            balanceWorker.TakeMoney(loan.dailyPayment, Bukkit.getPlayer(loan.Owner));
            loan.amount -= loan.dailyPayment;

            moneyTaker.ChangeBudget(loan.dailyPayment);
        }
    }

    public static LoanObject getLoan(UUID player, IMoney creditOwner) {
        for (LoanObject loan:
                creditOwner.GetCreditList()) {
            if (loan.Owner.equals(player)) return loan;
        }

        return null;
    }

    public static void destroyLoan(UUID player, IMoney creditOwner) {
        var credit = creditOwner.GetCreditList();
        // Закрытие кредита
        var loan = getLoan(player, creditOwner);

        var borrower = PcConomy.GlobalBorrowerWorker.getBorrowerObject(Bukkit.getPlayer(player));
        if (borrower != null) {
            borrower.CreditHistory.add(loan);
            PcConomy.GlobalBorrowerWorker.setBorrowerObject(borrower);
        } else {
            PcConomy.GlobalBorrowerWorker.borrowerObjects.add(new BorrowerObject(Bukkit.getPlayer(player), loan));
        }

        credit.remove(getLoan(player, creditOwner));
    }
}
