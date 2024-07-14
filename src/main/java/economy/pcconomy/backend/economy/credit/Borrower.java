package economy.pcconomy.backend.economy.credit;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
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

    private final UUID Borrower;
    @Setter public List<Loan> CreditHistory;
}
