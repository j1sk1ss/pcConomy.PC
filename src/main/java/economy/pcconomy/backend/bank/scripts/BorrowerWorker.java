package economy.pcconomy.backend.bank.scripts;

import economy.pcconomy.backend.bank.objects.BorrowerObject;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BorrowerWorker {
    public static List<BorrowerObject> borrowerObjects = new ArrayList<>();

    public static BorrowerObject getBorrowerObject(Player player) {
        for (BorrowerObject borrower:
                borrowerObjects) {
            if (borrower.Borrower.equals(player)) return borrower;
        }
        return null;
    }

    public static void setBorrowerObject(BorrowerObject borrowerObject) {
        for (BorrowerObject borrower:
                borrowerObjects) {
            if (borrower.Borrower.equals(borrowerObject.Borrower)) {
                borrowerObjects.remove(borrower);
                borrowerObjects.add(borrowerObject);
            }
        }
    }
}
