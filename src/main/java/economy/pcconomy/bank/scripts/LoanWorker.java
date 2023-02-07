package economy.pcconomy.bank.scripts;

import economy.pcconomy.PcConomy;
import economy.pcconomy.bank.objects.BorrowerObject;
import economy.pcconomy.bank.objects.LoanObject;
import org.bukkit.entity.Player;

public class LoanWorker {
    private final static double trustCoefficient = 1.5d;

    public static double getPercent(double amount, double duration) {
        // Выдать процент под параметры
        return (amount / duration) / 100d;
    }

    public static double getDailyPayment(double amount, double duration, double percent) {
        // Выдать дневной платёж по параметрам
        return (amount + amount * percent) / duration;
    }

    public static double getSafetyFactor(double amount, BorrowerObject borrowerObject) {
        var expired = 0;
        for (LoanObject loan:
                borrowerObject.CreditHistory) {
            expired += loan.expired;
        }

        return expired + (amount / 100d) / (borrowerObject.CreditHistory.size() + 1);
    }

    public static boolean isSafeLoan(double loanAmount, Player borrower) {
        if (PcConomy.GlobalBank.getBorrowerObject(borrower) != null) // есть кредитная история
            return !(LoanWorker.getSafetyFactor(loanAmount,
                    PcConomy.GlobalBank.getBorrowerObject(borrower)) > trustCoefficient); // коэффициент надёжности
        return true;
    }
}
