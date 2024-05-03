package economy.pcconomy.backend.license.objects;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;


public class License {
    public License(Player owner, LocalDateTime term, LicenseType licenseType) {
        Term        = term.toString();
        Owner       = owner.getUniqueId();
        LicenseType = licenseType;
    }

    public final UUID Owner;
    public final String Term;
    public final LicenseType LicenseType;

    /**
     * Checks status of license
     * @return Status of license
     */
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(Term));
    }
}



