package economy.pcconomy.backend.bank.scripts;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.bank.objects.Borrower;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BorrowerManager {
    public final List<Borrower> borrowers = new ArrayList<>();

    public Borrower getBorrowerObject(Player player) {
        for (Borrower borrower: borrowers) {
            if (borrower.Borrower.equals(player.getUniqueId())) return borrower;
        }
        return null;
    }

    public void setBorrowerObject(Borrower borrowerObject) {
        for (Borrower borrower: borrowers) {
            if (borrower.Borrower.equals(borrowerObject.Borrower)) {
                borrowers.remove(borrower);
                borrowers.add(borrowerObject);
            }
        }
    }

    public void SaveBorrowers(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".txt", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
