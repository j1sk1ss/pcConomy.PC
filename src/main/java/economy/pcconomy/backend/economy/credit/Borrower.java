package economy.pcconomy.backend.economy.credit;

import org.bukkit.entity.Player;

import java.util.*;

public class Borrower {
    /**
     * Borrower object that show credit history
     * @param player Player that own this history
     * @param loan Loan
     */
    public Borrower(Player player, Loan loan) {
        Borrower      = player.getUniqueId();
        CreditHistory = Collections.singletonList(loan);
    }

    public final UUID Borrower;
    public final List<Loan> CreditHistory;
}
