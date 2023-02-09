package economy.pcconomy.backend.license.objects;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class LicenseBody {

    public LicenseBody(Player owner, LocalDateTime term, LicenseType licenseType) {
        Term        = term;
        Owner       = owner;
        LicenseType = licenseType;
    }

    public Player Owner;
    public LocalDateTime Term;
    public LicenseType LicenseType;
}

