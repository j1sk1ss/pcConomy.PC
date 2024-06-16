package economy.pcconomy.backend.economy.license.objects;

import lombok.Getter;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.util.UUID;


public class License {
    public License(Player owner, LocalDateTime term, LicenseType licenseType) {
        this.term = term.toString();
        this.owner = owner.getUniqueId();
        this.licenseType = licenseType;
    }

    @Getter private final UUID owner;
    @Getter private final String term;
    @Getter private final LicenseType licenseType;

    /**
     * Checks status of license
     * @return Status of license
     */
    public boolean isOverdue() {
        return LocalDateTime.now().isAfter(LocalDateTime.parse(term));
    }
}
