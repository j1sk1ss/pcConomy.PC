package economy.pcconomy.backend.license.scripts;

import com.google.gson.*;
import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LicenseManager {
    public final List<LicenseBody> Licenses = new ArrayList<>();

    /***
     * Creates new license
     * @param licenseBody License body
     */
    public void createLicense(LicenseBody licenseBody) {
        Licenses.add(licenseBody);
    }

    /***
     * Gets license of player
     * @param player Player that should be checked
     * @return License body
     */
    public LicenseBody getLicense(Player player) {
        for (LicenseBody lic: Licenses)
            if (lic.Owner.equals(player.getUniqueId())) return lic;

        return null;
    }

    /***
     * Gets license body of player with specified type
     * @param player UUID of player
     * @param licenseType Specified license type
     * @return License body
     */
    public LicenseBody getLicense(UUID player, LicenseType licenseType) {
        for (LicenseBody lic : Licenses)
            if (lic.Owner.equals(player))
                if (lic.LicenseType.equals(licenseType)) return lic;

        return null;
    }

    /***
     * Saves license
     * @param fileName File name
     * @throws IOException If something goes wrong
     */
    public void saveLicenses(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".json", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
