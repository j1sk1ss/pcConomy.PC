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
    public boolean haveLoan;
    public List<LoanObject> CreditHistory;
}
