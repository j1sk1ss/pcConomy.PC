package economy.pcconomy.backend.bank.objects;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BorrowerObject {
    public BorrowerObject(Player player, LoanObject loanObject) {
        Borrower = player;
        CreditHistory = new ArrayList<>();
        CreditHistory.add(loanObject);
    }

    public Player Borrower;
    public List<LoanObject> CreditHistory;
}
