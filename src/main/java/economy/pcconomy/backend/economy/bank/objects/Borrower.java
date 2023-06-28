package economy.pcconomy.backend.economy.bank.objects;

import org.bukkit.entity.Player;

import java.util.*;

public class Borrower {
    public Borrower(Player player, Loan loan) {
        Borrower      = player.getUniqueId();
        CreditHistory = new ArrayList<>();

        CreditHistory.add(loan);
    }

    public final UUID Borrower;
    public final List<Loan> CreditHistory;
}
