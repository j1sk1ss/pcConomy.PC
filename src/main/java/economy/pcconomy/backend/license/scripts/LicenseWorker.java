package economy.pcconomy.backend.license.scripts;

import com.google.gson.*;
import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;
import org.bukkit.entity.Player;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LicenseWorker {
    public List<LicenseBody> Licenses = new ArrayList<>();

    public void CreateLicense(LicenseBody licenseBody) {
        Licenses.add(licenseBody);
    }

    public boolean isOverdue(LicenseBody licenseBody) {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(licenseBody.Term));
    }

    public LicenseBody GetLicense(Player player) {
        for (LicenseBody lic:
             Licenses) {
            if (lic.Owner.equals(player.getUniqueId())) return lic;
        }

        return null;
    }

    public LicenseBody GetLicense(Player player, LicenseType licenseType) {
        for (LicenseBody lic:
                Licenses) {
            if (lic.Owner.equals(player.getUniqueId())) if (lic.LicenseType.equals(licenseType)) return lic;
        }

        return null;
    }

    public void SaveLicenses(String fileName) throws IOException {
        FileWriter writer = new FileWriter(fileName + ".txt", false);
        new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create()
                .toJson(this, writer);
        writer.close();
    }
}
