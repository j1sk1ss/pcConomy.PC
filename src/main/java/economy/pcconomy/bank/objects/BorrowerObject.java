package economy.pcconomy.bank.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BorrowerObject {
    public BorrowerObject(Player player, LoanObject loanObject) {
        Borrower = player;
        CreditHistory = Arrays.asList(loanObject);
    }

    public Player Borrower;
    public List<LoanObject> CreditHistory;

    public double getSafetyFactor(double amount) {
        var expired = 0;
        for (LoanObject loan:
             CreditHistory) {
            expired += loan.expired;
        }

        return expired + (amount / 100d) / CreditHistory.size();
    }
}
