package economy.pcconomy.backend.license.objects;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;

public class LicenseBody {

    public LicenseBody(Player owner, LocalDateTime term, LicenseType licenseType) {
        Term        = term.toString();
        Owner       = owner.getUniqueId();
        LicenseType = licenseType;
    }

    public UUID Owner;
    public String Term;
    public LicenseType LicenseType;
}

