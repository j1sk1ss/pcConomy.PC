package economy.pcconomy.backend.bank.scripts;

import com.google.gson.GsonBuilder;
import economy.pcconomy.backend.bank.objects.BorrowerObject;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BorrowerWorker {
    public List<BorrowerObject> borrowerObjects = new ArrayList<>();

    public BorrowerObject getBorrowerObject(Player player) {
        for (BorrowerObject borrower:
                borrowerObjects) {
            if (borrower.Borrower.equals(player.getUniqueId())) return borrower;
        }
        return null;
    }

    public void setBorrowerObject(BorrowerObject borrowerObject) {
        for (BorrowerObject borrower:
                borrowerObjects) {
            if (borrower.Borrower.equals(borrowerObject.Borrower)) {
                borrowerObjects.remove(borrower);
                borrowerObjects.add(borrowerObject);
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
