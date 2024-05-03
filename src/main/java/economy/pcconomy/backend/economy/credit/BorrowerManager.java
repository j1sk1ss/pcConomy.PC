package economy.pcconomy.backend.economy.credit;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.economy.credit.Borrower;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BorrowerManager {
    public final List<Borrower> borrowers = new ArrayList<>();

    /***
     * Get borrower object of player
     * @param player Player
     * @return Borrower object
     */
    public Borrower getBorrowerObject(Player player) {
        for (var borrower : borrowers)
            if (borrower.Borrower.equals(player.getUniqueId())) return borrower;

        return null;
    }

    /***
     * Update or sets new borrower object of player
     * @param borrowerObject New borrower object
     */
    public void setBorrowerObject(Borrower borrowerObject) {
        for (var borrower = 0; borrower < borrowers.size(); borrower++)
            if (borrowers.get(borrower).Borrower.equals(borrowerObject.Borrower))
                borrowers.set(borrower, borrowerObject);
    }

    /***
     * Save borrowers data
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveBorrowers(String fileName) throws IOException {
        var writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
