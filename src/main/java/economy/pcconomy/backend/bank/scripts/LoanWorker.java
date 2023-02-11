package economy.pcconomy.backend.bank.scripts;

import economy.pcconomy.PcConomy;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import economy.pcconomy.backend.bank.objects.LoanObject;
import org.bukkit.entity.Player;

public class LoanWorker {
    private final static double trustCoefficient = 1.5d;

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
}
