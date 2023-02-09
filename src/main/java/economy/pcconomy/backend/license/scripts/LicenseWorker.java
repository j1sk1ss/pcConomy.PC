package economy.pcconomy.backend.license.scripts;

import economy.pcconomy.backend.license.objects.LicenseBody;
import economy.pcconomy.backend.license.objects.LicenseType;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LicenseWorker {
    public static List<LicenseBody> Licenses = new ArrayList<>();

    public static void CreateLicense(LicenseBody licenseBody) {
        Licenses.add(licenseBody);
    }

    public static boolean isOverdue(LicenseBody licenseBody) {
        return LocalDateTime.now().isAfter(licenseBody.Term);
    }

    public static LicenseBody GetLicense(Player player) {
        for (LicenseBody lic:
             Licenses) {
            if (lic.Owner.equals(player)) return lic;
        }

        return null;
    }

    public static LicenseBody GetLicense(Player player, LicenseType licenseType) {
        for (LicenseBody lic:
                Licenses) {
            if (lic.Owner.equals(player)) if (lic.LicenseType.equals(licenseType)) return lic;
        }

        return null;
    }
}
