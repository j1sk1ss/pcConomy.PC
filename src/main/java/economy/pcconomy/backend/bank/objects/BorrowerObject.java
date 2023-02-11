package economy.pcconomy.backend.bank.objects;

import org.bukkit.entity.Player;

import java.util.*;

public class BorrowerObject {
    public BorrowerObject(Player player, LoanObject loanObject) {
        Borrower = player.getUniqueId();
        CreditHistory = new ArrayList<>();
        CreditHistory.add(loanObject);
    }

    public UUID Borrower;
    public List<LoanObject> CreditHistory;
}
