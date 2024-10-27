package economy.pcconomy.backend.economy.credit;

import lombok.Getter;
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

    @Getter private final UUID Borrower;
    public List<Loan> CreditHistory;
}
